package net.herospvp.premiumvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.Setter;
import net.herospvp.premiumvelocity.commands.Cracked;
import net.herospvp.premiumvelocity.commands.HerosPremium;
import net.herospvp.premiumvelocity.commands.Hub;
import net.herospvp.premiumvelocity.commands.Premium;
import net.herospvp.premiumvelocity.databases.Hikari;
import net.herospvp.premiumvelocity.databases.Redis;
import net.herospvp.premiumvelocity.databases.Storage;
import net.herospvp.premiumvelocity.monitor.AuthMeBridgeEvents;
import net.herospvp.premiumvelocity.monitor.Events;
import net.herospvp.premiumvelocity.threadbakery.Oven;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Plugin(id = "herospremium", name = "HerosPremium", version = "0.1.1-SNAPSHOT",
        url = "", description = "MultiThreaded and Velocity based PremiumLock", authors = {"Sorridi"})
public class Main {

    @Getter
    private static ProxyServer server;
    @Getter
    private static Logger logger;
    private final Path dataDirectory;
    @Getter @Setter
    private static Redis redis;
    @Getter @Setter
    private static Hikari hikari;

    @Inject
    public Main(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        Main.server = server;
        Main.logger = logger;
        this.dataDirectory = dataDirectory;

        CommandManager commandManager = server.getCommandManager();
        CommandMeta meta = commandManager.metaBuilder("herospremium").aliases("hp").build();
        commandManager.register(meta, new HerosPremium());

        meta = commandManager.metaBuilder("premium").build();
        commandManager.register(meta, new Premium());

        meta = commandManager.metaBuilder("cracked").build();
        commandManager.register(meta, new Cracked());

        meta = commandManager.metaBuilder("hub").aliases("lobby").build();
        commandManager.register(meta, new Hub());
    }

    @Subscribe
    public void on(ProxyInitializeEvent event) {

        try {
            Storage.loadData();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Could not parse JSON at ./plugins/HerosPremium/config.json");
            logger.error("OR");
            logger.error("An error occurred when initializing the database");
            logger.error("Shutting down...");
            server.shutdown();
        }

        server.getEventManager().register(this, new Events());
        server.getEventManager().register(this, new AuthMeBridgeEvents());

        Oven.runSingleRepeatingTask(() -> {
            for (Player player : getServer().getAllPlayers()) {
                String playerName = player.getUsername();

                if (Storage.getAuthenticatedPlayers().contains(playerName)) {
                    continue;
                }

                try (Jedis jedis = Main.getRedis().getPool().getResource()) {
                    jedis.auth(Main.getRedis().getPassword());
                    String args = playerName + ":login";
                    if (jedis.exists(args)) {
                        if (jedis.get(args).equals("false")) {
                            Storage.addAuthPlayer(player);
                        }
                        jedis.del(args);
                    }
                }
            }
        }, 500);
    }

}

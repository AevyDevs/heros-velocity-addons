package net.herospvp.premiumvelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.Setter;
import net.herospvp.premiumvelocity.commands.HerosPremium;
import net.herospvp.premiumvelocity.databases.Hikari;
import net.herospvp.premiumvelocity.databases.Redis;
import net.herospvp.premiumvelocity.databases.Storage;
import net.herospvp.premiumvelocity.monitor.Events;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "herospremium", name = "HerosPremium", version = "0.1.0-SNAPSHOT",
        url = "", description = "", authors = {"Sorridi"})
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
    }

    @Subscribe
    public void on(ProxyShutdownEvent event) {
        if (redis != null)
            redis.getPool().close();
    }

}

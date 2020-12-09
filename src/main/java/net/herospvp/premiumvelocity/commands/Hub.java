package net.herospvp.premiumvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.databases.Storage;
import net.herospvp.premiumvelocity.threadbakery.Oven;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import redis.clients.jedis.Jedis;

import java.util.Optional;

public class Hub implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        // Get the arguments after the command alias

        if (source instanceof ConsoleCommandSource) {
            source.sendMessage(Component.text("Non puoi da qui!"));
            return;
        }

        Player player = (Player) source;
        String playerName = player.getUsername();

        Optional<RegisteredServer> serverOptional = Main.getServer().getServer("hub-1");

        if (serverOptional.isPresent()) {
            RegisteredServer registeredServer = serverOptional.get();

            if (player.getCurrentServer().isPresent()) {
                if (player.getCurrentServer().get().getServerInfo().getName().contains("hub")) {
                    player.sendMessage(Component.text("Sei gia' alla hub!").color(NamedTextColor.RED));
                    return;
                }

                player.createConnectionRequest(registeredServer).connect();

                Oven.runSingleThreaded(() -> {
                    try (Jedis jedis = Main.getRedis().getPool().getResource()) {
                        jedis.auth(Main.getRedis().getPassword());
                        jedis.set(playerName, "hub");
                    }
                });
            }
        } else {
            player.sendMessage(Component.text("Al momento la Hub non e' raggiungibile, mi spiace.")
                .color(NamedTextColor.RED));
        }
    }

}
package net.herospvp.premiumvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.herospvp.premiumvelocity.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class Where implements SimpleCommand {

    @Override
    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("heros.proxy")) {
            source.sendMessage(Component.text("Non puoi! (heros.proxy)").color(NamedTextColor.RED));
            return;
        }

        if (args.length == 0) {
            source.sendMessage(Component.text("Utilizza /where <player>").color(NamedTextColor.RED));
            return;
        }

        String playerName = args[0];
        Optional<Player> optionalPlayer = Main.getServer().getPlayer(playerName);

        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            Optional<ServerConnection> optionalServerConnection = player.getCurrentServer();

            if (optionalServerConnection.isPresent()) {
                source.sendMessage(Component.text(playerName + " si trova su: " +
                        optionalServerConnection.get().getServerInfo().getName()));
            } else {
                source.sendMessage(Component.text(playerName + " non e' in nessun server, the fuck?")
                        .color(NamedTextColor.RED));
            }
        } else {
            source.sendMessage(Component.text(playerName + " non e' online!")
                    .color(NamedTextColor.RED));
        }

    }

}

package net.herospvp.premiumvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.databases.Storage;
import net.herospvp.premiumvelocity.monitor.BlackListEvents;
import net.herospvp.premiumvelocity.threadbakery.Oven;
import net.herospvp.premiumvelocity.utils.Splitter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class BlackList implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        // controlla se lo staffer ha il permesso
        if (!source.hasPermission("heros.proxy")) {
            source.sendMessage(Component.text("Non puoi! (heros.proxy)").color(NamedTextColor.RED));
            return;
        }

        // controlla se il comando ha almeno 2 argomenti
        if (args.length < 2) {
            source.sendMessage(Component.text("Usa /blacklist add/remove <player>").color(NamedTextColor.RED));
            return;
        }

        String status = args[0], playerName = args[1];

        switch (status) {
            case "rem":
            case "remove": {
                if (!Storage.getBlacklistedPlayers().contains(playerName)) {
                    source.sendMessage(Component.text(playerName + " non e' in blacklist!")
                            .color(NamedTextColor.RED));
                    return;
                }
                source.sendMessage(Component.text(playerName + " e' stato rimosso dalla blacklist!")
                        .color(NamedTextColor.RED));

                Oven.runSingleThreaded(() -> Main.getHikari().addOrRemoveBlacklistedPlayer(playerName));
                break;
            }
            case "add": {
                if (Storage.getBlacklistedPlayers().contains(playerName)) {
                    source.sendMessage(Component.text(playerName + " e' gia' in blacklist!")
                        .color(NamedTextColor.RED));
                    return;
                }

                source.sendMessage(Component.text(playerName + " e' stato aggiunto alla blacklist!")
                    .color(NamedTextColor.RED));

                Oven.runSingleThreaded(() -> Main.getHikari().addOrRemoveBlacklistedPlayer(playerName));

                Optional<Player> optionalPlayer = Main.getServer().getPlayer(playerName);
                if (optionalPlayer.isPresent()) {
                    Player player = optionalPlayer.get();
                    player.disconnect(Component.text("Sei blacklistato!\n\nPuoi prendere l'unblacklist su: buy.herospvp.net")
                        .color(NamedTextColor.RED));
                }

                String staffer = "CONSOLE";
                if (source instanceof Player) {
                    staffer = ((Player) source).getUsername();
                }

                for (Player player : Main.getServer().getAllPlayers()) {
                    player.sendMessage(Component.text("\n" + playerName + " e' stato blacklistato da "
                            + staffer + " \n").color(NamedTextColor.RED));
                }
                break;
            }
        }
    }

}

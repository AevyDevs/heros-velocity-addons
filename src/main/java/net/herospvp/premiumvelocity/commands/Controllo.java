package net.herospvp.premiumvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.databases.Storage;
import net.herospvp.premiumvelocity.utils.Splitter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class Controllo implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        // controlla se il source e' uno staffer
        if (!(source instanceof Player)) {
            source.sendMessage(Component.text("Non puoi da qui!").color(NamedTextColor.RED));
            return;
        }
        Player staffer = (Player) source;

        // controlla se lo staffer ha il permesso
        if (!staffer.hasPermission("staff.t_mod")) {
            staffer.sendMessage(Component.text("Non puoi! (staff.t_mod)").color(NamedTextColor.RED));
            return;
        }

        // controlla se il comando ha almeno 2 argomenti
        if (args.length < 2) {
            staffer.sendMessage(Component.text("Usa /controllo start/end <player>").color(NamedTextColor.RED));
            return;
        }

        String status = args[0], playerName = args[1];
        Optional<Player> optionalPlayer = Main.getServer().getPlayer(playerName);

        // controlla se il player e' online
        if (!optionalPlayer.isPresent()) {
            staffer.sendMessage(Component.text(playerName + " non e' online!").color(NamedTextColor.RED));
            return;
        }
        Player player = optionalPlayer.get();
        Optional<ServerConnection> optionalServerConnection = player.getCurrentServer();

        // controlla se il player e' connesso a qualche server
        if (!optionalServerConnection.isPresent()) {
            source.sendMessage(Component.text(playerName + " non e' ancora connesso a nessun server!")
                    .color(NamedTextColor.RED));
            return;
        }
        ServerConnection serverConnection = optionalServerConnection.get();

        // controlla se il player non e' al login
        if (!Storage.containsAuthPlayer(player)) {
            staffer.sendMessage(Component.text(playerName
                    + " non ha ancora eseguito l'autenticazione!").color(NamedTextColor.RED));
            return;
        }

        // controlla se il player ha l'asterisco
        if (player.hasPermission("*")) {
            staffer.sendMessage(Component.text("Non puoi!").color(NamedTextColor.RED));
            return;
        }
        String stafferName = staffer.getUsername();

        switch (status) {
            case "e":
            case "end": {
                // controlla se il player e' in controllo
                if (!Storage.getPlayersAndStafferInSS().containsKey(player)) {
                    staffer.sendMessage(Component.text(playerName + " non e' in controllo!")
                            .color(NamedTextColor.RED));
                    return;
                }

                // controlla se il player e' gia' in controllo da qualcun'altro
                if (!Storage.getPlayersAndStafferInSS().get(player).equals(staffer)) {
                    staffer.sendMessage(Component.text(playerName + " e' sotto controllo da parte di "
                        + stafferName).color(NamedTextColor.RED));
                    return;
                }

                for (Player p : Main.getServer().getAllPlayers()) {
                    if (p.hasPermission("staff.t_mod")) {
                        p.sendMessage(Component.text(stafferName
                                + " ha terminato il controllo a " + playerName).color(NamedTextColor.YELLOW));
                    }
                }

                player.sendMessage(Component.text("Controllo cheat terminato! Grazie per la pazienza uwu!")
                        .color(NamedTextColor.GREEN));

                Splitter.sendHub(player);
                Splitter.sendHub(staffer);

                Storage.getPlayersAndStafferInSS().remove(player);
                break;
            }
            case "s":
            case "start": {
                // controlla se il player e' gia' in controllo
                if (Storage.getPlayersAndStafferInSS().containsKey(player)) {
                    staffer.sendMessage(Component.text(playerName + " e' gia' in controllo da parte di " +
                            Storage.getPlayersAndStafferInSS().get(player).getUsername()).color(NamedTextColor.RED));
                    return;
                }

                for (Player p : Main.getServer().getAllPlayers()) {
                    if (p.hasPermission("staff.t_mod")) {
                        p.sendMessage(Component.text("\n" + playerName
                                + " e' stato messo sotto controllo da " + stafferName + "\n")
                                .color(NamedTextColor.YELLOW));
                    }
                }

                player.sendMessage(Component.text("\n\nSei stato messo sotto controllo da parte di " + playerName
                    + "! Perpiacere, fornisci i dati AnyDesk allo staffer in questione.").color(NamedTextColor.RED));
                player.sendMessage(Component.text("Non hai AnyDesk? Scaricalo da: https://www.anydesk.com/")
                        .color(NamedTextColor.YELLOW));

                player.createConnectionRequest(Storage.getControllo()).connect();
                staffer.createConnectionRequest(Storage.getControllo()).connect();

                Storage.getPlayersAndStafferInSS().put(player, staffer);
                break;
            }

        }
    }

}

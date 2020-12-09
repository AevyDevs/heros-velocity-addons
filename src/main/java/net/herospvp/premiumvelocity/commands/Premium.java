package net.herospvp.premiumvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.databases.Storage;
import net.herospvp.premiumvelocity.threadbakery.Oven;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Premium implements SimpleCommand {

    private CommandSource source;

    @Override
    public void execute(Invocation invocation) {
        source = invocation.source();
        // Get the arguments after the command alias
        String[] args = invocation.arguments();

        if (source instanceof ConsoleCommandSource) {
            source.sendMessage(Component.text("Non puoi da qui!"));
            return;
        }

        Player player = (Player) source;
        String playerName = player.getUsername();

        if (Storage.getDatabaseData().containsKey(playerName)) {
            player.sendMessage(Component.text("Hai gia' impostato il tuo account su: "
                    + Storage.getDatabaseData().get(playerName)));
            return;
        }

        if (args.length == 0) {
            helpMessage();
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("confirm")) {
                Oven.runSingleThreaded(() -> {
                    Main.getHikari().setConnection(playerName, true);
                });
                player.sendMessage(Component.text("Ottimo, hai impostato il tuo account su premium!")
                        .color(NamedTextColor.GREEN));
            } else {
                helpMessage();
            }
        }
    }

    private void helpMessage() {
        source.sendMessage(Component.text("Sei sicuro della tua scelta? Questa azione e' irreversibile!" +
                " Se il tuo account e' PREMIUM digita: /premium confirm").color(NamedTextColor.RED));
    }

}
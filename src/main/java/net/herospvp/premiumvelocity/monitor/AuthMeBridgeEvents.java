package net.herospvp.premiumvelocity.monitor;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import net.herospvp.premiumvelocity.databases.Storage;

import java.util.TreeSet;

public class AuthMeBridgeEvents {

    @Subscribe(order = PostOrder.LATE)
    public void on(DisconnectEvent event) {
        Player player = event.getPlayer();
        Storage.removeAuthPlayer(player);
    }

    @Subscribe(order = PostOrder.LATE)
    public void on(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!event.getResult().isAllowed() || Storage.containsAuthPlayer(player)) {
            return;
        }
        event.setResult(PlayerChatEvent.ChatResult.denied());
    }

    private static final TreeSet<String> whitelistedCommands = new TreeSet<>();

    static {
        whitelistedCommands.add("login");
        whitelistedCommands.add("l");
        whitelistedCommands.add("register");
        whitelistedCommands.add("reg");
    }

    @Subscribe
    public void on(CommandExecuteEvent event) {
        if (event.getCommandSource() instanceof Player) {
            Player player = (Player) event.getCommandSource();

            String command = event.getCommand();
            String[] commandAndArgs = command.split(" ");

            if (Storage.containsAuthPlayer(player)) {
                return;
            }

            if (!whitelistedCommands.contains(commandAndArgs[0])) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            }
        }
    }

}

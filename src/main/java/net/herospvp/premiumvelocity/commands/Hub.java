package net.herospvp.premiumvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.herospvp.premiumvelocity.utils.Splitter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Hub implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (source instanceof ConsoleCommandSource) {
            source.sendMessage(Component.text("Non puoi da qui!"));
            return;
        }

        Player player = (Player) source;

        if (player.getCurrentServer().isPresent()) {
            if (player.getCurrentServer().get().getServerInfo().getName().contains("hub")) {
                player.sendMessage(Component.text("Sei gia' alla hub!").color(NamedTextColor.RED));
                return;
            }
            Splitter.sendHub(player);
        }

    }

}
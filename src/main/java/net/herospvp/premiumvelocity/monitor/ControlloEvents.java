package net.herospvp.premiumvelocity.monitor;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.databases.Storage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ControlloEvents {

    @Subscribe(order = PostOrder.LAST)
    public void on(DisconnectEvent event) {
        Player player = event.getPlayer();

        if (!Storage.getPlayersAndStafferInSS().containsKey(player)) {
            return;
        }
        Player staffer = Storage.getPlayersAndStafferInSS().get(player);

        for (Player player1 : Main.getServer().getAllPlayers()) {
            if (player1.hasPermission("staff.t_mod")) {
                player1.sendMessage(Component.text("\n" + player.getUsername() +
                        " e' uscito dal controllo di " + staffer.getUsername() + "!\n").color(NamedTextColor.YELLOW));
            }
        }
        Storage.getPlayersAndStafferInSS().remove(player);
    }

}

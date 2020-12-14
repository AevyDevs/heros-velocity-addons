package net.herospvp.premiumvelocity.monitor;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.databases.Storage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class BlackListEvents {

    private static final Component kickMessage = Component
            .text("Sei blacklistato!\n\nPuoi prendere l'unblacklist su: buy.herospvp.net")
            .color(NamedTextColor.RED);

    @Subscribe
    public void on(LoginEvent event) {
        String playerName = event.getPlayer().getUsername();

        if (!Storage.getBlacklistedPlayers().contains(playerName)) {
            return;
        }
        event.setResult(ResultedEvent.ComponentResult.denied(kickMessage));
    }

}

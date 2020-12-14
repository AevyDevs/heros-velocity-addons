package net.herospvp.premiumvelocity.monitor;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import net.herospvp.premiumvelocity.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.TreeSet;

public class IllegalNameEvent {

    private static final TreeSet<Character> allowedChars = new TreeSet<>();

    static {
        String allowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        for (Character c : allowed.toCharArray()) allowedChars.add(c);
    }

    private static final Component kickMessage = Component.text("Non puoi entrare con quel nome!")
            .color(NamedTextColor.RED);

    @Subscribe(order = PostOrder.FIRST)
    public void on(PreLoginEvent event) {
        String playerName = event.getUsername();

        for (Character c : playerName.toCharArray()) {
            if (!allowedChars.contains(c)) {
                Main.getLogger().warn("Il nome di " + playerName + " contiene " + c + " l'ho kickato.");
                event.setResult(PreLoginEvent.PreLoginComponentResult.denied(kickMessage));
            }
        }
    }

}

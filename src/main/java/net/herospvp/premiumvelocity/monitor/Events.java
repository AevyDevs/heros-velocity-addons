package net.herospvp.premiumvelocity.monitor;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.UuidUtils;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.databases.Storage;
import net.herospvp.premiumvelocity.threadbakery.Oven;
import net.kyori.adventure.text.Component;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class Events {

    @Subscribe(order = PostOrder.LAST)
    public void on(PreLoginEvent event) {
        InboundConnection inboundConnection = event.getConnection();
        String playerName = event.getUsername();

        // get domain
        String from = inboundConnection.getVirtualHost().toString().toLowerCase();
        // domain is premium? true otherwise false
        boolean domain = from.contains("premium.herospvp.net");
        // player is set as premium in the database? true otherwise false
        boolean isPremium = false;

        if (Storage.getDatabaseData().containsKey(playerName)) {
            isPremium = Storage.getDatabaseData().get(playerName);

            if (!domain && isPremium) {
                event.setResult(PreLoginEvent.PreLoginComponentResult.denied(
                        Component.text("Mi spiace, puoi entrare solo da: mc.herospvp.net")));
                return;
            } else if (domain && !isPremium) {
                event.setResult(PreLoginEvent.PreLoginComponentResult.denied(
                        Component.text("Mi spiace, puoi entrare solo da: premium.herospvp.net")));
                return;
            } else if (domain) {
                event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
            }
        }

        boolean finalIsPremium = isPremium;
        Oven.runSingleThreaded(() -> {
            Jedis jedis = Main.getRedis().getPool().getResource();
            jedis.auth(Main.getRedis().getPassword());
            jedis.set(playerName, finalIsPremium ? "premium" : "cracked");
        });

    }

    @Subscribe
    public void on(GameProfileRequestEvent event) {
        if (event.isOnlineMode()) {
            String playerName = event.getUsername();
            UUID uuid = UuidUtils.generateOfflinePlayerUuid(playerName);

            GameProfile gameProfile = new GameProfile(uuid, playerName, event.getOriginalProfile().getProperties());
            event.setGameProfile(gameProfile);
        }
    }

    @Subscribe
    public void on(LoginEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getUsername();

        if (!Storage.getDatabaseData().containsKey(playerName)) {
            Component component = Component.text("Hey! Ho notato che il tuo account non e' al sicuro! Se desideri " +
                    "proteggere il tuo account dai furti, perpiacere digita uno dei due seguenti comandi:\n" +
                    "Se il tuo account e' premium, digita: /premium\nSe il tuo account e' cracked (o SP), digita /cracked");
            player.sendMessage(component);
        }

    }

}

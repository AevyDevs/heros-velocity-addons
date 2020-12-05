package net.herospvp.premiumvelocity.threadbakery;

import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.monitor.Events;
import net.kyori.adventure.text.Component;
import redis.clients.jedis.Jedis;

import java.util.LinkedList;
import java.util.Optional;

public class ConnectionCheckerThread extends Thread {

    public ConnectionCheckerThread() {
        this.start();
    }

    @Override
    public void run() {
        try {
            do {
                LinkedList<PreLoginEvent> toProcess = new LinkedList<>(Events.requests);

                for (PreLoginEvent event : toProcess) {

                    InboundConnection inboundConnection = event.getConnection();
                    String from = inboundConnection.getVirtualHost().toString().toLowerCase();
                    String playerName = event.getUsername(),
                            expect = from.contains("premium.herospvp.net") ? "premium" : "cracked";

                    boolean isPremium = Main.getHikari().isPremium(playerName);

                    // TODO: testare l'event.setResult -> se non funziona utilizzare player.disconnect!!!
                    if (isPremium && expect.equals("cracked")) {
                        syncDisconnect(playerName, "mc.herospvp.net");
                        continue;
                    } else if (!isPremium && expect.equals("premium")) {
                        syncDisconnect(playerName, "premium.herospvp.net");
                        continue;
                    }

                    try {
                        Jedis jedis = Main.getRedis().getPool().getResource();
                        jedis.auth(Main.getRedis().getPassword());
                        jedis.set(playerName, expect);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                synchronized (currentThread()) {
                    Events.requests.removeAll(toProcess);
                }

                Thread.sleep(1000);
            } while (running);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncDisconnect(String playerName, String ip) {
        Optional<Player> player = Main.getServer().getPlayer(playerName);
        synchronized (currentThread()) {
            player.ifPresent(value -> value.disconnect(Component.text("Mi spiace, sei costretto ad entrare da: " + ip)));
        }
    }

    public static boolean running = true;

}

package net.herospvp.premiumvelocity.utils;

import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.databases.Storage;
import net.herospvp.premiumvelocity.threadbakery.Oven;
import redis.clients.jedis.Jedis;

public class Splitter {

    public static void initialHub(ServerPreConnectEvent event) {
        int playersHub1 = Storage.getHub1().getPlayersConnected().size();
        int playersHub2 = Storage.getHub2().getPlayersConnected().size();

        if (playersHub1 > playersHub2) {
            ServerPreConnectEvent.ServerResult result = ServerPreConnectEvent.ServerResult.allowed(Storage.getHub2());
            event.setResult(result);
        }

    }

    public static void sendHub(Player player) {
        int playersHub1 = Storage.getHub1().getPlayersConnected().size();
        int playersHub2 = Storage.getHub2().getPlayersConnected().size();

        if (playersHub1 > playersHub2) {
            player.createConnectionRequest(Storage.getHub2()).connect();
        } else {
            player.createConnectionRequest(Storage.getHub1()).connect();
        }

        Oven.runSingleThreaded(() -> {
            try (Jedis jedis = Main.getRedis().getPool().getResource()) {
                jedis.auth(Main.getRedis().getPassword());
                jedis.set(player.getUsername(), "hub");
            }
        });
    }

}

package net.herospvp.premiumvelocity.monitor;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.herospvp.premiumvelocity.utils.Splitter;

import java.util.TreeSet;

public class SplitterEvents {

    @Subscribe(order = PostOrder.LAST)
    public void on(ServerPreConnectEvent event) {

        if (!event.getOriginalServer().getServerInfo().getName().contains("hub")) {
            return;
        }

        Splitter.initialHub(event);
    }

}

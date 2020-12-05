package net.herospvp.premiumvelocity.monitor;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;

import java.util.LinkedList;

public class Events {

    public static final LinkedList<PreLoginEvent> requests = new LinkedList<>();

    @Subscribe(order = PostOrder.LAST)
    public void on(PreLoginEvent event) {
        requests.add(event);
    }

}

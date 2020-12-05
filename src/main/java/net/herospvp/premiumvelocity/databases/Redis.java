package net.herospvp.premiumvelocity.databases;

import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.JedisPool;

import javax.security.auth.login.Configuration;
import java.util.TreeSet;

public class Redis {

    @Getter
    private final TreeSet<String> playerPremium = new TreeSet<>(), playerCracked = new TreeSet<>();
    @Getter
    private final String password;
    @Getter
    private final JedisPool pool;

    public Redis(String password, JedisPool pool) {
        this.password = password;
        this.pool = pool;
    }

}

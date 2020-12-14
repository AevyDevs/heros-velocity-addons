package net.herospvp.premiumvelocity.databases;

import lombok.Getter;
import redis.clients.jedis.JedisPool;

public class Redis {

    @Getter
    private final String password;
    @Getter
    private final JedisPool pool;

    public Redis(String password, JedisPool pool) {
        this.password = password;
        this.pool = pool;
    }

}

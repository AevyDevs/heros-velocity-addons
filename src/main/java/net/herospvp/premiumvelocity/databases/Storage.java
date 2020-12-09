package net.herospvp.premiumvelocity.databases;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.threadbakery.Oven;
import redis.clients.jedis.JedisPool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Storage {

    @Getter
    private static final Map<String, Boolean> databaseData = new HashMap<>();
    @Getter
    private static final TreeSet<String> authenticatedPlayers = new TreeSet<>();

    public static void addAuthPlayer(Player player) {
        authenticatedPlayers.add(player.getUsername());
    }

    public static void removeAuthPlayer(Player player) {
        authenticatedPlayers.remove(player.getUsername());
    }

    public static boolean containsAuthPlayer(Player player) {
        return authenticatedPlayers.contains(player.getUsername());
    }

    public static void loadData() throws Exception {

        String configPath = "./plugins/HerosPremium/config.json";
        File file = new File(configPath);

        if (!file.exists()) {
            file.createNewFile();
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(configPath));
            JsonObject redis = new JsonObject(), mysql = new JsonObject();

            redis.put("ip", "localhost");
            redis.put("password", "password");

            mysql.put("ip", "localhost");
            mysql.put("port", "3307");
            mysql.put("database", "database");
            mysql.put("table", "table");
            mysql.put("user", "user");
            mysql.put("password", "password");

            JsonObject mainObject = new JsonObject();
            mainObject.put("redis", redis);
            mainObject.put("mysql", mysql);

            Jsoner.serialize(mainObject, writer);
            writer.close();
        }

        Reader reader = Files.newBufferedReader(Paths.get(configPath));
        JsonObject parser = (JsonObject) Jsoner.deserialize(reader);

        // redis
        JsonObject redisPath = (JsonObject) parser.get("redis");
        String redisIP = (String) redisPath.get("ip"),
                redisPassword = (String) redisPath.get("password");

        // mysql
        JsonObject mysqlPath = (JsonObject) parser.get("mysql");
        String mysqlIP = (String) mysqlPath.get("ip"),
                mysqlPassword = (String) mysqlPath.get("password"),
                user = (String) mysqlPath.get("user"),
                database = (String) mysqlPath.get("database"),
                table = (String) mysqlPath.get("table"),
                port = (String) mysqlPath.get("port");

        Main.setRedis(new Redis(redisPassword, new JedisPool(redisIP)));
        Main.setHikari(new Hikari(mysqlIP, port, database, table, user, mysqlPassword));

        reader.close();
    }

}

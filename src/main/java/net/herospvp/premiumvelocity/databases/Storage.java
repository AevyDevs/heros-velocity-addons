package net.herospvp.premiumvelocity.databases;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.databases.Hikari;
import net.herospvp.premiumvelocity.databases.Redis;
import redis.clients.jedis.JedisPool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Storage {

    public static void loadData() throws Exception {

        String configPath = "./plugins/HerosPremium/config.json";
        File file = new File(configPath);

        if (!file.exists()) {
            file.createNewFile();
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(configPath));
            JsonObject redis = new JsonObject(), mysql = new JsonObject();

            redis.put("ip", "localhost");
            redis.put("port", "6379");
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
        String ip = (String) redisPath.get("ip"), port = (String) redisPath.get("port"),
                password = (String) redisPath.get("password");

        Redis redis = new Redis(password, new JedisPool(ip + ":" + port));
        Main.setRedis(redis);

        // mysql
        JsonObject mysqlPath = (JsonObject) parser.get("mysql");
        ip = (String) mysqlPath.get("ip");
        port = (String) mysqlPath.get("port");
        password = (String) mysqlPath.get("password");
        String user = (String) mysqlPath.get("user"), database = (String) mysqlPath.get("database"),
                table = (String) mysqlPath.get("table");

        Hikari hikari = new Hikari(ip, port, database, table, user, password);
        Main.setHikari(hikari);

        reader.close();
    }

}

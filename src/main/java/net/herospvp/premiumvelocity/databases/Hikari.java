package net.herospvp.premiumvelocity.databases;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Hikari {

    @Getter
    private final DataSource dataSource;
    private final String table;

    public Hikari(String ip, String port, String database,
                  String table, String user, String password) throws Exception {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + database + "?useSSL=false&characterEncoding=utf8");
        config.setUsername(user);
        if (password != null) {
            config.setPassword(password);
        }
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        dataSource = new HikariDataSource(config);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement
                    ("CREATE TABLE IF NOT EXISTS " + table + " (name varchar(16), premium boolean)");
            preparedStatement.executeUpdate();
        } finally {
            close(connection, preparedStatement, null);
        }

        this.table = table;
    }

    public void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        if (connection != null) try { connection.close(); } catch (Exception ignored) {}
        if (preparedStatement != null) try { preparedStatement.close(); } catch (Exception ignored) {}
        if (resultSet != null) try { resultSet.close(); } catch (Exception ignored) {}
    }

    public boolean isPremium(String playerName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean itIs = false;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT PREMIUM FROM " + table + " WHERE NAME = ?;");
            preparedStatement.setString(1, playerName);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                itIs = resultSet.getBoolean(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection, preparedStatement, resultSet);
        }
        return itIs;
    }

    public void setConnection(String playerName, boolean maybeSetPremium) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO " + table +
                    " (name, premium) VALUES (\"" + playerName + "\", \"" + (maybeSetPremium ? 1 : 0) + "\");");
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection, preparedStatement, null);
        }
    }

}

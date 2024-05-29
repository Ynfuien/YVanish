package pl.ynfuien.yvanish.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqliteDatabase extends Database {
    @Override
    public boolean setup(ConfigurationSection config) {
        close();

        dbName = config.getString("path");

        HikariConfig dbConfig = new HikariConfig();
        dbConfig.setJdbcUrl(String.format("jdbc:sqlite:%s/%s", YVanish.getInstance().getDataFolder().getPath(), config.getString("path")));


        try {
            dbSource = new HikariDataSource(dbConfig);
        } catch (Exception e) {
            YLogger.error("Plugin couldn't connect to a database! Please check connection data, because some plugin's functionality requires the database!");
            return false;
        }

        usersTableName = config.getString("table");
        return createUsersTable();
    }

    @Override
    public boolean createUsersTable() {
        String query = String.format("CREATE TABLE IF NOT EXISTS `%s` (uuid TEXT NOT NULL, silent_chests BYTE DEFAULT -1, silent_sculk BYTE DEFAULT -1, silent_messages BYTE DEFAULT -1, no_pickup BYTE DEFAULT -1, no_mobs BYTE DEFAULT -1, action_bar BYTE DEFAULT -1, boss_bar BYTE DEFAULT -1, UNIQUE (uuid))", usersTableName);

        try (Connection conn = dbSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.execute();
        } catch (SQLException e) {
            YLogger.error(String.format("Couldn't create table '%s' in database '%s'", usersTableName, dbName));
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

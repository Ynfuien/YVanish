package pl.ynfuien.yvanish.data;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import pl.ynfuien.ydevlib.messages.YLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public abstract class Database {
    protected HikariDataSource dbSource;
    protected String dbName;
    protected String usersTableName = "yv_users";


    public abstract boolean setup(ConfigurationSection config);

    public void close() {
        if (dbSource != null) dbSource.close();
    }

    public boolean userExists(UUID uuid) {
        String query = String.format("SELECT silent_chests FROM `%s` WHERE uuid=? LIMIT 1", usersTableName);

        try (Connection conn = dbSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            YLogger.warn(String.format("Couldn't retrieve data from table '%s'.", usersTableName));
            e.printStackTrace();
            return false;
        }
    }

    public User getUser(UUID uuid) {
        String query = String.format("SELECT silent_chests, silent_sculk, silent_messages, no_pickup, no_mobs, action_bar, boss_bar FROM `%s` WHERE uuid=? LIMIT 1", usersTableName);

        try (Connection conn = dbSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) return new User(
                    resultSet.getByte("silent_chests"),
                    resultSet.getByte("silent_sculk"),
                    resultSet.getByte("silent_messages"),
                    resultSet.getByte("no_pickup"),
                    resultSet.getByte("no_mobs"),
                    resultSet.getByte("action_bar"),
                    resultSet.getByte("boss_bar"));
            return null;
        } catch (SQLException e) {
            YLogger.warn(String.format("Couldn't retrieve data from table '%s'.", usersTableName));
            e.printStackTrace();
            return null;
        }
    }

    public boolean setUser(UUID uuid, User user) {
        String query = String.format("UPDATE `%s` SET silent_chests=?, silent_sculk=?, silent_messages=?, no_pickup=?, no_mobs=?, action_bar=?, boss_bar=? WHERE uuid=?", usersTableName);

        if (!userExists(uuid)) {
            query = String.format("INSERT INTO `%s`(silent_chests, silent_sculk, silent_messages, no_pickup, no_mobs, action_bar, boss_bar, uuid) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", usersTableName);
        }

        try (Connection conn = dbSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setByte(1, boolToSqlValue(user.silentChests));
            stmt.setByte(2, boolToSqlValue(user.silentSculk));
            stmt.setByte(3, boolToSqlValue(user.silentMessages));
            stmt.setByte(4, boolToSqlValue(user.noPickup));
            stmt.setByte(5, boolToSqlValue(user.noMobs));
            stmt.setByte(6, boolToSqlValue(user.actionBar));
            stmt.setByte(7, boolToSqlValue(user.bossBar));

            stmt.setString(8, uuid.toString());

            stmt.execute();

        } catch (SQLException e) {
            YLogger.warn(String.format("Couldn't save data to table '%s'.", usersTableName));
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private byte boolToSqlValue(Boolean value) {
        if (value == null) return -1;
        if (value) return 1;
        return 0;
    }


    public abstract boolean createUsersTable();

    public boolean isSetup() {
        return dbSource != null;
    }
}

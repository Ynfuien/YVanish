package pl.ynfuien.yvanish.data;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.yvanish.YVanish;

import java.util.HashMap;
import java.util.UUID;

public class Storage {
    private static YVanish instance;
    private static Database database;
    private static HashMap<UUID, User> users = new HashMap<>();

    public static void setup(YVanish instance) {
        Storage.instance = instance;
        Storage.database = instance.getDatabase();
    }

    public static User getUser(@NotNull UUID uuid) {
        if (users.containsKey(uuid)) return users.get(uuid);

        User user = database.getUser(uuid);
        if (user == null) user = new User();

        if (Bukkit.getPlayer(uuid) != null) users.put(uuid, user);

        return user;
    }

    public static void updateUser(@NotNull UUID uuid) {
        updateUser(uuid, getUser(uuid));
    }
    public static void updateUser(@NotNull UUID uuid, User user) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            database.setUser(uuid, user);
        });
    }

    public static void removeUserFromCache(@NotNull UUID uuid) {
        users.remove(uuid);
    }

    public static HashMap<UUID, User> getUsers() {
        return users;
    }

    public static Database getDatabase() {
        return database;
    }
}
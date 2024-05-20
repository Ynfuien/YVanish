package pl.ynfuien.yvanish.api;

import com.google.common.base.Preconditions;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;

import java.util.UUID;

public class YVanishAPI {

    /**
     * Gets player's user object. It's used for changing vanish options.
     */
    @NotNull
    public static User getUser(@NotNull Player player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");

        return Storage.getUser(player.getUniqueId());
    }

    /**
     * Gets player's user object. It's used for changing vanish options.
     */
    @NotNull
    public static User getUser(@NotNull OfflinePlayer player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");

        return Storage.getUser(player.getUniqueId());
    }

    /**
     * Gets user object by uuid. It's used for changing player vanish options.
     */
    @NotNull
    public static User getUser(@NotNull UUID uuid) {
        Preconditions.checkArgument(uuid != null, "Uuid cannot be null");

        return Storage.getUser(uuid);
    }

    /**
     * Gets plugin's vanish manager. Use it to check and change players vanish status.
     */
    @NotNull
    public static VanishManager getVanishManager() {
        return YVanish.getInstance().getVanishManager();
    }
}

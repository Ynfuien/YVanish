package pl.ynfuien.yvanish.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.config.PluginConfig;
import pl.ynfuien.yvanish.hooks.essentials.EssentialsHook;

import java.util.HashSet;
import java.util.Set;

/**
 * General class for managing vanished players.
 */
public class VanishManager {
    private final YVanish instance;
    private final String seePermission;
    private final Set<Player> vanishedPlayers = new HashSet<>();

    public VanishManager(YVanish instance) {
        this.instance = instance;
        this.seePermission = YVanish.Permissions.VANISH_SEE.get();
    }

    /**
     * Vanishes a player.
     * @return False if player was already vanished
     */
    public boolean vanish(Player player) {
        if (vanishedPlayers.contains(player)) return false;

        if (PluginConfig.ignoreSleep) player.setSleepingIgnored(true);
        vanishedPlayers.add(player);
        refresh();

        EssentialsHook.vanishPlayer(player);
        return true;
    }

    /**
     * Unvanishes a player.
     * @return False if player wasn't vanished
     */
    public boolean unvanish(Player player) {
        if (!vanishedPlayers.contains(player)) return false;

        if (PluginConfig.ignoreSleep) player.setSleepingIgnored(false);
        vanishedPlayers.remove(player);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(instance, player);
        }
        refresh();

        EssentialsHook.unvanishPlayer(player);
        return true;
    }

    /**
     * Refreshes what players are visible to whom.
     */
    public void refresh() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission(seePermission)) {
                showVanishedToPlayer(p);
                continue;
            }

            hideVanishedFromPlayer(p);
        }
    }

    private void hideVanishedFromPlayer(Player p) {
        for (Player hidden : vanishedPlayers) {
            p.hidePlayer(instance, hidden);
        }
    }
    private void showVanishedToPlayer(Player p) {
        for (Player hidden : vanishedPlayers) {
            p.showPlayer(instance, hidden);
        }
    }

    /**
     * Checks whether provided player is vanished.
     */
    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player);
    }

    /**
     * Checks whether no one is currently vanished.
     */
    public boolean isNoOneVanished() {
        return vanishedPlayers.isEmpty();
    }

    /**
     * Gets set of all vanished players.
     */
    public Set<Player> getVanishedPlayers() {
        return vanishedPlayers;
    }


    /**
     * Removes a player from the cache.
     * Used only by the plugin when a player leaves the game.
     */
    public void removeFromCache(Player player) {
        vanishedPlayers.remove(player);
    }
}

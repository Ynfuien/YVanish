package pl.ynfuien.yvanish.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.config.PluginConfig;
import pl.ynfuien.yvanish.core.ActionAndBossBars;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;
//    private final static Set<Player> vanishedOnJoin = new HashSet<>();
    private final static Set<Player> freshlyJoined = new HashSet<>();

    public PlayerJoinListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Hide on join
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        UUID uuid = p.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            Storage.getUser(uuid);
        });

//        freshlyJoined.add(p);
//        Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> {
//            freshlyJoined.remove(p);
//        }, 10);

        if (!PluginConfig.onJoinEnabled) return;
//        if (!PluginConfig.onJoinSilent) return;

//        if (!p.hasPermission(YVanish.Permissions.VANISH_ON_JOIN.get())) return;

        if (!p.hasPermission(YVanish.Permissions.VANISH_ON_JOIN.get())) {
//            YLogger.warn("Before refresh: " + System.currentTimeMillis());
            freshlyJoined.add(p);
            vanishManager.refresh();
            freshlyJoined.remove(p);
//            YLogger.warn("After refresh: " + System.currentTimeMillis());
            return;
        }

        if (PluginConfig.onJoinSilent) event.joinMessage(null);
//        event.joinMessage(null);

//        vanishedOnJoin.add(p);
//        vanishManager.vanish(p);

        Lang.Message.VANISH_ON_JOIN.send(p);
        ActionAndBossBars.updateForPlayer(p);

//        Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> {
//            vanishedOnJoin.remove(p);
//        }, 10);
    }

//    public static Set<Player> getVanishedOnJoin() {
//        return vanishedOnJoin;
//    }

    public static Set<Player> getFreshlyJoined() {
        return freshlyJoined;
    }
}

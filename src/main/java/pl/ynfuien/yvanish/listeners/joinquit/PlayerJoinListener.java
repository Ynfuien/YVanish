package pl.ynfuien.yvanish.listeners.joinquit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
    private final static Set<UUID> freshlyJoined = new HashSet<>();

    public PlayerJoinListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Hide on join
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        UUID uuid = p.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            Storage.getUser(uuid);
        });


        if (!PluginConfig.onJoinEnabled) return;

        Bukkit.getGlobalRegionScheduler().runDelayed(instance, (task) -> {
            freshlyJoined.remove(uuid);
        }, 2);

        if (!p.hasPermission(YVanish.Permissions.VANISH_ON_JOIN.get())) {
            vanishManager.refresh();
            return;
        }

        if (PluginConfig.onJoinSilent) event.joinMessage(null);

        vanishManager.vanish(p);
        Lang.Message.VANISH_ON_JOIN.send(p);
        ActionAndBossBars.updateForPlayer(p);
    }

    public static Set<UUID> getFreshlyJoined() {
        return freshlyJoined;
    }
}

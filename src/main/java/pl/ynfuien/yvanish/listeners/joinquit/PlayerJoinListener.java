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


        if (!PluginConfig.onJoinEnabled) return;

        if (!p.hasPermission(YVanish.Permissions.VANISH_ON_JOIN.get())) {
            freshlyJoined.add(p);
            vanishManager.refresh();
            freshlyJoined.remove(p);
            return;
        }

        if (PluginConfig.onJoinSilent) event.joinMessage(null);

        Lang.Message.VANISH_ON_JOIN.send(p);
        ActionAndBossBars.updateForPlayer(p);
    }

    public static Set<Player> getFreshlyJoined() {
        return freshlyJoined;
    }
}

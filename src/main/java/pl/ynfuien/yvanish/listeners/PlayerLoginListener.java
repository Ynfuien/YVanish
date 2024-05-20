package pl.ynfuien.yvanish.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.config.PluginConfig;
import pl.ynfuien.yvanish.core.VanishManager;

public class PlayerLoginListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayerLoginListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Hide on join
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (!PluginConfig.onJoinEnabled) return;
        if (!event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) return;

        Player p = event.getPlayer();
        if (!p.hasPermission(YVanish.Permissions.VANISH_ON_JOIN.get())) return;
//        if (!p.hasPermission(YVanish.Permissions.VANISH_ON_JOIN.get())) {
//            vanishManager.refresh();
//            return;
//        }
//        Files.asByteSource(null).hash(Hashing.sha512())
        YLogger.debug("Before vanish: " + System.currentTimeMillis());
        vanishManager.vanish(p);
        YLogger.debug("After vanish: " + System.currentTimeMillis());
//
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            player.hidePlayer(instance, p);
//        }
//        Lang.Message.VANISH_ON_JOIN.send(p);
//        ActionAndBossBars.updateForPlayer(p);
    }
}

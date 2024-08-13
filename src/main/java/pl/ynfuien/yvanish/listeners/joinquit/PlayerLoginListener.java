package pl.ynfuien.yvanish.listeners.joinquit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
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
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (!PluginConfig.onJoinEnabled) return;
        if (!event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) return;

        Player p = event.getPlayer();
        if (!p.hasPermission(YVanish.Permissions.VANISH_ON_JOIN.get())) return;

        vanishManager.vanish(p);
    }
}

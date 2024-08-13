package pl.ynfuien.yvanish.listeners.joinquit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.config.PluginConfig;
import pl.ynfuien.yvanish.core.ActionAndBossBars;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;

import java.util.UUID;

public class PlayerQuitListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayerQuitListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Remove player from cache and hide quit message
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        UUID uuid = p.getUniqueId();
        Storage.removeUserFromCache(uuid);

        if (!vanishManager.isVanished(p)) return;

        if (PluginConfig.silentQuit) event.quitMessage(null);
        vanishManager.removeFromCache(p);
        ActionAndBossBars.hideBossBar(p);
    }
}

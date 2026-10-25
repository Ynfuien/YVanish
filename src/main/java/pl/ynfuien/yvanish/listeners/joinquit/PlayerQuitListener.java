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
import pl.ynfuien.yvanish.data.User;

import java.util.UUID;

public class PlayerQuitListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayerQuitListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Save vanish state, remove player from cache and hide quit message
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        UUID uuid = p.getUniqueId();

        boolean wasVanished = vanishManager.isVanished(p);

        // Remember the state for the next join, only when it changed
        User user = Storage.getUser(uuid);
        if (user.getVanished() != wasVanished) {
            user.setVanished(wasVanished);
            Storage.updateUser(uuid, user);
        }
        Storage.removeUserFromCache(uuid);

        if (!wasVanished) return;
        p.removeMetadata("vanished", instance);

        if (PluginConfig.silentQuit) event.quitMessage(null);
        vanishManager.removeFromCache(p);
        ActionAndBossBars.hideBossBar(p);
    }
}

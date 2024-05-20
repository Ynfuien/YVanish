package pl.ynfuien.yvanish.listeners.silentsculk;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockReceiveGameEvent;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;

public class BlockReceiveGameListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public BlockReceiveGameListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Prevent sculk sensor activation - general
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSculkSensorActivate(BlockReceiveGameEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;

        if (!vanishManager.isVanished(p)) return;

        User user = Storage.getUser(p.getUniqueId());
        if (!user.getSilentSculk()) return;

        event.setCancelled(true);
    }
}

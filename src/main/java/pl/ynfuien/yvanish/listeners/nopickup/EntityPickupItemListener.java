package pl.ynfuien.yvanish.listeners.nopickup;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;

public class EntityPickupItemListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public EntityPickupItemListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Prevent item pickup
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;

        if (!vanishManager.isVanished(p)) return;

        User user = Storage.getUser(p.getUniqueId());
        if (!user.getNoPickup()) return;

        event.setCancelled(true);
    }
}

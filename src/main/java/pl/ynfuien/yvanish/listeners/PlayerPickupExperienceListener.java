package pl.ynfuien.yvanish.listeners;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;

public class PlayerPickupExperienceListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayerPickupExperienceListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Prevent exp pickup
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerPickupExperience(PlayerPickupExperienceEvent event) {
        Player p = event.getPlayer();

        if (!vanishManager.isVanished(p)) return;

        User user = Storage.getUser(p.getUniqueId());
        if (!user.getNoPickup()) return;

        event.setCancelled(true);
    }
}

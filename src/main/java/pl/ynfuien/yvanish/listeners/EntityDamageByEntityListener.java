package pl.ynfuien.yvanish.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;

public class EntityDamageByEntityListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public EntityDamageByEntityListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Prevent PVP
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player p)) return;

        if (!vanishManager.isVanished(p)) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (p.hasPermission(YVanish.Permissions.VANISH_PVP.get())) return;

        event.setCancelled(true);
    }
}

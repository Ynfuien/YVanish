package pl.ynfuien.yvanish.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;

public class EntityTargetLivingEntityListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public EntityTargetLivingEntityListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Prevent monsters targeting a player
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onMonsterTargetPlayer(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player p)) return;

        if (!vanishManager.isVanished(p)) return;

        User user = Storage.getUser(p.getUniqueId());
        if (!user.getNoMobs()) return;

        event.setCancelled(true);
    }
}

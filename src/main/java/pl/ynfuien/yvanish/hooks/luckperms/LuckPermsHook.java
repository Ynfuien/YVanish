package pl.ynfuien.yvanish.hooks.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;

// Checks for VANISH_SEE permission changes and refreshes vanished players visibility
public class LuckPermsHook {
    private final YVanish instance;
    private final VanishManager vanishManager;
    private BukkitTask schedulerTask = null;

    public LuckPermsHook(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();

        LuckPerms lp = LuckPermsProvider.get();
        EventBus eventBus = lp.getEventBus();

        eventBus.subscribe(instance, NodeAddEvent.class, e -> {
            if (vanishManager.isNoOneVanished()) return;
            if (!e.getNode().getKey().contains(YVanish.Permissions.VANISH_SEE.get())) return;

            if (schedulerTask != null) return;
            schedulerTask = Bukkit.getScheduler().runTaskLater(instance, () -> {
                vanishManager.refresh();
                schedulerTask = null;
            }, 0);
        });

        eventBus.subscribe(instance, NodeRemoveEvent.class, e -> {
            if (vanishManager.isNoOneVanished()) return;
            if (!e.getNode().getKey().contains(YVanish.Permissions.VANISH_SEE.get())) return;

            if (schedulerTask != null) return;
            schedulerTask = Bukkit.getScheduler().runTaskLater(instance, () -> {
                vanishManager.refresh();
                schedulerTask = null;
            }, 0);
        });
    }
}

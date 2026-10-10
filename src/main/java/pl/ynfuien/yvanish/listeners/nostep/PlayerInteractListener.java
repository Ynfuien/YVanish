package pl.ynfuien.yvanish.listeners.nostep;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;

public class PlayerInteractListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayerInteractListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Prevent pressure plate and tripwire activation and farmland trampling
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPhysicalAction(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        if (!event.getAction().equals(Action.PHYSICAL)) return;

        Block b = event.getClickedBlock();
        if (b == null) return;

        Material type = b.getType();
        if (!type.equals(Material.TRIPWIRE) &&
                !type.equals(Material.FARMLAND) &&
                !type.equals(Material.BIG_DRIPLEAF) &&
                !Tag.PRESSURE_PLATES.isTagged(type)) return;

        Player p = event.getPlayer();

        if (!vanishManager.isVanished(p)) return;

        User user = Storage.getUser(p.getUniqueId());
        if (!user.getNoStep()) return;

        event.setCancelled(true);
    }
}

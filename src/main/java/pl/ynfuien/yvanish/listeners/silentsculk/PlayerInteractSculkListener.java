package pl.ynfuien.yvanish.listeners.silentsculk;

import org.bukkit.Material;
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

public class PlayerInteractSculkListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayerInteractSculkListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Prevent sculk sensor activation - when stepped
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSculkSensorTouch(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) return;
        if (!event.hasBlock()) return;

        Player p = event.getPlayer();
        if (!vanishManager.isVanished(p)) return;


        Block block = event.getClickedBlock();
        Material type = block.getType();

        if (!type.equals(Material.SCULK_SENSOR) &&
            !type.equals(Material.CALIBRATED_SCULK_SENSOR) &&
            !type.equals(Material.SCULK_SHRIEKER)) return;


        User user = Storage.getUser(p.getUniqueId());
        if (!user.getSilentSculk()) return;

        event.setCancelled(true);
    }
}

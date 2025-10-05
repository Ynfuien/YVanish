package pl.ynfuien.yvanish.listeners.silentchests;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.ChestableUtils;
import pl.ynfuien.yvanish.core.ChestableViewers;
import pl.ynfuien.yvanish.core.FakeOpenClose;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.packetevents.PacketEventsHook;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventoryCloseListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public InventoryCloseListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Part of the hidden chest animations logic
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChestableClose(InventoryCloseEvent event) {
        if (!PacketEventsHook.isEnabled()) return;

        Player p = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        InventoryType inventoryType = inventory.getType();
        if (!ChestableUtils.isInventoryTypeChestable(inventoryType)) return;

        Location location = inventory.getLocation();
        if (location == null) return;

        Block block = location.getBlock();

        List<Player> playersSeeingBlockChange = FakeOpenClose.getNearPlayersThatCanSeeBlockChange(block);

        ChestableViewers.removeViewer(block, p);

        Set<Player> viewers = new HashSet<>(ChestableViewers.getBlockViewers(block));
        viewers.remove(p);
        if (viewers.isEmpty()) return;

        for (Player player : playersSeeingBlockChange) {
            if (player.hasPermission(YVanish.Permissions.VANISH_SEE.get())) continue;
            if (PacketEventsHook.canSeeBlockChange(viewers, player)) continue;

            FakeOpenClose.fakeClose(player, block);
        }
    }
}

package pl.ynfuien.yvanish.listeners.silentchests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.ChestableUtils;
import pl.ynfuien.yvanish.core.ChestableViewers;
import pl.ynfuien.yvanish.core.FakeOpenClose;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.hooks.packetevents.PacketEventsHook;

import java.util.HashMap;
import java.util.List;

public class InventoryCloseListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;
    private final static HashMap<Player, BukkitTask> removeViewerTasks = new HashMap<>();

    public InventoryCloseListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Part of the hidden chest animations logic
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChestableClose(InventoryCloseEvent event) {
        if (!PacketEventsHook.isEnabled()) return;

        YLogger.debug("===== RealCloseInv =====");

        Player p = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        InventoryType inventoryType = inventory.getType();
        if (!ChestableUtils.isInventoryTypeChestable(inventoryType)) return;

        Location location = inventory.getLocation();
        if (location == null) {
            if (inventoryType.equals(InventoryType.ENDER_CHEST)) {
                YLogger.debug("<red>Ender chest null location!");
            }
            return;
        }

        Block block = ChestableUtils.getDoubleChestBlock(location.getBlock());
        YLogger.debug("Location: " + location);

        List<Player> playersSeeingBlockChange = FakeOpenClose.getNearPlayersThatCanSeeBlockChange(block);
        YLogger.debug("playersSeeingBlockChange: " + playersSeeingBlockChange.size());
        removeViewer(p, block);
        playersSeeingBlockChange.removeAll(FakeOpenClose.getNearPlayersThatCanSeeBlockChange(block));


        if (p.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;


        List<HumanEntity> viewers = ChestableViewers.getBlockViewers(block);
        viewers.remove(p);
        if (viewers.isEmpty()) return;

        for (int i = 0; i < viewers.size(); i++) {
            HumanEntity viewer = viewers.get(i);
            Player player = (Player) viewer;

            if (!vanishManager.isVanished(player)) continue;
            if (!Storage.getUser(player.getUniqueId()).getSilentChests()) continue;

            viewers.remove(i);
            i--;
        }

        if (!viewers.isEmpty()) return;


        YLogger.debug("====== AfterCloseEvent =====");
        if (inventoryType.equals(InventoryType.BARREL)) {

            Bukkit.getScheduler().runTask(instance, () -> {
                sendFakeClose(playersSeeingBlockChange, block);

                YLogger.debug("<red>After close BARREL!");
            });

            return;
        }


        YLogger.debug("<green>Before close " + inventoryType.name() + "!");

        Bukkit.getScheduler().runTask(instance, () -> {
            sendFakeClose(playersSeeingBlockChange, block);

            YLogger.debug("<red>After close " + inventoryType.name() + "!");
        });
    }

    private void sendFakeClose(List<Player> players, Block block) {
        for (Player p : players) FakeOpenClose.fakeClose(p, block);
    }

    private void removeViewer(Player player, Block block) {
        List<HumanEntity> blockViewers;
        if (block == null) {
            blockViewers = ChestableViewers.getBlockViewersOfPlayerCurrentBlock(player);
        } else {
            blockViewers = ChestableViewers.getBlockViewers(block);
        }

        if (blockViewers.size() > 1) {
            removeViewerNow(player, block);
            return;
        }

        removeViewerLater(player, block);
    }

    private void removeViewerNow(Player player, Block block) {
        if (block == null) {
            ChestableViewers.removeViewer(player);
            return;
        }

        Location loc = ChestableUtils.getDoubleChestBlock(block).getLocation();
        ChestableViewers.removeViewer(loc.getBlock(), player);
    }

    private void removeViewerLater(Player player, Block block) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(instance, () -> {
            removeViewerNow(player, block);

            removeViewerTasks.remove(player);
        }, 10);

        removeViewerTasks.put(player, bukkitTask);
    }


    public static void cancelRemoveViewerTask(Player player) {
        BukkitTask task = removeViewerTasks.get(player);
        if (task == null) return;

        task.cancel();
        removeViewerTasks.remove(player);
    }
}

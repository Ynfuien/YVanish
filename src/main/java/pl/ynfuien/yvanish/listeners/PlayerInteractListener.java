package pl.ynfuien.yvanish.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.ChestableUtils;
import pl.ynfuien.yvanish.core.ChestableViewers;
import pl.ynfuien.yvanish.core.FakeOpenClose;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.hooks.Hooks;
import pl.ynfuien.yvanish.hooks.protocollib.ProtocolLibHook;

import java.util.List;

public class PlayerInteractListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayerInteractListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Prevent sculk sensor activation
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

    // Hidden chest animations logic (at least part of it)
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onOpenChestable(PlayerInteractEvent event) {
        if (!Hooks.isProtocolLibHookEnabled()) return;

        if (!event.hasBlock()) return;

        Player p = event.getPlayer();
        if (event.isBlockInHand() && p.isSneaking()) return;

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Block block = event.getClickedBlock();
        Material type = block.getType();

        if (!ChestableUtils.isMaterialChestable(type)) return;
        if (!type.equals(Material.BARREL) && !ChestableUtils.isChestOpenable(block)) return;

        block = ChestableUtils.getDoubleChestBlock(block);


        List<Player> canSeeBefore = FakeOpenClose.getNearPlayersThatCanSeeBlockChange(block);
        ChestableViewers.addViewer(p, block);
        InventoryCloseListener.cancelRemoveViewerTask(p);

        System.out.println("===== PlayerInteract =====");
        YLogger.error("Added " + p.getName() + " as viewer!");

        if (ChestableViewers.getBlockViewers(block).size() == 1) return;

        List<Player> playersToPlaySound = FakeOpenClose.getNearPlayersThatCanSeeBlockChange(block);
        playersToPlaySound.removeAll(canSeeBefore);

        for (Player player : playersToPlaySound) {
            FakeOpenClose.playOpenSound(player, block);
        }
    }
}

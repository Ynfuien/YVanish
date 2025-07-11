package pl.ynfuien.yvanish.listeners.silentchests;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.ChestableUtils;
import pl.ynfuien.yvanish.core.ChestableViewers;
import pl.ynfuien.yvanish.core.FakeOpenClose;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.packetevents.PacketEventsHook;

import java.util.List;

public class PlayerInteractChestableListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayerInteractChestableListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Hidden chest animations logic (at least part of it)
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onOpenChestable(PlayerInteractEvent event) {
        if (!PacketEventsHook.isEnabled()) return;

        if (!event.hasBlock()) return;

        Player p = event.getPlayer();
        if (event.isBlockInHand() && p.isSneaking()) return;

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Block block = event.getClickedBlock();
        Material type = block.getType();

        if (!ChestableUtils.isMaterialChestable(type)) return;
        if (!type.equals(Material.BARREL) && !ChestableUtils.isChestOpenable(block)) return;


        List<Player> canSeeBefore = FakeOpenClose.getNearPlayersThatCanSeeBlockChange(block);
        ChestableViewers.addViewer(block, p);

        if (vanishManager.isNoOneVanished()) return;
        if (ChestableViewers.getBlockViewers(block).size() == 1) return;

        List<Player> playersToPlaySound = FakeOpenClose.getNearPlayersThatCanSeeBlockChange(block);
        playersToPlaySound.removeAll(canSeeBefore);

        for (Player player : playersToPlaySound) FakeOpenClose.fakeOpen(player, block);
    }
}

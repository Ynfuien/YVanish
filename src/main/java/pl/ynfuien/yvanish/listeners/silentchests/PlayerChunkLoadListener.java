package pl.ynfuien.yvanish.listeners.silentchests;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.ChestableViewers;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.packetevents.PacketEventsHook;

import java.util.List;

public class PlayerChunkLoadListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayerChunkLoadListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Part of the hidden chest animations logic - Barrel time!
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChunkLoad(PlayerChunkLoadEvent event) {
        if (!PacketEventsHook.isEnabled()) return;

        if (vanishManager.isNoOneVanished()) return;
        Player p = event.getPlayer();
        if (p.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;

        Chunk chunk = event.getChunk();

        List<Block> openedBarrels = ChestableViewers.getAllViewedBlocksOfType(Material.BARREL);
        for (Block block : openedBarrels) {
            if (!chunk.equals(block.getChunk())) continue;

            if (PacketEventsHook.canSeeBlockChange(p, block)) continue;


            Barrel barrel = (Barrel) block.getBlockData();
            barrel.setOpen(false);

            p.sendBlockChange(block.getLocation(), barrel);
        }
    }
}

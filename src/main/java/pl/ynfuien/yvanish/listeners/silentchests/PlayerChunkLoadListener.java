package pl.ynfuien.yvanish.listeners.silentchests;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.ChestableViewers;
import pl.ynfuien.yvanish.core.FakeOpenClose;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.packetevents.PacketEventsHook;

import java.util.HashSet;
import java.util.Set;


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

//        Set<Block> openedBarrels = ChestableViewers.getAllViewedBlocksOfType(Material.BARREL);
//        for (Block block : openedBarrels) {
//            if (!chunk.equals(block.getChunk())) continue;
//            if (PacketEventsHook.canSeeBlockChange(p, block)) continue;
//
//            FakeOpenClose.sendBarrelState(p, block, false);
//        }

        Set<Location> viewedLocations = ChestableViewers.getViewedLocations();
        Set<Block> sentCorrections = new HashSet<>();
        for (Location loc : viewedLocations) {
//            if (!loc.isChunkLoaded()) continue;
            if (!loc.getChunk().equals(chunk)) continue;

            Block block = loc.getBlock();
            if (sentCorrections.contains(block)) continue;
            if (!block.getType().equals(Material.BARREL)) continue;

            if (PacketEventsHook.canSeeBlockChange(p, block)) continue;

            FakeOpenClose.sendBarrelState(p, block, false);
            sentCorrections.add(block);
        }
    }
}

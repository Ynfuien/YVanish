package pl.ynfuien.yvanish.core;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.hooks.packetevents.listeners.PacketBlockActionListener;
import pl.ynfuien.yvanish.hooks.packetevents.listeners.PacketBlockChangeListener;
import pl.ynfuien.yvanish.hooks.packetevents.listeners.PacketSoundEffectListener;
import pl.ynfuien.yvanish.listeners.silentchests.InventoryCloseListener;
import pl.ynfuien.yvanish.listeners.silentchests.PlayerInteractChestableListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * <b>Description of this class;
 * {@link PlayerInteractChestableListener PlayerInteract} and {@link InventoryCloseListener InventoryClose} listeners;
 * {@link PacketBlockActionListener BlockAction}, {@link PacketBlockChangeListener BlockChange} and {@link PacketSoundEffectListener NamedSoundEffect} packet listeners.</b>
 * <br/><br/>
 * <p><b>TL;DR: Silent chest system.</b></p>
 * <ul>
 *     <li>PlayerInteract adds a viewer to the list in a HashMap.</li>
 *     <li>InventoryClose removes this viewer from the list of that block.</li>
 *     <li>It's saved with a key of the exact location of an interacted block.</li>
 *     <li>
 *         <p>The whole system works similar to the Bukkit's inventory.getViewers().
 *         And the reason behind its existence, is that Bukkit's system doesn't work
 *         for ender chests, which is understandable, since their inventory isn't
 *         bound to the block, but to the player. (I mean it works, but for the viewers
 *         of the same player's ender chest, and not the same ender chest block,
 *         which is what I need)</p>
 *         <p>Also Bukkit viewers are removed too fast (for my needs), since
 *         SoundEffect packets fire (speaking of closing sounds) when
 *         they are long gone (in millisecond scale), hence making the decision
 *         whether to cancel this packet impossible.</p>
 *     </li>
 *     <li>
 *         <p>BlockAction, Change and SoundEffect packets use this class and its list,
 *         to determine whether packet should go to the player or not, based on the players
 *         that are currently viewing checked block.</p>
 *         <p>For example:</p>
 *         <ol>
 *             <li>Vanished player (VP), standing near a normal player (NP), opens a chest.
 *                 PlayerInteract adds him as a viewer. Packets are send (by the server),
 *                 NamedSoundEffect and BlockAction, both to all players near this chest.
 *                 Based on the viewer list for this block, VP should receive these packets,
 *                 because he is on the list, and NP should not receive those, because
 *                 the list contains only vanished player(s), who can't be seen by a NP.</li>
 *             <li>There are 3 players, two normal (NP) and one vanished (VP). One NP opens
 *                 a chest, and everyone sees this (animation, sounds). Then VP opens the same
 *                 chest. Nothing changes for anyone, since the chest was already opened.
 *                 Now the NP closes the chest. Because of VP still looking into the chest,
 *                 normally nothing would happen, so the plugin, using InventoryClose event,
 *                 sends BlockAction packet and closing sound, to the two NPs, by its own.</li>
 *         </ol>
 *     </li>
 * </ul>
 */
public class ChestableViewers {
    private static final HashMap<Location, Set<Player>> blocksViewers = new HashMap<>();
    private static final HashMap<Pair<Player, Block>, ScheduledTask> removeSchedulers = new HashMap<>();

    public static Set<Player> getBlockViewers(Block block) {
        return getBlockViewers(block.getLocation());
    }

    public static Set<Player> getBlockViewers(Location blockLocation) {
        if (!blocksViewers.containsKey(blockLocation)) return Set.of();

        return blocksViewers.get(blockLocation);
    }

    public static Set<Block> getAllViewedBlocksOfType(Material type) {
        Set<Block> blocks = new HashSet<>();
        for (Location loc : blocksViewers.keySet()) {
            Block block = loc.getBlock();
            if (!block.getType().equals(type)) continue;

            blocks.add(block);
        }

        return blocks;
    }

    public static void addViewer(Block block, Player player) {
        block = ChestableUtils.getDoubleChestBlock(block);

        Iterator<Pair<Player, Block>> it = removeSchedulers.keySet().iterator();
        while (it.hasNext()) {
            Pair<Player, Block> scheduled = it.next();

            if (!scheduled.getLeft().equals(player)) continue;
            if (!scheduled.getRight().equals(block)) continue;

            removeSchedulers.get(scheduled).cancel();
            removeSchedulers.remove(scheduled);
            break;
        }

        for (Location location : ChestableUtils.getLocationsOfChestable(block)) {
            addViewer(location, player);
        }
    }

    private static void addViewer(Location location, Player player) {
        if (!blocksViewers.containsKey(location)) blocksViewers.put(location, new HashSet<>());
        Set<Player> blockViewers = blocksViewers.get(location);

        blockViewers.add(player);
    }

    public static void removeViewer(Block block, Player player) {
        if (getBlockViewers(block).size() > 1) {
            for (Location location : ChestableUtils.getLocationsOfChestable(block)) {
                removeViewer(location, player);
            }

            return;
        }

        Pair<Player, Block> key = Pair.of(player, ChestableUtils.getDoubleChestBlock(block));

        ScheduledTask task = Bukkit.getGlobalRegionScheduler().runDelayed(YVanish.getInstance(), (t) -> {
            for (Location location : ChestableUtils.getLocationsOfChestable(block)) {
                removeViewer(location, player);
            }

            removeSchedulers.remove(key);
        }, 10);

        removeSchedulers.put(key, task);
    }

    private static void removeViewer(Location location, Player player) {
        Set<Player> viewers = blocksViewers.get(location);
        if (viewers == null) return;

        viewers.remove(player);
        if (viewers.isEmpty()) blocksViewers.remove(location);
    }
}

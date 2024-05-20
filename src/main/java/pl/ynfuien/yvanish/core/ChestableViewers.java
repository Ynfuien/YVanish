package pl.ynfuien.yvanish.core;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.hooks.protocollib.listeners.PacketBlockActionListener;
import pl.ynfuien.yvanish.hooks.protocollib.listeners.PacketBlockChangeListener;
import pl.ynfuien.yvanish.hooks.protocollib.listeners.PacketNamedSoundEffectListener;
import pl.ynfuien.yvanish.listeners.InventoryCloseListener;
import pl.ynfuien.yvanish.listeners.PlayerInteractListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * <b>Description of this class;
 * {@link PlayerInteractListener PlayerInteract} and {@link InventoryCloseListener InventoryClose} listeners;
 * {@link PacketBlockActionListener BlockAction}, {@link PacketBlockChangeListener BlockChange} and {@link PacketNamedSoundEffectListener NamedSoundEffect} packet listeners.</b>
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
 *         NamedSoundEffect packets fire (speaking of closing sounds) when
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

    //#region Chestable blocks viewers
    private static final HashMap<Location, List<HumanEntity>> blocksViewers = new HashMap<>();

    public static List<HumanEntity> getBlockViewers(Block block) {
        Location loc = block.getLocation();
        if (!blocksViewers.containsKey(loc)) return new ArrayList<>();

        return new ArrayList<>(blocksViewers.get(loc));
    }

    public static List<HumanEntity> getBlockViewersOfPlayerCurrentBlock(Player player) {
        for (Location loc : blocksViewers.keySet()) {
            List<HumanEntity> blockViewers = blocksViewers.get(loc);

            if (blockViewers.contains(player)) return new ArrayList<>(blockViewers);
        }

        return new ArrayList<>();
    }

    public static List<Block> getAllViewedBlocksOfType(Material type) {
        List<Block> blocks = new ArrayList<>();
        for (Location loc : blocksViewers.keySet()) {
            Block block = loc.getBlock();
            if (!block.getType().equals(type)) continue;

            blocks.add(block);
        }

        return blocks;
    }

    public static boolean addViewer(Player player, Block block) {
        Location loc = block.getLocation();

        if (!blocksViewers.containsKey(loc)) blocksViewers.put(loc, new ArrayList<>());
        List<HumanEntity> blockViewers = blocksViewers.get(loc);

//        blockViewers.add(player);
//        return true;

        if (!blockViewers.contains(player)) {
            blockViewers.add(player);
            return true;
        }

        return false;
    }

    public static void removeViewer(Player player) {
        Set<Location> keySet = blocksViewers.keySet();
        for (Location loc : keySet) {
            List<HumanEntity> viewers = blocksViewers.get(loc);
            viewers.remove(player);

            if (viewers.isEmpty()) blocksViewers.remove(loc);
        }
    }

    public static void removeViewer(Block block, Player player) {
        Location loc = block.getLocation();
        List<HumanEntity> viewers = blocksViewers.get(loc);
        if (viewers == null) return;
        boolean removed = viewers.remove(player);
        YLogger.debug("Removed result: " + removed);

        if (viewers.isEmpty()) blocksViewers.remove(loc);
    }
    //#endregion
}

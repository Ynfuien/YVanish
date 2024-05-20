package pl.ynfuien.yvanish.core;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.DoubleChestInventory;
import pl.ynfuien.yvanish.hooks.protocollib.ProtocolLibHook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FakeOpenClose {
    // Distance in which BlockAction packets are send to the players.
    // Value got from a simple test on a 1.20.2 Purpur server.
    private static final int BLOCK_ACTION_DISTANCE = 65;


    public static List<Player> getNearPlayersThatCanSeeBlockChange(Block block) {
        List<Player> players = new ArrayList<>();

        Location loc = block.getLocation().toCenterLocation();
        for (Player p : loc.getNearbyPlayers(BLOCK_ACTION_DISTANCE)) {
            if (!ProtocolLibHook.canSeeBlockChange(p, block)) continue;

            players.add(p);
        }

        return players;
    }
    public static void fakeOpen(Player player, Block block) {
        playOpenSound(player, block);
        if (block.getType().equals(Material.BARREL)) {
            Barrel barrel = (Barrel) block.getBlockData();
            barrel.setOpen(false);
            player.sendBlockChange(block.getLocation(), barrel);

            return;
        }

        sendChestAction(player, block, 1);
    }

//    public static void fakeClose(Block block) {
//        Location loc = block.getLocation().toCenterLocation();
//        for (Player p : loc.getNearbyPlayers(BLOCK_ACTION_DISTANCE)) {
//            if (!ProtocolLibHook.canSeeBlockChange(p, block)) continue;
//
//            fakeClose(p, block);
//        }
//    }
    public static void fakeClose(Player player, Block block) {
        playCloseSound(player, block);
        if (block.getType().equals(Material.BARREL)) {
            Barrel barrel = (Barrel) block.getBlockData();
            barrel.setOpen(false);
            player.sendBlockChange(block.getLocation(), barrel);

            return;
        }

        sendChestAction(player, block, 0);

        // Send block action for the other side of a double chest
        if (!ChestableUtils.isBlockDoubleChest(block)) return;

        DoubleChestInventory doubleChest = ChestableUtils.getDoubleChestInventory(block);
        BlockInventoryHolder holder = (BlockInventoryHolder) doubleChest.getRightSide().getHolder();
        sendChestAction(player, holder.getBlock(), 0);
    }

    public static void sendChestAction(Player player, Block block, int parameter) {
        ProtocolManager protocol = ProtocolLibHook.getProtocolManager();
        PacketContainer packet = protocol.createPacket(PacketType.Play.Server.BLOCK_ACTION);

        // Action - for blocks with open animation (chest, ender chest, shulker),
        // it's always 1. Here I do 10, as an 'identifier', that this packet is sent
        // by this plugin, and not server itself. Then in PacketBlockActionListener,
        // I can check if it's 10, change it to 1, and let it be.
        packet.getIntegers().writeSafely(0, 10);
        // Action parameter - in this animation case, it's how many players are
        // looking in the chest. So when using this method, I do 1 for opening animation,
        // and 0 for closing.
        packet.getIntegers().writeSafely(1, parameter);
        packet.getBlocks().writeSafely(0, block.getType());
        BlockPosition position = new BlockPosition(block.getX(), block.getY(), block.getZ());
        packet.getBlockPositionModifier().writeSafely(0, position);

        protocol.sendServerPacket(player, packet);
    }

    private static final HashMap<Material, Sound> OPEN_SOUNDS = new HashMap<>() {{
        put(Material.CHEST, Sound.BLOCK_CHEST_OPEN);
        put(Material.TRAPPED_CHEST, Sound.BLOCK_CHEST_OPEN);
        put(Material.BARREL, Sound.BLOCK_BARREL_OPEN);
        put(Material.ENDER_CHEST, Sound.BLOCK_ENDER_CHEST_OPEN);
    }};
    public static void playOpenSound(Player player, Block block) {
        Material type = block.getType();
        if (!ChestableUtils.isMaterialChestable(type)) return;

        Sound sound = OPEN_SOUNDS.get(type);
        if (sound == null && type.name().contains(Material.SHULKER_BOX.name())) sound = Sound.BLOCK_SHULKER_BOX_OPEN;
        if (sound == null) return;

        playSound(player, block, sound);
    }

    private static final HashMap<Material, Sound> CLOSE_SOUNDS = new HashMap<>() {{
        put(Material.CHEST, Sound.BLOCK_CHEST_CLOSE);
        put(Material.TRAPPED_CHEST, Sound.BLOCK_CHEST_CLOSE);
        put(Material.BARREL, Sound.BLOCK_BARREL_CLOSE);
        put(Material.ENDER_CHEST, Sound.BLOCK_ENDER_CHEST_CLOSE);
    }};
    public static void playCloseSound(Player player, Block block) {
        Material type = block.getType();
        if (!ChestableUtils.isMaterialChestable(type)) return;

        Sound sound = CLOSE_SOUNDS.get(type);
        if (sound == null && type.name().contains(Material.SHULKER_BOX.name())) sound = Sound.BLOCK_SHULKER_BOX_CLOSE;
        if (sound == null) return;

        playSound(player, block, sound);
    }

    private static void playSound(Player player, Block block, Sound sound) {
        // Using volume of 10, to 'identify' this sound packet, as sent using this plugin.
        // So I can skip it in NamedSoundEffect packet listener.
        player.playSound(block.getLocation().toCenterLocation(), sound, SoundCategory.BLOCKS, 10, 1);
    }
}

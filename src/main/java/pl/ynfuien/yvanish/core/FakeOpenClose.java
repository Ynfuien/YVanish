package pl.ynfuien.yvanish.core;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.sound.Sound;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockAction;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.DoubleChestInventory;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.hooks.packetevents.PacketEventsHook;

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
            if (p.hasPermission(YVanish.Permissions.VANISH_SEE.get())) {
                players.add(p);
                continue;
            }

            if (PacketEventsHook.canSeeBlockChange(p, block)) players.add(p);
        }

        return players;
    }

    public static void fakeOpen(Player player, Block block) {
        playOpenSound(player, block);
        if (block.getType().equals(Material.BARREL)) {
            sendBarrelState(player, block, true);
            return;
        }

        sendChestAction(player, block, 1);
    }

    public static void fakeClose(Player player, Block block) {
        playCloseSound(player, block);
        if (block.getType().equals(Material.BARREL)) {
            sendBarrelState(player, block, false);
            return;
        }

        if (!ChestableUtils.isBlockDoubleChest(block)) {
            sendChestAction(player, block, 0);
            return;
        }

        DoubleChestInventory doubleChest = ChestableUtils.getDoubleChestInventory(block);
        BlockInventoryHolder left = (BlockInventoryHolder) doubleChest.getLeftSide().getHolder();
        BlockInventoryHolder right = (BlockInventoryHolder) doubleChest.getRightSide().getHolder();

        sendChestAction(player, left.getBlock(), 0);
        sendChestAction(player, right.getBlock(), 0);
    }

    public static void sendBarrelState(Player player, Block block, boolean open) {
        // Check for fake players
        if (player.getAddress() == null) return;

        if (!block.getType().equals(Material.BARREL)) return;

        Barrel barrel = (Barrel) block.getBlockData();
        barrel.setOpen(open);

        Vector3i position = new Vector3i(block.getX(), block.getY(), block.getZ());
        int blockId = SpigotConversionUtil.fromBukkitBlockData(barrel).getGlobalId();

        WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(position, blockId);
        PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, packet);

    }

    public static void sendChestAction(Player player, Block block, int parameter) {
        // Check for fake players
        if (player.getAddress() == null) return;

        Vector3i position = new Vector3i(block.getX(), block.getY(), block.getZ());

        ClientVersion version = PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();
        int blockTypeId = SpigotConversionUtil.fromBukkitBlockData(block.getBlockData()).getType().getMapped().getId(version);

        WrapperPlayServerBlockAction packet = new WrapperPlayServerBlockAction(position, 1, parameter, blockTypeId);
        PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, packet);
    }

    private static final HashMap<Material, Sound> OPEN_SOUNDS = new HashMap<>() {{
        put(Material.CHEST, Sounds.BLOCK_CHEST_OPEN);
        put(Material.TRAPPED_CHEST, Sounds.BLOCK_CHEST_OPEN);
        put(Material.BARREL, Sounds.BLOCK_BARREL_OPEN);
        put(Material.ENDER_CHEST, Sounds.BLOCK_ENDER_CHEST_OPEN);
    }};

    public static void playOpenSound(Player player, Block block) {
        Material type = block.getType();
        if (!ChestableUtils.isMaterialChestable(type)) return;

        Sound sound = OPEN_SOUNDS.get(type);
        if (sound == null && type.name().contains(Material.SHULKER_BOX.name())) sound = Sounds.BLOCK_SHULKER_BOX_OPEN;
        if (sound == null) return;

        playSound(player, block, sound);
    }

    private static final HashMap<Material, Sound> CLOSE_SOUNDS = new HashMap<>() {{
        put(Material.CHEST, Sounds.BLOCK_CHEST_CLOSE);
        put(Material.TRAPPED_CHEST, Sounds.BLOCK_CHEST_CLOSE);
        put(Material.BARREL, Sounds.BLOCK_BARREL_CLOSE);
        put(Material.ENDER_CHEST, Sounds.BLOCK_ENDER_CHEST_CLOSE);
    }};

    public static void playCloseSound(Player player, Block block) {
        Material type = block.getType();
        if (!ChestableUtils.isMaterialChestable(type)) return;

        Sound sound = CLOSE_SOUNDS.get(type);
        if (sound == null && type.name().contains(Material.SHULKER_BOX.name())) sound = Sounds.BLOCK_SHULKER_BOX_CLOSE;
        if (sound == null) return;

        playSound(player, block, sound);
    }

    private static void playSound(Player player, Block block, Sound sound) {
        // Check for fake players
        if (player.getAddress() == null) return;

        Location loc = block.getLocation().toCenterLocation();
        Vector3i position = new Vector3i((int) (loc.getX() * 8), (int) (loc.getY() * 8), (int) (loc.getZ() * 8));

        WrapperPlayServerSoundEffect packet = new WrapperPlayServerSoundEffect(sound, SoundCategory.BLOCK, position, 0.5f, 1);
        PacketEvents.getAPI().getPlayerManager().sendPacketSilently(player, packet);
    }
}

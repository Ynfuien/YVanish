package pl.ynfuien.yvanish.hooks.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.util.Vector;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.config.PluginConfig;
import pl.ynfuien.yvanish.core.ChestableUtils;
import pl.ynfuien.yvanish.core.ChestableViewers;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.hooks.packetevents.listeners.*;

import java.util.HashSet;
import java.util.Set;

public class PacketEventsHook {
    private final YVanish instance;
    private static VanishManager vanishManager;
    private static boolean enabled = false;

    // Block locations that will be checked when cancelling animation / sound packets
    private static final Set<Location> blockedLocations = new HashSet<>();

    public PacketEventsHook(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
        enabled = true;

        registerListeners(PluginConfig.packetListenersPriority);
    }

    private void registerListeners(PacketListenerPriority priority) {
        PacketListener[] listeners = new PacketListener[] {
                new PacketPlayerInfoListener(instance),
                new PacketPlayerInfoRemoveListener(instance),
                new PacketBlockActionListener(instance),
                new PacketBlockChangeListener(instance),
                new PacketSoundEffectListener(instance)
        };

        for (PacketListener listener : listeners) {
            PacketEvents.getAPI().getEventManager().registerListener(listener, priority);
        }
    }

    public static boolean canSeeBlockChange(Player player, Block block) {
        if (vanishManager.isNoOneVanished()) return true;
        if (player.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return true;

        Set<Player> viewers = ChestableViewers.getBlockViewers(block);

        if (viewers.isEmpty()) return true;
        if (viewers.contains(player)) return true;

        int size = viewers.size();
        for (Player p : viewers) {
            if (!vanishManager.isVanished(p)) continue;
            if (!Storage.getUser(p.getUniqueId()).getSilentChests()) continue;

            size--;
        }

        return size != 0;
    }

    private static Set<Location> getAllBlockLocations(Block block) {
        Set<Location> locations = new HashSet<>();
        // Put left and right side of a double chest
        if (ChestableUtils.isBlockDoubleChest(block)) {
            DoubleChestInventory doubleChest = ChestableUtils.getDoubleChestInventory(block);

            locations.add(doubleChest.getLeftSide().getLocation());
            locations.add(doubleChest.getRightSide().getLocation());
            return locations;
        }

        // Put barrel block location but also an offset location,
        // that for some forsaken reason is used in the sound packet
        if (block.getType().equals(Material.BARREL)) {
            Location location = block.getLocation();
            locations.add(location);

            Barrel barrel = (Barrel) block.getBlockData();

            BlockFace facing = barrel.getFacing();
            Vector direction = facing.getDirection();

            direction.multiply(0.5);
            Location center = location.toCenterLocation();
            center.add(direction);

            locations.add(center);
            return locations;
        }

        // Just a block location
        locations.add(block.getLocation());
        return locations;
    }

    public static void addLocationToBlock(Block block) {
        blockedLocations.addAll(getAllBlockLocations(block));
    }

    public static void removeLocationFromBlocked(Block block) {
        blockedLocations.removeAll(getAllBlockLocations(block));
    }

    public static boolean isLocationBlocked(Location location) {
        return blockedLocations.contains(location);
    }

    public static boolean isEnabled() {
        return enabled;
    }
}

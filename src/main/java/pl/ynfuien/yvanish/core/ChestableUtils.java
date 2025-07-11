package pl.ynfuien.yvanish.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.block.data.type.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import pl.ynfuien.yvanish.YVanish;

import java.util.*;

public class ChestableUtils {
    private static final HashMap<World, Boolean> catDetectionByWorld = new HashMap<>();
    public static void setupCatDetection(YVanish instance) {
        // I know, future-proof AF
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            final String SETTING_PATH = "entities.behavior.disable-chest-cat-detection";

            FileConfiguration config = Bukkit.getServer().spigot().getPaperConfig();
            ConfigurationSection worlds = config.getConfigurationSection("__________WORLDS__________");

            // Default value
            boolean defaultValue = false;
            if (worlds != null) {
                ConfigurationSection defaultSection = worlds.getConfigurationSection("__defaults__");
                if (defaultSection != null && defaultSection.isSet(SETTING_PATH)) {
                    defaultValue = defaultSection.getBoolean(SETTING_PATH);
                }
            }

            // Worlds
            for (World world : Bukkit.getWorlds()) {
                boolean value = defaultValue;
                ConfigurationSection worldConfig = worlds.getConfigurationSection(world.getName());
                if (worldConfig != null && worldConfig.isSet(SETTING_PATH)) {
                    value = worldConfig.getBoolean(SETTING_PATH);
                }

                catDetectionByWorld.put(world, !value);
            }
        });
    }

    private static final List<InventoryType> CHESTABLE_INVENTORY_TYPES = List.of(
            InventoryType.BARREL,
            InventoryType.ENDER_CHEST,
            InventoryType.CHEST,
            InventoryType.SHULKER_BOX
    );
    public static boolean isInventoryTypeChestable(InventoryType type) {
        return CHESTABLE_INVENTORY_TYPES.contains(type);
    }

    public static boolean isMaterialChestable(Material material) {
        if (material.equals(Material.CHEST)) return true;
        if (material.equals(Material.TRAPPED_CHEST)) return true;
        if (material.equals(Material.BARREL)) return true;
        if (material.equals(Material.ENDER_CHEST)) return true;
        return material.name().contains(Material.SHULKER_BOX.name());
    }

    public static boolean isBlockDoubleChest(Block block) {
        if (!block.getType().equals(Material.CHEST)) return false;

        Chest chest = (Chest) block.getBlockData();
        return !chest.getType().equals(Chest.Type.SINGLE);
    }
    public static Block getDoubleChestBlock(Block block) {
        if (block.getType() != Material.CHEST) return block;

        Container container = (Container) block.getState();
        if (!(container.getInventory() instanceof DoubleChestInventory doubleChest)) return block;

        BlockInventoryHolder holder = (BlockInventoryHolder) doubleChest.getLeftSide().getHolder();
        if (holder == null) return block;

        return holder.getBlock();
    }
    public static DoubleChestInventory getDoubleChestInventory(Block block) {
        Container container = (Container) block.getState();
        return (DoubleChestInventory) container.getInventory();
    }

    public static Set<Location> getLocationsOfChestable(Block block) {
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

            org.bukkit.block.data.type.Barrel barrel = (Barrel) block.getBlockData();

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


    /**
     * Checks whether a chest or shulker box can be opened.
     * @param block Chest/shulker box to check
     */
    public static boolean isChestOpenable(Block block) {
        // Shulker boxes
        if (block.getState() instanceof ShulkerBox) {
            // Check if faced block is even collidable
            Directional directional = (Directional) block.getBlockData();
            BlockFace shulkerFacing = directional.getFacing();
            Block facedBlock = block.getRelative(directional.getFacing());
            if (!facedBlock.getType().isCollidable()) return true;

            // Create bounding box of the shulker box
            BoundingBox shulkerBB = new BoundingBox(0, 0, 0, 1, 1, 1);
            // And expand it so it reflects opened shulker box
            shulkerBB.expand(shulkerFacing, 0.5);

            // Get collision shapes of faced block, and check
            // whether opened shulker box would collide with it
            for (BoundingBox boundingBox : facedBlock.getCollisionShape().getBoundingBoxes()) {
                // Change location of bounding box, so that it
                // reflects the position of faced block.
                boundingBox.shift(shulkerFacing.getDirection());

                if (shulkerBB.overlaps(boundingBox)) return false;
            }

            return true;
        }

        // Double chests
        if (isBlockDoubleChest(block)) {
            DoubleChest doubleChest = getDoubleChestInventory(block).getHolder();
            if (doubleChest == null) return true;

            BlockInventoryHolder leftSide = (BlockInventoryHolder) doubleChest.getLeftSide();
            if (leftSide != null && isBlockBlocked(leftSide.getBlock())) return false;

            BlockInventoryHolder rightSide = (BlockInventoryHolder) doubleChest.getRightSide();
            return !(rightSide != null && isBlockBlocked(rightSide.getBlock()));
        }

        // Single chests (normal, trapped, ender)
        return !isBlockBlocked(block);
    }

    // Just a shorthand function
    private static boolean isBlockBlocked(Block block) {
        if (block.getRelative(BlockFace.UP).getType().isOccluding()) return true;
        if (isCatBlockingChest(block)) return true;

        return false;
    }

    /**
     * Checks for sitting cats blocking the chest.
     * @param block The chest block
     * @return Whether cat is sitting on the chest
     */
    private static boolean isCatBlockingChest(Block block) {
        if (block.getType().equals(Material.ENDER_CHEST)) return false;

        // Check for Paper's 'disable-chest-cat-detection' config option
        World world = block.getLocation().getWorld();
        if (catDetectionByWorld.containsKey(world) && !catDetectionByWorld.get(world)) return false;

        // Get entities above the chest
        Collection<LivingEntity> entities = block.getLocation().add(0, 1, 0).getNearbyLivingEntities(1);

        BoundingBox blockBoundingBox = BoundingBox.of(block);
        // Loop through the cats
        for (LivingEntity entity : entities) {
            if (!entity.getType().equals(EntityType.CAT)) continue;

            Cat cat = (Cat) entity;
            if (!cat.isSitting()) continue;

            // Check if cat's bounding box overlaps with the block (when it's Y is lowered)
            BoundingBox catBoundingBox = cat.getBoundingBox().shift(0, -1, 0);
            if (blockBoundingBox.overlaps(catBoundingBox)) return true;
        }

        return false;
    }
}

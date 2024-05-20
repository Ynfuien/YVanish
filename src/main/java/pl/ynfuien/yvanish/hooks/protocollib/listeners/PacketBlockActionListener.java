package pl.ynfuien.yvanish.hooks.protocollib.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.ChestableUtils;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.protocollib.ProtocolLibHook;

public class PacketBlockActionListener extends PacketAdapter {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PacketBlockActionListener(YVanish instance, ListenerPriority priority) {
        super(instance, priority, PacketType.Play.Server.BLOCK_ACTION);
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (vanishManager.isNoOneVanished()) return;

        Player receiver = event.getPlayer();
        if (receiver.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;

        PacketContainer packet = event.getPacket();

        Integer actionID = packet.getIntegers().readSafely(0);
        if (actionID == null || actionID == 0) return;
        if (actionID == 10) {
            YLogger.debug("===== BlockAction =====");
            YLogger.debug("My own packet");
            YLogger.debug("Player: " + receiver.getName());
            YLogger.debug("actionParameter: " + packet.getIntegers().readSafely(1));
            packet.getIntegers().writeSafely(0, 1);
            return;
        }

        Integer actionParameter = packet.getIntegers().readSafely(1);
        if (actionParameter == null || actionParameter < 0) return;

        Material blockType = packet.getBlocks().readSafely(0);
        YLogger.debug("===== BlockAction =====");
        YLogger.debug("Player: " + receiver.getName());
//        YLogger.debug("Timestamp: " + new Date().getTime());
        YLogger.debug("Type: " + blockType);
        YLogger.debug("actionParameter: " + actionParameter);
//        if (actionParameter == 0) return;
        if (blockType == null || !ChestableUtils.isMaterialChestable(blockType)) return;

        BlockPosition position = packet.getBlockPositionModifier().readSafely(0);
        if (position == null) return;

        if (actionParameter > 1) packet.getIntegers().writeSafely(1, 1);


        Location loc = position.toLocation(receiver.getWorld());
        Block block = loc.getBlock();
        block = ChestableUtils.getDoubleChestBlock(block);
        if (!ProtocolLibHook.canSeeBlockChange(receiver, block)) {
//            if (actionParameter == 0) return;
            YLogger.debug("Cancelled!");
            event.setCancelled(true);
        }
//        List<HumanEntity> viewers = getBlockViewers(block);
//        YLogger.debug("Viewers before: " + viewers.size());
//
//        if (viewers.isEmpty()) return;
//        if (viewers.contains(receiver)) return;
//
//        for (int i = 0; i < viewers.size(); i++) {
//            HumanEntity viewer = viewers.get(i);
//            Player p = (Player) viewer;
//
//            if (!vanishManager.isVanished(p)) continue;
//
//            viewers.remove(i);
//            i--;
//        }
//
//        YLogger.debug("Viewers after: " + viewers.size());
//        if (viewers.isEmpty()) event.setCancelled(true);
    }

//    private List<HumanEntity> getBlockViewers(Block block) {
//        if (block.getType().equals(Material.ENDER_CHEST)) return PlayerInteractListener.getEnderBlockViewers(block);
//
//        BlockInventoryHolder inventoryHolder = (BlockInventoryHolder) block.getState();
//        Inventory inventory = inventoryHolder.getInventory();
//        // Some damn magic with double chests.
//        if (inventory instanceof DoubleChestInventory doubleChestInventory) {
//            inventory = doubleChestInventory.getLeftSide();
//        }
//
//        return new ArrayList<>(inventory.getViewers());
//    }
}

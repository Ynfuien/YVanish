package pl.ynfuien.yvanish.hooks.protocollib.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.entity.Player;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.protocollib.ProtocolLibHook;

public class PacketBlockChangeListener extends PacketAdapter {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PacketBlockChangeListener(YVanish instance, ListenerPriority priority) {
        super(instance, priority, PacketType.Play.Server.BLOCK_CHANGE);
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (vanishManager.isNoOneVanished()) return;

        Player receiver = event.getPlayer();
        if (receiver.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;

        PacketContainer packet = event.getPacket();

        WrappedBlockData blockData = packet.getBlockData().readSafely(0);
        if (blockData == null) return;

        Material blockType = blockData.getType();
        if (!blockType.equals(Material.BARREL)) return;

        BlockPosition position = packet.getBlockPositionModifier().readSafely(0);
        if (position == null) return;

        YLogger.debug("===== BlockChange =====");

        Location loc = position.toLocation(receiver.getWorld());
        Block block = loc.getBlock();
        Barrel barrel = (Barrel) block.getBlockData();
        boolean isReallyClosed = !barrel.isOpen();

        boolean isClosePacket = false;
        if (blockData.getHandle().toString().contains("open=false")) isClosePacket = true;

        if (isClosePacket && !isReallyClosed) return;


        if (!ProtocolLibHook.canSeeBlockChange(receiver, block)) event.setCancelled(true);
    }
}

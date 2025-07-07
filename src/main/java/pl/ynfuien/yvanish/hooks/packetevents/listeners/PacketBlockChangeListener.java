package pl.ynfuien.yvanish.hooks.packetevents.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.packetevents.PacketEventsHook;

// Barrel lid open / close - Cancelling if necessary
public class PacketBlockChangeListener implements PacketListener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PacketBlockChangeListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.BLOCK_CHANGE)) return;
        if (vanishManager.isNoOneVanished()) return;

        Player receiver = event.getPlayer();
        if (receiver.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;

        WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(event);

        WrappedBlockState blockState = packet.getBlockState();
        StateType blockType = blockState.getType();
        if (!blockType.equals(StateTypes.BARREL)) return;


//        YLogger.debug("<yellow>===== BlockChange =====");
//        YLogger.debug("<yellow>Primary thread: " + Bukkit.isPrimaryThread());
//        YLogger.debug("<yellow>Block state: " + blockState);
//        YLogger.debug("<yellow>Is open: " + blockState.isOpen());

        Vector3i pos = packet.getBlockPosition();
        Location loc = new Location(receiver.getWorld(), pos.x, pos.y, pos.z);
        if (!PacketEventsHook.isLocationBlocked(loc)) return;
        event.setCancelled(true);

//        int blockId = packet.getBlockId();
//        Bukkit.getGlobalRegionScheduler().run(instance, (task) -> {
//            YLogger.debug("<yellow>== BlockChange task ==");
//            Block block = receiver.getWorld().getBlockAt(position.x, position.y, position.z);
//            Barrel barrel = (Barrel) block.getBlockData();
//            boolean isReallyClosed = !barrel.isOpen();
//
//            boolean isClosePacket = !blockState.isOpen();
//            YLogger.debug("<yellow>isReallyClosed: " + isReallyClosed);
//            YLogger.debug("<yellow>isClosePacket: " + isClosePacket);
//
//            if (!(isClosePacket && !isReallyClosed) && !PacketEventsHook.canSeeBlockChange(receiver, block)) return;
//
//            YLogger.debug("<yellow>Send duplicate");
//            WrapperPlayServerBlockChange packetDuplicate = new WrapperPlayServerBlockChange(position, blockId);
//            PacketEvents.getAPI().getPlayerManager().sendPacketSilently(receiver, packetDuplicate);
//        });
    }
}

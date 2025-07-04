package pl.ynfuien.yvanish.hooks.packetevents.listeners;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockAction;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.ChestableUtils;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.packetevents.PacketEventsHook;

import java.util.Set;

// Chest animation packet - Cancelling or modifying if necessary
public class PacketBlockActionListener implements PacketListener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    private final static Set<StateType> CHESTABLE = Set.of(
        StateTypes.CHEST,
        StateTypes.TRAPPED_CHEST,
        StateTypes.BARREL,
        StateTypes.ENDER_CHEST,
        StateTypes.SHULKER_BOX,
        StateTypes.WHITE_SHULKER_BOX,
        StateTypes.ORANGE_SHULKER_BOX,
        StateTypes.MAGENTA_SHULKER_BOX,
        StateTypes.LIGHT_BLUE_SHULKER_BOX,
        StateTypes.YELLOW_SHULKER_BOX,
        StateTypes.LIME_SHULKER_BOX,
        StateTypes.PINK_SHULKER_BOX,
        StateTypes.GRAY_SHULKER_BOX,
        StateTypes.LIGHT_GRAY_SHULKER_BOX,
        StateTypes.CYAN_SHULKER_BOX,
        StateTypes.PURPLE_SHULKER_BOX,
        StateTypes.BLUE_SHULKER_BOX,
        StateTypes.BROWN_SHULKER_BOX,
        StateTypes.GREEN_SHULKER_BOX,
        StateTypes.RED_SHULKER_BOX,
        StateTypes.BLACK_SHULKER_BOX
    );

    public PacketBlockActionListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.BLOCK_ACTION)) return;
        if (vanishManager.isNoOneVanished()) return;

        Player receiver = event.getPlayer();
        if (receiver.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;

        WrapperPlayServerBlockAction packet = new WrapperPlayServerBlockAction(event);

        int actionId = packet.getActionId();
        int actionData = packet.getActionData();

        YLogger.debug("===== BlockAction =====");
        int blockTypeId = packet.getBlockTypeId();
        StateType blockType = StateTypes.getById(event.getServerVersion().toClientVersion(), blockTypeId);

        YLogger.debug("BlockTypeID: " + blockTypeId);
        YLogger.debug("Player: " + receiver.getName());
        YLogger.debug("BlockType: " + blockType.getName());
        YLogger.debug("actionId: " + actionId);
        YLogger.debug("actionData: " + actionData);
        if (!CHESTABLE.contains(blockType)) return;

        Vector3i position = packet.getBlockPosition();

        event.setCancelled(true);

        Bukkit.getGlobalRegionScheduler().run(instance, (task) -> {
            Block block = receiver.getWorld().getBlockAt(position.x, position.y, position.z);
            block = ChestableUtils.getDoubleChestBlock(block);

            if (!PacketEventsHook.canSeeBlockChange(receiver, block)) return;

            WrapperPlayServerBlockAction packetDuplicate = new WrapperPlayServerBlockAction(position, actionId, Math.min(actionData, 1), blockTypeId);
            PacketEvents.getAPI().getPlayerManager().sendPacketSilently(receiver, packetDuplicate);
        });
    }
}

package pl.ynfuien.yvanish.hooks.packetevents.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWaypoint;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.listeners.joinquit.PlayerJoinListener;

import java.util.Set;
import java.util.UUID;

// Waypoint packet - Something
public class PacketWaypointListener implements PacketListener {
    private final YVanish instance;
    private final VanishManager vanishManager;
    private final Set<UUID> freshlyJoined = PlayerJoinListener.getFreshlyJoined();

    public PacketWaypointListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.WAYPOINT)) return;
        if (vanishManager.isNoOneVanished()) return;

        Player receiver = event.getPlayer();
        if (receiver.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;

        if (!freshlyJoined.contains(receiver.getUniqueId())) return;

        WrapperPlayServerWaypoint packet = new WrapperPlayServerWaypoint(event);

        UUID uuid = packet.getWaypoint().getIdentifier().getLeft();
        if (uuid == null) return;

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        if (!vanishManager.isVanished(player)) return;

        event.setCancelled(true);
    }
}

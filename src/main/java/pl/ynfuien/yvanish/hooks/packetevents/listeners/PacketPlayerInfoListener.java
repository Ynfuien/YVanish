package pl.ynfuien.yvanish.hooks.packetevents.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;

import java.util.List;
import java.util.UUID;

// Player info packet with game mode, ping, display name etc.
// Removing players that should be invisible
public class PacketPlayerInfoListener implements PacketListener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PacketPlayerInfoListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.PLAYER_INFO_UPDATE)) return;
        if (vanishManager.isNoOneVanished()) return;

        Player receiver = event.getPlayer();
        if (receiver.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;

        WrapperPlayServerPlayerInfoUpdate packet = new WrapperPlayServerPlayerInfoUpdate(event);
        List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> playerInfoList = packet.getEntries();

        boolean changes = false;
        for (int i = 0; i < playerInfoList.size(); i++) {
            WrapperPlayServerPlayerInfoUpdate.PlayerInfo playerInfo = playerInfoList.get(i);

            UUID uuid = playerInfo.getProfileId();
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;

            if (p.equals(receiver)) continue;

            if (vanishManager.isVanished(p)) {
                playerInfoList.remove(i);
                changes = true;
                i--;
            }
        }

        if (!changes) return;

        if (playerInfoList.isEmpty()) {
            event.setCancelled(true);
            return;
        }

        event.markForReEncode(true);
    }
}

package pl.ynfuien.yvanish.hooks.packetevents.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.listeners.joinquit.PlayerJoinListener;

import java.util.List;
import java.util.Set;
import java.util.UUID;

// Player info remove packet - Removing from the packet players that should be invisible
public class PacketPlayerInfoRemoveListener implements PacketListener {
    private final YVanish instance;
    private final VanishManager vanishManager;
//    private final Set<Player> vanishedOnJoin = PlayerJoinListener.getVanishedOnJoin();
    private final Set<Player> freshlyJoined = PlayerJoinListener.getFreshlyJoined();

    public PacketPlayerInfoRemoveListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.PLAYER_INFO_REMOVE)) return;
        if (vanishManager.isNoOneVanished()) return;

        Player receiver = event.getPlayer();
        if (receiver.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;

        YLogger.debug("===== PLAYER_INFO_REMOVE =====");
        YLogger.debug("Primary thread: " + Bukkit.isPrimaryThread());
        WrapperPlayServerPlayerInfoRemove packet = new WrapperPlayServerPlayerInfoRemove(event);

        List<UUID> uuidList = packet.getProfileIds();
        YLogger.debug("Size: " + uuidList.size());
        if (uuidList.isEmpty()) return;

        boolean changed = false;
        for (int i = 0; i < uuidList.size(); i++) {
            UUID uuid = uuidList.get(0);
            if (uuid == null) continue;

            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;

            if (p.equals(receiver)) continue;

            if (!vanishManager.isVanished(p)) continue;
//            if (!vanishedOnJoin.contains(p) && !freshlyJoined.contains(receiver)) continue;
            if (!freshlyJoined.contains(receiver)) continue;

            uuidList.remove(i);
            changed = true;
            i--;
        }


        YLogger.debug("Changed: " + changed);
        if (!changed) return;

        if (uuidList.isEmpty()) event.setCancelled(true);
    }
}

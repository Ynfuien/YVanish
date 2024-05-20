package pl.ynfuien.yvanish.hooks.protocollib.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.listeners.PlayerJoinListener;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PacketPlayerInfoRemoveListener extends PacketAdapter {
    private final YVanish instance;
    private final VanishManager vanishManager;
//    private final Set<Player> vanishedOnJoin = PlayerJoinListener.getVanishedOnJoin();
    private final Set<Player> freshlyJoined = PlayerJoinListener.getFreshlyJoined();

    public PacketPlayerInfoRemoveListener(YVanish instance, ListenerPriority priority) {
        super(instance, priority, PacketType.Play.Server.PLAYER_INFO_REMOVE);
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (vanishManager.isNoOneVanished()) return;

        Player receiver = event.getPlayer();
        if (receiver.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;

//        if (receiver.getName().equalsIgnoreCase("Tsurtnu")) YLogger.debug("Player remove packet: " + System.currentTimeMillis());

        PacketContainer packet = event.getPacket();
        List<UUID> uuidList = packet.getUUIDLists().readSafely(0);
        if (uuidList == null) return;

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


        if (!changed) return;

        if (uuidList.isEmpty()) {
            event.setCancelled(true);
            return;
        }

        packet.getUUIDLists().write(0, uuidList);
    }
}

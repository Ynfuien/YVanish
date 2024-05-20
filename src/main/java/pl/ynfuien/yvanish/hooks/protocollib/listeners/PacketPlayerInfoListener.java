package pl.ynfuien.yvanish.hooks.protocollib.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;

import java.util.List;
import java.util.UUID;

public class PacketPlayerInfoListener extends PacketAdapter {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PacketPlayerInfoListener(YVanish instance, ListenerPriority priority) {
        super(instance, priority, PacketType.Play.Server.PLAYER_INFO);
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (vanishManager.isNoOneVanished()) return;

        Player receiver = event.getPlayer();
        if (receiver.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;

//        if (receiver.getName().equalsIgnoreCase("Tsurtnu")) YLogger.warn("Player info packet: " + new Date().getTime());

        PacketContainer packet = event.getPacket();
        // First list is an array of actions,
        // and second list an array of players with their action values
        StructureModifier<List<PlayerInfoData>> dataLists = packet.getPlayerInfoDataLists();
        List<PlayerInfoData> playerList = dataLists.read(1);

        boolean changes = false;
        for (int i = 0; i < playerList.size(); i++) {
            PlayerInfoData playerInfo = playerList.get(i);
            if (playerInfo == null) continue;

            UUID uuid = playerInfo.getProfileId();
            if (uuid == null) continue;

            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;

            if (p.equals(receiver)) continue;

            if (vanishManager.isVanished(p)) {
                playerList.remove(i);
                changes = true;
                i--;
            }
        }

        if (!changes) return;

        if (playerList.isEmpty()) {
            event.setCancelled(true);
            return;
        }

        dataLists.write(1, playerList);
    }
}

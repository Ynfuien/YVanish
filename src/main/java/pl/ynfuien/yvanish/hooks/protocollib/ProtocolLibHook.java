package pl.ynfuien.yvanish.hooks.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.ChestableViewers;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.hooks.protocollib.listeners.PacketBlockActionListener;
import pl.ynfuien.yvanish.hooks.protocollib.listeners.PacketBlockChangeListener;
import pl.ynfuien.yvanish.hooks.protocollib.listeners.PacketNamedSoundEffectListener;
import pl.ynfuien.yvanish.hooks.protocollib.listeners.PacketPlayerInfoRemoveListener;

import java.util.List;

public class ProtocolLibHook {
    private final YVanish instance;
    private static VanishManager vanishManager;
    private static ProtocolManager protocolManager = null;

    public ProtocolLibHook(YVanish instance) {
        this.instance = instance;
        vanishManager = instance.getVanishManager();
        protocolManager = ProtocolLibrary.getProtocolManager();

        registerListeners(ListenerPriority.NORMAL);
    }

    private void registerListeners(ListenerPriority priority) {
        PacketAdapter[] listeners = new PacketAdapter[] {
//                new PacketPlayerInfoListener(instance, priority),
                new PacketPlayerInfoRemoveListener(instance, priority),
                new PacketBlockActionListener(instance, priority),
                new PacketBlockChangeListener(instance, priority),
                new PacketNamedSoundEffectListener(instance, priority)
        };

        for (PacketAdapter listener : listeners) {
            protocolManager.addPacketListener(listener);
        }
    }

    public static boolean canSeeBlockChange(Player player, Block block) {
        if (vanishManager.isNoOneVanished()) return true;
        if (player.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return true;

        List<HumanEntity> viewers = ChestableViewers.getBlockViewers(block);
        YLogger.debug("Viewers: " + viewers.size());

        if (viewers.isEmpty()) return true;
        if (viewers.contains(player)) return true;

        for (int i = 0; i < viewers.size(); i++) {
            HumanEntity viewer = viewers.get(i);
            Player p = (Player) viewer;

            if (!vanishManager.isVanished(p)) continue;
            if (!Storage.getUser(p.getUniqueId()).getSilentChests()) continue;

            viewers.remove(i);
            i--;
        }

        YLogger.debug("After: " + viewers.size());
        if (viewers.isEmpty()) {
            return false;
        }
        return true;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}

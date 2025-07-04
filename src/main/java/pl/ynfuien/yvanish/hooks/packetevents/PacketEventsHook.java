package pl.ynfuien.yvanish.hooks.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.config.PluginConfig;
import pl.ynfuien.yvanish.core.ChestableViewers;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.hooks.packetevents.listeners.*;

import java.util.List;

public class PacketEventsHook {
    private final YVanish instance;
    private static VanishManager vanishManager;
    private final PacketEventsAPI<?> api;
    private static boolean enabled = false;

    public PacketEventsHook(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
        this.api = PacketEvents.getAPI();
        enabled = true;

        registerListeners(PluginConfig.packetListenersPriority);
    }

    private void registerListeners(PacketListenerPriority priority) {
        PacketListener[] listeners = new PacketListener[] {
                new PacketPlayerInfoListener(instance),
                new PacketPlayerInfoRemoveListener(instance),
                new PacketBlockActionListener(instance),
                new PacketBlockChangeListener(instance),
                new PacketSoundEffectListener(instance)
        };

        for (PacketListener listener : listeners) {
            PacketEvents.getAPI().getEventManager().registerListener(listener, priority);
        }
    }

    public static boolean canSeeBlockChange(Player player, Block block) {
        if (vanishManager.isNoOneVanished()) return true;
        if (player.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return true;

        List<HumanEntity> viewers = ChestableViewers.getBlockViewers(block);
//        YLogger.debug("Viewers: " + viewers.size());

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

//        YLogger.debug("After: " + viewers.size());
        return !viewers.isEmpty();
    }

    public static boolean isEnabled() {
        return enabled;
    }
}

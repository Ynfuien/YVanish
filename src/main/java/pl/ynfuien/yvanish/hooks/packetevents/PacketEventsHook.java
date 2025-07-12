package pl.ynfuien.yvanish.hooks.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.config.PluginConfig;
import pl.ynfuien.yvanish.core.ChestableViewers;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.hooks.packetevents.listeners.*;

import java.util.Set;

public class PacketEventsHook {
    private final YVanish instance;
    private static VanishManager vanishManager;
    private static boolean enabled = false;

    public PacketEventsHook(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
        enabled = true;

//        PacketEvents.getAPI().init();

        PacketListener[] listeners = new PacketListener[] {
                new PacketPlayerInfoListener(instance),
                new PacketPlayerInfoRemoveListener(instance),
                new PacketBlockActionListener(instance),
                new PacketBlockChangeListener(instance),
                new PacketSoundEffectListener(instance)
        };

        for (PacketListener listener : listeners) {
            PacketEvents.getAPI().getEventManager().registerListener(listener, PluginConfig.packetListenersPriority);
        }
    }

    public static boolean canSeeBlockChange(Player player, Block block) {
        return canSeeBlockChange(player, block.getLocation());
    }

    public static boolean canSeeBlockChange(Player player, Location blockLocation) {
        Set<Player> viewers = ChestableViewers.getBlockViewers(blockLocation);
        return canSeeBlockChange(viewers, player);
    }

    public static boolean canSeeBlockChange(Set<Player> viewers, Player player) {
        if (viewers.isEmpty()) return true;
        if (viewers.contains(player)) return true;

        int size = viewers.size();
        for (Player p : viewers) {
            if (!vanishManager.isVanished(p)) continue;
            if (!Storage.getUser(p.getUniqueId()).getSilentChests()) continue;

            size--;
        }

        return size != 0;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}

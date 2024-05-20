package pl.ynfuien.yvanish.listeners.serverlist;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.config.PluginConfig;
import pl.ynfuien.yvanish.core.VanishManager;

import java.util.Iterator;

public class PaperServerListPingListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PaperServerListPingListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Hide vanished players from server list ping
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onServerListPing(PaperServerListPingEvent event) {
        if (!PluginConfig.changeServerStatus) return;
        if (vanishManager.isNoOneVanished()) return;

        Iterator<Player> i = event.iterator();
        while (i.hasNext()) {
            Player p = i.next();
            if (!vanishManager.isVanished(p)) continue;

            i.remove();
        }
    }
}

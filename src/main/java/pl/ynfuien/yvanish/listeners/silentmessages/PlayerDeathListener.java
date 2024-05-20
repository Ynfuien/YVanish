package pl.ynfuien.yvanish.listeners.silentmessages;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;

public class PlayerDeathListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayerDeathListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Prevent death message
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getPlayer();
        if (!vanishManager.isVanished(p)) return;

        User user = Storage.getUser(p.getUniqueId());
        if (!user.getSilentMessages()) return;

        event.deathMessage(null);
    }
}

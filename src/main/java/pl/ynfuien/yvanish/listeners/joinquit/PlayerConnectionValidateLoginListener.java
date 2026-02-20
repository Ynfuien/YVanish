package pl.ynfuien.yvanish.listeners.joinquit;

import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;

import java.util.UUID;

public class PlayerConnectionValidateLoginListener implements Listener {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayerConnectionValidateLoginListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    // Hide on join
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerConnectionValidateLogin(PlayerConnectionValidateLoginEvent event) {
        if (!event.isAllowed()) return;
        if (!(event.getConnection() instanceof PlayerConfigurationConnection connection)) return;

        UUID uuid = connection.getProfile().getId();
        PlayerJoinListener.getFreshlyJoined().add(uuid);
    }
}

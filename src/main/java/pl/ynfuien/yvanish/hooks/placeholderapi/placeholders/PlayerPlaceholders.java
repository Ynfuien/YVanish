package pl.ynfuien.yvanish.hooks.placeholderapi.placeholders;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.hooks.placeholderapi.Placeholder;

public class PlayerPlaceholders implements Placeholder {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayerPlaceholders(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public String name() {
        return "player";
    }

    @Override
    public String getPlaceholder(String id, OfflinePlayer p) {
        if (id.equalsIgnoreCase("vanished")) {
            Player player = p.getPlayer();
            if (player == null || !player.isOnline()) return "offline player";

            if (vanishManager.isVanished(player)) return "yes";
            return "no";
        }

        if (!id.toLowerCase().startsWith("option_")) return null;
        id = id.substring(7);


        User user = Storage.getUser(p.getUniqueId());
        Boolean value = getValue(user, id);
        if (value == null) return "incorrect option";

        return value ? "yes" : "no";
    }

    private Boolean getValue(User user, String id) {
        if (id.equalsIgnoreCase("silent-chests")) return user.getSilentChests();
        if (id.equalsIgnoreCase("silent-sculk")) return user.getSilentSculk();
        if (id.equalsIgnoreCase("silent-messages")) return user.getSilentMessages();
        if (id.equalsIgnoreCase("no-pickup")) return user.getNoPickup();
        if (id.equalsIgnoreCase("no-mobs")) return user.getNoMobs();
        if (id.equalsIgnoreCase("action-bar")) return user.getActionBar();
        if (id.equalsIgnoreCase("boss-bar")) return user.getBossBar();

        return null;
    }
}

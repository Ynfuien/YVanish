package pl.ynfuien.yvanish.hooks.placeholderapi.placeholders;

import org.bukkit.OfflinePlayer;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.hooks.placeholderapi.Placeholder;

public class VanishOptionsPlaceholders implements Placeholder {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public VanishOptionsPlaceholders(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public String name() {
        return "vanishoption";
    }

    @Override
    public String getPlaceholder(String id, OfflinePlayer p) {
        User user = Storage.getUser(p.getUniqueId());

        if (id.equalsIgnoreCase("silent-chests")) {
            return user.getSilentChests() ? "yes" : "no";
        }

        if (id.equalsIgnoreCase("silent-sculk")) {
            return user.getSilentSculk() ? "yes" : "no";
        }

        if (id.equalsIgnoreCase("no-pickup")) {
            return user.getNoPickup() ? "yes" : "no";
        }

        if (id.equalsIgnoreCase("no-mobs")) {
            return user.getNoMobs() ? "yes" : "no";
        }

        if (id.equalsIgnoreCase("action-bar")) {
            return user.getActionBar() ? "yes" : "no";
        }

        if (id.equalsIgnoreCase("boss-bar")) {
            return user.getBossBar() ? "yes" : "no";
        }

        return "incorrect option";
    }
}

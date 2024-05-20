package pl.ynfuien.yvanish.hooks.placeholderapi.placeholders;

import org.bukkit.OfflinePlayer;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.placeholderapi.Placeholder;

public class VanishPlaceholders implements Placeholder {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public VanishPlaceholders(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public String name() {
        return "vanished";
    }

    @Override
    public String getPlaceholder(String id, OfflinePlayer p) {
        if (p == null || !p.isOnline()) return "offline player";

        if (vanishManager.isVanished(p.getPlayer())) return "yes";
        return "no";
    }
}

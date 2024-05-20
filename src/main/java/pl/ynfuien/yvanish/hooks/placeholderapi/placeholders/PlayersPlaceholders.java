package pl.ynfuien.yvanish.hooks.placeholderapi.placeholders;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.placeholderapi.Placeholder;

import java.util.Iterator;
import java.util.Set;

public class PlayersPlaceholders implements Placeholder {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public PlayersPlaceholders(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public String name() {
        return "players";
    }

    @Override
    public String getPlaceholder(String id, OfflinePlayer p) {
        Set<Player> vanished = vanishManager.getVanishedPlayers();

        if (id.equalsIgnoreCase("list")) {
            StringBuilder result = new StringBuilder();

            if (!vanished.isEmpty()) {
                Iterator<Player> i = vanished.iterator();
                while (true) {
                    Player player = i.next();
                    result.append(player.getName());

                    if (!i.hasNext()) break;
                    result.append(", ");
                }
            }

            return result.toString();
        }

        if (id.equalsIgnoreCase("count")) return Integer.toString(vanished.size());

        return null;
    }
}

package pl.ynfuien.yvanish.hooks.placeholderapi.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.placeholderapi.Placeholder;

import java.util.Collection;

public class OnlinePlayersPlaceholders implements Placeholder {
    private final YVanish instance;
    private final VanishManager vanishManager;

    public OnlinePlayersPlaceholders(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public String name() {
        return "onlineplayers";
    }

    @Override
    public String getPlaceholder(String id, OfflinePlayer p) {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        if (id.equalsIgnoreCase("count")) {
            int vanished = vanishManager.getVanishedPlayers().size();
            int online = onlinePlayers.size();

            return Integer.toString(online - vanished);
        }

        // Relative to whom a player can see
        if (id.equalsIgnoreCase("count_relative")) {
            Player player = p.getPlayer();
            if (player == null || !player.isOnline()) return "offline player";

            int count = 0;
            for (Player loopPlayer : onlinePlayers) {
                if (player.canSee(loopPlayer)) count++;
            }

            return Integer.toString(count);
        }

        return null;
    }
}

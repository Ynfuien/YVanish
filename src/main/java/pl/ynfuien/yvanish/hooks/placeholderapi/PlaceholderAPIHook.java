package pl.ynfuien.yvanish.hooks.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.hooks.placeholderapi.placeholders.PlayerPlaceholders;
import pl.ynfuien.yvanish.hooks.placeholderapi.placeholders.PlayersPlaceholders;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final YVanish instance;

    private final Placeholder[] placeholders;

    public PlaceholderAPIHook(YVanish instance) {
        this.instance = instance;

        placeholders = new Placeholder[] {
            new PlayerPlaceholders(instance),
            new PlayersPlaceholders(instance)
        };
    }

    @Override @NotNull
    public String getAuthor() {
        return "Ynfuien";
    }

    @Override @NotNull
    public String getIdentifier() {
        return "yvanish";
    }

    @Override @NotNull
    public String getVersion() {
        return "0.0.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    // Vanished player
    // %yvanish_player_vanished%
    // %yvanish_player_option_silent-chests%
    // %yvanish_player_option_silent-sculk%
    // %yvanish_player_option_silent-messages%
    // %yvanish_player_option_no-pickup%
    // %yvanish_player_option_no-mobs%
    // %yvanish_player_option_action-bar%
    // %yvanish_player_option_boss-bar%

    // Vanished players
    // %yvanish_players_list%
    // %yvanish_players_count%

    // Online players
    // %yvanish_onlineplayers_count%
    // %yvanish_onlineplayers_count_relative%

    @Override
    public String onRequest(OfflinePlayer p, @NotNull String params) {
        Placeholder placeholder = null;

        // Loop through placeholders and get that provided by name
        for (Placeholder ph : placeholders) {
            if (params.startsWith(ph.name() + "_") || params.equalsIgnoreCase(ph.name())) {
                placeholder = ph;
                break;
            }
        }

        // If provided placeholder is incorrect
        if (placeholder == null) return "incorrect placeholder";
        String phName = placeholder.name();

        // Get placeholder properties from params
        String id = !params.equalsIgnoreCase(phName) ? params.substring(phName.length() + 1) : "";
        // Get placeholder result
        String result = placeholder.getPlaceholder(id, p);

        // If result is null
        if (result == null) return "incorrect property";

        // Return result
        return result;
    }
}
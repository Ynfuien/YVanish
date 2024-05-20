package pl.ynfuien.yvanish.hooks.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.hooks.placeholderapi.placeholders.VanishOptionsPlaceholders;
import pl.ynfuien.yvanish.hooks.placeholderapi.placeholders.VanishPlaceholders;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final YVanish instance;

    private final Placeholder[] placeholders;

    public PlaceholderAPIHook(YVanish instance) {
        this.instance = instance;

        placeholders = new Placeholder[] {
            new VanishPlaceholders(instance),
            new VanishOptionsPlaceholders(instance)
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
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    // Vanish
    // %yvanish_vanished%

    // Vanish options
    // %yvanish_vanishoption_silent-chests%
    // %yvanish_vanishoption_silent-sculk%
    // %yvanish_vanishoption_no-pickup%
    // %yvanish_vanishoption_no-mobs%
    // %yvanish_vanishoption_action-bar%
    // %yvanish_vanishoption_boss-bar%

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
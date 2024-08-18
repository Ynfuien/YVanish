package pl.ynfuien.yvanish.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Vanish toggle event - It's only fired when vanish is changed with a command.
 * It won't be used in case of another plugin using the API.
 */
public class VanishToggleEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private boolean vanish;


    public VanishToggleEvent(@NotNull Player player, boolean vanish) {
        super(false);

        this.player = player;
        this.vanish = vanish;
    }

    /**
     * Gets player that will be hidden/shown.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets vanish status that will be set.
     */
    public boolean getVanish() {
        return vanish;
    }

    /**
     * Sets vanish status.
     */
    public void setVanish(boolean vanish) {
        this.vanish = vanish;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

package pl.ynfuien.yvanish.listeners.fakejoin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.api.event.VanishToggleEvent;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.HashMap;

public class VanishToggleListener implements Listener {
    private final YVanish instance;

    public VanishToggleListener(YVanish instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVanishToggle(VanishToggleEvent event) {
        Player p = event.getPlayer();

        User user = Storage.getUser(p.getUniqueId());
        if (!user.getFakeJoin()) return;

        // Scheduled task to broadcast the message after a "Vanish enabled/disabled" message
        Bukkit.getScheduler().runTask(instance, () -> {
            HashMap<String, Object> placeholders = new HashMap<>();
            placeholders.put("username", p.getName());
            placeholders.put("uuid", p.getUniqueId());
            placeholders.put("display-name", ColorFormatter.SERIALIZER.serialize(p.displayName()));

            Component message = event.getVanish() ? Lang.Message.FAKE_QUIT.getComponent(p, placeholders) : Lang.Message.FAKE_JOIN.getComponent(p, placeholders);
            for (Player player : Bukkit.getOnlinePlayers()) player.sendMessage(message);
        });
    }
}

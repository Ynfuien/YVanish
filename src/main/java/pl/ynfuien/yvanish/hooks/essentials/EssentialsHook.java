package pl.ynfuien.yvanish.hooks.essentials;

import com.earth2me.essentials.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.config.PluginConfig;

public class EssentialsHook {
    private static YVanish instance;
    private static IEssentials essentials = null;

    public EssentialsHook(YVanish instance) {
        EssentialsHook.instance = instance;

        essentials = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }

    public static void vanishPlayer(Player player) {
        if (essentials == null) return;
        if (!PluginConfig.hooksEssentialsX) return;

        if (player.isOnline()) {
            essentials.getUser(player).setVanished(true);
            return;
        }

        // Player is vanished on PlayerLogin event,
        // and with Essentials wanting to give a potion effect
        // to not yet spawned player, there was an error.
        // So we delay this first vanish.
        Bukkit.getScheduler().runTaskTimer(instance, (task) -> {
            if (!player.isOnline()) return;

            essentials.getUser(player).setVanished(true);
            task.cancel();
        }, 1, 1);
    }

    public static void unvanishPlayer(Player player) {
        if (essentials == null) return;
        if (!PluginConfig.hooksEssentialsX) return;

        essentials.getUser(player).setVanished(false);
    }
}

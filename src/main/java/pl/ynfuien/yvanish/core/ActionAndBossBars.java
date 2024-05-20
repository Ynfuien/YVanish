package pl.ynfuien.yvanish.core;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.config.PluginConfig;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;

import java.util.HashMap;

public class ActionAndBossBars {
    private static YVanish instance;
    private static VanishManager vanishManager;

    private static BukkitTask actionBarInterval = null;
    private static BukkitTask bossBarInterval = null;
    private static final HashMap<Player, BossBar> playerBossBars = new HashMap<>();

    public static void setup(YVanish instance) {
        ActionAndBossBars.instance = instance;
        vanishManager = instance.getVanishManager();
    }

    public static void startIntervals() {
        // Action bar
        actionBarInterval = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            if (vanishManager.isNoOneVanished()) return;

            for (Player p : vanishManager.getVanishedPlayers()) sendActionBar(p);
        }, 2, PluginConfig.actionBarRefreshRate);


        // Boss bar
        int rate = PluginConfig.bossBarRefreshRate;
        if (rate < 0) {
            if (vanishManager.isNoOneVanished()) return;
            for (Player p : vanishManager.getVanishedPlayers()) showBossBar(p);

            return;
        }

        bossBarInterval = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            if (vanishManager.isNoOneVanished()) return;

            for (Player p : vanishManager.getVanishedPlayers()) showBossBar(p);
        }, 2, rate);
    }

    public static void stopIntervals() {
        if (actionBarInterval != null) actionBarInterval.cancel();
        if (bossBarInterval != null) bossBarInterval.cancel();

        for (Player p : playerBossBars.keySet()) {
            p.hideBossBar(playerBossBars.get(p));
        }
        playerBossBars.clear();
    }

    public static void updateForPlayer(Player player) {
        if (!vanishManager.isVanished(player)) {
            sendEmptyActionBar(player);
            hideBossBar(player);
            return;
        }

        sendActionBar(player);
        showBossBar(player);
    }

    public static void sendActionBar(Player player) {
        if (!player.isOnline()) return;

        User user = Storage.getUser(player.getUniqueId());
        if (!user.getActionBar()) return;

        String message = PluginConfig.actionBarMessage;
        Component formatted = ColorFormatter.SERIALIZER.deserialize(ColorFormatter.parsePAPI(player, message));

        player.sendActionBar(formatted);
    }

    public static void sendEmptyActionBar(Player player) {
        if (!player.isOnline()) return;

        player.sendActionBar(Component.empty());
    }

    public static void showBossBar(Player player) {
        if (!player.isOnline()) return;

        User user = Storage.getUser(player.getUniqueId());
        if (!user.getBossBar()) return;

        String title = PluginConfig.bossBarTitle;
        Component formatted = ColorFormatter.SERIALIZER.deserialize(ColorFormatter.parsePAPI(player, title));

        if (playerBossBars.containsKey(player)) {
            BossBar bossBar = playerBossBars.get(player);
            bossBar.name(formatted);

            return;
        }

        BossBar bossBar = BossBar.bossBar(
                formatted,
                PluginConfig.bossBarProgress,
                PluginConfig.bossBarColor,
                PluginConfig.bossBarOverlay,
                PluginConfig.bossBarFlags
        );

        playerBossBars.put(player, bossBar);
        player.showBossBar(bossBar);
    }

    public static void hideBossBar(Player player) {
        BossBar bossBar = playerBossBars.get(player);
        if (bossBar == null) return;

        player.hideBossBar(bossBar);
        playerBossBars.remove(player);
    }
}

package pl.ynfuien.yvanish.config;

import com.comphenix.protocol.events.ListenerPriority;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.hooks.Hooks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PluginConfig {
    public static ConfigurationSection database = null;
    public static boolean onJoinEnabled = false;
    public static boolean onJoinSilent = true;
    public static boolean silentQuit = true;
    public static ListenerPriority packetListenersPriority = null;
    public static boolean silentChests = true;
    public static boolean silentSculk = true;
    public static boolean silentMessages = true;
    public static boolean noPickup = false;
    public static boolean noMobs = false;

    public static boolean actionBarEnabled = false;
    public static int actionBarRefreshRate = 5;
    public static String actionBarMessage = "";
    public static boolean bossBarEnabled = false;
    public static int bossBarRefreshRate = 200;
    public static String bossBarTitle = "";
    public static BossBar.Color bossBarColor = BossBar.Color.YELLOW;
    public static BossBar.Overlay bossBarOverlay = BossBar.Overlay.PROGRESS;
    public static float bossBarProgress = 1;
    public static Set<BossBar.Flag> bossBarFlags = new HashSet<>();
    public static boolean changeServerStatus = true;

    public static void load(ConfigurationSection config) {
        database = config.getConfigurationSection("database");

        ConfigurationSection vanish = config.getConfigurationSection("vanish");
        onJoinEnabled = vanish.getBoolean("on-join.enabled");
        onJoinSilent = vanish.getBoolean("on-join.silent");
        silentQuit = vanish.getBoolean("silent-quit");

        if (Hooks.isPluginEnabled(Hooks.Plugin.PROTOCOLLIB)) {
            String priority = vanish.getString("packet-listeners-priority");
            try {
                packetListenersPriority = ListenerPriority.valueOf(priority.toUpperCase());
            } catch (IllegalArgumentException e) {
                packetListenersPriority = ListenerPriority.HIGH;
                YLogger.warn("[Config] Provided priority '%s' for packet-listeners-priority is incorrect! Will be used priority HIGH.");
            }
        }

        ConfigurationSection defaultOptions = vanish.getConfigurationSection("default-options");
        silentChests = defaultOptions.getBoolean("silent-chests");
        silentSculk = defaultOptions.getBoolean("silent-sculk");
        silentMessages = defaultOptions.getBoolean("silent-messages");
        noPickup = defaultOptions.getBoolean("no-pickup");
        noMobs = defaultOptions.getBoolean("no-mobs");

        // Action bar
        actionBarEnabled = defaultOptions.getBoolean("action-bar.enabled");
        actionBarRefreshRate = defaultOptions.getInt("action-bar.refresh-rate");
        actionBarMessage = defaultOptions.getString("action-bar.message");


        // Boss bar
        ConfigurationSection bossBar = defaultOptions.getConfigurationSection("boss-bar");
        bossBarEnabled = bossBar.getBoolean("enabled");
        bossBarRefreshRate = bossBar.getInt("refresh-rate");
        bossBarTitle = bossBar.getString("title");

        String color = bossBar.getString("color");
        try {
            bossBarColor = BossBar.Color.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            bossBarColor = BossBar.Color.YELLOW;
            YLogger.warn("[Config] Provided color '%s' for boss-bar.color is incorrect! Will be used color YELLOW.");
        }

        String overlay = bossBar.getString("overlay");
        try {
            bossBarOverlay = BossBar.Overlay.valueOf(overlay.toUpperCase());
        } catch (IllegalArgumentException e) {
            bossBarOverlay = BossBar.Overlay.PROGRESS;
            YLogger.warn("[Config] Provided overlay '%s' for boss-bar.overlay is incorrect! Will be used overlay PROGRESS.");
        }

        bossBarProgress = (float) bossBar.getDouble("progress");
        if (bossBarProgress > 1) {
            bossBarProgress = 1;
            YLogger.warn("[Config] Provided progress '%s' for boss-bar.progress is too high! It will be set to 1.");
        }
        if (bossBarProgress < 0) {
            bossBarProgress = 0;
            YLogger.warn("[Config] Provided progress '%s' for boss-bar.progress is too low! It will be set to 0.");
        }

        bossBarFlags.clear();
        List<String> flagList = bossBar.getStringList("flags");
        for (String flag : flagList) {
            try {
                BossBar.Flag matchedFlag = BossBar.Flag.valueOf(flag.toUpperCase());
                bossBarFlags.add(matchedFlag);
            } catch (IllegalArgumentException e) {
                YLogger.warn("[Config] Provided flag '%s' in boss-bar.flags is incorrect! It won't be used.");
            }
        }

        changeServerStatus = config.getBoolean("change-server-status");
    }
}

package pl.ynfuien.yvanish.hooks;

import org.bukkit.Bukkit;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.hooks.essentials.EssentialsHook;
import pl.ynfuien.yvanish.hooks.luckperms.LuckPermsHook;
import pl.ynfuien.yvanish.hooks.placeholderapi.PlaceholderAPIHook;
import pl.ynfuien.yvanish.hooks.protocollib.ProtocolLibHook;

public class Hooks {
    private static ProtocolLibHook protocolLibHook = null;

    public static void load(YVanish instance) {
        // Register PlaceholderAPI hook
        if (isPluginEnabled(Plugin.PAPI)) {
            PlaceholderAPIHook papiHook = new PlaceholderAPIHook(instance);
            if (!papiHook.register()) {
                YLogger.error("[Hooks] Something went wrong while registering PlaceholderAPI hook!");
            }
            else {
                YLogger.info("[Hooks] Successfully registered hook for PlaceholderAPI!");
            }
        }

        // Register ProtocolLib hook
        if (isPluginEnabled(Plugin.PROTOCOLLIB)) {
            protocolLibHook = new ProtocolLibHook(instance);
            YLogger.info("[Hooks] Successfully registered hook for ProtocolLib!");
        } else {
            YLogger.error("[Hooks] ProtocolLib hasn't been found. Plugin won't have it's full functionality - silent chests will NOT work.");
        }


        // Register LuckPerms hook
        if (isPluginEnabled(Plugin.LUCKPERMS)) {
            new LuckPermsHook(instance);
            YLogger.info("[Hooks] Successfully registered hook for LuckPerms!");
        }


        // Register EssentialsX hook
        if (isPluginEnabled(Plugin.ESSENTIALS)) {
            new EssentialsHook(instance);
            YLogger.info("[Hooks] Successfully registered hook for EssentialsX!");
        }
    }

    public static boolean isProtocolLibHookEnabled() {
        return protocolLibHook != null;
    }

    public static boolean isPluginEnabled(Plugin plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin.getName());
    }

    public enum Plugin {
        PAPI("PlaceholderAPI"),
        PROTOCOLLIB("ProtocolLib"),
        LUCKPERMS("LuckPerms"),
        ESSENTIALS("Essentials");

        private final String name;
        Plugin(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

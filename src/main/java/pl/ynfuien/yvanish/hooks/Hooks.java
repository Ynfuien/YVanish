package pl.ynfuien.yvanish.hooks;

import org.bukkit.Bukkit;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.hooks.essentials.EssentialsHook;
import pl.ynfuien.yvanish.hooks.luckperms.LuckPermsHook;
import pl.ynfuien.yvanish.hooks.packetevents.PacketEventsHook;
import pl.ynfuien.yvanish.hooks.placeholderapi.PlaceholderAPIHook;

public class Hooks {
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

        // Register PacketEvents hook
        if (isPluginEnabled(Plugin.PACKETEVENTS)) {
            new PacketEventsHook(instance);
            YLogger.info("[Hooks] Successfully registered hook for PacketEvents!");
        }
        else {
            YLogger.error("[Hooks] PacketEvents hasn't been found. Plugin won't have it's full functionality - silent chests will NOT work.");
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

    public static boolean isPluginEnabled(Plugin plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin.getName());
    }

    public enum Plugin {
        PAPI("PlaceholderAPI"),
        LUCKPERMS("LuckPerms"),
        ESSENTIALS("Essentials"),
        PACKETEVENTS("packetevents");

        private final String name;
        Plugin(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

package pl.ynfuien.yvanish.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import pl.ynfuien.ydevlib.messages.YLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class ConfigHandler {
    private final Plugin plugin;
    private final HashMap<ConfigName, ConfigObject> configs = new HashMap<>();

    public ConfigHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean load(ConfigName name) {
        return load(name, true);
    }

    public boolean load(ConfigName name, boolean updating) {
        return load(name, updating, false);
    }

    public boolean load(ConfigName name, boolean updating, boolean useDefault) {
        return load(name, updating, useDefault, new ArrayList<>());
    }

    public boolean load(ConfigName name, boolean updating, boolean useDefault, List<String> ignoredKeys) {
        ConfigObject config = new ConfigObject(plugin, name);
        config.setUpdating(updating);
        config.setUseDefault(useDefault);
        config.setIgnoredKeys(ignoredKeys);

        if (config.load() == null) {
            logError("Fix the error and restart server.");
            getServer().getPluginManager().disablePlugin(plugin);
            return false;
        }

        configs.put(name, config);
        return true;
    }

    public void saveAll() {
        for (ConfigObject config : configs.values()) {
            config.save();
        }
    }

    public void reloadAll() {
        for (ConfigObject configObject : configs.values()) {
            String name = configObject.getName().getFileName();

            logInfo(String.format("Reloading config '%s'...", name));
            String oldConfig = configObject.getConfig().saveToString();
            if (configObject.load() == null) {
                logError(String.format("Config '%s' couldn't be reloaded!", name));
                try {
                    configObject.getConfig().loadFromString(oldConfig);
                } catch (InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
        }
    }

    public ConfigObject get(ConfigName name) {
        return configs.get(name);
    }


    public FileConfiguration getConfig(ConfigName name) {
        ConfigObject config = configs.get(name);
        if (config == null) return null;

        return config.getConfig();
    }


    //// Logging methods
    private void logError(String message) {
        YLogger.warn("[Configs] " + message);
    }
    private void logInfo(String message) {
        YLogger.info("[Configs] " + message);
    }
}

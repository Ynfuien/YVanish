package pl.ynfuien.yvanish.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.config.updater.ConfigUpdater;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConfigObject {
    private final Plugin plugin;
    private final ConfigName name;
    private final String fileName;
    private final File file;
    private FileConfiguration config = new YamlConfiguration();
    private final FileConfiguration defaultConfig;
    private boolean updating = true;
    private boolean useDefault = false;
    private List<String> ignoredKeys = new ArrayList<>();

    public ConfigObject(Plugin plugin, ConfigName name) {
        this.plugin = plugin;
        this.name = name;
        fileName = name.getFileName();

        InputStream resourceStream = plugin.getResource(fileName);
        if (resourceStream == null) {
            throw new IllegalArgumentException(String.format("Config with name '%s' doesn't exist in resource folder!", fileName));
        }

        file = new File(plugin.getDataFolder(), fileName);
        defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(resourceStream, StandardCharsets.UTF_8));
    }

    public FileConfiguration load() {
        logInfo("Loading...");

        // If file doesn't exist create it
        boolean factoryNew = false;
        if (!file.exists()) {
            logInfo("Config doesn't exist, creating new...");
            file.getParentFile().mkdirs();
            plugin.saveResource(fileName, false);
            factoryNew = true;
        }

        // Try loading config
        try {
            config.load(file);

            // Return if config was just created
            if (factoryNew) return config;
            // Return if config shouldn't be updated
            if (!updating) return config;

            if (update()) return config;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            logError("An error occurred while loading config from file!");
        }

        // If default config can't be used
        if (!useDefault) return null;

        logError("Will be used default one...");
        // Get default config and return it
        config = defaultConfig;
        return defaultConfig;
    }

    private boolean update() {
        // Get whether config is missing some keys
        boolean isMissingKeys = false;
        defaultKeysLoop : for (String key : defaultConfig.getKeys(true)) {
            if (config.contains(key)) continue;
            if (ignoredKeys.contains(key)) continue;

            for (String dontUpdateKey : ignoredKeys) {
                if (key.startsWith(dontUpdateKey+".")) {
                    continue defaultKeysLoop;
                }
            }

            isMissingKeys = true;
            break;
        }

        // Return if loaded config isn't missing any key
        if (!isMissingKeys) return true;

        logError("Config is missing some keys, updating..");

        // Date for old config name
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm");

        // Split name at dot
        String[] split = fileName.split("\\.");
        // Create name for old config in format: <name>-old_<date>.<extension>
        String oldConfigName = String.format("%s-old_%s.%s", split[0], formatter.format(date), split[1]);

        logError("Old file will be saved as " + oldConfigName);

        // Create file object for old config
        File oldConfig = new File(plugin.getDataFolder(), oldConfigName);

        // Copy existing config to old config path
        try {
            Files.copy(file.toPath(), oldConfig.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            logError("Couldn't backup current config.");
            return false;
        }

        // Update existing config
        try {
            ConfigUpdater.update(plugin, fileName, file);
        } catch (IOException e) {
            e.printStackTrace();
            logError("Couldn't update config file.");
            return false;
        }

        // Load updated config
        try {
            config.load(file);
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            logError("Couldn't load updated config.");
            return false;
        }
    }


    public boolean save() {
        if (config == null) return false;

        try {
            ConfigUpdater.save(plugin, file, config);
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();

            logError("Couldn't save config!");
            return false;
        }
    }

    public boolean reload() {
        logInfo("Reloading...");

        String oldConfig = config.saveToString();
        if (load() == null) {
            logError(String.format("Config couldn't be reloaded!", name));
            try {
                config.loadFromString(oldConfig);
            } catch (InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
            return false;
        }

        logInfo("Reloaded!");
        return true;
    }


    //// Log methods
    private void logError(String message) {
        YLogger.warn(String.format("[Config] [%s] %s", name.getFileName(), message));
    }
    private void logInfo(String message) {
        YLogger.info(String.format("[Config] [%s] %s", name.getFileName(), message));
    }


    // Getters
    public ConfigName getName() {
        return name;
    }
    public File getFile() {
        return file;
    }
    public FileConfiguration getConfig() {
        return config;
    }
    public FileConfiguration getDefaultConfig() {
        return defaultConfig;
    }
    public boolean isUpdating() {
        return updating;
    }
    public boolean isUseDefault() {
        return useDefault;
    }
    public List<String> getIgnoredKeys() {
        return ignoredKeys;
    }


    // Setters
    public void setUpdating(boolean updating) {
        this.updating = updating;
    }
    public void setUseDefault(boolean useDefault) {
        this.useDefault = useDefault;
    }
    public void setIgnoredKeys(@NotNull List<String> ignoredKeys) {
        this.ignoredKeys = ignoredKeys;
    }
}

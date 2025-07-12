package pl.ynfuien.yvanish;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.ynfuien.ydevlib.config.ConfigHandler;
import pl.ynfuien.ydevlib.config.ConfigObject;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.commands.main.MainCommand;
import pl.ynfuien.yvanish.commands.vanish.VanishCommand;
import pl.ynfuien.yvanish.commands.vanishoptions.VanishOptionsCommand;
import pl.ynfuien.yvanish.config.ConfigName;
import pl.ynfuien.yvanish.config.PluginConfig;
import pl.ynfuien.yvanish.core.ActionAndBossBars;
import pl.ynfuien.yvanish.core.ChestableUtils;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.core.mobsstaring.StopMobsStaring;
import pl.ynfuien.yvanish.data.Database;
import pl.ynfuien.yvanish.data.MysqlDatabase;
import pl.ynfuien.yvanish.data.SqliteDatabase;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.hooks.Hooks;
import pl.ynfuien.yvanish.hooks.packetevents.PacketEventsHook;
import pl.ynfuien.yvanish.listeners.fakejoin.VanishToggleListener;
import pl.ynfuien.yvanish.listeners.joinquit.PlayerJoinListener;
import pl.ynfuien.yvanish.listeners.joinquit.PlayerLoginListener;
import pl.ynfuien.yvanish.listeners.joinquit.PlayerQuitListener;
import pl.ynfuien.yvanish.listeners.nomobs.EntityTargetLivingEntityListener;
import pl.ynfuien.yvanish.listeners.nopickup.EntityPickupItemListener;
import pl.ynfuien.yvanish.listeners.nopickup.PlayerPickupExperienceListener;
import pl.ynfuien.yvanish.listeners.pvp.EntityDamageByEntityListener;
import pl.ynfuien.yvanish.listeners.serverlist.PaperServerListPingListener;
import pl.ynfuien.yvanish.listeners.silentchests.InventoryCloseListener;
import pl.ynfuien.yvanish.listeners.silentchests.PlayerChunkLoadListener;
import pl.ynfuien.yvanish.listeners.silentchests.PlayerInteractChestableListener;
import pl.ynfuien.yvanish.listeners.silentmessages.PlayerAdvancementDoneListener;
import pl.ynfuien.yvanish.listeners.silentmessages.PlayerDeathListener;
import pl.ynfuien.yvanish.listeners.silentsculk.BlockReceiveGameListener;
import pl.ynfuien.yvanish.listeners.silentsculk.PlayerInteractSculkListener;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.HashMap;

public final class YVanish extends JavaPlugin {
    private static YVanish instance;
    private final VanishManager vanishManager = new VanishManager(this);
    private final ConfigHandler configHandler = new ConfigHandler(this);
    private Database database = null;
    private ConfigObject config;

    @Override
    public void onLoad() {
        if (Bukkit.getPluginManager().getPlugin("packetevents") == null) return;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;
        YLogger.setup("<dark_aqua>[<aqua>Y<gradient:white:#ADE1FF>Vanish</gradient><dark_aqua>] <white>", getComponentLogger());

        // Configs
        loadConfigs();
        loadLang();

        config = configHandler.getConfigObject(ConfigName.CONFIG);
        PluginConfig.load(config.getConfig());

        // Database
        database = getDatabase(PluginConfig.database);
        if (database == null || !database.setup(PluginConfig.database)) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Storage.setup(this);

        // Commands, listeners
        setupCommands();
        registerListeners();

        Hooks.load(this);

        ChestableUtils.setupCatDetection(this);

        ActionAndBossBars.setup(this);
        ActionAndBossBars.startIntervals();

        StopMobsStaring.setup(this);
        StopMobsStaring.startInterval();

        // BStats
        new Metrics(this, 21793);

        YLogger.info("Plugin successfully <green>enabled<white>!");
    }


    @Override
    public void onDisable() {
        if (PacketEventsHook.isEnabled()) PacketEvents.getAPI().terminate();

        if (database != null) database.close();

        ActionAndBossBars.stopIntervals();
        StopMobsStaring.stopInterval();

        YLogger.info("Plugin successfully <red>disabled<white>!");
    }

    private void setupCommands() {
        HashMap<String, CommandExecutor> commands = new HashMap<>() {{
            put("yvanish", new MainCommand(instance, "reload"));
            put("vanish", new VanishCommand(instance, "vanish"));
            put("vanishoptions", new VanishOptionsCommand(instance, "options"));
        }};

        for (String name : commands.keySet()) {
            CommandExecutor cmd = commands.get(name);

            getCommand(name).setExecutor(cmd);
            getCommand(name).setTabCompleter((TabCompleter) cmd);
        }
    }

    private void registerListeners() {
        Listener[] listeners = new Listener[] {
                // Join/quit
                new PlayerJoinListener(this),
                new PlayerLoginListener(this),
                new PlayerQuitListener(this),
                // Silent chests
                new InventoryCloseListener(this),
                new PlayerInteractChestableListener(this),
                new PlayerChunkLoadListener(this),
                // Silent sculk
                new BlockReceiveGameListener(this),
                new PlayerInteractSculkListener(this),
                // Silent messages
                new PlayerAdvancementDoneListener(this),
                new PlayerDeathListener(this),
                // No pickup
                new EntityPickupItemListener(this),
                new PlayerPickupExperienceListener(this),
                // No mobs
                new EntityTargetLivingEntityListener(this),
                // Vanish pvp
                new EntityDamageByEntityListener(this),
                // Server list ping
                new PaperServerListPingListener(this),
                // Fake join/quit
                new VanishToggleListener(this)
        };

        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }


    private Database getDatabase(ConfigurationSection config) {
        String type = config.getString("type");
        if (type.equalsIgnoreCase("sqlite")) return new SqliteDatabase();
        else if (type.equalsIgnoreCase("mysql")) return new MysqlDatabase();

        YLogger.error("Database type is incorrect! Available database types: sqlite, mysql");
        return null;
    }

    private void loadLang() {
        // Get lang config
        FileConfiguration config = configHandler.getConfig(ConfigName.LANG);

        // Reload lang
        Lang.loadLang(config);
    }

    private void loadConfigs() {
        configHandler.load(ConfigName.CONFIG);
        configHandler.load(ConfigName.LANG, true, true);
    }

    public boolean reloadPlugin() {
        // Reload all configs
        boolean fullSuccess = configHandler.reloadAll();

        PluginConfig.load(config.getConfig());

        // Reload lang
        instance.loadLang();

        ActionAndBossBars.stopIntervals();
        ActionAndBossBars.startIntervals();

        StopMobsStaring.stopInterval();
        StopMobsStaring.startInterval();

        return fullSuccess;
    }

    public static YVanish getInstance() {
        return instance;
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }


    public Database getDatabase() {
        return database;
    }

    public enum Permissions {
        VANISH_OTHERS("yvanish.vanish.others"),
        VANISH_ON_JOIN("yvanish.vanish.on-join"),
        VANISH_SEE("yvanish.vanish.see"),
        VANISH_PVP("yvanish.vanish.pvp");

        private final String permission;

        Permissions(String permission) {
            this.permission = permission;
        }

        public String get() {
            return permission;
        }
    }
}

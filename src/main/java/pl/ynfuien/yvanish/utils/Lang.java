package pl.ynfuien.yvanish.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.ydevlib.messages.Messenger;
import pl.ynfuien.ydevlib.messages.YLogger;

import java.util.HashMap;
import java.util.List;

public class Lang {
    private static String prefix;
    private static FileConfiguration langConfig;

    public static void loadLang(FileConfiguration langConfig) {
        Lang.langConfig = langConfig;
        prefix = Message.PREFIX.get();
    }

    // Gets message by message enum
    @Nullable
    public static String get(Message message) {
        return get(message.getName());
    }
    // Gets message by path
    @Nullable
    public static String get(String path) {
        return langConfig.getString(path);
    }
    // Gets message by path and replaces placeholders
    @Nullable
    public static String get(String path, HashMap<String, Object> placeholders) {
        placeholders.put("prefix", prefix);
        // Return message with used placeholders
        return Messenger.parsePluginPlaceholders(langConfig.getString(path), placeholders);
    }

    public static void sendMessage(CommandSender sender, Message message) {
        sendMessage(sender, message.getName());
    }
    public static void sendMessage(CommandSender sender, String path) {
        sendMessage(sender, path, new HashMap<>());
    }
    public static void sendMessage(CommandSender sender, String path, HashMap<String, Object> placeholders) {
        List<String> messages;

        if (!langConfig.isSet(path)) {
            YLogger.error(String.format("There is no message '%s'!", path));
            return;
        }

        if (langConfig.isList(path)) messages = langConfig.getStringList(path);
        else messages = List.of(langConfig.getString(path));

        for (String message : messages) {
            // Return if message is empty
            if (message.isEmpty()) continue;

            // Get message with used placeholders
            placeholders.put("prefix", prefix);

            Messenger.send(sender, message, placeholders);
        }
    }

    // Messages enum
    public enum Message {
        PREFIX,

        // Main command
        COMMAND_MAIN_USAGE,
        COMMAND_MAIN_RELOAD_FAIL,
        COMMAND_MAIN_RELOAD_SUCCESS,
        COMMAND_MAIN_VERSION,

        // Vanish
        COMMAND_VANISH_USAGE_OTHERS,
        COMMAND_VANISH_FAIL_PLAYER_DOESNT_EXIST,
        COMMAND_VANISH_SUCCESS,
        COMMAND_VANISH_SUCCESS_OTHER,
        COMMAND_VANISH_SUCCESS_RECEIVE,
        COMMAND_VANISH_ENABLED,
        COMMAND_VANISH_DISABLED,

        VANISH_ON_JOIN,

        // Vanish options
        COMMAND_VANISHOPTIONS_USAGE_OTHERS,
        COMMAND_VANISHOPTIONS_WRONG_OPTION,
        COMMAND_VANISHOPTIONS_LIST_HEADER,
        COMMAND_VANISHOPTIONS_LIST_ENTRY,
        COMMAND_VANISHOPTIONS_LIST_ENTRY_STATE_ENABLED,
        COMMAND_VANISHOPTIONS_LIST_ENTRY_STATE_DISABLED,
        COMMAND_VANISHOPTIONS_LIST_EMPTY,
        COMMAND_VANISHOPTIONS_LIST_CONSOLE_HEADER,
        COMMAND_VANISHOPTIONS_LIST_CONSOLE_ENTRY,
        COMMAND_VANISHOPTIONS_DESCRIPTION_SILENT_CHESTS,
        COMMAND_VANISHOPTIONS_DESCRIPTION_SILENT_SCULK,
        COMMAND_VANISHOPTIONS_DESCRIPTION_SILENT_MESSAGES,
        COMMAND_VANISHOPTIONS_DESCRIPTION_NO_PICKUP,
        COMMAND_VANISHOPTIONS_DESCRIPTION_NO_MOBS,
        COMMAND_VANISHOPTIONS_DESCRIPTION_ACTION_BAR,
        COMMAND_VANISHOPTIONS_DESCRIPTION_BOSS_BAR,
        COMMAND_VANISHOPTIONS_FAIL_PLAYER_ISNT_ONLINE,
        COMMAND_VANISHOPTIONS_SUCCESS_ENABLE,
        COMMAND_VANISHOPTIONS_SUCCESS_DISABLE,
        COMMAND_VANISHOPTIONS_SUCCESS_ENABLE_OTHER,
        COMMAND_VANISHOPTIONS_SUCCESS_DISABLE_OTHER,
        ;


        // Gets message name
        public String getName() {
            return name().toLowerCase().replace('_', '-');
        }

        // Gets message
        public String get() {
            return Lang.get(getName());
        }
        // Gets message with replaced normal placeholders
        public String get(HashMap<String, Object> placeholders) {
            return Lang.get(getName(), placeholders);
        }
        // Gets component message with replaced all placeholders
        public Component get(CommandSender sender, HashMap<String, Object> placeholders) {
            return Messenger.parseMessage(sender, Lang.get(getName()), placeholders);
        }

        // Sends message
        public void send(CommandSender sender) {
            Lang.sendMessage(sender, getName());
        }
        // Sends message with replaced placeholders
        public void send(CommandSender sender, HashMap<String, Object> placeholders) {
            Lang.sendMessage(sender, getName(), placeholders);
        }
    }
}

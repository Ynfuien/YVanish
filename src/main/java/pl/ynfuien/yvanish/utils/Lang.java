package pl.ynfuien.yvanish.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import pl.ynfuien.ydevlib.messages.LangBase;
import pl.ynfuien.ydevlib.messages.Messenger;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;

import java.util.HashMap;

public class Lang extends LangBase {
    public enum Message implements LangBase.Message {
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
        COMMAND_VANISHOPTIONS_DESCRIPTION_FAKE_JOIN,
        COMMAND_VANISHOPTIONS_FAIL_PLAYER_ISNT_ONLINE,
        COMMAND_VANISHOPTIONS_SUCCESS_ENABLE,
        COMMAND_VANISHOPTIONS_SUCCESS_DISABLE,
        COMMAND_VANISHOPTIONS_SUCCESS_ENABLE_OTHER,
        COMMAND_VANISHOPTIONS_SUCCESS_DISABLE_OTHER,

        // Fake join/quit
        FAKE_JOIN,
        FAKE_QUIT
        ;

        /**
         * Gets name/path of this message.
         */
        @Override
        public String getName() {
            return name().toLowerCase().replace('_', '-');
        }

        /**
         * Gets original unformatted message.
         */
        public String get() {
            return Lang.get(getName());
        }

        /**
         * Gets message with parsed:
         * - {prefix} placeholder
         * - additional provided placeholders
         */
        public String get(HashMap<String, Object> placeholders) {
            return Lang.get(getName(), placeholders);
        }

        /**
         * Gets message with parsed:
         * - PlaceholderAPI
         * - {prefix} placeholder
         * - additional provided placeholders
         */
        public String get(CommandSender sender, HashMap<String, Object> placeholders) {
            return ColorFormatter.parsePAPI(sender, Lang.get(getName(), placeholders));
        }

        /**
         * Gets message as component with parsed:
         * - MiniMessage
         * - PlaceholderAPI
         * - {prefix} placeholder
         * - additional provided placeholders
         */
        public Component getComponent(CommandSender sender, HashMap<String, Object> placeholders) {
            return Messenger.parseMessage(sender, Lang.get(getName()), placeholders);
        }

        /**
         * Sends this message to provided sender.<br/>
         * Parses:<br/>
         * - MiniMessage<br/>
         * - PlaceholderAPI<br/>
         * - {prefix} placeholder
         */
        public void send(CommandSender sender) {
            this.send(sender, new HashMap<>());
        }

        /**
         * Sends this message to provided sender.<br/>
         * Parses:<br/>
         * - MiniMessage<br/>
         * - PlaceholderAPI<br/>
         * - {prefix} placeholder<br/>
         * - additional provided placeholders
         */
        public void send(CommandSender sender, HashMap<String, Object> placeholders) {
            Lang.sendMessage(sender, this, placeholders);
        }
    }
}
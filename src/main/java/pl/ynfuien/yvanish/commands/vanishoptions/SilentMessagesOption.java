package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.UUID;

public class SilentMessagesOption extends VanishOption {
    public SilentMessagesOption(String permissionBase) {
        super("silent-messages", "sm", Lang.Message.COMMAND_VANISHOPTIONS_DESCRIPTION_SILENT_MESSAGES, permissionBase);
    }

    @Override
    public boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();

        User user = Storage.getUser(uuid);
        if (user.getSilentMessages()) {
            user.setSilentMessages(false);
            Storage.updateUser(uuid, user);
            return false;
        }

        user.setSilentMessages(true);
        Storage.updateUser(uuid, user);
        return true;
    }

    @Override
    public boolean getState(User user) {
        return user.getSilentMessages();
    }
}

package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.UUID;

public class SilentChestsOption extends VanishOption {
    public SilentChestsOption(String permissionBase) {
        super("silent-chests", "sc", Lang.Message.COMMAND_VANISHOPTIONS_DESCRIPTION_SILENT_CHESTS, permissionBase);
    }

    @Override
    public boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();

        User user = Storage.getUser(uuid);
        if (user.getSilentChests()) {
            user.setSilentChests(false);
            Storage.updateUser(uuid, user);
            return false;
        }

        user.setSilentChests(true);
        Storage.updateUser(uuid, user);
        return true;
    }

    @Override
    public boolean getState(User user) {
        return user.getSilentChests();
    }
}

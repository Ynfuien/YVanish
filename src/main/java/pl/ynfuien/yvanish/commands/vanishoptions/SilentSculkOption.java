package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.UUID;

public class SilentSculkOption extends VanishOption {
    public SilentSculkOption(String permissionBase) {
        super("silent-sculk", "ss", Lang.Message.COMMAND_VANISHOPTIONS_DESCRIPTION_SILENT_SCULK, permissionBase);
    }

    @Override
    public boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();

        User user = Storage.getUser(uuid);
        if (user.getSilentSculk()) {
            user.setSilentSculk(false);
            Storage.updateUser(uuid, user);
            return false;
        }

        user.setSilentSculk(true);
        Storage.updateUser(uuid, user);
        return true;
    }

    @Override
    public boolean getState(User user) {
        return user.getSilentSculk();
    }
}

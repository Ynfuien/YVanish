package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.UUID;

public class NoMobsOption extends VanishOption {
    public NoMobsOption(String permissionBase) {
        super("no-mobs", "nm", Lang.Message.COMMAND_VANISHOPTIONS_DESCRIPTION_NO_MOBS, permissionBase);
    }

    @Override
    public boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();

        User user = Storage.getUser(uuid);
        if (user.getNoMobs()) {
            user.setNoMobs(false);
            Storage.updateUser(uuid, user);
            return false;
        }

        user.setNoMobs(true);
        Storage.updateUser(uuid, user);
        return true;
    }

    @Override
    public boolean getState(User user) {
        return user.getNoMobs();
    }
}

package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.UUID;

public class NoPickupOption extends VanishOption {
    public NoPickupOption(String permissionBase) {
        super("no-pickup", "np", Lang.Message.COMMAND_VANISHOPTIONS_DESCRIPTION_NO_PICKUP, permissionBase);
    }

    @Override
    public boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();

        User user = Storage.getUser(uuid);
        if (user.getNoPickup()) {
            user.setNoPickup(false);
            Storage.updateUser(uuid, user);
            return false;
        }

        user.setNoPickup(true);
        Storage.updateUser(uuid, user);
        return true;
    }

    @Override
    public boolean getState(User user) {
        return user.getNoPickup();
    }
}

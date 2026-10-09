package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.UUID;

public class NoStepOption extends VanishOption {
    public NoStepOption(String permissionBase) {
        super("no-step", "ns", Lang.Message.COMMAND_VANISHOPTIONS_DESCRIPTION_NO_STEP, permissionBase);
    }

    @Override
    public boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();

        User user = Storage.getUser(uuid);
        if (user.getNoStep()) {
            user.setNoStep(false);
            Storage.updateUser(uuid, user);
            return false;
        }

        user.setNoStep(true);
        Storage.updateUser(uuid, user);
        return true;
    }

    @Override
    public boolean getState(User user) {
        return user.getNoStep();
    }
}

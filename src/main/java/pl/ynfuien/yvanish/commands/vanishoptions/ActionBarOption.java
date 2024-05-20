package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.core.ActionAndBossBars;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.UUID;

public class ActionBarOption extends VanishOption {
    public ActionBarOption(String permissionBase) {
        super("action-bar", "ab", Lang.Message.COMMAND_VANISHOPTIONS_DESCRIPTION_ACTION_BAR, permissionBase);
    }

    @Override
    public boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();

        User user = Storage.getUser(uuid);
        if (user.getActionBar()) {
            user.setActionBar(false);
            Storage.updateUser(uuid, user);
            ActionAndBossBars.sendEmptyActionBar(player);
            return false;
        }

        user.setActionBar(true);
        Storage.updateUser(uuid, user);
        ActionAndBossBars.sendActionBar(player);
        return true;
    }

    @Override
    public boolean getState(User user) {
        return user.getActionBar();
    }
}

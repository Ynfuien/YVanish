package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.UUID;

public class OnJoinOption extends VanishOption {
    public OnJoinOption(String permissionBase) {
        super("on-join", "oj", Lang.Message.COMMAND_VANISHOPTIONS_DESCRIPTION_ON_JOIN, permissionBase);
    }

    @Override
    public boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();

        User user = Storage.getUser(uuid);
        if (user.getOnJoin()) {
            user.setOnJoin(false);
            Storage.updateUser(uuid, user);
            return false;
        }

        user.setOnJoin(true);
        Storage.updateUser(uuid, user);
        return true;
    }

    @Override
    public boolean getState(User user) {
        return user.getOnJoin();
    }
}
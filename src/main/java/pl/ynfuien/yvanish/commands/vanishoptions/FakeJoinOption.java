package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.UUID;

public class FakeJoinOption extends VanishOption {
    public FakeJoinOption(String permissionBase) {
        super("fake-join", "fj", Lang.Message.COMMAND_VANISHOPTIONS_DESCRIPTION_FAKE_JOIN, permissionBase);
    }

    @Override
    public boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();

        User user = Storage.getUser(uuid);
        if (user.getFakeJoin()) {
            user.setFakeJoin(false);
            Storage.updateUser(uuid, user);
            return false;
        }

        user.setFakeJoin(true);
        Storage.updateUser(uuid, user);
        return true;
    }

    @Override
    public boolean getState(User user) {
        return user.getFakeJoin();
    }
}

package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.ActionAndBossBars;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.UUID;

public class BossBarOption extends VanishOption {
    private final VanishManager vanishManager;

    public BossBarOption(String permissionBase) {
        super("boss-bar", "bb", Lang.Message.COMMAND_VANISHOPTIONS_DESCRIPTION_BOSS_BAR, permissionBase);

        vanishManager = YVanish.getInstance().getVanishManager();
    }

    @Override
    public boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();

        User user = Storage.getUser(uuid);
        if (user.getBossBar()) {
            user.setBossBar(false);
            Storage.updateUser(uuid, user);
            ActionAndBossBars.hideBossBar(player);
            return false;
        }

        user.setBossBar(true);
        Storage.updateUser(uuid, user);
        ActionAndBossBars.updateForPlayer(player);
        return true;
    }

    @Override
    public boolean getState(User user) {
        return user.getBossBar();
    }
}

package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.List;
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
        clearMobsTarget(player);
        return true;
    }

    @Override
    public boolean getState(User user) {
        return user.getNoMobs();
    }

    public static void clearMobsTarget(Player player) {
        if (!Storage.getUser(player.getUniqueId()).getNoMobs()) return;

        int range = 256;
        List<Entity> nearby = player.getNearbyEntities(range, range, range);
        for (Entity entity : nearby) {
            if (!(entity instanceof Mob mob)) continue;

            LivingEntity target = mob.getTarget();
            if (target == null) continue;

            if (!target.equals(player)) continue;
            mob.setTarget(null);
        }
    }
}

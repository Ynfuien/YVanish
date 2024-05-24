package pl.ynfuien.yvanish.commands.vanish;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.commands.YCommand;
import pl.ynfuien.yvanish.core.ActionAndBossBars;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VanishCommand extends YCommand {
    private static final Lang.Message ENABLED_MSG = Lang.Message.COMMAND_VANISH_ENABLED;
    private static final Lang.Message DISABLED_MSG = Lang.Message.COMMAND_VANISH_DISABLED;
    private final VanishManager vanishManager;

    public VanishCommand(YVanish instance, String permission) {
        super(instance, permission);
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        if (args.length == 0 || !sender.hasPermission(YVanish.Permissions.VANISH_OTHERS.get())) {
            if (!(sender instanceof Player p)) {
                Lang.Message.COMMAND_VANISH_USAGE_OTHERS.send(sender, placeholders);
                return;
            }

            toggleVanish(p, placeholders, "");
            ActionAndBossBars.updateForPlayer(p);

            Lang.Message.COMMAND_VANISH_SUCCESS.send(sender, placeholders);
            return;
        }


        Player player = Bukkit.getPlayer(args[0]);
        placeholders.put("player", args[0]);
        // Offline player or hidden
        boolean playerOffline = player == null || (sender instanceof Player p && !p.canSee(player));

        if (playerOffline) {
            Lang.Message.COMMAND_VANISH_FAIL_PLAYER_DOESNT_EXIST.send(sender, placeholders);
            return;
        }

        addPlayerPlaceholders(placeholders, player);

        toggleVanish(player, placeholders, args.length > 1 ? args[1].toLowerCase() : "");
        ActionAndBossBars.updateForPlayer(player);

        Lang.Message.COMMAND_VANISH_SUCCESS_OTHER.send(sender, placeholders);
        if (args.length > 1 && args[1].equalsIgnoreCase("-s")) return;
        if (sender.equals(player)) return;

        Lang.Message.COMMAND_VANISH_SUCCESS_RECEIVE.send(player, placeholders);
    }

    private boolean toggleVanish(Player p, HashMap<String, Object> ph, String arg) {
        boolean enable = false;
        boolean disable = false;

        if (arg.equals("enable")) enable = true;
        else if (arg.equals("disable")) disable = true;

        if (!enable && !disable) {
            if (vanishManager.isVanished(p)) {
                vanishManager.unvanish(p);
                ph.put("action", DISABLED_MSG.get(ph));
                return false;
            }

            vanishManager.vanish(p);
            ph.put("action", ENABLED_MSG.get(ph));
            return true;
        }

        if (enable) {
            vanishManager.vanish(p);
            ph.put("action", ENABLED_MSG.get(ph));
            return true;
        }

        vanishManager.unvanish(p);
        ph.put("action", DISABLED_MSG.get(ph));
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (!sender.hasPermission(YVanish.Permissions.VANISH_OTHERS.get())) return completions;
        if (args.length > 2) return completions;

        // Player list
        if (args.length == 1) return getPlayerListCompletions(sender, args[0]);


        String arg2 = args[1].toLowerCase();
        for (String tab : new String[] {"enable", "disable"}) {
            if (tab.startsWith(arg2)) completions.add(tab);
        }
        return completions;
    }
}

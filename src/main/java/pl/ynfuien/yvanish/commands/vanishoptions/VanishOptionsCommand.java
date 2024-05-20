package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.commands.YCommand;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VanishOptionsCommand extends YCommand {
    private final VanishManager vanishManager;
    public final String PERMISSION_OTHERS = permissionBase + ".others";
    private static final Lang.Message STATE_ENABLED = Lang.Message.COMMAND_VANISHOPTIONS_LIST_ENTRY_STATE_ENABLED;
    private static final Lang.Message STATE_DISABLED = Lang.Message.COMMAND_VANISHOPTIONS_LIST_ENTRY_STATE_DISABLED;

    private final VanishOption[] vanishOptions = new VanishOption[] {
            new SilentChestsOption(permissionBase),
            new SilentSculkOption(permissionBase),
            new SilentMessagesOption(permissionBase),
            new NoPickupOption(permissionBase),
            new NoMobsOption(permissionBase),
            new ActionBarOption(permissionBase),
            new BossBarOption(permissionBase)
    };

    public VanishOptionsCommand(YVanish instance, String permission) {
        super(instance, permission);
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        // Option list
        if (args.length == 0) {
            sendOptionList(sender, placeholders);
            return;
        }

        // Selected option
        String arg1 = args[0].toLowerCase();
        for (VanishOption option : vanishOptions) {
            if (!option.getName().equals(arg1) && !option.getAlias().equals(arg1)) continue;

            if (!sender.hasPermission(option.getPermission())) break;

            String[] argsLeft = Arrays.copyOfRange(args, 1, args.length);
            runVanishOption(option, sender, argsLeft, placeholders);
            return;
        }

        // Usage messages
        if (sender instanceof Player) {
            Lang.Message.COMMAND_VANISHOPTIONS_USAGE.send(sender, placeholders);
        }

        if (sender.hasPermission(PERMISSION_OTHERS)) {
            Lang.Message.COMMAND_VANISHOPTIONS_USAGE_OTHERS.send(sender, placeholders);
        }
    }

    private void sendOptionList(CommandSender sender, HashMap<String, Object> placeholders) {
        // Send option list
        List<VanishOption> permittedOptions = Arrays.stream(vanishOptions).filter((option) -> sender.hasPermission(option.getPermission())).toList();
        if (permittedOptions.isEmpty()) {
            Lang.Message.COMMAND_VANISHOPTIONS_LIST_EMPTY.send(sender, placeholders);
            return;
        }

        Player p = sender instanceof Player player ? player : null;

        placeholders.put("options-count", permittedOptions.size());
        if (p != null) Lang.Message.COMMAND_VANISHOPTIONS_LIST_HEADER.send(sender, placeholders);
        else  Lang.Message.COMMAND_VANISHOPTIONS_LIST_CONSOLE_HEADER.send(sender, placeholders);

        User user = p == null ? null : Storage.getUser(p.getUniqueId());
        for (VanishOption option : permittedOptions) {
            if (user != null) placeholders.put("option-state", option.getState(user) ? STATE_ENABLED.get() : STATE_DISABLED.get());
            placeholders.put("option-name", option.getName());
            placeholders.put("option-alias", option.getAlias());
            placeholders.put("option-description", option.getDescription().get());

            if (user != null) Lang.Message.COMMAND_VANISHOPTIONS_LIST_ENTRY.send(sender, placeholders);
            else Lang.Message.COMMAND_VANISHOPTIONS_LIST_CONSOLE_ENTRY.send(sender, placeholders);
        }
    }

    private void runVanishOption(VanishOption option, CommandSender sender, String[] args, HashMap<String, Object> placeholders) {
        placeholders.put("option-name", option.getName());
        placeholders.put("option-alias", option.getAlias());
        placeholders.put("option-description", option.getDescription().get());

        if (args.length == 0 || !sender.hasPermission(PERMISSION_OTHERS)) {
            if (!(sender instanceof Player p)) {
                Lang.Message.COMMAND_VANISHOPTIONS_USAGE_OTHERS.send(sender, placeholders);
                return;
            }

            boolean result = option.toggle(p);
            if (result) Lang.Message.COMMAND_VANISHOPTIONS_SUCCESS_ENABLE.send(sender, placeholders);
            else Lang.Message.COMMAND_VANISHOPTIONS_SUCCESS_DISABLE.send(sender, placeholders);
            return;
        }

        Player p = Bukkit.getPlayer(args[0]);
        placeholders.put("player", args[0]);
        if (p == null) {
            Lang.Message.COMMAND_VANISHOPTIONS_FAIL_PLAYER_ISNT_ONLINE.send(sender, placeholders);
            return;
        }

        YCommand.addPlayerPlaceholders(placeholders, p);

        boolean result = option.toggle(p);
        if (result) Lang.Message.COMMAND_VANISHOPTIONS_SUCCESS_ENABLE_OTHER.send(sender, placeholders);
        else Lang.Message.COMMAND_VANISHOPTIONS_SUCCESS_DISABLE_OTHER.send(sender, placeholders);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length > 2) return completions;
        if (args.length > 1 && !sender.hasPermission(PERMISSION_OTHERS)) return completions;

        List<VanishOption> permittedOptions = Arrays.stream(vanishOptions).filter((option) -> sender.hasPermission(option.getPermission())).toList();
        if (permittedOptions.isEmpty()) return completions;


        // Vanish options
        String arg1 = args[0].toLowerCase();
        if (args.length == 1) {
            for (VanishOption option : permittedOptions) {
                String name = option.getName();

                if (name.startsWith(arg1)) completions.add(name);;
            }

            return completions;
        }

        // Player list
        return YCommand.getPlayerListCompletions(sender, args[1]);
    }
}

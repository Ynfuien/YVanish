package pl.ynfuien.yvanish.commands.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.commands.Subcommand;
import pl.ynfuien.yvanish.commands.YCommand;
import pl.ynfuien.yvanish.utils.Lang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainCommand extends YCommand {
    public final Subcommand[] subcommands = {
            new ReloadSubcommand(this),
            new VersionSubcommand(this)
    };

    public MainCommand(YVanish instance, String permission) {
        super(instance, permission);
    }


    @Override
    protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders) {
        // Run help subcommand if none is provided
        if (args.length == 0) {
            Lang.Message.COMMAND_MAIN_USAGE.send(sender, placeholders);
            return;
        }

        // Loop through and check every subcommand
        String arg1 = args[0].toLowerCase();
        for (Subcommand cmd : subcommands) {
            if (!cmd.name().equals(arg1)) continue;

            String[] argsLeft = Arrays.copyOfRange(args, 1, args.length);
            cmd.run(sender, argsLeft, placeholders);
            return;
        }

        Lang.Message.COMMAND_MAIN_USAGE.send(sender, placeholders);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return tabCompleteSubcommands(sender, subcommands, args);
    }
}

package pl.ynfuien.yvanish.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yvanish.YVanish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class YCommand implements CommandExecutor, TabCompleter {
    protected final YVanish instance;
    public final String permissionBase;

    public YCommand(YVanish instance, String permission) {
        this.instance = instance;
        this.permissionBase = String.format("%s.%s", instance.getName().toLowerCase(), permission);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        HashMap<String, Object> placeholders = new HashMap<>() {{put("command", label);}};

        run(sender, args, placeholders);
        return true;
    }

    abstract protected void run(@NotNull CommandSender sender, @NotNull String[] args, @NotNull HashMap<String, Object> placeholders);

    //// Placeholder methods, for messages
    public static void addPlayerPlaceholders(HashMap<String, Object> phs, OfflinePlayer p) {
        addPlayerPlaceholders(phs, p, null);
    }
    public static void addPlayerPlaceholders(HashMap<String, Object> phs, OfflinePlayer p, String placeholderPrefix) {
        String pp = placeholderPrefix == null ? "" : placeholderPrefix + "-";

        phs.put(pp+"player-uuid", p.getUniqueId());
        phs.put(pp+"player-username", p.getName());
        phs.put(pp+"player-name", p.getName());
        if (p.isOnline()) phs.put(pp+"player-displayname", MiniMessage.miniMessage().serialize(p.getPlayer().displayName()));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    protected static List<String> tabCompleteSubcommands(CommandSender sender, Subcommand[] subcommands, String[] args) {
        List<String> completions = new ArrayList<>();

        // Get commands the sender has permissions for
        List<Subcommand> canUse = Arrays.stream(subcommands).filter(cmd -> sender.hasPermission(cmd.permission())).toList();
        if (canUse.isEmpty()) return completions;

        //// Tab completion for subcommands
        String arg1 = args[0].toLowerCase();
        if (args.length == 1) {
            for (Subcommand cmd : canUse) {
                String name = cmd.name();

                if (name.startsWith(args[0])) {
                    completions.add(name);
                }
            }

            return completions;
        }

        //// Tab completion for subcommand arguments

        // Get provided command in first arg
        Subcommand subcommand = canUse.stream().filter(cmd -> cmd.name().equals(arg1)).findAny().orElse(null);
        if (subcommand == null) return completions;

        // Get completions from provided command and return them if there are any
        List<String> subcommandCompletions = subcommand.getTabCompletions(sender, Arrays.copyOfRange(args, 1, args.length));
        if (subcommandCompletions != null) return subcommandCompletions;

        return completions;
    }

    // Completions for player list, that provided sender can see
    public static List<String> getPlayerListCompletions(CommandSender sender, String arg) {
        List<String> players = new ArrayList<>();
        arg = arg.toLowerCase();

        Player player = null;
        if (sender instanceof Player) player = (Player) sender;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player != null && !player.canSee(p)) continue;

            String name = p.getName();
            if (name.toLowerCase().startsWith(arg)) {
                players.add(name);
            }
        }

        return players;
    }
}

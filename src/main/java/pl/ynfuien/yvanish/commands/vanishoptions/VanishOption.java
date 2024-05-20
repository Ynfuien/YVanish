package pl.ynfuien.yvanish.commands.vanishoptions;

import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.data.User;
import pl.ynfuien.yvanish.utils.Lang;

public abstract class VanishOption {
    private final String name;
    private final String permission;
    private final String alias;
    private final Lang.Message description;


    public VanishOption(String name, String alias, Lang.Message description, String permissionBase) {
        this.name = name;
        this.permission = String.format("%s.%s", permissionBase, name.replace("-", ""));
        this.alias = alias;
        this.description = description;
    }

    abstract public boolean toggle(Player player);

    abstract public boolean getState(User user);


    public String getName() {
        return name;
    }
    public String getPermission() {
        return permission;
    }
    public String getAlias() {
        return alias;
    }
    public Lang.Message getDescription() {
        return description;
    }
}

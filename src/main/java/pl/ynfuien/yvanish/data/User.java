package pl.ynfuien.yvanish.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.yvanish.config.PluginConfig;

/**
 * User object used only for saving players vanish options.
 * <p>Fields reflect only changed options, so:</p>
 * <p>true - enabled option</p>
 * <p>false - disabled</p>
 * <p>null - never changed</p>
 * <br>
 * <p>And get methods are what you are looking for.
 * They return default config value, or player's set value
 * if it is set.</p>
 */
public class User {
    // Data saved in the database
    @Nullable
    public Boolean silentChests = null;
    @Nullable
    public Boolean silentSculk = null;
    @Nullable
    public Boolean silentMessages = null;
    @Nullable
    public Boolean noPickup = null;
    @Nullable
    public Boolean noMobs = null;
    @Nullable
    public Boolean actionBar = null;
    @Nullable
    public Boolean bossBar = null;

    /**
     * <b>Constructor for internal use only!</b>
     */
    public User() {};

    /**
     * <b>Constructor for internal use only!</b>
     * <br>
     * <p>It's used in database get user methods,
     * to convert from byte values, to Boolean objects.</p>
     */
    public User(byte silentChests, byte silentSculk, byte silentMessages, byte noPickup, byte noMobs, byte actionBar, byte bossBar) {
        this.silentChests = sqlToBool(silentChests);
        this.silentSculk = sqlToBool(silentSculk);
        this.silentMessages = sqlToBool(silentMessages);
        this.noPickup = sqlToBool(noPickup);
        this.noMobs = sqlToBool(noMobs);
        this.actionBar = sqlToBool(actionBar);
        this.bossBar = sqlToBool(bossBar);
    }

    /**
     * Byte value to Boolean "converter".
     * <br>
     * Byte - Boolean:
     * <ul>
     *     <li>1 - true</li>
     *     <li>0 - false</li>
     *     <li>-1 - null</li>
     * </ul>
     * @return Boolean object according to the list above.
     */
    private static Boolean sqlToBool(byte value) {
        if (value < 0) return null;
        return value > 0;
    }

    @NotNull
    public Boolean getSilentChests() {
        return silentChests != null ? silentChests : PluginConfig.silentChests;
    }

    @NotNull
    public Boolean getSilentSculk() {
        return silentSculk != null ? silentSculk : PluginConfig.silentSculk;
    }

    @NotNull
    public Boolean getSilentMessages() {
        return silentMessages != null ? silentMessages : PluginConfig.silentMessages;
    }

    @NotNull
    public Boolean getNoPickup() {
        return noPickup != null ? noPickup : PluginConfig.noPickup;
    }

    @NotNull
    public Boolean getNoMobs() {
        return noMobs != null ? noMobs : PluginConfig.noMobs;
    }

    @NotNull
    public Boolean getActionBar() {
        return actionBar != null ? actionBar : PluginConfig.actionBarEnabled;
    }

    @NotNull
    public Boolean getBossBar() {
        return bossBar != null ? bossBar : PluginConfig.bossBarEnabled;
    }


    /**
     * <p>Sets silent-chests option for this user.</p>
     * <p>Set null to make the player use default value from the config.</p>
     */
    public void setSilentChests(@Nullable Boolean silentChests) {
        this.silentChests = silentChests;
    }

    /**
     * <p>Sets silent-sculk option for this user.</p>
     * <p>Set null to make the player use default value from the config.</p>
     */
    public void setSilentSculk(@Nullable Boolean silentSculk) {
        this.silentSculk = silentSculk;
    }

    /**
     * <p>Sets silent-messages option for this user.</p>
     * <p>Set null to make the player use default value from the config.</p>
     */
    public void setSilentMessages(@Nullable Boolean silentMessages) {
        this.silentMessages = silentMessages;
    }

    /**
     * <p>Sets no-pickup option for this user.</p>
     * <p>Set null to make the player use default value from the config.</p>
     */
    public void setNoPickup(@Nullable Boolean noPickup) {
        this.noPickup = noPickup;
    }

    /**
     * <p>Sets no-mobs option for this user.</p>
     * <p>Set null to make the player use default value from the config.</p>
     */
    public void setNoMobs(@Nullable Boolean noMobs) {
        this.noMobs = noMobs;
    }

    /**
     * <p>Sets action-bar option for this user.</p>
     * <p>Set null to make the player use default value from the config.</p>
     */
    public void setActionBar(@Nullable Boolean actionBar) {
        this.actionBar = actionBar;
    }

    /**
     * <p>Sets boss-bar option for this user.</p>
     * <p>Set null to make the player use default value from the config.</p>
     */
    public void setBossBar(@Nullable Boolean bossBar) {
        this.bossBar = bossBar;
    }
}

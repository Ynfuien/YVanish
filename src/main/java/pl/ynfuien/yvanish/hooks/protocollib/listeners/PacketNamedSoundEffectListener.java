package pl.ynfuien.yvanish.hooks.protocollib.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.entity.Player;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.ydevlib.utils.DoubleFormatter;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.ChestableUtils;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.protocollib.ProtocolLibHook;

import java.util.List;

public class PacketNamedSoundEffectListener extends PacketAdapter {
    private final YVanish instance;
    private final VanishManager vanishManager;
    private static final List<Sound> EXPECTED_SOUND_EFFECTS = List.of(
            Sound.BLOCK_CHEST_OPEN,
            Sound.BLOCK_CHEST_CLOSE,
            Sound.BLOCK_ENDER_CHEST_OPEN,
            Sound.BLOCK_ENDER_CHEST_CLOSE,
            Sound.BLOCK_BARREL_OPEN,
            Sound.BLOCK_BARREL_CLOSE,
            Sound.BLOCK_SHULKER_BOX_OPEN,
            Sound.BLOCK_SHULKER_BOX_CLOSE
    );

    public PacketNamedSoundEffectListener(YVanish instance, ListenerPriority priority) {
        super(instance, priority, PacketType.Play.Server.NAMED_SOUND_EFFECT);
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (vanishManager.isNoOneVanished()) return;

        Player receiver = event.getPlayer();
        if (receiver.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;


        PacketContainer packet = event.getPacket();
        EnumWrappers.SoundCategory soundCategory = packet.getSoundCategories().readSafely(0);
        if (soundCategory == null) return;

        YLogger.debug("===== NAMED_SOUND_EFFECT =====");
        YLogger.debug(receiver.getName());
        YLogger.debug("Sound category: " + soundCategory.name());

        if (!soundCategory.equals(EnumWrappers.SoundCategory.BLOCKS)) return;

        Sound sound = packet.getSoundEffects().readSafely(0);
        if (sound == null || !EXPECTED_SOUND_EFFECTS.contains(sound)) return;
        YLogger.debug("Sound: " + sound.name());

        Float volume = packet.getFloat().readSafely(0);
        if (volume == null) return;
        YLogger.debug("Volume: " + volume);

        // My own packet
        if (volume == 10) {
            packet.getFloat().writeSafely(0, 0.5f);
            return;
        }

        Integer x = packet.getIntegers().readSafely(0);
        if (x == null) return;
        Integer y = packet.getIntegers().readSafely(1);
        if (y == null) return;
        Integer z = packet.getIntegers().readSafely(2);
        if (z == null) return;


        Location loc = new Location(receiver.getWorld(), (double) x / 8, (double) y / 8, (double) z / 8);
        YLogger.debug("OG Location: " + formatLocation(loc));
        if (sound.name().contains("BARREL")) loc = getBarrelLocation(loc);
        YLogger.debug("Corrected: " + formatLocation(loc));
//        if (sound.name().contains("BARREL")) loc.setY(loc.y() - 0.5);
        Block block = loc.getBlock();

        YLogger.debug("Found block: " + block.getType().name());
//        YLogger.debug("Location: " + loc);
        block = ChestableUtils.getDoubleChestBlock(block);
//        YLogger.debug("A Loc Change: " + block);

        if (!ProtocolLibHook.canSeeBlockChange(receiver, block)) {
            YLogger.debug("Can't see this!");
            event.setCancelled(true);
        }
    }

    private static final DoubleFormatter df = new DoubleFormatter();
    private static String formatLocation(Location loc) {
        return String.format("%s %s %s",df.format(loc.getX()), df.format(loc.getY()), df.format(loc.getZ()));
    }

    private Location getBarrelLocation(Location ogLocation) {
        Location attempt = null;

        YLogger.debug(String.format("Get barrel location:"));
        CorrectionType correctionType = CorrectionType.PLUS;
        for (int i = 0; i < 6; i++) {
            YLogger.debug(String.format("%d. attempt - %s", i + 1, correctionType.name()));
            attempt = ogLocation.clone();
            correctBarrelLocation(attempt, correctionType);

            Block block = attempt.getBlock();
            if (!block.getType().equals(Material.BARREL)) {
                correctionType = CorrectionType.MINUS;
                continue;
            }

            Barrel barrel = (Barrel) block.getBlockData();
            BlockFace facing = barrel.getFacing();

            if (correctionType.equals(CorrectionType.PLUS)) {
                if (List.of(BlockFace.SOUTH, BlockFace.UP, BlockFace.EAST).contains(facing)) {
                    correctionType = CorrectionType.MINUS;
                    continue;
                }

                break;
            }

            if (List.of(BlockFace.NORTH, BlockFace.DOWN, BlockFace.WEST).contains(facing)) {
                correctionType = CorrectionType.PLUS;
                continue;
            }
            break;
        }

        return attempt;
    }

    private void correctBarrelLocation(Location loc, CorrectionType type) {
        double[] coords = new double[] {loc.getX(), loc.getY(), loc.getZ()};

        for (int i = 0; i < coords.length; i++) {
            double coord = coords[i];
            if (hasDecimalNumbers(coord)) continue;

            coords[i] = coord + (type.getOperation() * 0.5);
            break;
        }

        loc.setX(coords[0]);
        loc.setY(coords[1]);
        loc.setZ(coords[2]);
    }

    private boolean hasDecimalNumbers(double number) {
        return (int) number != number;
    }

    private enum CorrectionType {
        PLUS(1),
        MINUS(-1);

        private final int operation;

        CorrectionType(int operation) {
            this.operation = operation;
        }

        public int getOperation() {
            return operation;
        }
    }
}

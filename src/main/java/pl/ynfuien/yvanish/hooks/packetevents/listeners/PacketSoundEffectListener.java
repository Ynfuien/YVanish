package pl.ynfuien.yvanish.hooks.packetevents.listeners;


import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.sound.Sound;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.entity.Player;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.ydevlib.utils.DoubleFormatter;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.ChestableUtils;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.packetevents.PacketEventsHook;

import java.util.List;
import java.util.Set;

// Chest, barrel and shulker box open / close sounds - Cancelling if necessary
public class PacketSoundEffectListener implements PacketListener {
    private final YVanish instance;
    private final VanishManager vanishManager;
    private static final Set<Sound> EXPECTED_SOUND_EFFECTS = Set.of(
            Sounds.BLOCK_CHEST_OPEN,
            Sounds.BLOCK_CHEST_CLOSE,
            Sounds.BLOCK_ENDER_CHEST_OPEN,
            Sounds.BLOCK_ENDER_CHEST_CLOSE,
            Sounds.BLOCK_BARREL_OPEN,
            Sounds.BLOCK_BARREL_CLOSE,
            Sounds.BLOCK_SHULKER_BOX_OPEN,
            Sounds.BLOCK_SHULKER_BOX_CLOSE
    );

    public PacketSoundEffectListener(YVanish instance) {
        this.instance = instance;
        this.vanishManager = instance.getVanishManager();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.SOUND_EFFECT)) return;
        if (vanishManager.isNoOneVanished()) return;

        Player receiver = event.getPlayer();
        if (receiver.hasPermission(YVanish.Permissions.VANISH_SEE.get())) return;


        YLogger.debug("===== SOUND_EFFECT =====");
        YLogger.debug("Primary thread: " + Bukkit.isPrimaryThread());
        WrapperPlayServerSoundEffect packet = new WrapperPlayServerSoundEffect(event);
        SoundCategory soundCategory = packet.getSoundCategory();
        if (soundCategory == null) return;

        YLogger.debug(receiver.getName());
        YLogger.debug("Sound category: " + soundCategory.name());

        if (!soundCategory.equals(SoundCategory.BLOCK) && !soundCategory.equals(SoundCategory.NEUTRAL)) return;

        Sound sound = packet.getSound();
        if (!EXPECTED_SOUND_EFFECTS.contains(sound)) return;
        YLogger.debug("Sound: " + sound.getName());

        float volume = packet.getVolume();
        YLogger.debug("Volume: " + volume);


        Vector3i pos = packet.getEffectPosition();

        event.setCancelled(true);

        float pitch = packet.getPitch();
        long seed = packet.getSeed();
        Bukkit.getGlobalRegionScheduler().run(instance, (task) -> {
            Location loc = new Location(receiver.getWorld(), (double) pos.x / 8, (double) pos.y / 8, (double) pos.z / 8);
            YLogger.debug("OG Location: " + formatLocation(loc));
            if (sound.equals(Sounds.BLOCK_BARREL_CLOSE) || sound.equals(Sounds.BLOCK_BARREL_OPEN)) loc = getBarrelLocation(loc);
            YLogger.debug("Corrected: " + formatLocation(loc));
            Block block = loc.getBlock();

            YLogger.debug("Found block: " + block.getType().name());
            block = ChestableUtils.getDoubleChestBlock(block);
            if (!PacketEventsHook.canSeeBlockChange(receiver, block)) return;

            YLogger.debug("Send packet duplicate");
            WrapperPlayServerSoundEffect packetDuplicate = new WrapperPlayServerSoundEffect(sound, soundCategory, pos, volume, pitch, seed);
            PacketEvents.getAPI().getPlayerManager().sendPacketSilently(receiver, packetDuplicate);
        });


    }

    private static final DoubleFormatter df = new DoubleFormatter();
    private static String formatLocation(Location loc) {
        return String.format("%s %s %s",df.format(loc.getX()), df.format(loc.getY()), df.format(loc.getZ()));
    }

    private Location getBarrelLocation(Location ogLocation) {
        Location attempt = null;

        YLogger.debug("Get barrel location:");
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

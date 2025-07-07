package pl.ynfuien.yvanish.hooks.packetevents.listeners;


import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.sound.Sound;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.hooks.packetevents.PacketEventsHook;

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


//        YLogger.debug("<green>===== SOUND_EFFECT =====");
        WrapperPlayServerSoundEffect packet = new WrapperPlayServerSoundEffect(event);
        SoundCategory soundCategory = packet.getSoundCategory();
        if (soundCategory == null) return;

//        YLogger.debug("<green>Sound category: " + soundCategory.name());

        if (!soundCategory.equals(SoundCategory.BLOCK) && !soundCategory.equals(SoundCategory.NEUTRAL)) return;


        Sound sound = packet.getSound();
//        YLogger.debug("<green>Sound id: " + sound.getSoundId());
        if (!EXPECTED_SOUND_EFFECTS.contains(sound)) return;
//        YLogger.debug("<green>Sound name: " + sound.getName());

//        float volume = packet.getVolume();
//        YLogger.debug("<green>Volume: " + volume);


        Vector3i pos = packet.getEffectPosition();
        Location loc = new Location(receiver.getWorld(), (double) pos.x / 8, (double) pos.y / 8, (double) pos.z / 8);
//        YLogger.debug("<green>Loc: " + formatLocation(loc));
//        YLogger.debug("<green>Block loc: " + formatLocation(loc.toBlockLocation()));

        if (sound.equals(Sounds.BLOCK_BARREL_CLOSE) || sound.equals(Sounds.BLOCK_BARREL_OPEN)) {
            if (!PacketEventsHook.isLocationBlocked(loc)) return;
        } else if (!PacketEventsHook.isLocationBlocked(loc.toBlockLocation())) return;

//        YLogger.debug("<green>Cancel!");
        event.setCancelled(true);

//        float pitch = packet.getPitch();
//        long seed = packet.getSeed();
//        Bukkit.getGlobalRegionScheduler().run(instance, (task) -> {
//            Location loc = new Location(receiver.getWorld(), (double) pos.x / 8, (double) pos.y / 8, (double) pos.z / 8);
//            YLogger.debug("<green>OG Location: " + formatLocation(loc));
//            if (sound.equals(Sounds.BLOCK_BARREL_CLOSE) || sound.equals(Sounds.BLOCK_BARREL_OPEN)) loc = getBarrelLocation(loc);
//            YLogger.debug("<green>Corrected: " + formatLocation(loc));
//            Block block = loc.getBlock();
//
//            YLogger.debug("<green>Found block: " + block.getType().name());
//            block = ChestableUtils.getDoubleChestBlock(block);
//            if (!PacketEventsHook.canSeeBlockChange(receiver, block)) return;
//
//            YLogger.debug("<green>Send packet duplicate");
//            WrapperPlayServerSoundEffect packetDuplicate = new WrapperPlayServerSoundEffect(sound, soundCategory, pos, volume, pitch, seed);
//            PacketEvents.getAPI().getPlayerManager().sendPacketSilently(receiver, packetDuplicate);
//        });


    }
}

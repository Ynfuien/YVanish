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


        WrapperPlayServerSoundEffect packet = new WrapperPlayServerSoundEffect(event);
        SoundCategory soundCategory = packet.getSoundCategory();
        if (soundCategory == null) return;

        if (!soundCategory.equals(SoundCategory.BLOCK)) return;

        Sound sound = packet.getSound();
        if (!EXPECTED_SOUND_EFFECTS.contains(sound)) return;


        Vector3i pos = packet.getEffectPosition();
        Location loc = new Location(receiver.getWorld(), (double) pos.x / 8, (double) pos.y / 8, (double) pos.z / 8);

        if (!sound.equals(Sounds.BLOCK_BARREL_CLOSE) && !sound.equals(Sounds.BLOCK_BARREL_OPEN)) loc = loc.toBlockLocation();

        if (!PacketEventsHook.canSeeBlockChange(receiver, loc)) event.setCancelled(true);
    }
}

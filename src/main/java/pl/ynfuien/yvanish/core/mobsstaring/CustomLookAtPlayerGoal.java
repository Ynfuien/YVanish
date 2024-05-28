package pl.ynfuien.yvanish.core.mobsstaring;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.yvanish.core.VanishManager;
import pl.ynfuien.yvanish.data.Storage;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Random;

public class CustomLookAtPlayerGoal implements Goal<Mob> {
    private static final Random RANDOM = new Random();

    private final VanishManager vanishManager;

    private final Mob mob;
    private Entity lookAt;
    private final float lookDistance;
    private int lookTime;
    private final float probability;
    private final boolean onlyHorizontal;

    public CustomLookAtPlayerGoal(VanishManager vanishManager, Mob mob, float range, float chance, boolean lookForward) {
        this.vanishManager = vanishManager;
        this.mob = mob;
        this.lookDistance = range;
        this.probability = chance;
        this.onlyHorizontal = lookForward;
    }

    @Override
    public boolean shouldActivate() {
        if (RANDOM.nextFloat() >= probability) return false;

        // Find nearest player
        Collection<Player> nearby = mob.getLocation().getNearbyPlayers(this.lookDistance);
        double lastDistance = -1d;
        for (Player p : nearby) {
            // Conditions
            if (p.isDead()) continue;
            if (p.getGameMode().equals(GameMode.SPECTATOR)) continue;
            if (vanishManager.isVanished(p) && Storage.getUser(p.getUniqueId()).getNoMobs()) continue;
            if (isMobRiddenBy(mob, p)) continue;

            // Distance checking
            double distance = mob.getLocation().distanceSquared(p.getLocation());
            if (lastDistance == -1d || distance < lastDistance) {
                lookAt = p;
                lastDistance = distance;
            }
        }

        // Panda check
        if (mob instanceof Panda panda) {
            if (panda.isOnBack() || panda.isScared()) return false;
            if (panda.isEating() || panda.isRolling()) return false;
            if (panda.isSitting()) return false;
        }

        // Fox check
        if (mob instanceof Fox fox) {
            if (fox.isFaceplanted() || fox.isInterested()) return false;
        }

        return lookAt != null;
    }

    @Override
    public boolean shouldStayActive() {
        if (lookAt == null || lookAt.isDead()) return false;

        // Fox check
        if (mob instanceof Fox fox) {
            if (fox.isFaceplanted() || fox.isInterested()) return false;
        }

        // If player is still close enough
        double distance = mob.getLocation().distanceSquared(lookAt.getLocation());
        if (distance > (double) (lookDistance * lookDistance)) return false;

        return lookTime > 0;
    }

    @Override
    public void start() {
        lookTime = positiveCeilDiv(40 + RANDOM.nextInt(40), 2);
    }

    @Override
    public void stop() {
        lookAt = null;
    }

    @Override
    public void tick() {
        if (lookAt == null || lookAt.isDead()) return;

        // Look at the player
        lookTime--;
        if (onlyHorizontal) {
            mob.lookAt(lookAt.getX(), mob.getEyeHeight() + mob.getY(), lookAt.getZ());
            return;
        }

        mob.lookAt(lookAt);
    }

    @Override
    public @NotNull GoalKey<Mob> getKey() {
        return StopMobsStaring.CUSTOM_LOOK_GOAL;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.LOOK);
    }


    private static int positiveCeilDiv(int a, int b) {
        return -Math.floorDiv(-a, b);
    }

    private static boolean isMobRiddenBy(Mob mob, Player player) {
        Entity vehicle = player;
        while (true) {
            assert vehicle != null;
            if (!vehicle.isInsideVehicle()) return false;

            vehicle = vehicle.getVehicle();
            if (vehicle == mob) return true;
        }
    }
}

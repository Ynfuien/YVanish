package pl.ynfuien.yvanish.core.mobsstaring;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.MobGoals;
import com.destroystokyo.paper.entity.ai.VanillaGoal;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pl.ynfuien.yvanish.YVanish;
import pl.ynfuien.yvanish.config.PluginConfig;
import pl.ynfuien.yvanish.core.VanishManager;

import java.util.*;

public class StopMobsStaring {
    private static YVanish instance;
    private static VanishManager vanishManager;

    private static final float HIGHEST_RANGE = LookGoalProvider.getHighestRange();
    private static final MobGoals mobGoals = Bukkit.getMobGoals();
    private static final GoalKey<Mob>[] LOOK_GOALS = new GoalKey[] {
            VanillaGoal.LOOK_AT_PLAYER,
            VanillaGoal.FOX_LOOK_AT_PLAYER,
            VanillaGoal.PANDA_LOOK_AT_PLAYER,
            VanillaGoal.INTERACT // Wandering trader
    };
    public static GoalKey<Mob> CUSTOM_LOOK_GOAL = null;

    private static BukkitTask interval = null;
    private static final HashMap<Mob, Collection<Goal<Mob>>> temporaryModified = new HashMap<>();

    // https://github.com/PaperMC/Paper/issues/10743
    // It's just a lazy check whether version is 1.20.6 or higher.
    // No need for more complex stuff.
    private static final boolean PAPER_FIX = Bukkit.getUnsafe().getProtocolVersion() >= 766;

    public static void setup(YVanish instance) {
        StopMobsStaring.instance = instance;
        vanishManager = instance.getVanishManager();

        CUSTOM_LOOK_GOAL = GoalKey.of(Mob.class, new NamespacedKey(instance, "look_at_player"));
    }

    public static void startInterval() {
        if (!PluginConfig.mobsNoStaring) return;

        interval = Bukkit.getScheduler().runTaskTimer(instance, () -> {
            Set<Mob> mobsInRange = new HashSet<>();

            for (Player p : vanishManager.getVanishedPlayers()) {
                Collection<LivingEntity> entities = p.getLocation().getNearbyLivingEntities(HIGHEST_RANGE);
                for (LivingEntity entity : entities) {
                    if (!(entity instanceof Mob mob)) continue;
                    mobsInRange.add(mob);

                    if (temporaryModified.containsKey(mob)) continue;

                    makeMobSpecial(mob);
                }
            }

            // Loop through modified mobs
            for (Mob mob : new HashSet<>(temporaryModified.keySet())) {
                if (mob.isDead()) {
                    temporaryModified.remove(mob);
                    continue;
                }

                if (mobsInRange.contains(mob)) continue;

                makeMobNotSoSpecial(mob);
            }
        }, 5, 5);

    }

    public static void stopInterval() {
        if (interval == null) return;

        for (Mob mob : new HashSet<>(temporaryModified.keySet())) {
            makeMobNotSoSpecial(mob);
        }
    }

    /**
     * Removes vanilla look goals; adds custom one.
     */
    private static void makeMobSpecial(Mob mob) {
        // Get look goals
        List<Goal<Mob>> goals = new ArrayList<>();
        for (GoalKey<Mob> goalKey : LOOK_GOALS) {
            goals.addAll(mobGoals.getGoals(mob, goalKey));
        }
        if (goals.isEmpty()) return;

        // Remove them
        for (Goal<Mob> goal : goals) mobGoals.removeGoal(mob, goal);
        if (PAPER_FIX) temporaryModified.put(mob, goals);

        // Add a custom one
        mobGoals.addGoal(mob, LookGoalProvider.getPriority(mob), LookGoalProvider.getGoal(vanishManager, mob));
    }

    /**
     * Removes custom look goal; adds vanilla ones back.
     */
    private static void makeMobNotSoSpecial(Mob mob) {
        if (!PAPER_FIX) return;

        // Remove custom goal
        mobGoals.removeGoal(mob, CUSTOM_LOOK_GOAL);
        // Add original ones back
        for (Goal<Mob> goal: temporaryModified.get(mob)) {
            mobGoals.addGoal(mob, LookGoalProvider.getPriority(mob), goal);
        }

        temporaryModified.remove(mob);
    }
}

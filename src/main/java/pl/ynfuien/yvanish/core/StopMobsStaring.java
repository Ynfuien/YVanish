package pl.ynfuien.yvanish.core;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.VanillaGoal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.yvanish.YVanish;

import java.util.*;

// Waiting for:
// https://github.com/PaperMC/Paper/issues/10743
public class StopMobsStaring {
    private static YVanish instance;
    private static VanishManager vanishManager;

    private static final GoalKey<Mob> LOOK_AT_PLAYER_GOAL = VanillaGoal.LOOK_AT_PLAYER;

    private static BukkitTask interval = null;

    private static HashMap<Mob, Goal<Mob>> temporaryModified = new HashMap<>();

    public static void setup(YVanish instance) {
        StopMobsStaring.instance = instance;
        vanishManager = instance.getVanishManager();
    }

    public static void startInterval() {
        interval = Bukkit.getScheduler().runTaskTimer(instance, () -> {
            if (vanishManager.isNoOneVanished()) return;

            Set<Mob> mobsInRange = new HashSet<>();

            for (Player p : vanishManager.getVanishedPlayers()) {
                List<Entity> entities = p.getNearbyEntities(10, 10, 10);
                for (Entity entity : entities) {
                    if (!(entity instanceof Mob mob)) continue;

                    mobsInRange.add(mob);

                    if (temporaryModified.containsKey(mob)) continue;

//                    Bukkit.getMobGoals().removeGoal(mob, LOOK_AT_PLAYER_GOAL);
                    Collection<Goal<Mob>> goals = Bukkit.getMobGoals().getGoals(mob, LOOK_AT_PLAYER_GOAL);
                    if (goals.isEmpty()) continue;

                    YLogger.debug(String.format("Removed %d goals from a %s", goals.size(), mob.getType()));
                    temporaryModified.put(mob, goals.stream().findFirst().get());
//                    Bukkit.getMobGoals().removeGoal(mob, LOOK_AT_PLAYER_GOAL);
                    Bukkit.getMobGoals().removeAllGoals(mob);

                    Goal<Mob> goal2 = Bukkit.getMobGoals().getGoal(mob, LOOK_AT_PLAYER_GOAL);
                    if (goal2 == null) YLogger.debug("Goal 2 is null!");
                    else YLogger.debug("Goal 2 is: " + goal2.getKey().getNamespacedKey());
                    mob.setGlowing(true);
                }
            }

            for (Mob mob : new HashSet<>(temporaryModified.keySet())) {
                if (mob.isDead()) {
                    temporaryModified.remove(mob);
                    continue;
                }

                if (mobsInRange.contains(mob)) continue;

//                Goal<Mob> goal = Bukkit.getMobGoals().getGoal(mob, LOOK_AT_PLAYER_GOAL);
//                if (goal == null) YLogger.debug("Goal is null");
//
//                mob.setAI(false);
//                mob.setAI(true);
//                Goal<Mob> goal2 = Bukkit.getMobGoals().getGoal(mob, LOOK_AT_PLAYER_GOAL);
//                if (goal2 == null) YLogger.debug("Goal is null again");

                Bukkit.getMobGoals().addGoal(mob, 1, temporaryModified.get(mob));

                YLogger.debug(String.format("Added goal back to %s", mob.getType()));
                temporaryModified.remove(mob);
                mob.setGlowing(false);
            }
        }, 5, 5);

    }

    public static void stopInterval() {
        if (interval != null) interval.cancel();
    }
}

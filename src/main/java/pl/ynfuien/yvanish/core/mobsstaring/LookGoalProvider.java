package pl.ynfuien.yvanish.core.mobsstaring;

import org.bukkit.entity.*;
import pl.ynfuien.yvanish.core.VanishManager;

import java.util.HashMap;

public class LookGoalProvider {
    private static final float DEFAULT_CHANCE = 0.02F;
    private static final Props DEFAULT_LOOK_GOAL_PROPERTIES = new Props(10, 6, DEFAULT_CHANCE);

    private static final HashMap<Class<? extends Mob>, Props> LOOK_GOAL_PROPERTIES = new HashMap<>() {{
        // Kindly borrowed from Mojang
        put(Cat.class, new Props(12, 10.0F, DEFAULT_CHANCE));
        put(Chicken.class, new Props(6, 6.0F, DEFAULT_CHANCE));
        put(Cow.class, new Props(6, 6.0F, DEFAULT_CHANCE));
        put(Dolphin.class, new Props(5, 6.0F, DEFAULT_CHANCE));
        put(IronGolem.class, new Props(7, 6.0F, DEFAULT_CHANCE));
        put(Ocelot.class, new Props(11, 10.0F, DEFAULT_CHANCE));
        put(Parrot.class, new Props(1, 8.0F, DEFAULT_CHANCE));
        put(Pig.class, new Props(7, 6.0F, DEFAULT_CHANCE));
        put(PolarBear.class, new Props(6, 6.0F, DEFAULT_CHANCE));
        put(Rabbit.class, new Props(11, 10.0F, DEFAULT_CHANCE));
        put(Sheep.class, new Props(7, 6.0F, DEFAULT_CHANCE));
        put(Snowman.class, new Props(3, 6.0F, DEFAULT_CHANCE));
        put(Turtle.class, new Props(8, 8.0F, DEFAULT_CHANCE));
        put(Wolf.class, new Props(10, 8.0F, DEFAULT_CHANCE));
        put(Skeleton.class, new Props(6, 8.0F, DEFAULT_CHANCE));
        put(Blaze.class, new Props(8, 8.0F, DEFAULT_CHANCE));
        put(Creeper.class, new Props(6, 8.0F, DEFAULT_CHANCE));
        put(Enderman.class, new Props(8, 8.0F, DEFAULT_CHANCE));
        put(Endermite.class, new Props(7, 8.0F, DEFAULT_CHANCE));
        put(Evoker.class, new Props(9, 3.0F, 1.0F));
        put(Guardian.class, new Props(8, 8.0F, DEFAULT_CHANCE));
        put(Illusioner.class, new Props(9, 3.0F, 1.0F));
        put(Pillager.class, new Props(9, 15.0F, 1.0F));
        put(Ravager.class, new Props(6, 6.0F, DEFAULT_CHANCE));
        put(Shulker.class, new Props(1, 8.0F, 0.02F));
        put(Spider.class, new Props(6, 8.0F, DEFAULT_CHANCE));
        put(Strider.class, new Props(8, 8.0F, DEFAULT_CHANCE));
        put(Vex.class, new Props(9, 3.0F, 1.0F));
        put(Vindicator.class, new Props(9, 3.0F, 1.0F));
        put(Witch.class, new Props(3, 8.0F, DEFAULT_CHANCE));
        put(Zombie.class, new Props(8, 8.0F, DEFAULT_CHANCE));
        put(Husk.class, new Props(8, 8.0F, DEFAULT_CHANCE));
        put(Drowned.class, new Props(8, 8.0F, DEFAULT_CHANCE));
        put(Horse.class, new Props(7, 6.0F, DEFAULT_CHANCE));
        put(Llama.class, new Props(8, 6.0F, DEFAULT_CHANCE));
        put(Wither.class, new Props(6, 8.0F, DEFAULT_CHANCE));

        put(Fox.class, new Props(12, 24.0F, 1.0F));
        put(Panda.class, new Props(9, 6.0F, 1.0F));
        put(WanderingTrader.class, new Props(9, 3.0F, 1.0F));
    }};

    public static CustomLookAtPlayerGoal getGoal(VanishManager vanishManager, Mob mob) {
        Props properties = DEFAULT_LOOK_GOAL_PROPERTIES;
        for (Class<? extends Mob> clazz : LOOK_GOAL_PROPERTIES.keySet()) {
            if (clazz.isInstance(mob)) {
                properties = LOOK_GOAL_PROPERTIES.get(clazz);
                break;
            }
        }

        boolean forward = mob.getType().equals(EntityType.SHULKER);
        return new CustomLookAtPlayerGoal(vanishManager, mob, properties.range, properties.chance, forward);
    }

    public static int getPriority(Mob mob) {
        Props properties = DEFAULT_LOOK_GOAL_PROPERTIES;
        for (Class<? extends Mob> clazz : LOOK_GOAL_PROPERTIES.keySet()) {
            if (clazz.isInstance(mob)) {
                properties = LOOK_GOAL_PROPERTIES.get(clazz);
                break;
            }
        }

        return properties.priority;
    }

    public static float getHighestRange() {
        float range = 0;
        for (Props properties : LOOK_GOAL_PROPERTIES.values()) {
            if (properties.range > range) range = properties.range;
        }

        return range;
    }

    private record Props(int priority, float range, float chance) {}
}

package io.github.favfarms.navigation;

import io.github.favfarms.utils.ReflectionUtil;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * FavouriteFarms Created by Awesome Red on 10/21/2016.
 */
public class FarmNavigation {

    private static FarmNavigation instance = new FarmNavigation();

    public static FarmNavigation getInstance() {
        return instance;
    }

    @SuppressWarnings("ConstantConditions")
    public void navigate(LivingEntity animal, Location loc, double velocity) {
        try {
            Object entityLiving = ReflectionUtil.getMethod("getHandle", animal.getClass(), 0).invoke(animal);
            Object navigation = ReflectionUtil.getMethod("getNavigation", entityLiving.getClass(), 0).invoke(entityLiving);

            ReflectionUtil.getMethod("a", navigation.getClass(), 4).invoke(navigation, loc.getX(), loc.getY()
                    , loc.getZ(), velocity);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void facePlayerDirection (LivingEntity animal, Player player) {
        try {
            Object entityLiving = ReflectionUtil.getMethod("getHandle", animal.getClass(), 0).invoke(animal);
            ReflectionUtil.getMethod("i", entityLiving.getClass(), 1).invoke(entityLiving, player.getLocation().getYaw());
            ReflectionUtil.getMethod("h", entityLiving.getClass(), 1).invoke(entityLiving, player.getLocation().getYaw());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void deNavigate(LivingEntity animal) {
        try {
            Location loc = animal.getLocation();
            Animals anim = (Animals) animal;
            anim.setTarget(null);
            Object entityLiving = ReflectionUtil.getMethod("getHandle", animal.getClass(), 0).invoke(animal);
            Object navigation = ReflectionUtil.getMethod("getNavigation", entityLiving.getClass(), 0).invoke(entityLiving);

            ReflectionUtil.getMethod("a", navigation.getClass(), 4).invoke(navigation, loc.getX(), loc.getY(), loc.getZ(), 1.0);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

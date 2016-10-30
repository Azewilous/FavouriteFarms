package io.github.favfarms.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * FavouriteFarms Created by Awesome Red on 10/20/2016.
 */
public class AbilityDisplay {

    private static AbilityDisplay instance = new AbilityDisplay();

    public static AbilityDisplay getInstance() {
        return instance;
    }

    public void calculateLine(Animals animal, Player player, Particle particle) {
        World world = animal.getWorld();

        Location start = animal.getEyeLocation();
        Vector increase = player.getEyeLocation().getDirection();

        for (int i = 0; i < 20; i++) {
            Location point = start.add(increase);
            world.spawnParticle(particle, point, 0, 0, 0, 0, 10);
        }
    }

}

package io.github.favfarms.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Set;

/**
 * FavouriteFarms Created by Awesome Red on 10/20/2016.
 */
public class AbilityDisplay {

    private static AbilityDisplay instance = new AbilityDisplay();

    public static AbilityDisplay getInstance() {
        return instance;
    }

    public void calculateLine(Animals animal, Player player) {
        World world = animal.getWorld();

        Location modified = animal.getEyeLocation().getDirection().multiply(2).toLocation(world);

        Vector start = modified.getDirection();
        Vector end = player.getTargetBlock((Set<Material>) null, 100).getLocation().getDirection();

        Vector line = end.clone().subtract(start);

        world.spawnEntity(animal.getEyeLocation(), EntityType.SNOWBALL).setVelocity(line);
    }

    public void calculateLine(Animals animal, Player player, Particle particle) {
        World world = animal.getWorld();

        Vector start = animal.getEyeLocation().getDirection();
        Vector end = player.getLocation().getDirection().normalize().multiply(5);

        world.spawnParticle(particle, animal.getEyeLocation(), 10);
    }

}

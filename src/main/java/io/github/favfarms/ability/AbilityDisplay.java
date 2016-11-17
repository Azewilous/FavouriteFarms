package io.github.favfarms.ability;

import io.github.favfarms.FavFarms;
import io.github.favfarms.navigation.FarmNavigation;
import org.bukkit.*;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * FavouriteFarms Created by Awesome Red on 10/20/2016.
 */
public class AbilityDisplay {

    Plugin farms = FavFarms.getInstance();

    private static AbilityDisplay instance = new AbilityDisplay();

    public static AbilityDisplay getInstance() {
        return instance;
    }

    Map<Animals, Long> repeat = new HashMap<>();
    Map<Animals, Integer> taskId_01 = new HashMap<>();
    Map<Animals, Integer> taskId_02 = new HashMap<>();

    public void calculateLine(Animals animal, Player player, Particle particle) {
        World world = animal.getWorld();

        Location start = animal.getEyeLocation();
        Vector increase = player.getEyeLocation().getDirection();

        if (start != player.getEyeLocation() || increase != player.getEyeLocation().getDirection()) {
            FarmNavigation.getInstance().facePlayerDirection(animal, player);
        }

        for (int i = 0; i < 20; i++) {
            Location point = start.add(increase);
            world.spawnParticle(particle, point, 10, 0, 0, 0, 10);
        }
    }


    public void calculateArc(Animals animal, Player player, Particle particle) {
        World world = animal.getWorld();

        //Location start = animal.getLocation();
        Location end = player.getEyeLocation().multiply(10);

        if (world.getBlockAt(end.subtract(0, 1, 0)).getType() == Material.AIR) {
            do {
                end = end.subtract(0, 1, 0);
            } while (world.getBlockAt(end.subtract(0, 1, 0)).getType() == Material.AIR);
        }

        if (world.getBlockAt(end.add(0, 2, 0)).getType() != Material.AIR) {
            do {
                end = end.add(0, 1, 0);
            } while (world.getBlockAt(end.subtract(0, 1, 0)).getType() == Material.AIR);
        }

        world.spawnParticle(particle, end, 0);

    }


    public void calculateSphere(Animals animal, Particle particle) {
        World world = animal.getWorld();

        Location location = animal.getLocation().add(0, 1, 0);

        for (double i = 0; i <= Math.PI; i += Math.PI / 25) {
            double radius = Math.sin(i);
            double y = Math.cos(i);
            for (double a = 0; a < Math.PI * 2; a += Math.PI / 10) {
                double x = Math.cos(a) * (radius * 1.2);
                double z = Math.sin(a) * (radius * 1.2);
                location.add(x, y, z);

                world.spawnParticle(particle, location, 5, 0, 0, 0, 0);

                location.subtract(x, y, z);
            }
        }
    }

    public void calculateContinuousSphere(Animals animal, Particle particle, long time) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        countDown(animal, time);
        taskId_01.put(animal, scheduler.scheduleSyncRepeatingTask(farms, () -> {
            if (repeat.containsKey(animal)) {
                calculateSphere(animal, particle);
            }
        }, 0,(long) 0.8));
    }

    public void countDown(Animals animal, long time) {
        repeat.put(animal, time);
        if (repeat.containsKey(animal)) {
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            taskId_02.put(animal, scheduler.scheduleSyncRepeatingTask(farms, () -> {
                if (repeat.get(animal) != 0) {
                    repeat.put(animal, repeat.get(animal) - 1);
                } else {
                    Bukkit.getServer().getScheduler().cancelTask(taskId_01.get(animal));
                    Bukkit.getServer().getScheduler().cancelTask(taskId_02.get(animal));
                    repeat.remove(animal);
                    taskId_01.remove(animal);
                    taskId_02.remove(animal);
                }
            }, 0, 20));
        }
    }
}

package io.github.favfarms.farm;

import io.github.favfarms.configuration.FarmConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * FavFarms Created by Awesome Red on 7/15/2016.
 */
class Farm {

    private Farm() {}

    private static Farm instance = new Farm();

    public static Farm getInstance() {
        return instance;
    }

    private FarmConfig config = FarmConfig.getInstance();

    String XS = "Extra Small";
    String S = "Small";
    String M = "Medium";
    String L = "Large";
    String XL = "Extra Large";

    String name;
    int ID;
    int blocks;
    UUID ownerUUID;
    int level;
    Location locFirst;
    Vector blockVecFirst;
    Vector blockVecSecond;
    int experience;

    public void createFarm(String name, UUID uuid, int ID, int blocks, int level, int experience) {
        this.name = name;
        this.ID = ID;
        ownerUUID = uuid;
        this.level = level;
        this.blocks = blocks;
        this.experience = experience;

        int amount = config.getFarms().getInt("Amount");
        config.getFarms().set("Amount", amount + 1);
        config.getFarms().set("Farms." + ownerUUID + ".Name", getName());
        config.getFarms().set("Farms." + ownerUUID + ".ID", getID());
        config.getFarms().set("Farms." + ownerUUID + ".Creator", getCreatorName());
        config.getFarms().set("Farms." + ownerUUID + ".Size", getFarmSize());
        config.getFarms().set("Farms." + ownerUUID + ".Level", getLevel());
        config.getFarms().set("Farms." + ownerUUID + ".Experience", getExperience());
        config.saveFarms();
    }

    public void createArea(Vector vec1, Vector vec2, Location loc1) {
        locFirst = loc1;
        blockVecFirst = vec1;
        blockVecSecond = vec2;

        if (config.getFarms().get("Farms." + getName()) != null) {
            if (locFirst != null && blockVecFirst != null && blockVecSecond != null) {
                config.getFarms().set("Farms." + ownerUUID + ".World", locFirst.getWorld().getName());
                config.getFarms().set("Farms." + ownerUUID + ".Start_Loc", "(" + blockVecFirst.getBlockX() + ", "
                        + blockVecFirst.getBlockY() + ", " + blockVecFirst.getBlockZ() + ")");
                config.getFarms().set("Farms." + ownerUUID + ".End_Loc", "(" + blockVecSecond.getBlockX() + ", "
                        + blockVecSecond.getBlockY() + ", " + blockVecSecond.getBlockZ() + ")");
                config.getFarms().set("Farms." + ownerUUID + ".Start.VectorX", blockVecFirst.getBlockX());
                config.getFarms().set("Farms." + ownerUUID + ".Start.VectorY", blockVecFirst.getBlockY());
                config.getFarms().set("Farms." + ownerUUID + ".Start.VectorZ", blockVecFirst.getBlockZ());
                config.getFarms().set("Farms." + ownerUUID + ".End.VectorX", blockVecSecond.getBlockX());
                config.getFarms().set("Farms." + ownerUUID + ".End.VectorY", blockVecSecond.getBlockY());
                config.getFarms().set("Farms." + ownerUUID + ".End.VectorZ", blockVecSecond.getBlockZ());
                config.saveFarms();
            }
        }
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getCreatorName() {
        Player owner = Bukkit.getPlayer(ownerUUID);
        return owner.getName();
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public String getFarmSize() {
        String size;
        int amount = blocks;

        if (amount <= 300) {
            size = XS + ", " + amount + " blocks";
        } else if (amount <= 700) {
            size = S + ", " + amount + " blocks";
        } else if (amount <= 1800) {
            size = M + ", " + amount + " blocks";
        } else if (amount <= 2500) {
            size = L + ", " + amount + " blocks";
        } else if (amount <= 5000) {
            size = XL + ", " + amount + " blocks";
        } else
            size = "Empty";

        return size;
    }

    /*
    public boolean isFarmArea() {
        Vector vecFirst = new Vector(blockVecFirst.getX(), blockVecFirst.getY(), blockVecFirst.getZ());
        Vector vecSecond = new Vector(blockVecSecond.getX(), blockVecSecond.getY(), blockVecSecond.getZ());
        return Vector
    }
    */
}

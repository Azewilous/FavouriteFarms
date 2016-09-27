package io.github.favfarms.farm;

import io.github.favfarms.configuration.FarmConfig;
import org.bukkit.Location;
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
    Location locFirst;
    Vector vecFirst;
    Vector vecSecond;

    public void createFarm(String name, UUID uuid, int ID, int blocks) {
        this.name = name;
        this.ID = ID;
        ownerUUID = uuid;
        this.blocks = blocks;

        int amount = config.getFarms().getInt("Amount");
        config.getFarms().set("Amount", amount + 1);
        config.getFarms().set("Farms." + getName() + ".Name", getName());
        config.getFarms().set("Farms." + getName() + ".ID", getID());
        config.getFarms().set("Farms." + getName() + ".Creator", getOwnerUUID().toString());
        config.getFarms().set("Farms." + getName() + ".Size", getFarmSize());
        config.saveFarms();
    }

    public void createArea(Vector vec1, Vector vec2, Location loc1) {
        locFirst = loc1;
        vecFirst = vec1;
        vecSecond = vec2;

        if (config.getFarms().get("Farms." + getName()) != null) {
            if (locFirst != null && vecFirst != null && vecSecond != null) {
                config.getFarms().set("Farms." + getName() + ".World", locFirst.getWorld().getName());
                config.getFarms().set("Farms." + getName() + ".Min.X", vecFirst.getBlockX());
                config.getFarms().set("Farms." + getName() + ".Min.Y", vecFirst.getBlockY());
                config.getFarms().set("Farms." + getName() + ".Min.Z", vecFirst.getBlockZ());
                config.getFarms().set("Farms." + getName() + ".Max.X", vecSecond.getBlockX());
                config.getFarms().set("Farms." + getName() + ".Max.Y", vecSecond.getBlockY());
                config.getFarms().set("Farms." + getName() + ".Max.Z", vecSecond.getBlockZ());
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

    public UUID getOwnerUUID() {
        return ownerUUID;
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

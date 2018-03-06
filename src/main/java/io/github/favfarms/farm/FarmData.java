package io.github.favfarms.farm;

import io.github.favfarms.configuration.FarmConfig;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * FavFarms Created by Awesome Red on 7/15/2016.
 */
public class FarmData {

    private FarmData() {}

    private static FarmData instance = new FarmData();

    public static FarmData getInstance() {
        return instance;
    }

    private FarmConfig config = FarmConfig.getInstance();

    private String name;
    private int ID;
    private int blocks;
    private UUID ownerUUID;

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
        if (config.getFarms().get("Farms." + getName()) != null) {
            if (loc1 != null && vec1 != null && vec2 != null) {
                config.getFarms().set("Farms." + getName() + ".World", loc1.getWorld().getName());
                config.getFarms().set("Farms." + getName() + ".Min.X", vec1.getBlockX());
                config.getFarms().set("Farms." + getName() + ".Min.Y", vec1.getBlockY());
                config.getFarms().set("Farms." + getName() + ".Min.Z", vec1.getBlockZ());
                config.getFarms().set("Farms." + getName() + ".Max.X", vec2.getBlockX());
                config.getFarms().set("Farms." + getName() + ".Max.Y", vec2.getBlockY());
                config.getFarms().set("Farms." + getName() + ".Max.Z", vec2.getBlockZ());
                config.saveFarms();
            }
        }
    }

    private int getID() {
        return ID;
    }

    private String getName() {
        return name;
    }

    private UUID getOwnerUUID() {
        return ownerUUID;
    }

    private String getFarmSize() {
        String size;
        int amount = blocks;

        if (amount <= 300) {
            String XS = "Extra Small";
            size = XS + ", " + amount + " blocks";
        } else if (amount <= 700) {
            String s = "Small";
            size = s + ", " + amount + " blocks";
        } else if (amount <= 1800) {
            String m = "Medium";
            size = m + ", " + amount + " blocks";
        } else if (amount <= 2500) {
            String l = "Large";
            size = l + ", " + amount + " blocks";
        } else if (amount <= 5000) {
            String XL = "Extra Large";
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

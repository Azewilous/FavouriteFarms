package io.github.favfarms.configuration;

import io.github.favfarms.FavFarms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * FavFarms Created by Awesome Red on 7/15/2016.
 */
public class FarmConfig {

    private FarmConfig () {}

    static FarmConfig instance = new FarmConfig();

    public static FarmConfig getInstance () {
        return instance;
    }

    public static FileConfiguration fav = null;
    public static File favFile = null;

    public static FileConfiguration farms = null;
    public static File farmsFile = null;

    public static FileConfiguration animals = null;
    public static File animalsFile = null;

    public static FileConfiguration playerData = null;
    public static File playerDataFile = null;

    public static FileConfiguration savTimers = null;
    public static File savTimersFile = null;

    //Farms
    public FileConfiguration getFarms() {
        if (farms == null) {
           reloadFarms();
        }
        return farms;
    }

    public void saveFarms() {
        if (farms != null && farmsFile != null) {
            try {
                farms.save(farmsFile);
            } catch (IOException ex) {
                Bukkit.getServer().getLogger().info(ChatColor.DARK_PURPLE + "Farms Config Could Not Be Saved " + ex);
            }
        }
    }

    public void reloadFarms() {
        if (farmsFile == null) {
            if (farms.getConfigurationSection("Farms") != null) {
                farms.createSection("Farms");
            }
            if (farms.getConfigurationSection("Owners") != null) {
                farms.createSection("Owners");
            }
            farmsFile = new File(FavFarms.getInstance().getDataFolder(), "farms.yml");
        }
        farms = YamlConfiguration.loadConfiguration(farmsFile);
    }

    public void saveDefaultFarms() {
        if (farmsFile == null) {
            farmsFile = new File(FavFarms.getInstance().getDataFolder(), "farms.yml");
        }
        if (!farmsFile.exists()) {
            FavFarms.getInstance().saveResource("farms.yml", false);
        }
    }

    //Config
    public FileConfiguration getFav() {
        if (fav == null) {
            reloadFav();
        }
        return fav;
    }

    public void saveFav() {
        if (fav != null && favFile != null) {
            try {
                getFav().save(favFile);
            } catch (IOException ex) {
                Bukkit.getServer().getLogger().info(ChatColor.DARK_PURPLE + "Config Could Not Be Saved " + ex);
            }
        }
    }

    public void reloadFav() {
        if (favFile == null) {
            favFile = new File(FavFarms.getInstance().getDataFolder(), "config.yml");
        }
        fav = YamlConfiguration.loadConfiguration(favFile);
    }

    public void saveDefaultFav() {
        if (favFile == null) {
            favFile = new File(FavFarms.getInstance().getDataFolder(), "config.yml");
        }
        if (!favFile.exists()) {
            FavFarms.getInstance().saveResource("config.yml", false);
        }
    }

    //Animals
    public FileConfiguration getAnimals() {
        if (animals == null) {
            reloadAnimals();
        }
        return animals;
    }

    public void saveAnimals() {
        if (animals != null && animalsFile != null) {
            try {
                getAnimals().save(animalsFile);
            } catch (Exception ex) {
                Bukkit.getServer().getLogger().info(ChatColor.DARK_PURPLE + "Animals Config Could Not Be Saved "
                        + ex);
            }
        }
    }

    public void reloadAnimals() {
        if (animalsFile == null) {
            if (!animals.contains("Animals")) {
                animals.createSection("Animals");
            }
            animalsFile = new File(FavFarms.getInstance().getDataFolder(), "animals.yml");
        }
        animals = YamlConfiguration.loadConfiguration(animalsFile);
    }

    public void saveDefaultAnimals() {
        if (animalsFile == null) {
            animalsFile = new File(FavFarms.getInstance().getDataFolder(), "animals.yml");
        }
        if (!animalsFile.exists()) {
            FavFarms.getInstance().saveResource("animals.yml", false);
        }
    }

    //Player Data
    public FileConfiguration getPlayerData() {
        if (playerData == null) {
            reloadPlayerData();
        }
        return playerData;
    }

    public void savePlayerData() {
        if (playerData != null && playerDataFile != null) {
            try {
                getPlayerData().save(playerDataFile);
            } catch (Exception ex) {
                Bukkit.getServer().getLogger().info(ChatColor.DARK_PURPLE + "Player Data Config Could Not Be Saved "
                        + ex);
            }
        }
    }

    public void reloadPlayerData() {
        if (playerDataFile == null) {
            playerDataFile = new File(FavFarms.getInstance().getDataFolder(), "playerdata.yml");
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public void saveDefaultPlayerData() {
        if (playerDataFile == null) {
            playerDataFile = new File(FavFarms.getInstance().getDataFolder(), "playerdata.yml");
        }
        if (!playerDataFile.exists()) {
            FavFarms.getInstance().saveResource("playerdata.yml", false);
        }
    }

    //Saving Timers
    public FileConfiguration getSavTimers() {
        if (savTimers == null) {
            reloadSavTimers();
        }
        return savTimers;
    }

    public void saveSavTimers() {
        if (savTimers != null && savTimersFile != null) {
            try {
                getSavTimers().save(savTimersFile);
            } catch (Exception ex) {
                Bukkit.getServer().getLogger().info(ChatColor.DARK_PURPLE + "Timer Data Could Not Be Saved "
                        + ex);
            }
        }
    }

    public void reloadSavTimers() {
        if (savTimersFile == null) {
            savTimersFile = new File(FavFarms.getInstance().getDataFolder(), "sav.yml");
        }
        savTimers = YamlConfiguration.loadConfiguration(savTimersFile);
    }

    public void saveDefaultSavTimers() {
        if (savTimersFile == null) {
            savTimersFile = new File(FavFarms.getInstance().getDataFolder(), "sav.yml");
        }
        if (!savTimersFile.exists()) {
            FavFarms.getInstance().saveResource("sav.yml", false);
        }
    }

}

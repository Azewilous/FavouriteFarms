package io.github.favfarms;

import io.github.favfarms.configuration.FarmConfig;
import io.github.favfarms.farm.FarmMethods;
import io.github.favfarms.listener.FarmExecutor;
import io.github.favfarms.listener.FarmHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class FavFarms extends JavaPlugin {

    public static Plugin instance = null;

    public ConsoleCommandSender console = Bukkit.getConsoleSender();

    FarmMethods method = FarmMethods.getInstance();

    @Override
	public void onEnable() {

        FavFarms.instance = getInstance();
        getCommand("farm").setExecutor(new FarmExecutor(this));
        getServer().getPluginManager().registerEvents(new FarmHandler(this), instance);

        console.sendMessage(ChatColor.LIGHT_PURPLE + "[Favourite Farms] Initiating - Check Config");

        setupConfigs();
        safeLoadFiles();
	}
	
	@Override
	public void onDisable() {

        safeSaveFiles();
        saveConfigs();

        console.sendMessage(ChatColor.LIGHT_PURPLE + "[Favourite Farms ]  De-Initiating");
        instance = null;
    }

    public static Plugin getInstance() {
        if (instance == null) {
            instance = Bukkit.getServer().getPluginManager().getPlugin("FavouriteFarms");
        }
        return instance;
     }

     public void safeLoadFiles() {
         for (Player players : Bukkit.getOnlinePlayers()) {
             FarmMethods.getInstance().loadAnimals(players);
         }
         method.loadFarmLevel();
         method.loadFarmExp();
         method.loadOwners();
     }

    public void safeSaveFiles() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            method.saveAnimals(player);
            method.saveFarmLevel(player);
            method.saveFarmExp(player);
        }
        method.saveOwners();
    }

    public void setupConfigs() {
        FarmConfig fc = FarmConfig.getInstance();

        fc.saveDefaultFav();
        fc.saveDefaultFarms();
        fc.saveDefaultAnimals();
        fc.saveDefaultPlayerData();

    }

    public void saveConfigs() {
        FarmConfig fc = FarmConfig.getInstance();

        fc.saveFav();
        fc.saveFarms();
        fc.saveAnimals();
        fc.savePlayerData();
    }

}

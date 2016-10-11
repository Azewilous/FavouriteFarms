package io.github.favfarms;

import io.github.favfarms.configuration.FarmConfig;
import io.github.favfarms.executor.FarmExecutor;
import io.github.favfarms.farm.FarmMethods;
import io.github.favfarms.listener.FarmHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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
        safeLoadFile();
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

    public void safeSaveFiles() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            method.saveAnimals(player);
            method.saveFarmLevel(player);
            method.saveFarmExp(player);
        }
        method.saveSavTimers();
    }

    public void safeLoadFile() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            method.loadAnimals(player);
            method.loadFarmLevel(player);
            method.loadFarmExp(player);
        }
        method.loadSavTimers();
    }

    public void setupConfigs() {
        FarmConfig fc = FarmConfig.getInstance();

        fc.saveDefaultFav();
        fc.saveDefaultFarms();
        fc.saveDefaultAnimals();
        fc.saveDefaultPlayerData();
        fc.saveDefaultSavTimers();
    }

    public void saveConfigs() {
        FarmConfig fc = FarmConfig.getInstance();

        fc.saveFav();
        fc.saveFarms();
        fc.saveAnimals();
        fc.savePlayerData();
        fc.saveSavTimers();
    }

    public static void update() {
        File src = new File("C:\\Users\\Awesome Red\\Documents\\GitHub\\FavouriteFarms\\build\\libs" +
                "\\FavouriteFarms-1.0-SNAPSHOT.jar");
        File dest = new File("C:\\Users\\Awesome Red\\Desktop\\SpigotDev\\Spigot Server\\plugins" +
                "\\FavouriteFarms-1.0-SNAPSHOT.jar");
        try {
            copyFileUsingChannel(src, dest);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "reload");
            Bukkit.getServer().broadcastMessage("Updated 15");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }finally{
            if (sourceChannel != null) {
                sourceChannel.close();
            }
            if (destChannel != null) {
                destChannel.close();
            }
        }
    }

}

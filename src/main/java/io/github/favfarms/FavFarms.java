package io.github.favfarms;

import io.github.favfarms.configuration.FarmConfig;
import io.github.favfarms.configuration.FarmText;
import io.github.favfarms.executor.FarmExecutor;
import io.github.favfarms.farm.FarmData;
import io.github.favfarms.farm.FarmMethods;
import io.github.favfarms.item.FarmItems;
import io.github.favfarms.listener.FarmHandler;
import io.github.favfarms.navigation.FarmNavigation;
import io.github.favfarms.select.SelectionTool;
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

    FarmMethods method;
    FarmConfig fConfig;
    FarmText text;
    FarmNavigation nav;
    FarmItems items;
    FarmData data;
    SelectionTool tool;

    @Override
	public void onEnable() {

        FavFarms.instance = getInstance();
        getCommand("farm").setExecutor(new FarmExecutor(this));
        getServer().getPluginManager().registerEvents(new FarmHandler(this), instance);

        console.sendMessage(ChatColor.LIGHT_PURPLE + "[Favourite Farms] Initiating - Check Config");

        method = FarmMethods.getInstance();
        fConfig = FarmConfig.getInstance();
        text = FarmText.getInstance();
        nav = FarmNavigation.getInstance();
        items = FarmItems.getInstance();
        data = FarmData.getInstance();
        tool = SelectionTool.getInstance();

        setupConfigs();
        safeLoadFile();
	}

	@Override
	public void onDisable() {

        safeSaveFiles();
        //saveConfigs();
        fConfig.saveDefaultFav();
        fConfig.saveDefaultFarms();
        fConfig.saveDefaultAnimals();
        fConfig.saveDefaultPlayerData();
        fConfig.saveDefaultSavTimers();
        fConfig.saveDefaultFarmMail();

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
        fConfig.saveDefaultFav();
        fConfig.saveDefaultFarms();
        fConfig.saveDefaultAnimals();
        fConfig.saveDefaultPlayerData();
        fConfig.saveDefaultSavTimers();
        fConfig.saveDefaultFarmMail();
    }

    /*
    public void saveConfigs() {
        FarmConfig fConfig = FarmConfig.getInstance();

        fConfig.saveFav();
        fConfig.saveFarms();
        fConfig.saveAnimals();
        fConfig.savePlayerData();
        fConfig.saveSavTimers();
        fConfig.saveFarmMail();
    }
    */

    public static void update() {
        try {
            File src = new File("C:\\Users\\Awesome Red\\Documents\\GitHub\\FavouriteFarms\\build\\libs" +
                    "\\FavouriteFarms-1.0-SNAPSHOT.jar");
            File dest = new File(FavFarms.getInstance().getClass().getProtectionDomain().getCodeSource().getLocation()
                    .toURI().getPath() + "\\FavouriteFarms-1.0-SNAPSHOT.jar");

            copyFileUsingChannel(src, dest);
            Bukkit.getServer().broadcastMessage(dest.toString());
        } catch (Exception ex) {
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
        } finally {
            if (sourceChannel != null) {
                sourceChannel.close();
            }
            if (destChannel != null) {
                destChannel.close();
            }
        }
    }

}

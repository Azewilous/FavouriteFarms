package io.github.favfarms.select;

import io.github.favfarms.configuration.FarmConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * FavFarms Created by Awesome Red on 7/15/2016.
 */
public class SelectionTool {

    private SelectionTool() {}

    private static SelectionTool instance = new SelectionTool();

    public static SelectionTool getInstance(){
        return instance;
    }

    FarmConfig config = FarmConfig.getInstance();

    public ItemStack createTool() {
        FileConfiguration favCfg = config.getFav();
        String itemName = favCfg.getString("Selector.Material").toUpperCase();
        String itemDisplay = favCfg.getString("Selector.DisplayName");
        ItemStack selTool = new ItemStack(Material.getMaterial(itemName));
        ItemMeta toolMeta = selTool.getItemMeta();
        toolMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemDisplay));
        selTool.setItemMeta(toolMeta);
        return selTool;
    }

    public ItemStack createCatcher() {
        FileConfiguration favCfg = config.getFav();
        String itemName = favCfg.getString("Catcher.Material").toUpperCase();
        String itemDisplay = favCfg.getString("Catcher.DisplayName");
        ItemStack catcher = new ItemStack(Material.getMaterial(itemName));
        ItemMeta catcherMeta = catcher.getItemMeta();
        catcherMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemDisplay));
        catcher.setItemMeta(catcherMeta);
        catcher.setAmount(favCfg.getInt("Catcher.Amount"));
        return catcher;
    }

    public ItemStack getTool() {
        ItemStack item = createTool();
        if (item != null) {
            return item;
        }
        return null;
    }

    public ItemStack getCatcher() {
        ItemStack item = createCatcher();
        if (item != null) {
            return item;
        }
        return null;
    }

}

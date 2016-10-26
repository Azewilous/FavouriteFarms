package io.github.favfarms.ability;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

/**
 * FavouriteFarms Created by Awesome Red on 10/19/2016.
 */
public class AbilityItems {

    private static AbilityItems instance = new AbilityItems();

    public static AbilityItems getInstance() {
        return instance;
    }

    @SuppressWarnings("deprecation")
    public ItemStack createSprinklerDisplay(Animals animal) {
        Potion potion = new Potion(PotionType.WATER, 1, false, false);
        ItemStack display = potion.toItemStack(1);
        ItemMeta displayMeta = display.getItemMeta();
        displayMeta.setDisplayName(ChatColor.DARK_GRAY + "Sprinkler");
        List<String> lore = new ArrayList<>();
        lore.add(org.bukkit.ChatColor.DARK_GRAY + "UUID: " + animal.getUniqueId());
        displayMeta.setLore(lore);
        display.setItemMeta(displayMeta);
        return display;
    }

    public ItemStack createSnowBlowerDisplay(Animals animal) {
        ItemStack display = new ItemStack(Material.SNOW_BALL, 1);
        ItemMeta displayMeta = display.getItemMeta();
        displayMeta.setDisplayName(ChatColor.DARK_GRAY + "Snow_Blower");
        List<String> lore = new ArrayList<>();
        lore.add(org.bukkit.ChatColor.DARK_GRAY + "UUID: " + animal.getUniqueId());
        displayMeta.setLore(lore);
        display.setItemMeta(displayMeta);
        return display;
    }

}

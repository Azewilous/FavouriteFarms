package io.github.favfarms.farm;

import io.github.favfarms.item.FarmItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * FavouriteFarms Created by Awesome Red on 9/10/2016.
 */
public class FarmInventory {

    private FarmInventory() {}

    private static FarmInventory instance = new FarmInventory();

    public static FarmInventory getInstance() {
        return instance;
    }

    public Inventory createFarmInventory(Player player, List<Animals> animals) {
        int count = -1;
        Inventory farmInv = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA + player.getName() + "'s Farm");
        for (Animals animal : animals) {
            count++;
            farmInv.setItem(count, FarmMethods.getInstance().getItemForAnimal(animal));
        }
        return farmInv;
    }

    @SuppressWarnings("deprecation")
    public Inventory createAnimalInventory(Player player, Animals animal) {
        Inventory animalInv = Bukkit.createInventory(player, 9, ChatColor.GRAY + animal.getName() + "'s Inventory");
        animalInv.setItem(0, FarmItems.getInstance().createTeleportHereItem(animal));
        animalInv.setItem(2, FarmItems.getInstance().createHomeItem(animal));
        animalInv.setItem(3, FarmItems.getInstance().createModificationItem(animal));
        animalInv.setItem(5, FarmItems.getInstance().createFreezeAnimalItem(animal));
        animalInv.setItem(6, FarmItems.getInstance().createTeleportToItem(animal));
        animalInv.setItem(8, FarmItems.getInstance().createReturnItem(animal));
        if (animal instanceof Sheep) {
            animalInv.setItem(4, FarmItems.getInstance().createSheepFurReset(animal));
        }

        if (animal instanceof Ocelot) {
            animalInv.setItem(4, FarmItems.getInstance().createTameItem(animal));
        }

        if (animal instanceof Horse) {
            animalInv.setItem(4, FarmItems.getInstance().createTameItem(animal));
        }

        if (animal instanceof Wolf) {
            animalInv.setItem(4, FarmItems.getInstance().createTameItem(animal));
        }
        return animalInv;
    }

    @SuppressWarnings("deprecation")
    public Inventory createModificationInventory(Player player, Animals animal) {
        Inventory modifyInv = Bukkit.createInventory(player, 36, ChatColor.BLUE + "Customize " + animal.getName());
        modifyInv.setItem(8, FarmItems.getInstance().createReturnItem(animal));
        int count = 0;
        if (animal instanceof Ocelot) {
            for (Ocelot.Type type : Ocelot.Type.values()) {
                if (count == 8) {
                    count = 10;
                }
                if (!(type.name().equalsIgnoreCase("none"))) {
                    modifyInv.setItem(count, FarmItems.getInstance().createOcelotChangeItem(animal, type));
                    count = count + 2;
                }
            }
        } else if (animal instanceof Sheep || animal instanceof Wolf) {
            count = 0;
            for (DyeColor color : DyeColor.values()) {
                if (count == 8) {
                    count = 10;
                }
                if (!(color.name().equalsIgnoreCase("none"))) {
                    modifyInv.setItem(count, FarmItems.getInstance().createAnimalColorItem(animal, color));
                    count = count + 2;
                }
            }
        } else if (animal instanceof Horse) {
            count = 0;
            for (Horse.Variant variant : Horse.Variant.values()) {
                if (count == 8) {
                    count = 10;
                }
                if ((!variant.name().equalsIgnoreCase("none"))) {
                    modifyInv.setItem(count, FarmItems.getInstance().createHorseChangeItem(animal, variant));
                    count = count + 2;
                }
            }
            for (Horse.Style style : Horse.Style.values()) {
                if ((!style.name().equalsIgnoreCase("none"))) {
                    modifyInv.setItem(count, FarmItems.getInstance().createHorseStyleItem(animal, style));
                    count = count + 2;
                }
            }
            for (Horse.Color color : Horse.Color.values()) {
                if (!(color.name().equalsIgnoreCase("none"))) {
                    modifyInv.setItem(count, FarmItems.getInstance().createHorseColorItem(animal, color));
                    count = count + 2;
                }
            }
        }
        return modifyInv;
    }

}
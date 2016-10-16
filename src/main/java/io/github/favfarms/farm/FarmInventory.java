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

    public Inventory createFarmListInventory(Player player, List<String> farms) {
        Inventory farmListInv = Bukkit.createInventory(player, 54, ChatColor.DARK_GRAY + player.getName() + " Farms");
        int count = 0;
        for (String str : farms) {
            farmListInv.setItem(count, FarmMethods.getInstance().getItemForFarm(str));
            count++;
        }
        return farmListInv;
    }

    public Inventory createFarmInventory(Player player, List<Animals> animals) {
        int count = 0;
        Inventory farmInv = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA + player.getName() + "'s Farm");
        for (Animals animal : animals) {
            farmInv.setItem(count, FarmMethods.getInstance().getItemForAnimal(animal));
            count++;
        }
        return farmInv;
    }

    @SuppressWarnings("deprecation")
    public Inventory createAnimalInventory(Player player, Animals animal) {
        if (animal != null) {
            Inventory animalInv = Bukkit.createInventory(player, 9, ChatColor.GRAY + animal.getName() + "'s Inventory");
            animalInv.setItem(0, FarmItems.getInstance().createTeleportHereItem(animal));
            animalInv.setItem(2, FarmItems.getInstance().createHomeItem(animal));
            animalInv.setItem(3, FarmItems.getInstance().createModificationItem(animal));
            animalInv.setItem(5, FarmItems.getInstance().createFreezeAnimalItem(animal));
            animalInv.setItem(6, FarmItems.getInstance().createTeleportToItem(animal));
            animalInv.setItem(7, FarmItems.getInstance().createTransferBetweenFarms(animal, player));
            animalInv.setItem(8, FarmItems.getInstance().createReturnItem(animal));
            if (animal instanceof Sheep) {
                animalInv.setItem(4, FarmItems.getInstance().createSheepFurReset(animal));
                animalInv.setItem(1, FarmItems.getInstance().createFurResetUsages(player));
            }

            if (animal instanceof Ocelot) {
                animalInv.setItem(4, FarmItems.getInstance().createTameItem(animal));
                animalInv.setItem(1, FarmItems.getInstance().createTameUsages(player));
            }

            if (animal instanceof Horse) {
                animalInv.setItem(4, FarmItems.getInstance().createTameItem(animal));
                animalInv.setItem(1, FarmItems.getInstance().createTameUsages(player));
            }

            if (animal instanceof Wolf) {
                animalInv.setItem(4, FarmItems.getInstance().createTameItem(animal));
                animalInv.setItem(1, FarmItems.getInstance().createTameUsages(player));
            }
            return animalInv;
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public Inventory createModificationInventory(Player player, Animals animal) {
        if (animal != null) {
            Inventory modifyInv = Bukkit.createInventory(player, 36, ChatColor.BLUE + "Customize " + animal.getName());
            modifyInv.setItem(8, FarmItems.getInstance().createReturnItem(animal));
            modifyInv.setItem(15, FarmItems.getInstance().createStyleSetUsages(player));
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
        return null;
    }

}

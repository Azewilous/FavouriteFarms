package io.github.favfarms.item;

import io.github.favfarms.configuration.FarmConfig;
import io.github.favfarms.farm.FarmMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * FavouriteFarms Created by Awesome Red on 9/10/2016.
 */
public class FarmItems {

    private FarmItems() {}

    private static FarmItems instance = new FarmItems();

    public static FarmItems getInstance() {
        return instance;
    }

    FarmConfig config = FarmConfig.getInstance();
    FarmMethods method = FarmMethods.getInstance();

    public ItemStack createEgg(Animals animal) {
        ItemStack egg = new ItemStack(Material.MONSTER_EGG, 1);
        ItemMeta eggMeta = egg.getItemMeta();
        eggMeta.setDisplayName(ChatColor.GOLD + animal.getName());
        List<String> lore = new ArrayList<>();
        if (animal.getCustomName() != null) {
            lore.add(ChatColor.BLUE + "NickName: " + animal.getCustomName());
        } else {
            lore.add(ChatColor.BLUE + "NickName: None");
        }
        lore.add(ChatColor.BLUE + "IsAdult: " + animal.isAdult());
        lore.add(ChatColor.BLUE + "Age: " + animal.getAge());
        lore.add(ChatColor.BLUE + "AgeLock: " + animal.getAgeLock());
        lore.add(ChatColor.BLUE + "Health: " + animal.getHealth());
        lore.add(ChatColor.BLUE + "Breedable: " + animal.canBreed());
        lore.add(ChatColor.GRAY + "UUID: " + animal.getUniqueId());
        eggMeta.setLore(lore);
        egg.setItemMeta(eggMeta);
        return setMonsterEggType(egg, FarmMethods.getInstance().getEntityType(animal));
    }

    public ItemStack createTeleportHereItem(Animals animal) {
        ItemStack paper = new ItemStack(Material.PAPER, 1);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName(ChatColor.GREEN + "Location " + animal.getName());
        List<String> lore = new ArrayList<>();
        if (animal.getCustomName() != null){
            lore.add(ChatColor.DARK_AQUA + "Click To Teleport " + animal.getCustomName() + " Here");
        } else
            lore.add(ChatColor.DARK_AQUA + "Click To Teleport " + animal.getName() + " Here");
        lore.add(ChatColor.AQUA + "World: " + animal.getLocation().getWorld().getName());
        lore.add(ChatColor.AQUA + "Location X: " + animal.getLocation().getX());
        lore.add(ChatColor.AQUA + "Location Y: " + animal.getLocation().getY());
        lore.add(ChatColor.AQUA + "Location Z: " + animal.getLocation().getX());
        lore.add(ChatColor.GRAY + "UUID: " + animal.getUniqueId());
        paperMeta.setLore(lore);
        paper.setItemMeta(paperMeta);
        return paper;
    }

    public ItemStack createHomeItem(Animals animal) {
        ItemStack paper = new ItemStack(Material.EMPTY_MAP, 1);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName(ChatColor.GREEN + "Send Home " + animal.getName());
        List<String> lore = new ArrayList<>();
        if (animal.getCustomName() != null){
            lore.add(ChatColor.DARK_AQUA + "Click To Send Home " + animal.getCustomName());
        } else
            lore.add(ChatColor.DARK_AQUA + "Click To Send Home " + animal.getName());
        lore.add(ChatColor.AQUA + "World: " + animal.getLocation().getWorld().getName());
        lore.add(ChatColor.AQUA + "Location X: " + animal.getLocation().getX());
        lore.add(ChatColor.AQUA + "Location Y: " + animal.getLocation().getY());
        lore.add(ChatColor.AQUA + "Location Z: " + animal.getLocation().getX());
        lore.add(ChatColor.GRAY + "UUID: " + animal.getUniqueId());
        paperMeta.setLore(lore);
        paper.setItemMeta(paperMeta);
        return paper;
    }

    public ItemStack createTeleportToItem(Animals animal) {
        ItemStack paper = new ItemStack(Material.MAP, 1);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName(ChatColor.GREEN + "Location " + animal.getName());
        List<String> lore = new ArrayList<>();
        if (animal.getCustomName() != null){
            lore.add(ChatColor.DARK_AQUA + "Click To Teleport To " + animal.getCustomName());
        } else
            lore.add(ChatColor.DARK_AQUA + "Click To Teleport To " + animal.getName());
        lore.add(ChatColor.AQUA + "World: " + animal.getLocation().getWorld().getName());
        lore.add(ChatColor.AQUA + "Location X: " + animal.getLocation().getX());
        lore.add(ChatColor.AQUA + "Location Y: " + animal.getLocation().getY());
        lore.add(ChatColor.AQUA + "Location Z: " + animal.getLocation().getX());
        lore.add(ChatColor.GRAY + "UUID: " + animal.getUniqueId());
        paperMeta.setLore(lore);
        paper.setItemMeta(paperMeta);
        return paper;
    }

    public ItemStack createSheepFurReset(Animals animal) {
        if (animal instanceof Sheep) {
            ItemStack reset = new ItemStack(Material.WHEAT);
            ItemMeta resetMeta = reset.getItemMeta();
            resetMeta.setDisplayName(ChatColor.GREEN + "Replenish " + animal.getName());
            List<String> lore = new ArrayList<>();
            Sheep sheep = (Sheep) animal;
            lore.add(ChatColor.AQUA + "Sheared: " + sheep.isSheared());
            lore.add(ChatColor.GRAY + "UUID: " + animal.getUniqueId());
            resetMeta.setLore(lore);
            reset.setItemMeta(resetMeta);
            return reset;
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public ItemStack createTameItem(Animals animal) {
        if (animal instanceof Ocelot) {
            Ocelot ocelot = (Ocelot) animal;
            ItemStack tame = new ItemStack(Material.RAW_FISH);
            ItemMeta tameMeta = tame.getItemMeta();
            tameMeta.setDisplayName(ChatColor.GREEN + "Tame " + ocelot.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "Tamed: " + ocelot.isTamed());
            if (ocelot.getOwner() != null) {
                lore.add(ChatColor.AQUA + "Owner: " + ocelot.getOwner().getName());
            }
            lore.add(ChatColor.GRAY + "UUID: " + ocelot.getUniqueId());
            tameMeta.setLore(lore);
            tame.setItemMeta(tameMeta);
            return tame;
        } else if (animal instanceof Horse) {
            Horse horse = (Horse) animal;
            ItemStack tame = new ItemStack(Material.APPLE);
            ItemMeta tameMeta = tame.getItemMeta();
            tameMeta.setDisplayName(ChatColor.GREEN + "Tame " + horse.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "Tamed: " + horse.isTamed());
            if (horse.getOwner() != null) {
                lore.add(ChatColor.AQUA + "Owner: " + horse.getOwner().getName());
            }
            lore.add(ChatColor.GRAY + "UUID: " + horse.getUniqueId());
            tameMeta.setLore(lore);
            tame.setItemMeta(tameMeta);
            return tame;
        } else if (animal instanceof Wolf) {
            Wolf wolf = (Wolf) animal;
            ItemStack tame = new ItemStack(Material.BONE);
            ItemMeta tameMeta = tame.getItemMeta();
            tameMeta.setDisplayName(ChatColor.GREEN + "Tame " + wolf.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "Tamed: " + wolf.isTamed());
            if (wolf.getOwner() != null) {
                lore.add(ChatColor.AQUA + "Owner: " + wolf.getOwner().getName());
            }
            lore.add(ChatColor.GRAY + "UUID: " + wolf.getUniqueId());
            tameMeta.setLore(lore);
            tame.setItemMeta(tameMeta);
            return tame;
        }
        return null;
    }

    public ItemStack createFreezeAnimalItem(Animals animal) {
        ItemStack freeze = new ItemStack(Material.NETHER_STAR);
        ItemMeta freezeMeta = freeze.getItemMeta();
        freezeMeta.setDisplayName(ChatColor.GREEN + "Freeze " + animal.getName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "Frozen: " + animal.hasAI());
        lore.add(ChatColor.GRAY + "UUID: " + animal.getUniqueId());
        freezeMeta.setLore(lore);
        freeze.setItemMeta(freezeMeta);
        return freeze;
    }

    public ItemStack createModificationItem(Animals animal) {
        ItemStack modify = new ItemStack(Material.BOOK_AND_QUILL);
        ItemMeta modifyMeta = modify.getItemMeta();
        modifyMeta.setDisplayName(ChatColor.GREEN + "Modify " + animal.getName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "UUID: " + animal.getUniqueId());
        modifyMeta.setLore(lore);
        modify.setItemMeta(modifyMeta);
        return modify;
    }

    public ItemStack createOcelotChangeItem(Animals animal, Ocelot.Type type) {
        Ocelot ocelot = (Ocelot) animal;
        ItemStack change = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta changeMeta = change.getItemMeta();
        changeMeta.setDisplayName(ChatColor.GREEN + "Change " + ocelot.getName() + " Type To " + type.name());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "Type: " + type.name());
        lore.add(ChatColor.GRAY + "UUID: " + animal.getUniqueId());
        changeMeta.setLore(lore);
        change.setItemMeta(changeMeta);
        return change;
    }

    @SuppressWarnings("deprecation")
    public ItemStack createAnimalColorItem(Animals animal, DyeColor color) {
        ItemStack ink = new ItemStack(Material.INK_SACK, 1);
        ink.setDurability(color.getDyeData());
        ItemMeta inkMeta = ink.getItemMeta();
        inkMeta.setDisplayName(ChatColor.GREEN + "Color " + animal.getName() + " To " + color.name());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "Color: " + color.name());
        lore.add(ChatColor.GRAY + "UUID: " + animal.getUniqueId());
        inkMeta.setLore(lore);
        ink.setItemMeta(inkMeta);
        return ink;
    }

    @SuppressWarnings("deprecation")
    public ItemStack createHorseChangeItem(Animals animal, Horse.Variant variant) {
        Horse horse = (Horse) animal;
        ItemStack change = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta changeMeta = change.getItemMeta();
        changeMeta.setDisplayName(ChatColor.GREEN + "Change " + horse.getName() + " Variant To " + variant.name());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "Variant: " + variant.name());
        lore.add(ChatColor.GRAY + "UUID: " + animal.getUniqueId());
        changeMeta.setLore(lore);
        change.setItemMeta(changeMeta);
        return change;
    }

    @SuppressWarnings("deprecation")
    public ItemStack createHorseStyleItem(Animals animal, Horse.Style style) {
        Horse horse = (Horse) animal;
        ItemStack change = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta changeMeta = change.getItemMeta();
        changeMeta.setDisplayName(ChatColor.GREEN + "Change " + horse.getName() + " Style To " + style.name());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "Style: " + style.name());
        lore.add(ChatColor.GRAY + "UUID: " + animal.getUniqueId());
        changeMeta.setLore(lore);
        change.setItemMeta(changeMeta);
        return change;
    }

    @SuppressWarnings("deprecation")
    public ItemStack createHorseColorItem(Animals animal, Horse.Color color) {
        Horse horse = (Horse) animal;
        ItemStack change = new ItemStack(Material.CLAY_BALL);
        ItemMeta changeMeta = change.getItemMeta();
        changeMeta.setDisplayName(ChatColor.GREEN + "Change " + horse.getName() + " Color To " + color.name());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "Color: " + color.name());
        lore.add(ChatColor.GRAY + "UUID: " + animal.getUniqueId());
        changeMeta.setLore(lore);
        change.setItemMeta(changeMeta);
        return change;
    }

    public ItemStack createReturnItem(Animals animal) {
        ItemStack back = new ItemStack(Material.STAINED_CLAY, 1, (short)14);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "Return To Farm Inventory");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.MAGIC + "UUID: " + animal.getUniqueId());
        backMeta.setLore(lore);
        back.setItemMeta(backMeta);
        return back;
    }

    public ItemStack createChanceModifier(Player player) {
        Long remaining = config.getFav().getLong("Delays.IncreasedRateTime");
        int percentage = config.getFav().getInt("Enhancement.CatchRateIncrease");
        String time = remaining + " Minutes";
        String percent = percentage + "% Increase";
        ItemStack increase = new ItemStack(Material.PRISMARINE_SHARD, 1);
        ItemMeta increaseMeta = increase.getItemMeta();
        increaseMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Increase Catch Rate");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_AQUA + percent);
        lore.add(ChatColor.DARK_AQUA + time);
        lore.add(ChatColor.GRAY + "" + ChatColor.MAGIC + player.getUniqueId());
        increaseMeta.setLore(lore);
        increase.setItemMeta(increaseMeta);
        return increase;
    }

    public ItemStack createResetCatcherCooldown() {
        ItemStack reset = new ItemStack(Material.WATCH, 1);
        ItemMeta resetMeta = reset.getItemMeta();
        resetMeta.setDisplayName(ChatColor.BLACK  + "" + ChatColor.MAGIC + "Catcher Cooldown Reset");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Reset Catcher Cooldown");
        resetMeta.setLore(lore);
        reset.setItemMeta(resetMeta);
        return reset;
    }

    public ItemStack createTameUsages(Player player) {
        ItemStack usage = new ItemStack(Material.GHAST_TEAR, 1);
        ItemMeta usageMeta = usage.getItemMeta();
        Integer amount = method.getTameUsages(player);
        usageMeta.setDisplayName(ChatColor.GOLD + "(" + amount + ") " + ChatColor.GREEN + "Tame Usages");
        usage.setItemMeta(usageMeta);
        return usage;
    }

    public ItemStack createFurResetUsages(Player player) {
        ItemStack usage = new ItemStack(Material.GHAST_TEAR, 1);
        ItemMeta usageMeta = usage.getItemMeta();
        Integer amount = method.getReplenishUsage(player);
        usageMeta.setDisplayName(ChatColor.GOLD + "(" + amount + ") " + ChatColor.GREEN + "Fur Reset Usages");
        usage.setItemMeta(usageMeta);
        return usage;
    }

    public ItemStack createStyleSetUsages(Player player) {
        ItemStack usage = new ItemStack(Material.GHAST_TEAR, 1);
        ItemMeta usageMeta = usage.getItemMeta();
        Integer amount = method.getStyleChangeUsage(player);
        usageMeta.setDisplayName(ChatColor.GOLD + "(" + amount + ") " + ChatColor.GREEN + "Style Set Usages");
        usage.setItemMeta(usageMeta);
        return usage;
    }

    @SuppressWarnings("deprecation")
    private ItemStack setMonsterEggType(ItemStack item, EntityType type) {
        if ((item != null) && (item.getType() == Material.MONSTER_EGG) && (type != null) && (type.getName() != null))
        {
            try
            {
                String version = Bukkit.getServer().getClass().toString().split("\\.")[3];
                Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + version
                        + ".inventory.CraftItemStack");

                Object nmsItemStack = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, item);
                Object nbtTagCompound = Class.forName("net.minecraft.server." + version + ".NBTTagCompound")
                        .newInstance();

                Field nbtTagCompoundField = nmsItemStack.getClass().getDeclaredField("tag");
                nbtTagCompoundField.setAccessible(true);

                nbtTagCompound.getClass().getMethod("setString", String.class, String.class).invoke(nbtTagCompound, "id"
                        , type.getName());
                nbtTagCompound.getClass().getMethod("set", String.class, Class.forName("net.minecraft.server." + version
                        + ".NBTBase")).invoke(nbtTagCompoundField.get(nmsItemStack), "EntityTag", nbtTagCompound);

                item = (ItemStack)  craftItemStack.getDeclaredMethod("asCraftMirror", nmsItemStack.getClass())
                        .invoke(null, nmsItemStack);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return item;
    }

}
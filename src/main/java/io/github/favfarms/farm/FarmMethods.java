package io.github.favfarms.farm;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.favfarms.FavFarms;
import io.github.favfarms.configuration.FarmConfig;
import io.github.favfarms.item.FarmItems;
import io.github.favfarms.permission.FarmPermissions;
import io.github.favfarms.select.SelectionTool;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

/**
 * FavFarms Created by Awesome Red on 8/24/2016.
 */
public class FarmMethods {
//Fix ClassDefNotFoundWGPRRG
    public FarmMethods() {}

    public static FarmMethods instance = new FarmMethods();

    public static FarmMethods getInstance() {
        return instance;
    }

    Farm farm = Farm.getInstance();
    FarmConfig config = FarmConfig.getInstance();
    SelectionTool sel = SelectionTool.getInstance();

    int ID = 0;

    public HashMap<Player, Vector> max = new HashMap<>();
    public HashMap<Player, Vector> min = new HashMap<>();

    public HashMap<Player, Vector> blkVec1 = new HashMap<>();
    public HashMap<Player, Vector> blkVec2 = new HashMap<>();
    public HashMap<Player, Location> blkLoc1 = new HashMap<>();
    public HashMap<Player, Location> blkLoc2 = new HashMap<>();
    public HashMap<Player, Vector> vector1 = new HashMap<>();
    public HashMap<Player, Vector> vector2 = new HashMap<>();

    public boolean isShowing = false;
    boolean caught = false;
    public boolean obtainable = false;

    public int count = 0;

    public List<Player> capture = new ArrayList<>();

    List<Block> blocksOrigs = new ArrayList<>();
    List<Block> blocksNew = new ArrayList<>();
    List<Material> materials = new ArrayList<>();
    List<Byte> values = new ArrayList<>();
    List<UUID> owners = new ArrayList<>();

    HashMap<UUID, Integer> level = new HashMap<>();
    HashMap<UUID, Integer> experience = new HashMap<>();
    //CoolDowns

    //Farm Animal Data
    Multimap<UUID, UUID> animalsMap = ArrayListMultimap.create();
    Multimap<UUID, Boolean> delayed = ArrayListMultimap.create();
    //End Of

    public void createFarmData(Player player, String farmName) {
        if (getFarmsOwned(player.getUniqueId()) < 1) {
            if (delayed.get(player.getUniqueId()).contains(false)) {
                if (!farmExist(farmName)) {
                    setID();
                    owners.add(player.getUniqueId());
                    saveOwners();
                    farm.createFarm(farmName, player.getUniqueId(), ID, blocks, getLevel(player), getExp(player));
                    createFarmArea(player, blkLoc1.get(player));
                    createPlayerData(player, farmName);
                    delayed.put(player.getUniqueId(), true);
                    applyCooldown(player, 10L);
                    player.sendMessage(ChatColor.GREEN + "Created Farm " + farmName + " with bounds min("
                            + max.get(player) + ") - max (" + min.get(player) + ")");
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "This Farm Name Is Taken");
                }
            }
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "Currently Max Number Of Farms Per Player Is 1,"
                    + " In Future Updates More Will Be Available");
        }
    }

    HashMap<UUID, String> farms = new HashMap<>();

    public void createPlayerData(Player player, String farmName) {
        farms.put(player.getUniqueId(), farmName);
        UUID uuid = player.getUniqueId();
        config.getPlayerData().set("PlayerData." + uuid + ".PlayerName", player.getName());
        config.getPlayerData().set("PlayerData." + uuid + ".FarmName", farms.get(player.getUniqueId()));
        config.getPlayerData().set("PlayerData." + uuid + ".FarmsOwned", getFarmsOwned(player.getUniqueId()));
        config.savePlayerData();
    }

    public Integer getFarmsOwned(UUID id) {
        if (config.getPlayerData().get("PlayerData") != null) {
            if (config.getPlayerData().get("PlayerData." + id + ".FarmsOwned") != null) {
                return config.getPlayerData().getInt("PlayerData." + id + ".FarmsOwned");
            } else {
                config.getPlayerData().set("PlayerData." + id + ".FarmsOwned", 1);
                return config.getPlayerData().getInt("PlayerData." + id + ".FarmsOwned");
            }
        }
        return null;
    }

    public void createFarmArea(Player player, Location blockLoc1) {
        farm.createArea(vector1.get(player), vector2.get(player), blockLoc1);
    }

    public int blocks = 0;

    public void calculateFarmSize(Player player, BlockVector blockVec1, BlockVector blockVec2) {
        blkVec1.put(player, blockVec1);
        blkVec2.put(player, blockVec2);
        blkLoc1.put(player, blockVec1.toLocation(player.getWorld()));
        blkLoc2.put(player, blockVec2.toLocation(player.getWorld()));
        vector1.put(player, new Vector(blockVec1.getX(), blockVec1.getY() - 10, blockVec1.getZ()));
        vector2.put(player, new Vector(blockVec2.getX(), blockVec2.getY() + 20, blockVec2.getZ()));
        min.put(player, Vector.getMinimum(vector1.get(player), vector2.get(player)));
        max.put(player, Vector.getMaximum(vector1.get(player), vector2.get(player)));
        for (int x = min.get(player).getBlockX(); x <= max.get(player).getBlockX(); x++) {
            for (int z = min.get(player).getBlockZ(); z <= max.get(player).getBlockZ(); z++) {
                blocks++;
                for (int y = min.get(player).getBlockY(); y <= max.get(player).getBlockY(); y++) {
                    locations.add(new Location(player.getWorld(), x, y, z));
                }
            }
        }
    }

    /*
    public void expandVertically (Player player) {
        Vector vertical1 = new Vector(vector1.get(player).getX(), 0, vector1.get(player).getZ());
        Vector vertical2 = new Vector(vector1.get(player).getX(), 256, vector1.get(player).getZ());

        int blks = 0;
        min = Vector.getMinimum(vertical1, vertical2);
        max = Vector.getMaximum(vertical1, vertical2);
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    blks++;
                }
            }
        }
        blocks = blks;
        player.sendMessage("Farm Has Been Expanded Vertically");
        if (isValidFarmSize()) {
            count--;
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "Selection must be larger than 15 blocks");
        }
    }

    public void checkExpand (Player player) {
        new FancyMessage("Would You Like To Expand Farm Vertically For Protection ")
                .color(ChatColor.AQUA)
                .then("Yes")
                .color(ChatColor.GREEN)
                .tooltip("Best Option Allow For Greater Protection")
                .color(ChatColor.AQUA)
                .style(ChatColor.BOLD)
                .command("/farm expand")
                .then("No")
                .color(ChatColor.RED)
                .style(ChatColor.BOLD)
                .command("/farm cancel")
                .tooltip(" Lest Protection, And Selection Must Be More Than 2 Blocks High")
                .send(player);
    }

    public void cancelExpand (Player player) {
        Vector vec1 = new Vector(vector1.get(player).getX(), vector1.get(player).getY(), vector1.get(player).getZ());
        Vector vec2 = new Vector(vector2.get(player).getX(), vector2.get(player).getY(), vector2.get(player).getZ());

        int blks = 0;
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    blks++;
                }
            }
        }
        blocks = blks;
        if (!(vec2.getY() - vec1.getY() > 2)) {
            player.sendMessage(ChatColor.DARK_AQUA + "Selection Height Must Be Greater Than 2");
        }
    }
    */
    @SuppressWarnings("deprecation")
    public void showFarmSelection(Player player) {
        if (min == null && max == null) {
            int x1 = config.getFarms().getInt("Farms." + player.getUniqueId() + "Start.VectorX");
            int y1 = config.getFarms().getInt("Farms." + player.getUniqueId() + "Start.VectorY");
            int z1 = config.getFarms().getInt("Farms." + player.getUniqueId() + "Start.VectorZ");
            int x2 = config.getFarms().getInt("Farms." + player.getUniqueId() + "End.VectorX");
            int y2 = config.getFarms().getInt("Farms." + player.getUniqueId() + "End.VectorY");
            int z2 = config.getFarms().getInt("Farms." + player.getUniqueId() + "End.VectorZ");
            Vector vec1 = new Vector(x1, y1, z1);
            Vector vec2 = new Vector(x2, y2, z2);
            min.put(player, Vector.getMinimum(vec1, vec2));
            max.put(player, Vector.getMaximum(vec1, vec2));
        }
        World world = player.getWorld();
        savePreviousBlocks(player);
        blocksNew.add(world.getBlockAt(min.get(player).getBlockX(), min.get(player).getBlockY()
                , min.get(player).getBlockZ()));
        blocksNew.add(world.getBlockAt(max.get(player).getBlockX(), max.get(player).getBlockY()
                , max.get(player).getBlockZ()));
        for (Block block : blocksNew) {
            block.setType(Material.STAINED_GLASS);
            block.setData(DyeColor.PURPLE.getData());
            hideFarmSelection(player);
            isShowing = true;
        }
    }

    @SuppressWarnings("deprecation")
    public void hideFarmSelection(final Player player) {
        final World world = player.getWorld();
        final BukkitScheduler scheduler = FavFarms.getInstance().getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(FavFarms.getInstance(), () -> {
            if (isShowing) {
                for (Block blockOrig : blocksOrigs) {
                    for (Material material : materials) {
                        for (Byte value : values) {
                            world.getBlockAt(blockOrig.getLocation()).setType(material);
                            world.getBlockAt(blockOrig.getLocation()).setData(value);
                        }
                    }
                }
                player.sendMessage(ChatColor.DARK_AQUA + "Highlights Have Faded");
                isShowing = false;
            }
        }, 20 * 5);
    }

    public void applyCooldown(final Player player, Long time) {
        final BukkitScheduler scheduler = FavFarms.getInstance().getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(FavFarms.getInstance(), () -> {
            for (Boolean bool : delayed.get(player.getUniqueId())) {
                if (bool) {
                    player.sendMessage(ChatColor.DARK_AQUA + "This Command Is Still On Cooldown");
                }
            }
            player.sendMessage(ChatColor.DARK_AQUA + "You Can Now Create A Farm Again");
            delayed.put(player.getUniqueId(), false);
        }, (20 * 60) * time);
    }

    @SuppressWarnings("deprecation")
    public void savePreviousBlocks(Player player) {
        World world = player.getWorld();

        blocksOrigs.add(world.getBlockAt(min.get(player).getBlockX(), min.get(player).getBlockY()
                , min.get(player).getBlockZ()));
        materials.add(world.getBlockAt(min.get(player).getBlockX(), min.get(player).getBlockY()
                , min.get(player).getBlockZ()).getType());
        values.add(world.getBlockAt(min.get(player).getBlockX(), min.get(player).getBlockY()
                , min.get(player).getBlockZ()).getData());

        blocksOrigs.add(world.getBlockAt(max.get(player).getBlockX(), max.get(player).getBlockY()
                , max.get(player).getBlockZ()));
        materials.add(world.getBlockAt(max.get(player).getBlockX(), max.get(player).getBlockY()
                , max.get(player).getBlockZ()).getType());
        values.add(world.getBlockAt(max.get(player).getBlockX(), max.get(player).getBlockY()
                , max.get(player).getBlockZ()).getData());
    }

    public void reloadFavFarms() {
        config.reloadFav();
        config.reloadFarms();
        config.reloadAnimals();

        FavFarms.getInstance().getPluginLoader().disablePlugin(FavFarms.getInstance());
        FavFarms.getInstance().getPluginLoader().enablePlugin(FavFarms.getInstance());
    }

    public boolean isSelection() {
        return getBlocks() != 0;
    }

    public boolean isValidFarmSize() {
        return getBlocks() > 15;
    }

    public Integer getBlocks() {
        return blocks;
    }

    List<Location> locations = new ArrayList<>();

    public boolean isValidFarmLocation(Player player, BlockVector blockVecFirst, BlockVector blockVecSecond) {
        World world = player.getWorld();
        if (!hasEntitiesInArea(player, blockVecFirst, blockVecSecond)) {

            Plugin pluginGP = getServer().getPluginManager().getPlugin("GriefPrevention");

            Location loc1 = new Location(world, blockVecFirst.getBlockX(), blockVecFirst.getBlockY(), blockVecFirst.getBlockZ());

            if (pluginGP != null) {
                if (GriefPrevention.instance.dataStore != null) {
                    Claim claim = null;
                    if (GriefPrevention.instance.dataStore.getClaims().size() != 0) {
                        for (Location tLoc : locations) {
                            claim = GriefPrevention.instance.dataStore.getClaimAt(tLoc, true, null);
                        }
                    }
                    if (claim != null) {
                        if (!claim.getOwnerName().equalsIgnoreCase(player.getName())) {
                            player.sendMessage(ChatColor.DARK_AQUA + "You Cannot Create A Farm In This Claim");
                            return false;
                        } else {
                            player.sendMessage(ChatColor.RED + "[Warning]"
                                    + ChatColor.DARK_AQUA + " You Are Creating A Farm That Intersects A Claim");
                            return true;
                        }
                    } else {
                        player.sendMessage("Claim Null");
                    }
                }
            }

            Plugin pluginWG = getServer().getPluginManager().getPlugin("WorldGuard");
            if (pluginWG != null) {

                WorldGuardPlugin wg = (WorldGuardPlugin) pluginWG;


                if (new RegionPermissionModel(wg, player).mayIgnoreRegionProtection(world)) {
                    return true;
                }

                RegionManager manager = wg.getRegionManager(world);
                LocalPlayer lp = (LocalPlayer) player;
                if (manager != null) {
                    Map<String, ProtectedRegion> rgs = WorldGuardPlugin.inst().getRegionManager(loc1.getWorld()).getRegions();
                    for (ProtectedRegion rg : rgs.values()) {
                        for (Location loc : locations) {
                            if (rg.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                                if (!rg.isOwner(lp)) {
                                    player.sendMessage(ChatColor.DARK_AQUA + "You Cannot Create A Farm In This Region");
                                    return false;
                                } else {
                                    player.sendMessage(ChatColor.RED + "[Warning]"
                                            + ChatColor.DARK_AQUA + " You Are Creating A Farm That Intersects A Region");
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "You Cannot Create A Farm That Has Mobs In It");
        }
        return true;
    }

    public boolean intersectsWithFarm(Location location, Player player) {
        int locX = location.getBlockX();
        int locY = location.getBlockY();
        int locZ = location.getBlockZ();

        int x = 0;
        int y = 0;
        int z = 0;

        int x1 = 0;
        int y1 = 0;
        int z1 = 0;

        if (min.get(player) == null || max.get(player) == null) {
            if (config.getFarms().getInt("Amount") != 0) {
                for (String keys : config.getFarms().getConfigurationSection("Farms").getKeys(false)) {
                    UUID ownerUUID = UUID.fromString(keys);
                    x = config.getFarms().getInt("Farms." + ownerUUID + ".Start.VectorX");
                    y = config.getFarms().getInt("Farms." + ownerUUID + ".Start.VectorY");
                    z = config.getFarms().getInt("Farms." + ownerUUID + ".Start.VectorZ");

                    x1 = config.getFarms().getInt("Farms." + ownerUUID + ".End.VectorX");
                    y1 = config.getFarms().getInt("Farms." + ownerUUID + ".End.VectorY");
                    z1 = config.getFarms().getInt("Farms." + ownerUUID + ".End.VectorZ");
                }
            } else {
                return false;
            }
        }

        if ((locX >= x && locX <= x1) || (locX <= x && locX >= x1)) {
            if ((locZ >= z && locZ <= z1) || (locZ <= z && locZ >= z1)) {
                if ((locY >= y && locY <= y1) || (locY <= y && locY >= y1)) {
                    if (player.hasPermission(FarmPermissions.BYPASS_FARM_AREA.toString())) {
                        player.sendMessage(ChatColor.AQUA + "The Selection Intersects With A Farm");
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasEntitiesInArea(Player player, BlockVector blockVector1, BlockVector blockVector2) {
        List<Entity> temp = getServer().getWorld(player.getWorld().getUID()).getEntities();
        for (Entity mob : temp) {
            double pX = mob.getLocation().getX();
            double pY = mob.getLocation().getY();
            double pZ = mob.getLocation().getZ();

            double x = blockVector1.getX();
            double y = blockVector1.getY();
            double z = blockVector1.getZ();

            double x1 = blockVector2.getX();
            double z1 = blockVector2.getZ();
            double y1 = blockVector2.getY() + 5;

            if ((pX >= x && pX <= x1) || (pX <= x && pX >= x1)) {
                if ((pZ >= z && pZ <= z1) || (pZ <= z && pZ >= z1)) {
                    if ((pY >= y && pY <= y1) || (pY <= y && pY >= y1)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean checkPlayerInFarm(Player player) {
        double pX = player.getLocation().getX();
        double pY = player.getLocation().getY();
        double pZ = player.getLocation().getZ();

        int x = 0;
        int y = 0;
        int z = 0;

        int x1 = 0;
        int y1 = 0;
        int z1 = 0;

        for (String keys : config.getFarms().getConfigurationSection("Farms").getKeys(false)) {
            UUID ownerUUID = UUID.fromString(keys);
            x = config.getFarms().getInt("Farms." + ownerUUID + ".Start.VectorX");
            y = config.getFarms().getInt("Farms." + ownerUUID + ".Start.VectorY");
            z = config.getFarms().getInt("Farms." + ownerUUID + ".Start.VectorZ");

            x1 = config.getFarms().getInt("Farms." + ownerUUID + ".End.VectorX");
            y1 = config.getFarms().getInt("Farms." + ownerUUID + ".End.VectorY");
            z1 = config.getFarms().getInt("Farms." + ownerUUID + ".End.VectorZ");
        }

        if ((pX >= x && pX <= x1) || (pX <= x && pX >= x1)) {
            if ((pZ >= z && pZ <= z1) || (pZ <= z && pZ >= z1)) {
                if ((pY >= y && pY <= y1) || (pY <= y && pY >= y1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkEntityInFarm(Entity entity) {
        if (entity instanceof Animals) {
            Animals animal = ((Animals) entity);
            double pX = animal.getLocation().getX();
            double pY = animal.getLocation().getY();
            double pZ = animal.getLocation().getZ();

            int x = 0;
            int y = 0;
            int z = 0;

            int x1 = 0;
            int y1 = 0;
            int z1 = 0;

            for (String keys : config.getFarms().getConfigurationSection("Farms").getKeys(false)) {
                UUID ownerUUID = UUID.fromString(keys);
                x = config.getFarms().getInt("Farms." + ownerUUID + ".Start.VectorX");
                y = config.getFarms().getInt("Farms." + ownerUUID + ".Start.VectorY");
                z = config.getFarms().getInt("Farms." + ownerUUID + ".Start.VectorZ");

                x1 = config.getFarms().getInt("Farms." + ownerUUID + ".End.VectorX");
                y1 = config.getFarms().getInt("Farms." + ownerUUID + ".End.VectorY");
                z1 = config.getFarms().getInt("Farms." + ownerUUID + ".End.VectorZ");
            }

            if ((pX >= x && pX <= x1) || (pX <= x && pX >= x1)) {
                if ((pZ >= z && pZ <= z1) || (pZ <= z && pZ >= z1)) {
                    if ((pY >= y && pY <= y1) || (pY <= y && pY >= y1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*
    public void animalDropItem (LivingEntity animal) {
        if (checkEntityInFarm(animal)) {

        }
    }
    */

    public void setFarmSpawn (Player player) {
        if (hasFarm(player.getUniqueId())) {
            if (checkPlayerInFarm(player)) {
                UUID ownerUUID = player.getUniqueId();
                World world = player.getWorld();
                double x = player.getLocation().getX();
                double y = player.getLocation().getY() + 2;
                double z = player.getLocation().getZ();
                config.getFarms().set("Farms." + ownerUUID + ".World", world.getName());
                config.getFarms().set("Farms." + ownerUUID + ".Spawn.X", x);
                config.getFarms().set("Farms." + ownerUUID + ".Spawn.Y", y);
                config.getFarms().set("Farms." + ownerUUID + ".Spawn.Z", z);
                config.saveFarms();
                player.sendMessage(ChatColor.DARK_AQUA + "Farm Spawn Set At (X:" + x + ", Y: " + y + ", Z: " + z);
            } else {
                player.sendMessage (ChatColor.DARK_AQUA + "You Must Be In Your Farm To Set A Spawn Point");
            }
        } else {
            player.sendMessage (ChatColor.DARK_AQUA + "You Do Not Own A Farm");
        }
    }

    public Location getFarmSpawn (Player player) {
        if (hasFarm(player.getUniqueId())) {
            UUID ownerUUID = player.getUniqueId();
            World world = FavFarms.getInstance().getServer().getWorld(config.getFarms().getString("Farms." + ownerUUID
                    + ".World"));
            double x = config.getFarms().getDouble("Farms." + ownerUUID + ".Spawn.X");
            double y = config.getFarms().getDouble("Farms." + ownerUUID + ".Spawn.Y");
            double z = config.getFarms().getDouble("Farms." + ownerUUID + ".Spawn.Z");
            return new Location(world, x, y, z);
        }
        return null;
    }

    public Player getPlayerFromUUID(UUID uuid) {
        return FavFarms.getInstance().getServer().getPlayer(uuid);
    }

    public void removeFarm(Player player) {
        if (hasFarm(player.getUniqueId())) {
            if (config.getFarms().getString("Farms." + player.getUniqueId()) != null) {
                config.getFarms().set("Farms." + player.getUniqueId(), null);
                int amount = config.getFarms().getInt("Amount");
                amount--;
                config.getFarms().set("Amount", amount);
                config.getPlayerData().set("PlayerData." + player.getUniqueId() + ".FarmsOwned", amount);
                if (getFarmsOwned(player.getUniqueId()) == 0) {
                    owners.remove(player.getUniqueId());
                    config.getFarms().set("Owners." + owners, null);
                }
            }
        }
    }

    public void removeFarmAnimal(Animals animal, Player player) {
        UUID animalUUID = animal.getUniqueId();
        if (hasOwner(animalUUID)) {
            if (hasFarm(player.getUniqueId())) {
                animalsMap.remove(player.getUniqueId(), animalUUID);
                config.getAnimals().get("Animals." + player.getUniqueId() + ".Ids." + animalUUID, null);
                saveAnimals(player);
                player.sendMessage(ChatColor.DARK_AQUA + animal.getName() + " Has Been Removed From Farm");
            }
        }
    }

    public void addFarmAnimal(Animals animal, Player player) {
        if (hasFarm(player.getUniqueId())) {
            UUID animalUUID = animal.getUniqueId();
            if (!checkEntityInFarm(animal)) {
                if (!hasOwner(animalUUID)) {
                    if (getFarmSpawn(player) != null) {
                        obtainable = true;
                        calculateCatchRate(animal);
                        if (hasCaught()) {
                            caught = false;
                            if (animal instanceof Wolf) {
                                Wolf wolf = (Wolf) animal;
                                wolf.setSitting(true);
                            } else if (animal instanceof Ocelot) {
                                Ocelot ocelot = (Ocelot) animal;
                                ocelot.setSitting(true);
                            }
                            animal.teleport(getFarmSpawn(player));
                            animalsMap.put(player.getUniqueId(), animalUUID);
                            giveExp(player, animal);
                            player.sendMessage(ChatColor.DARK_AQUA + animal.getName() + " Has Been Added To Farm");
                            player.sendMessage(ChatColor.YELLOW + "You Have Gained " + calculateExpGain(animal)
                                    + " Experience");
                        } else {
                            player.sendMessage(ChatColor.DARK_AQUA + "You Almost Caught " + animal.getName());
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_AQUA + "You Have Not Set A Spawn For Your Farm Yet");
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "This Animal Has An Owner");
                }
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "This Animal Is In A Farm");
            }
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Own A Farm");
        }
    }

    public boolean hasOwner(UUID animalUUID) {
        for (UUID value : animalsMap.values()) {
            if (value.equals(animalUUID)) {
                return true;
            }
        }
        return false;
    }

    public Player getOwnerFromAnimal(UUID animalUUID) {
        for (UUID ids : animalsMap.keySet()) {
            if (animalsMap.get(ids).contains(animalUUID)) {
                return getPlayerFromUUID(ids);
            }
        }
        return null;
    }

    public boolean isFarmAnimal(UUID animalUUID) {
        return animalsMap.values().contains(animalUUID);
    }

    public void getFarmAnimals(UUID uuid, World world) {
        if (hasFarm(uuid)) {
            Collection<UUID> ids = animalsMap.get(uuid);
            for (UUID animalIds : ids) {
                if (getAnimalFromUUID(animalIds, world) != null) {
                    getPlayerFromUUID(uuid).sendMessage(ChatColor.DARK_AQUA + "Animals "
                            + getAnimalFromUUID(animalIds, world).getName());
                }
            }
        } else
            getPlayerFromUUID(uuid).sendMessage(ChatColor.DARK_AQUA + "This Player Does Not Have A Farm");
    }

    public Inventory getFarmInv(Player player) {
        Collection<UUID> ids = animalsMap.get(player.getUniqueId());
        List<Animals> temp = new ArrayList<>();
        for (UUID animalIds : ids) {
            if (getAnimalFromUUID(animalIds, player.getWorld()) instanceof LivingEntity) {
                temp.add((Animals) getAnimalFromUUID(animalIds, player.getWorld()));
            }
        }
        return FarmInventory.getInstance().createFarmInventory(player, temp);
    }

    public void openFarmInv(Player player) {
        player.openInventory(getFarmInv(player));
    }

    public void openFarmInv(Player player, Player target) {
        player.openInventory(getFarmInv(target));
    }

    public Inventory getAnimalInv(Player player, ItemStack item) {
        Animals animal = (Animals) getAnimalFromItem(item, player.getWorld());

        return FarmInventory.getInstance().createAnimalInventory(player, animal);
    }

    public void openAnimalInv(Player player, ItemStack item) {
        player.openInventory(getAnimalInv(player, item));
    }

    public Inventory getModifyInv(Player player, ItemStack item) {
        Animals animal = (Animals) getAnimalFromItem(item, player.getWorld());

        return FarmInventory.getInstance().createModificationInventory(player, animal);
    }

    public void openModifyInv(Player player, ItemStack item) {
        player.openInventory(getModifyInv(player, item));
    }

    public Entity getAnimalFromUUID(UUID animalID, World world) {
        List<Entity> animals = getServer().getWorld(world.getName()).getEntities();
        for (Entity animal : animals) {
            if (animalID.equals(animal.getUniqueId())) {
                return animal;
            }
        }
        return null;
    }

    public boolean hasFarm(UUID uuid) {
        return config.getFarms().get("Farms." + uuid) != null;
    }

    public void getFarmInfo(Player player, UUID uuid) {
        String info;
        if (hasFarm(uuid)) {
            String owner = ChatColor.BLUE + "Owner UUID: " + config.getFarms().get("Farms." + uuid).toString();
            String creator = ChatColor.BLUE + "Owner Name: " + config.getFarms()
                    .get("Farms." + uuid + ".Creator");
            String name = ChatColor.BLUE + "Farm Name: " + config.getFarms().get("Farms." + uuid + ".Name");
            String id = ChatColor.BLUE + "Farm ID: " + config.getFarms().get("Farms." + uuid + ".ID");
            String size = ChatColor.BLUE + "Farm Size: " + config.getFarms().get("Farms." + uuid + ".Size");

            info = owner + "\n" + creator + "\n" + name + "\n" + id + "\n" + size;
        } else {
            info = ChatColor.DARK_AQUA + "This Player Does Not Have A Farm";
        }
        player.sendMessage(info);
    }

    public void getFarmInfo(Player player) {
        String info;
        UUID uuid = player.getUniqueId();
        if (hasFarm(uuid)) {
            String owner = ChatColor.BLUE + "Owner UUID: " + config.getFarms().get("Farms." + uuid).toString();
            String creator = ChatColor.BLUE + "Owner Name: " + config.getFarms()
                    .get("Farms." + uuid + ".Creator");
            String name = ChatColor.BLUE + "Farm Name: " + config.getFarms().get("Farms." + uuid + ".Name");
            String id = ChatColor.BLUE + "Farm ID: " + config.getFarms().get("Farms." + uuid + ".ID");
            String size = ChatColor.BLUE + "Farm Size: " + config.getFarms().get("Farms." + uuid + ".Size");

            info = owner + "\n" + creator + "\n" + name + "\n" + id + "\n" + size;
        } else {
            info = ChatColor.DARK_AQUA + "This Player Does Not Have A Farm";
        }
        player.sendMessage(info);
    }

    public String getFarmSize(UUID uuid) {
        String str = config.getFarms().getString("Farms." + uuid + ".Size");
        return str.substring(0, str.indexOf(","));
    }

    public boolean hasSpace(UUID uuid) {
        if (animalsMap.get(uuid) == null) {
            return true;
        }
        if (animalsMap.get(uuid) != null) {
            if (getFarmSize(uuid).equals("Extra Small")) {
                if (animalsMap.get(uuid).size() <= 8) {
                    return true;
                }
            }
            if (getFarmSize(uuid).equals("Small")) {
                if (animalsMap.get(uuid).size() <= 14) {
                    return true;
                }
            }
            if (getFarmSize(uuid).equals("Medium")) {
                if (animalsMap.get(uuid).size() <= 26) {
                    return true;
                }
            }
            if (getFarmSize(uuid).equals("Large")) {
                if (animalsMap.get(uuid).size() <= 34) {
                    return true;
                }
            }
            if (getFarmSize(uuid).equals("Extra Larger")) {
                if (animalsMap.get(uuid).size() <= 64) {
                    return true;
                }
            }
        }
        return false;
    }

    public void giveCatcher(Player player) {
        Inventory inv = player.getInventory();
        if (hasEmptySlots(inv)) {
            inv.addItem(sel.getCatcher());
            player.sendMessage(ChatColor.DARK_AQUA + "You Have Been given (" + sel.getCatcher().getAmount()
                    + ") Catchers");
        }
    }

    public Entity getAnimalFromItem(ItemStack item, World world) {
        ItemMeta meta = item.getItemMeta();
        UUID uuid = null;
        for (String id : meta.getLore()) {
            if (id.contains("UUID")) {
                uuid = UUID.fromString(id.substring(8));
            }
        }
        return getAnimalFromUUID(uuid, world);
    }

    public void teleportPlayerToAnimal(Player player, ItemStack item) {
        Animals animal = (Animals) getAnimalFromItem(item, player.getWorld());
        player.teleport(animal);
        player.sendMessage(ChatColor.AQUA + "You Have Teleported To " + animal.getName());
    }

    public void teleportAnimalToPlayer(Player player, ItemStack item) {
        Animals animal = (Animals) getAnimalFromItem(item, player.getWorld());
        animal.teleport(player);
        if (animal instanceof Ocelot) {
            Ocelot ocelot = (Ocelot) animal;
            if (ocelot.isSitting()) {
                ocelot.setSitting(false);
            }
        }
        player.sendMessage(ChatColor.AQUA + animal.getName() + " Has Been Teleported To You");
    }

    public void teleportAnimalHome(Player player, ItemStack item) {
        Animals animal = (Animals) getAnimalFromItem(item, player.getWorld());
        animal.teleport(getFarmSpawn(player));
        if (animal instanceof Ocelot) {
            Ocelot ocelot = (Ocelot) animal;
            ocelot.setSitting(true);
        }
        player.sendMessage(ChatColor.AQUA + animal.getName() + " Has Been Sent Home");
    }

    public void resetSheepFur(ItemStack item, World world) {
        Animals animal = (Animals) getAnimalFromItem(item, world);
        if (animal instanceof Sheep) {
            Sheep sheep = (Sheep) animal;
            if (sheep.isSheared()) {
                sheep.setSheared(false);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void tameAnimal(ItemStack item, Player player) {
        World world = player.getWorld();
        Animals animal = (Animals) getAnimalFromItem(item, world);
        if (animal instanceof Ocelot) {
            Ocelot ocelot = (Ocelot) animal;
            if (!ocelot.isTamed()) {
                ocelot.setTamed(true);
                ocelot.setOwner(player);
            } else {
                player.sendMessage(ChatColor.AQUA + "This Animal Is Already Tamed");
            }
        } else if (animal instanceof Horse) {
            Horse horse = (Horse) animal;
            if (!horse.isTamed()) {
                horse.setTamed(true);
                horse.setOwner(player);
            } else {
                player.sendMessage(ChatColor.AQUA + "This Animal Is Already Tamed");
            }
        } else if (animal instanceof Wolf){
            Wolf wolf = (Wolf) animal;
            if (!wolf.isTamed()) {
                wolf.setTamed(true);
                wolf.setOwner(player);
            } else {
                player.sendMessage(ChatColor.AQUA + "This Animal Is Already Tamed");
            }
        }
    }

    public void setOcelotType(ItemStack item, Player player) {
        World world = player.getWorld();
        Animals animal = (Animals) getAnimalFromItem(item, world);
        Ocelot.Type type = getOcelotTypeFromString(item);
        if (animal instanceof Ocelot) {
            Ocelot ocelot = (Ocelot) animal;
            if (ocelot.isTamed()) {
                ocelot.setCatType(type);
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "This Animal Is Not Tamed");
            }
        }
    }

    public void colorAnimal(ItemStack item, Player player, DyeColor color) {
        World world = player.getWorld();
        Animals animal = (Animals) getAnimalFromItem(item, world);
        if (animal instanceof Sheep) {
            Sheep sheep = (Sheep) animal;
            sheep.setColor(color);
        } else if (animal instanceof Wolf) {
            Wolf wolf = (Wolf) animal;
            if (wolf.isTamed()) {
                wolf.setCollarColor(color);
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "This Animal Is Not Tamed");
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void setHorseVariant(ItemStack item, Player player) {
        World world = player.getWorld();
        Animals animal = (Animals) getAnimalFromItem(item, world);
        Horse.Variant variant = getHorseVariantFromString(item);
        if (animal instanceof Horse) {
            Horse horse = (Horse) animal;
            if (horse.isTamed()) {
                horse.setVariant(variant);
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "This Animal Is Not Tamed");
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void setHorseStyle(ItemStack item, Player player) {
        World world = player.getWorld();
        Animals animal = (Animals) getAnimalFromItem(item, world);
        Horse.Style style = getHorseStyleFromString(item);
        if (animal instanceof Horse) {
            Horse horse = (Horse) animal;
            if (horse.isTamed()) {
                horse.setStyle(style);
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "This Animal Is Not Tamed");
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void setHorseColor(ItemStack item, Player player) {
        World world = player.getWorld();
        Animals animal = (Animals) getAnimalFromItem(item, world);
        Horse.Color color = getHorseColorFromString(item);
        if (animal instanceof Horse) {
            Horse horse = (Horse) animal;
            if (horse.isTamed()) {
                horse.setColor(color);
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "This Animal Is Not Tamed");
            }
        }
    }

    public void freezeAnimal(ItemStack item, Player player) {
        World world = player.getWorld();
        Animals animal = (Animals) getAnimalFromItem(item, world);
        if (!animal.hasAI()) {
            animal.setAI(true);
            player.sendMessage(ChatColor.AQUA + "You Have Defrosted " + animal.getName());
        } else if (animal.hasAI()){
            animal.setAI(false);
            player.sendMessage(ChatColor.AQUA + "You Have Frozen " + animal.getName() + " In Place");
        }
    }

    /*public String getAnimalName(ItemStack item, World world) {
        return getAnimalFromItem(item, world).getName();
    }
    */

    public Ocelot.Type getOcelotTypeFromString(ItemStack item) {
        Ocelot.Type type = null;
        ItemMeta meta = item.getItemMeta();
        for (Ocelot.Type value : Ocelot.Type.values()) {
            if (value.name().equalsIgnoreCase(meta.getLore().get(0).substring(8))) {
                type = value;
            }
        }
        return type;
    }

    public DyeColor getColorFromString(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        DyeColor color = null;
        for (DyeColor value : DyeColor.values()) {
            if (value.name().equalsIgnoreCase(meta.getLore().get(0).substring(9))) {
                color = value;
            }
        }
        return color;
    }

    @SuppressWarnings("deprecation")
    public Horse.Variant getHorseVariantFromString(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        Horse.Variant variant = null;
        for (Horse.Variant value : Horse.Variant.values()) {
            if (value.name().equalsIgnoreCase(meta.getLore().get(0).substring(11))) {
                variant = value;
            }
        }
        return variant;
    }

    @SuppressWarnings("deprecation")
    public Horse.Style getHorseStyleFromString(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        Horse.Style style = null;
        for (Horse.Style value : Horse.Style.values()) {
            if (value.name().equalsIgnoreCase(meta.getLore().get(0).substring(9))) {
                style = value;
            }
        }
        return style;
    }

    @SuppressWarnings("deprecation")
    public Horse.Color getHorseColorFromString(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        Horse.Color color = null;
        for (Horse.Color value : Horse.Color.values()) {
            if (value.name().equalsIgnoreCase(meta.getLore().get(0).substring(9))) {
                color = value;
            }
        }
        return color;
    }



    public boolean hasEmptySlots(Inventory inv) {
        return getEmptySlots(inv).size() > 0;
    }

    public List<Integer> getEmptySlots(Inventory inv) {
        List<Integer> emptySlot = new ArrayList<>();
        for (int x = 0; x < inv.getSize(); x++) {
            if (inv.getItem(x) == null) {
                emptySlot.add(x);
            }
        }
        return emptySlot;
    }

    public void levelUp(Player player) {
        if (getExp(player) >= getExpToLevel(player)) {
            int value = getLevel(player) + 1;
            level.put(player.getUniqueId(), value);
            player.sendMessage(ChatColor.DARK_AQUA + "Leveled Up To " + getLevel(player));
        }
    }

    public Integer getExpToLevel(Player player) {
        int required;
        if (getLevel(player) > 0 && getLevel(player) <= 12) {
            required = 2 * getLevel(player) + 8;
            return required;
        } else if (getLevel(player) > 13 && getLevel(player) <= 25) {
            required = 4 * getLevel(player) - 16;
            return required;
        } else if (getLevel(player) > 26 && getLevel(player) <= 35) {
            required = 6 * getLevel(player) - 32;
            return required;
        } else if (getLevel(player) > 36 && getLevel(player) <= 47) {
            required = 8 * getLevel(player) - 64;
            return required;
        } else if (getLevel(player) >= 48) {
            required = 10 * getLevel(player) - 128;
            return required;
        }
        return null;
    }

    public Integer getLevel(Player player) {
        if (level.isEmpty()) {
            level.put(player.getUniqueId(), 1);
        }
        return level.get(player.getUniqueId());
    }

    public Integer getExp(Player player) {
        if (experience.isEmpty()) {
            experience.put(player.getUniqueId(), 0);
        }
        return experience.get(player.getUniqueId());
    }

    public void giveExp(Player player, Animals animal) {
        int exp = calculateExpGain(animal);
        experience.put(player.getUniqueId(), exp);
        levelUp(player);
    }

    public void calculateCatchRate(Animals animal) {
        double rand = Math.random() * 100;
        double chance = getCatchRate(animal);
        if (chance > rand) {
            caught = true;
        }
    }

    public Integer calculateExpGain(Animals animal) {
        Random rand = new Random();
        int least = getExpGainedLeast(animal);
        int most = getExpGainedMost(animal);
        return rand.nextInt(most - least) + least;
    }

    @SuppressWarnings("deprecation")
    public Double getCatchRate(Animals animal) {
        double rate;
        if (animal instanceof Chicken) {
            rate = config.getFav().getDouble("CatchRate.Chicken");
            return rate;
        } else if (animal instanceof Cow) {
            rate = config.getFav().getDouble("CatchRate.Cow");
            return rate;
        } else if (animal instanceof Pig) {
            rate = config.getFav().getDouble("CatchRate.Pig");
            return rate;
        } else if (animal instanceof Sheep) {
            rate = config.getFav().getDouble("CatchRate.Sheep");
            return rate;
        } else if (animal instanceof Wolf) {
            rate = config.getFav().getDouble("CatchRate.Wolf");
            return rate;
        } else if (animal instanceof Horse) {
            rate = config.getFav().getDouble("CatchRate.Horse");
            return rate;
        } else if (animal instanceof Ocelot) {
            rate = config.getFav().getDouble("CatchRate.Ocelot");
            return rate;
        } else if (animal instanceof PolarBear) {
            rate = config.getFav().getDouble("CatchRate.PolarBear");
            return rate;
        } else if (animal instanceof Rabbit) {
            rate = config.getFav().getDouble("CatchRate.Rabbit");
            return rate;
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public Integer getExpGainedMost(Animals animal) {
        Integer rate;
        if (animal instanceof Chicken) {
            rate = config.getFav().getInt("Experience.Most.Chicken");
            return rate;
        } else if (animal instanceof Cow) {
            rate = config.getFav().getInt("Experience.Most.Cow");
            return rate;
        } else if (animal instanceof Pig) {
            rate = config.getFav().getInt("Experience.Most.Pig");
            return rate;
        } else if (animal instanceof Sheep) {
            rate = config.getFav().getInt("Experience.Most.Sheep");
            return rate;
        } else if (animal instanceof Wolf) {
            rate = config.getFav().getInt("Experience.Most.Wolf");
            return rate;
        } else if (animal instanceof Horse) {
            rate = config.getFav().getInt("Experience.Most.Horse");
            return rate;
        } else if (animal instanceof Ocelot) {
            rate = config.getFav().getInt("Experience.Most.Ocelot");
            return rate;
        } else if (animal instanceof PolarBear) {
            rate = config.getFav().getInt("Experience.Most.PolarBear");
            return rate;
        } else if (animal instanceof Rabbit) {
            rate = config.getFav().getInt("Experience.Most.Rabbit");
            return rate;
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public Integer getExpGainedLeast(Animals animal) {
        Integer rate;
        if (animal instanceof Chicken) {
            rate = config.getFav().getInt("Experience.Least.Chicken");
            return rate;
        } else if (animal instanceof Cow) {
            rate = config.getFav().getInt("Experience.Least.Cow");
            return rate;
        } else if (animal instanceof Pig) {
            rate = config.getFav().getInt("Experience.Least.Pig");
            return rate;
        } else if (animal instanceof Sheep) {
            rate = config.getFav().getInt("Experience.Least.Sheep");
            return rate;
        } else if (animal instanceof Wolf) {
            rate = config.getFav().getInt("Experience.Least.Wolf");
            return rate;
        } else if (animal instanceof Horse) {
            rate = config.getFav().getInt("Experience.Least.Horse");
            return rate;
        } else if (animal instanceof Ocelot) {
            rate = config.getFav().getInt("Experience.Least.Ocelot");
            return rate;
        } else if (animal instanceof PolarBear) {
            rate = config.getFav().getInt("Experience.Least.PolarBear");
            return rate;
        } else if (animal instanceof Rabbit) {
            rate = config.getFav().getInt("Experience.Least.Rabbit");
            return rate;
        }
        return null;
    }

    public boolean hasCaught() {
        return caught;
    }

    public boolean isObtainable() {
        return obtainable;
    }

    public Integer getID() {
        return config.getFarms().getInt("Amount");
    }

    public void setID() {
        ID = getID() + 1;
    }

    public void saveAnimals(Player player) {
        List<String> temp = new ArrayList<>();
        if (animalsMap != null) {
            if (hasFarm(player.getUniqueId())) {
                for (UUID ownerId : animalsMap.keySet()) {
                    for (UUID animalIds : animalsMap.get(ownerId)) {
                        String str = animalIds.toString();
                        temp.add(str);
                    }
                    config.getAnimals().set("Animals." + ownerId.toString() + ".Ids", temp);
                    config.saveAnimals();
                }
            }
        }
    }

    public void loadAnimals(Player player) {
        animalsMap = ArrayListMultimap.create();
        if (config.getAnimals().get("Animals." + player.getUniqueId()) != null) {
            List<String> strList = config.getAnimals().getStringList("Animals." + player.getUniqueId() + ".Ids");
            for (String animalIds : strList) {
                animalsMap.put(player.getUniqueId(), UUID.fromString(animalIds));
            }
        } else {
            config.getAnimals().createSection("Animals");
        }
    }

    public void saveFarmLevel(Player player) {
        if (config.getAnimals().get("Farms") != null) {
            UUID playerUUID = UUID.fromString(config.getAnimals().getString("Farms."));
            config.getFarms().set("Farms." + playerUUID + ".Level", getLevel(player));
            config.saveFarms();
        }
    }

    public void loadFarmLevel() {
        if (config.getAnimals().get("Farms") != null) {
            UUID playerUUID = UUID.fromString(config.getAnimals().getString("Farms."));
            int tempLevel = config.getFarms().getInt("Farms." + playerUUID + ".Level");
            level.put(playerUUID, tempLevel);
        }
    }

    public void saveFarmExp(Player player) {
        if (config.getAnimals().get("Farms") != null) {
            UUID playerUUID = UUID.fromString(config.getAnimals().getString("Farms."));
            config.getFarms().set("Farms." + playerUUID + ".Experience", getExp(player));
            config.saveFarms();
        }
    }

    public void loadFarmExp() {
        if (config.getAnimals().get("Farms") != null) {
            UUID playerUUID = UUID.fromString(config.getAnimals().getString("Farms."));
            int tempExp = config.getFarms().getInt("Farms." + playerUUID + ".Experience");
            experience.put(playerUUID, tempExp);
        }
    }

    public void saveOwners() {
        if (config.getFarms().get("Owners") == null) {
            config.getFarms().createSection("Owners");
            config.getFarms().set("Owners", owners.toString());
            config.saveFarms();
        } else {
            config.getFarms().set("Owners", owners.toString());
            config.saveFarms();
        }
    }

    public void loadOwners() {
        if (config.getFarms().get("Owners") != null) {
            if (owners == null) {
                for (String id : config.getFarms().getStringList("Owners")) {
                    owners.add(UUID.fromString(id));
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public EntityType getEntityType(Entity entity) {
        if (entity instanceof Chicken) {
            return EntityType.CHICKEN;
        } else if (entity instanceof Cow) {
            return EntityType.COW;
        } else if (entity instanceof Pig) {
            return EntityType.PIG;
        } else if (entity instanceof Sheep) {
            return EntityType.SHEEP;
        } else if (entity instanceof Wolf) {
            return EntityType.WOLF;
        } else if (entity instanceof Horse) {
            return EntityType.HORSE;
        } else if (entity instanceof Ocelot) {
            return EntityType.OCELOT;
        } else if (entity instanceof PolarBear) {
            return EntityType.POLAR_BEAR;
        } else if (entity instanceof Rabbit) {
            return EntityType.RABBIT;
        }
        return null;
    }


    ItemStack getItemForAnimal(Animals animal) {
        return FarmItems.getInstance().createEgg(animal);
    }

    /*
    public Short getDamageForEgg(Animals animal) {
        if (animal instanceof Chicken) {
            return 93;
        } else if (animal instanceof Cow) {
            return 92;
        } else if (animal instanceof Pig) {
            return 90;
        } else if (animal instanceof Sheep) {
            return 91;
        } else if (animal instanceof Wolf) {
            return 95;
        } else if (animal.getName().equalsIgnoreCase("Horse")) {
            return 100;
        } else if (animal instanceof Ocelot) {
            return 98;
        } else if (animal instanceof PolarBear) {
            return 102;
        } else if (animal instanceof Rabbit) {
            return 101;
        }
        return 0;
    }
    */

    public boolean isFarmEgg(ItemStack stack) {
        Short[] durability = new Short[] {0, 90, 91, 92, 93, 95, 98, 100, 101, 102};
        List<Short> values = new ArrayList<>();
        values.addAll(Arrays.asList(durability));
        if (stack.getType() == Material.MONSTER_EGG) {
            if (values.contains(stack.getDurability())) {
                return true;
            }
        }
        return false;
    }

    public boolean farmExist(String name) {
        if (config.getFarms() != null) {
            for (UUID id : owners) {
                if (!config.getFarms().getString("Farms." + id + ".Name").equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

}
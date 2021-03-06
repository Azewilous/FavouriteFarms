package io.github.favfarms.farm;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.favfarms.FavFarms;
import io.github.favfarms.ability.Abilities;
import io.github.favfarms.configuration.FarmConfig;
import io.github.favfarms.item.FarmItems;
import io.github.favfarms.navigation.FarmNavigation;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.Map.Entry;

import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getServer;

/**
 * FavFarms Created by Awesome Red on 8/24/2016.
 */
public class FarmMethods {

    private FarmMethods() {}

    public static FarmMethods instance = new FarmMethods();

    public static FarmMethods getInstance() {
        return instance;
    }

    FarmData farmData = FarmData.getInstance();
    FarmConfig config = FarmConfig.getInstance();
    SelectionTool sel = SelectionTool.getInstance();
    FarmItems fItems = FarmItems.getInstance();

    int ID = 0;

    public HashMap<Player, Vector> max = new HashMap<>();
    public HashMap<Player, Vector> min = new HashMap<>();

    public HashMap<Player, Vector> blkVec1 = new HashMap<>();
    public HashMap<Player, Vector> blkVec2 = new HashMap<>();
    public HashMap<Player, Location> blkLoc1 = new HashMap<>();
    public HashMap<Player, Location> blkLoc2 = new HashMap<>();
    public HashMap<Player, Vector> vector1 = new HashMap<>();
    public HashMap<Player, Vector> vector2 = new HashMap<>();

    HashMap<Animals, Boolean> caught = new HashMap<>();
    HashMap<Animals, Boolean> obtainable = new HashMap<>();

    public HashMap<Player, Integer> count = new HashMap<>();

    public List<Player> capture = new ArrayList<>();
    List<Location> locations = new ArrayList<>();

    HashMap<UUID, Integer> level = new HashMap<>();
    HashMap<UUID, Integer> experience = new HashMap<>();
    HashMap<UUID, Double> increasedCatchRate = new HashMap<>();

    //Item Usages
    HashMap<UUID, Integer> tameUsage = new HashMap<>();
    HashMap<UUID, Integer> ReplenishUsage = new HashMap<>();
    HashMap<UUID, Integer> StyleChangeUsage = new HashMap<>();

    //CoolDowns
    HashMap<UUID, Long> recentCreateTime = new HashMap<>();
    HashMap<UUID, Long> highlighting = new HashMap<>();
    HashMap<UUID, Long> delayCmdSend = new HashMap<>();
    HashMap<UUID, Long> catcherObtainTime = new HashMap<>();
    HashMap<UUID, Long> catchRateTime = new HashMap<>();

    //FarmData Animal Data
    Multimap<UUID, UUID> animalsMap = ArrayListMultimap.create();

    //End Of
    HashMap<UUID, Integer> blocks = new HashMap<>();

    //Tasks
    HashMap<UUID, Integer> taskID_01 = new HashMap<>();
    HashMap<UUID, Integer> taskID_02 = new HashMap<>();
    HashMap<UUID, Integer> taskID_03 = new HashMap<>();
    HashMap<Player, Integer> taskID_04 = new HashMap<>();

    //Transferring Animals
    HashMap<Player, Animals> inTransfer = new HashMap<>();

    //Toggle Abilities
    public HashMap<Animals, Boolean> toggleSnowBlower = new HashMap<>();
    public HashMap<Animals, Boolean> toggleSprinkler = new HashMap<>();
    public HashMap<Animals, Boolean> toggleShield = new HashMap<>();
    public HashMap<Animals, Boolean> toggleExplosive = new HashMap<>();

    //Toggle Follow
    Multimap<Player, Animals> following = ArrayListMultimap.create();

    //FarmData Mail
    HashMap<UUID, List<String>> farmMail = new HashMap<>();

    //Animal Info Toggle
    List<Player> animalInfo = new ArrayList<>();

    public void createFarmData(Player player, String farmName) {
        if (player.hasPermission(FarmPermissions.BYPASS_FARM_CREATE_COOLDOWN.toString()) || player.isOp()) {
            if (recentCreateTime.containsKey(player.getUniqueId())) {
                recentCreateTime.remove(player.getUniqueId());
            }
        }
        if (canCreate(player)) {
            if (!recentCreateTime.containsKey(player.getUniqueId())) {
                if (!farmExist(farmName)) {
                    setID();
                    farmData.createFarm(farmName, player.getUniqueId(), ID, blocks.get(player.getUniqueId()));

                    createFarmArea(player, blkLoc1.get(player));
                    createPlayerData(player, getLevel(player), getExp(player));

                    Long time = config.getFav().getLong("Delays.FarmCreation");

                    recentCreateTime.put(player.getUniqueId(), time);
                    applyCooldown(player);

                    player.sendMessage(ChatColor.GREEN + "Created FarmData " + farmName + " with bounds min("
                            + max.get(player) + ") - max (" + min.get(player) + ")");
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "This FarmData Name Is Taken");
                }
            } else {
                long time = recentCreateTime.get(player.getUniqueId());
                player.sendMessage(ChatColor.DARK_AQUA + "This Command Has" + ChatColor.GOLD + " (" + ChatColor.GOLD + time
                        + ") " + ChatColor.DARK_AQUA + "Minute Cooldown");
            }
        }
    }

    public void applyCooldown(final Player player) {
        final BukkitScheduler scheduler = FavFarms.getInstance().getServer().getScheduler();
        taskID_01.put(player.getUniqueId(), scheduler.scheduleSyncRepeatingTask(FavFarms.getInstance(), () -> {
            if (recentCreateTime.get(player.getUniqueId()) != 0) {
                recentCreateTime.put(player.getUniqueId(), recentCreateTime.get(player.getUniqueId()) - 1);
            } else {
                recentCreateTime.remove(player.getUniqueId());
                for (Integer taskId : taskID_01.values()) {
                    if (taskID_01.get(player.getUniqueId()).equals(taskId)) {
                        Bukkit.getServer().getScheduler().cancelTask(taskId);
                    }
                }
                player.sendMessage(ChatColor.DARK_AQUA + "You Can Now Create A FarmData Again");
            }
        }, 0, 20));
    }

    public boolean canCreate(Player player) {
        if (hasFarm(player.getUniqueId())) {
            if (!player.isOp()) {
                if (getFarmsOwned(player.getUniqueId()) >= 1) {
                    if (!player.hasPermission(FarmPermissions.COMMAND_FARM_MULTIPLE_UNLIMITED.toString())) {
                        if (player.hasPermission(FarmPermissions.COMMAND_FARM_MULTIPLE.toString())) {
                            for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
                                if (perm.getPermission().startsWith(FarmPermissions.
                                        COMMAND_FARM_MULTIPLEX.toString())) {
                                    int size = Integer.parseInt(perm.getPermission().replaceAll("[^0-9]", ""));
                                    if (size >= getFarmsOwned(player.getUniqueId())) {
                                        player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have Permission To " +
                                                "Create More Farms");
                                        return false;
                                    }
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have Permission To " +
                                    "Create More Farms");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void createPlayerData(Player player, Integer level, Integer exp) {
        config.getPlayerData().set("PlayerData." + player.getUniqueId() + ".OwnerUUID", player.getUniqueId().toString());
        config.getPlayerData().set("PlayerData." + player.getUniqueId() + ".FarmsOwned", getFarmsOwned(player.getUniqueId()));
        config.getPlayerData().set("PlayerData." + player.getUniqueId() + ".Level", level);
        config.getPlayerData().set("PlayerData." + player.getUniqueId() + ".Experience", exp);
        config.getPlayerData().set("PlayerData." + player.getUniqueId() + ".FarmBlocks", config.getFav().getInt("Size.Amount"));
        config.savePlayerData();
    }

    public Integer getFarmsOwned(UUID id) {
        Player player = getPlayer(id);
        if (config.getPlayerData().get("PlayerData") != null) {
            if (config.getPlayerData().get("PlayerData." + player.getUniqueId() + ".FarmsOwned") != null) {
                return config.getPlayerData().getInt("PlayerData." + player.getUniqueId() + ".FarmsOwned");
            } else {
                config.getPlayerData().set("PlayerData." + player.getUniqueId() + ".FarmsOwned", 1);
                return config.getPlayerData().getInt("PlayerData." + player.getUniqueId() + ".FarmsOwned");
            }
        }
        return null;
    }

    public void createFarmArea(Player player, Location blockLoc1) {
        farmData.createArea(vector1.get(player), vector2.get(player), blockLoc1);
    }

    public void calculateFarmSize(Player player, BlockVector blockVec1, BlockVector blockVec2) {
        blkVec1.put(player, blockVec1);
        blkVec2.put(player, blockVec2);
        blkLoc1.put(player, blockVec1.toLocation(player.getWorld()));
        blkLoc2.put(player, blockVec2.toLocation(player.getWorld()));
        vector1.put(player, new Vector(blockVec1.getX(), blockVec1.getY() - 15, blockVec1.getZ()));
        vector2.put(player, new Vector(blockVec2.getX(), blockVec2.getY() + 15, blockVec2.getZ()));
        min.put(player, Vector.getMinimum(vector1.get(player), vector2.get(player)));
        max.put(player, Vector.getMaximum(vector1.get(player), vector2.get(player)));
        int counter = 0;
        for (int x = min.get(player).getBlockX(); x <= max.get(player).getBlockX(); x++) {
            for (int z = min.get(player).getBlockZ(); z <= max.get(player).getBlockZ(); z++) {
                counter++;
                for (int y = min.get(player).getBlockY(); y <= max.get(player).getBlockY(); y++) {
                    locations.add(new Location(player.getWorld(), x, y, z));
                }
            }
        }
        blocks.put(player.getUniqueId(), counter);
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
        player.sendMessage("FarmData Has Been Expanded Vertically");
        if (isValidFarmSize()) {
            count--;
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "Selection must be larger than 15 blocks");
        }
    }

    public void checkExpand (Player player) {
        new FancyMessage("Would You Like To Expand FarmData Vertically For Protection ")
                .color(ChatColor.AQUA)
                .then("Yes")
                .color(ChatColor.GREEN)
                .tooltip("Best Option Allow For Greater Protection")
                .color(ChatColor.AQUA)
                .style(ChatColor.BOLD)
                .command("/farmData expand")
                .then("No")
                .color(ChatColor.RED)
                .style(ChatColor.BOLD)
                .command("/farmData cancel")
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

    public void highlightCorners(Player player) {
        min.put(player, getMinimum(player.getLocation()));
        max.put(player, getMaximum(player.getLocation()));

        World world = player.getWorld();

        int yMin = (max.get(player).getBlockY() + min.get(player).getBlockY()) / 2;
        int yMax = (max.get(player).getBlockY() + min.get(player).getBlockY()) / 2;

        Block cornerFirst;
        Block cornerSecond;

        do {
            cornerFirst = world.getBlockAt(min.get(player).getBlockX(), yMin
                    , min.get(player).getBlockZ());
            yMin++;
        } while (world.getBlockAt(max.get(player).getBlockX(), yMin - 1
                , max.get(player).getBlockZ()).getType() != Material.AIR);

        do {
            cornerSecond = world.getBlockAt(max.get(player).getBlockX(), yMax
                    , max.get(player).getBlockZ());
            yMax++;
        } while (world.getBlockAt(max.get(player).getBlockX(), yMax - 1
                , max.get(player).getBlockZ()).getType() != Material.AIR);

        highlighting.put(player.getUniqueId(), 10L);

        final HashMap<UUID, Block> blockBot = new HashMap<>();
        final HashMap<UUID, Block> blockTop = new HashMap<>();

        blockBot.put(player.getUniqueId(), cornerSecond);
        blockTop.put(player.getUniqueId(), cornerFirst);

        countDownHighlight(player);

        HashMap<Player, Integer> taskID_001 = new HashMap<>();

        final BukkitScheduler scheduler = FavFarms.getInstance().getServer().getScheduler();
        taskID_001.put(player , scheduler.scheduleSyncRepeatingTask(FavFarms.getInstance(), () -> {
            if (highlighting.get(player.getUniqueId()) != 0) {
                world.spawnParticle(Particle.SPELL_WITCH, blockTop.get(player.getUniqueId()).getLocation()
                        .add(0, 0, 0), 20, 0.0, -10, 0.0, 15);
                world.spawnParticle(Particle.SPELL_WITCH, blockBot.get(player.getUniqueId()).getLocation()
                        .add(0, 0, 0), 20, 0.0, -10, 0.0, 15);
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "FarmData Highlights Have Faded");
                Bukkit.getServer().getScheduler().cancelTask(taskID_001.get(player));
            }
        }, 0, 1));
    }

    public void countDownHighlight(Player player) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        HashMap<Player, Integer> taskID_002 = new HashMap<>();
        taskID_002.put(player, scheduler.scheduleSyncRepeatingTask(FavFarms.getInstance(), () -> {
            if (highlighting.containsKey(player.getUniqueId())) {
                if (highlighting.get(player.getUniqueId()) != 0) {
                    highlighting.put(player.getUniqueId(), highlighting.get(player.getUniqueId()) - 1);
                } else {
                    highlighting.remove(player.getUniqueId());
                    Bukkit.getServer().getScheduler().cancelTask(taskID_002.get(player));
                }
            }
        }, 0, 20));
    }

    public void delayCommand(Player player, Long time) {
        delayCmdSend.put(player.getUniqueId(), time);
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        HashMap<Player, Integer> taskID_003 = new HashMap<>();
        taskID_003.put(player, scheduler.scheduleSyncRepeatingTask(FavFarms.getInstance(), () -> {
            if (delayCmdSend.containsKey(player.getUniqueId())) {
                if (delayCmdSend.get(player.getUniqueId()) != 0) {
                    delayCmdSend.put(player.getUniqueId(), getDelayedLeft(player) - 1);
                } else {
                    delayCmdSend.remove(player.getUniqueId());
                    player.sendMessage(ChatColor.DARK_AQUA + "FarmData Commands Are Know Off Cooldown");
                    Bukkit.getServer().getScheduler().cancelTask(taskID_003.get(player));
                }
            }
        }, 0, 1));
    }

    public boolean isDelayedCommand(Player player) {
        return delayCmdSend.containsKey(player.getUniqueId());
    }

    public Long getDelayedLeft(Player player) {
        return delayCmdSend.get(player.getUniqueId());
    }

    public void reloadFavFarms() {
        config.reloadFav();
        config.reloadFarms();
        config.reloadAnimals();

        FavFarms.getInstance().getPluginLoader().disablePlugin(FavFarms.getInstance());
        FavFarms.getInstance().getPluginLoader().enablePlugin(FavFarms.getInstance());
    }

    public boolean isSelection(Player player) {
        return getBlocks(player) != 0;
    }

    public boolean isValidFarmSize(Player player) {
        if (getBlocks(player) > 15) {
            if (player.hasPermission(FarmPermissions.BYPASS_FARM_SIZE_LIMIT.toString()) || player.isOp()) {
                return true;
            }
            if (!hasFarm(player.getUniqueId())) {
                int size = config.getFav().getInt("Size.Amount");
                if (getBlocks(player) <= size) {
                    return true;
                } else {
                    int required = getBlocks(player) - size;
                    player.sendMessage(ChatColor.DARK_AQUA + "You Don't Have Enough FarmData Blocks For This Claim, You Require "
                            + required + " More Blocks");
                    return false;
                }
            } else {
                int size = config.getPlayerData().getInt("PlayerData." + player.getUniqueId() + ".FarmBlocks");
                if (getBlocks(player) <= size) {
                    return true;
                } else {
                    int required = getBlocks(player) - size;
                    player.sendMessage(ChatColor.DARK_AQUA + "You Don't Have Enough FarmData Blocks For This Claim, You Require "
                            + required + " More Blocks");
                    return false;
                }
            }
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "Selection Must Be At least 15 Blocks");
        }
        return false;
    }

    public Integer getBlocks(Player player) {
        int amount = 0;
        if (blocks.get(player.getUniqueId()) != null) {
            return blocks.get(player.getUniqueId());
        }
        if (config.getFarms().getString("Farms.") != null) {
            for (String string : getFarmsForPlayer(player)) {
                String str = config.getFarms().getString("Farms." + string + ".Size");
                str = str.replaceAll("[^0-9]+", " ");
                int newAmt = Integer.parseInt(str.trim());
                player.sendMessage("Amount " + newAmt);
                amount = amount + newAmt;
            }
        }
        return amount;
    }

    public boolean isValidFarmLocation(Player player, BlockVector blockVecFirst, BlockVector blockVecSecond) {
        World world = player.getWorld();
        if (intersectsWithFarm(blockVecFirst.toLocation(world), blockVecSecond.toLocation(world), player)) {
            player.sendMessage(ChatColor.DARK_AQUA + "This Selection Overlaps Another FarmData");
            return false;
        }
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
                            player.sendMessage(ChatColor.DARK_AQUA + "You Cannot Create A FarmData In This Claim");
                            return false;
                        } else {
                            player.sendMessage(ChatColor.RED + "[Warning]"
                                    + ChatColor.DARK_AQUA + " You Are Creating A FarmData That Intersects A Claim");
                            return true;
                        }
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
                LocalPlayer lp = wg.wrapPlayer(player);
                if (manager != null) {
                    ProtectedRegion rg = null;
                    Map<String, ProtectedRegion> rgs = WorldGuardPlugin.inst().getRegionManager(loc1.getWorld()).getRegions();
                    for (ProtectedRegion reg : rgs.values()) {
                        for (Location loc : locations) {
                            if (reg.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                                rg = reg;
                            }
                        }
                    }
                    if (rg != null) {
                        if (!rg.isOwner(lp)) {
                            player.sendMessage(ChatColor.DARK_AQUA + "You Cannot Create A FarmData In This Region");
                            return false;
                        } else {
                            player.sendMessage(ChatColor.RED + "[Warning]"
                                    + ChatColor.DARK_AQUA + " You Are Creating A FarmData That Intersects A Region");
                            return true;
                        }
                    }
                }
            }
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "You Cannot Create A FarmData That Has Mobs In It");
        }
        return true;
    }

    public boolean intersectsWithFarm(Location location1, Location location2, Player player) {
        if (config.getFarms().getInt("Amount") != 0) {
            int locX1 = location1.getBlockX();
            int locY1 = location1.getBlockY();
            int locZ1 = location1.getBlockZ();

            int locX2 = location2.getBlockX();
            int locY2 = location2.getBlockY();
            int locZ2 = location2.getBlockZ();

            int x;
            int y;
            int z;

            int x1;
            int y1;
            int z1;

            boolean check = false;

            if (min.get(player) != null || max.get(player) != null) {
                x = min.get(player).getBlockX();
                y = min.get(player).getBlockY();
                z = min.get(player).getBlockZ();

                x1 = max.get(player).getBlockX();
                y1 = max.get(player).getBlockY();
                z1 = max.get(player).getBlockZ();
            } else {
                x = getMinimum(player.getLocation()).getBlockX();
                y = getMinimum(player.getLocation()).getBlockY();
                z = getMinimum(player.getLocation()).getBlockZ();

                x1 = getMaximum(player.getLocation()).getBlockX();
                y1 = getMaximum(player.getLocation()).getBlockY();
                z1 = getMaximum(player.getLocation()).getBlockZ();
            }

            if ((locX1 >= x && locX1 <= x1) || (locX1 <= x && locX1 >= x1)) {
                if ((locZ1 >= z && locZ1 <= z1) || (locZ1 <= z && locZ1 >= z1)) {
                    if ((locY1 >= y && locY1 <= y1) || (locY1 <= y && locY1 >= y1)) {
                        check = true;
                    }
                }
            }

            if (!check) {
                if ((locX2 >= x && locX2 <= x1) || (locX2 <= x && locX2 >= x1)) {
                    if ((locZ2 >= z && locZ2 <= z1) || (locZ2 <= z && locZ2 >= z1)) {
                        if ((locY2 >= y && locY2 <= y1) || (locY2 <= y && locY2 >= y1)) {
                            check = true;
                        }
                    }
                }
            }

            if (check) {
                if (player.hasPermission(FarmPermissions.BYPASS_FARM_AREA.toString())) {
                    player.sendMessage(ChatColor.AQUA + "The Selection Intersects With A FarmData");
                    return false;
                } else {
                    return true;
                }
            }

        }
        return false;
    }

    public boolean intersectsWithFarm(Location location1, Player player) {
        if (config.getFarms().getInt("Amount") != 0) {
            int locX1 = location1.getBlockX();
            int locY1 = location1.getBlockY();
            int locZ1 = location1.getBlockZ();

            int x;
            int y;
            int z;

            int x1;
            int y1;
            int z1;

            boolean check = false;

            if (min.get(player) != null || max.get(player) != null) {
                x = min.get(player).getBlockX();
                y = min.get(player).getBlockY();
                z = min.get(player).getBlockZ();

                x1 = max.get(player).getBlockX();
                y1 = max.get(player).getBlockY();
                z1 = max.get(player).getBlockZ();
            } else {
                x = getMinimum(player.getLocation()).getBlockX();
                y = getMinimum(player.getLocation()).getBlockY();
                z = getMinimum(player.getLocation()).getBlockZ();

                x1 = getMaximum(player.getLocation()).getBlockX();
                y1 = getMaximum(player.getLocation()).getBlockY();
                z1 = getMaximum(player.getLocation()).getBlockZ();
            }

            if ((locX1 >= x && locX1 <= x1) || (locX1 <= x && locX1 >= x1)) {
                if ((locZ1 >= z && locZ1 <= z1) || (locZ1 <= z && locZ1 >= z1)) {
                    if ((locY1 >= y && locY1 <= y1) || (locY1 <= y && locY1 >= y1)) {
                        check = true;
                    }
                }
            }

            if (check) {
                if (player.hasPermission(FarmPermissions.BYPASS_FARM_AREA.toString())) {
                    player.sendMessage(ChatColor.AQUA + "The Selection Intersects With A FarmData");
                    return false;
                } else {
                    return true;
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
            double y1 = blockVector2.getY();
            double z1 = blockVector2.getZ();

            if ((pX >= x && pX <= x1) || (pX <= x && pX >= x1)) {
                if ((pZ >= z && pZ <= z1) || (pZ <= z && pZ >= z1)) {
                    if ((pY >= y && pY <= y1) || (pY <= y && pY >= y1)) {
                        return !(mob instanceof Player);
                    }
                }
            }
        }
        return false;
    }

    public boolean checkPlayerInFarm(Player player) {
        double locX = player.getLocation().getX();
        double locY = player.getLocation().getY();
        double locZ = player.getLocation().getZ();

        int x = getMinimum(player.getLocation()).getBlockX();
        int y = getMinimum(player.getLocation()).getBlockY();
        int z = getMinimum(player.getLocation()).getBlockZ();

        int x1 = getMaximum(player.getLocation()).getBlockX();
        int y1 = getMaximum(player.getLocation()).getBlockY();
        int z1 = getMaximum(player.getLocation()).getBlockZ();

        if ((locX >= x && locX <= x1) || (locX <= x && locX >= x1)) {
            if ((locZ >= z && locZ <= z1) || (locZ <= z && locZ >= z1)) {
                if ((locY >= y && locY <= y1) || (locY <= y && locY >= y1)) {
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

            int x = getMinimum(animal.getLocation()).getBlockX();
            int y = getMinimum(animal.getLocation()).getBlockY();
            int z = getMinimum(animal.getLocation()).getBlockZ();

            int x1 = getMaximum(animal.getLocation()).getBlockX();
            int y1 = getMaximum(animal.getLocation()).getBlockY();
            int z1 = getMaximum(animal.getLocation()).getBlockZ();

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
                World world = player.getWorld();
                String name = getFarmFromLocation(player.getLocation());
                if (player.getUniqueId().equals(getFarmOwner(name))) {
                    double x = player.getLocation().getX();
                    double y = player.getLocation().getY() + 2;
                    double z = player.getLocation().getZ();
                    config.getFarms().set("Farms." + name + ".Spawn.World", world.getName());
                    config.getFarms().set("Farms." + name + ".Spawn.X", x);
                    config.getFarms().set("Farms." + name + ".Spawn.Y", y);
                    config.getFarms().set("Farms." + name + ".Spawn.Z", z);
                    config.saveFarms();
                    player.sendMessage(ChatColor.DARK_AQUA + "FarmData Spawn Set At (X:" + x + ", Y: " + y + ", Z: " + z);
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "You Must Be The Owner Of The FarmData To Set It's Spawn");
                }
            } else {
                player.sendMessage (ChatColor.DARK_AQUA + "You Must Be In Your FarmData To Set A Spawn Point");
            }
        } else {
            player.sendMessage (ChatColor.DARK_AQUA + "You Do Not Own A FarmData");
        }
    }

    public UUID getFarmOwner(String name) {
        return UUID.fromString(config.getFarms().getString("Farms." + name + ".Creator"));
    }

    public Location getFarmSpawn (Player player, String name) {
        if (hasFarm(player.getUniqueId())) {
            if (config.getFarms().get("Farms." + name + ".Spawn.World") != null) {
                World world = FavFarms.getInstance().getServer().getWorld(config.getFarms().getString("Farms." + name
                        + ".Spawn.World"));
                double x = config.getFarms().getDouble("Farms." + name + ".Spawn.X");
                double y = config.getFarms().getDouble("Farms." + name + ".Spawn.Y");
                double z = config.getFarms().getDouble("Farms." + name + ".Spawn.Z");
                return new Location(world, x, y, z);
            }
        }
        return null;
    }

    public void listFarms(Player player) {
        List<String> farmList = getFarmList();
        StringBuilder build = new StringBuilder();
        if (!farmList.isEmpty()) {
            for (String aFarmList : farmList) {
                build.append(aFarmList);
                if (!aFarmList.equals(farmList.get(farmList.size() - 1))) {
                    build.append(", ");
                }
            }
            player.sendMessage(ChatColor.DARK_AQUA + "Farms: " + ChatColor.DARK_GRAY + build.toString());
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "No Farms To List");
        }
    }

    public List<String> getFarmList() {
        List<String> names = new ArrayList<>();
        for (String farmNames: config.getFarms().getConfigurationSection("Farms").getKeys(false)) {
            names.add(farmNames);
        }
        return names;
    }

    public Player getPlayerFromUUID(UUID uuid) {
        return FavFarms.getInstance().getServer().getPlayer(uuid);
    }

    public void removeFarm(Player player, String name) {
        if (hasFarm(player.getUniqueId())) {
            if (farmExist(name)) {
                if (isFarmOwner(player, name) || player.hasPermission(FarmPermissions.COMMAND_REMOVE_FARM_OTHER.toString())
                        || player.isOp()) {
                    if (config.getFarms().getString("Farms." + name) != null) {
                        config.getFarms().set("Farms." + name, null);
                        int amount = config.getFarms().getInt("Amount");
                        amount--;
                        config.getFarms().set("Amount", amount);
                        config.getPlayerData().set("PlayerData." + player.getUniqueId() + ".FarmsOwned", amount);
                        config.saveFarms();
                        config.savePlayerData();
                        player.sendMessage(ChatColor.DARK_AQUA + "You Have Deleted The FarmData " + name);
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have Permission To Remove This FarmData");
                }
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "This FarmData Does Not Exist");
            }
        }
    }

    public boolean isFarmOwner(Player player, String name) {
        if (farmExist(name)) {
            if (config.getFarms().getString("Farms." + name + ".Creator").equalsIgnoreCase(player.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }

    public void removeFarmAnimal(Animals animal) {
        UUID animalUUID = animal.getUniqueId();
        List<String> mail = new ArrayList<>();
        if (hasOwner(animalUUID)) {
            Player player = getOwnerFromAnimal(animalUUID);
            String home = animal.getMetadata("Home").get(0).asString();
            if (player.isOnline()) {
                animalsMap.remove(player.getUniqueId(), animalUUID);
                config.getAnimals().set("Animals." + player.getUniqueId() + "." + home + "." + animalUUID, null);
                saveAnimals(player);
                player.sendMessage(ChatColor.DARK_AQUA + animal.getName() + " Has Been Removed From FarmData");
            } else {
                String string = "&6" + animal.getName() + "&3 Was Killed and Has Been Removed From FarmData";
                mail.add(string);
                animalsMap.remove(player.getUniqueId(), animalUUID);
                farmMail.put(player.getUniqueId(), mail);
                config.getAnimals().set("Animals." + player.getUniqueId() + "." + home + "." + animalUUID, null);
                saveAnimals(player);
                saveFarmMail(player);
            }
        }
    }

    public void addFarmAnimal(Animals animal, Player player) {
        if (hasFarm(player.getUniqueId())) {
            UUID animalUUID = animal.getUniqueId();
            String name = getFirstFarmWithSpace(player);
            if (!checkEntityInFarm(animal)) {
                if (!hasOwner(animalUUID)) {
                    if (hasSpace(player.getUniqueId(), name)) {
                        if (getFarmSpawn(player, name) != null) {
                            obtainable.put(animal, true);
                            calculateCatchRate(animal, player);
                            if (hasCaught(animal)) {
                                if (animal instanceof Wolf) {
                                    Wolf wolf = (Wolf) animal;
                                    wolf.setSitting(true);
                                } else if (animal instanceof Ocelot) {
                                    Ocelot ocelot = (Ocelot) animal;
                                    ocelot.setSitting(true);
                                }
                                animal.setMetadata("Home", new FixedMetadataValue(FavFarms.getInstance(), name));
                                animal.teleport(getRandomLocWithinFarm(player));
                                animalsMap.put(player.getUniqueId(), animalUUID);
                                giveExp(player, animal);
                                player.sendMessage(ChatColor.DARK_AQUA + animal.getName() + " Has Been Added To FarmData");
                                player.sendMessage(ChatColor.YELLOW + "You Have Gained " + calculateExpGain(animal)
                                        + " Experience");
                                caught.remove(animal);
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Almost Caught " + animal.getName());
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_AQUA + "You Have Not Set A Spawn For Your FarmData Yet");
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_AQUA + "This FarmData Doesn't Have Space");
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "This Animal Has An Owner");
                }
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "This Animal Is In A FarmData");
            }
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Own A FarmData");
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
            getPlayerFromUUID(uuid).sendMessage(ChatColor.DARK_AQUA + "This Player Does Not Have A FarmData");
    }

    public void toggleAnimalInfo(Player player) {
        if (animalInfo.contains(player)) {
            animalInfo.remove(player);
            player.sendMessage(ChatColor.DARK_AQUA + "Toggled Animal Checker Off");
        } else {
            animalInfo.add(player);
            player.sendMessage(ChatColor.DARK_AQUA + "Toggled Animal Checker On");
        }
    }

    public boolean isCheckingAnimals(Player player) {
        return animalInfo.contains(player);
    }

    public void checkAnimal(Player player, Animals animal) {
        String msg = ChatColor.DARK_AQUA + "Is Farm Animal: ";
        player.sendMessage(msg + ChatColor.GOLD + isFarmAnimal(animal.getUniqueId()));
    }

    public Inventory getPlayerFarmsInv(Player player) {
        List<String> listFarms = getFarmsForPlayer(player);
        return FarmInventory.getInstance().createFarmListInventory(player, listFarms);
    }

    public void transferAnimal(Player player, String farm) {
        Animals animal = inTransfer.get(player);
        Location loc = getFarmSpawn(player, farm);
        String home = animal.getMetadata("Home").get(0).asString();
        if (!farm.equalsIgnoreCase(home)) {
            animal.removeMetadata("Home", FavFarms.getInstance());
            animal.setMetadata("Home", new FixedMetadataValue(FavFarms.getInstance(), farm));
            animal.teleport(loc);
            player.sendMessage(ChatColor.DARK_AQUA + "You Have Successfully Transferred " + ChatColor.GOLD + animal.getName()
                    + ChatColor.DARK_AQUA + " To " + ChatColor.DARK_GRAY + farm);
            transferComplete(player);
            saveAnimals(player);
        } else {
            cancelTransfer(player);
            player.sendMessage(ChatColor.DARK_AQUA + "Transfer Failed " + ChatColor.GOLD + animal.getName()
                    + ChatColor.DARK_AQUA + " Is Already In FarmData " + ChatColor.DARK_GRAY + farm);
        }
    }

    public boolean isInTransfer(Player player) {
        return inTransfer.containsKey(player);
    }

    public void setInTransfer(Player player, ItemStack item) {
        Animals animal = (Animals) getAnimalFromItem(item, player.getWorld());
        inTransfer.put(player, animal);
    }

    public void transferComplete(Player player) {
        inTransfer.remove(player);
        player.closeInventory();
    }

    public void cancelTransfer(Player player) {
        inTransfer.remove(player);
        player.sendMessage(ChatColor.DARK_AQUA + "You Have Canceled Animal Transfer");
        player.closeInventory();
    }

    public void openPlayerFarmsInv(Player player) {
        player.openInventory(getPlayerFarmsInv(player));
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
        if (item != null) {
            Animals animal = (Animals) getAnimalFromItem(item, player.getWorld());
            return FarmInventory.getInstance().createAnimalInventory(player, animal);
        }
        return null;
    }

    public void openAnimalInv(Player player, ItemStack item) {
        player.openInventory(getAnimalInv(player, item));
    }

    public Inventory getModifyInv(Player player, ItemStack item) {
        if (item != null) {
            Animals animal = (Animals) getAnimalFromItem(item, player.getWorld());
            if (item.getItemMeta().getDisplayName().equalsIgnoreCase(FarmItems.getInstance()
                    .createModificationItem(animal).getItemMeta().getDisplayName())) {
                return FarmInventory.getInstance().createModificationInventory(player, animal);
            }
        }
        return null;
    }

    public void openModifyInv(Player player, ItemStack item) {
        player.openInventory(getModifyInv(player, item));
    }

    public Inventory getAbilityInv(Player player, ItemStack item) {
        Animals animal = (Animals) getAnimalFromItem(item, player.getWorld());
        return FarmInventory.getInstance().createAnimalAbilitiesInventory(player, animal);
    }

    public void openAbilityInv(Player player, ItemStack item) {
        player.openInventory(getAbilityInv(player, item));
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
        List<String> name = getFarmsForPlayer(getPlayer(uuid));
        return name != null && name.size() != 0;
    }

    public void getFarmInfo(Player player, UUID uuid) {
        if (hasFarm(uuid)) {
            player.openInventory(getPlayerFarmsInv(Bukkit.getServer().getPlayer(uuid)));
        }
    }

    public void getFarmInfo(Player player) {
        if (hasFarm(player.getUniqueId())) {
            openPlayerFarmsInv(player);
        }
    }

    public String getFarmSize(String name) {
        String str = config.getFarms().getString("Farms." + name + ".Size");
        return str.substring(0, str.indexOf(","));
    }

    public boolean hasSpace(UUID uuid, String name) {
        if (animalsMap.get(uuid) == null) {
            return true;
        }
        if (animalsMap.get(uuid) != null) {
            if (getFarmSize(name).equals("Extra Small")) {
                if (animalsMap.get(uuid).size() <= 8) {
                    return true;
                }
            }
            if (getFarmSize(name).equals("Small")) {
                if (animalsMap.get(uuid).size() <= 14) {
                    return true;
                }
            }
            if (getFarmSize(name).equals("Medium")) {
                if (animalsMap.get(uuid).size() <= 26) {
                    return true;
                }
            }
            if (getFarmSize(name).equals("Large")) {
                if (animalsMap.get(uuid).size() <= 34) {
                    return true;
                }
            }
            if (getFarmSize(name).equals("Extra Larger")) {
                if (animalsMap.get(uuid).size() <= 64) {
                    return true;
                }
            }
        }
        return false;
    }

    public Location getRandomLocWithinFarm(Player player) {
        String name = getFirstFarmWithSpace(player);

        World world = getFarmSpawn(player, name).getWorld();

        int x = getFarmSpawn(player, name).getBlockX();
        int y = getFarmSpawn(player, name).getBlockY();
        int z = getFarmSpawn(player, name).getBlockZ();

        Location origin = new Location(world, x, y, z);

        Random r = new Random();

        Double randomRadius = r.nextDouble() * 3;
        Double theta =  Math.toRadians(r.nextDouble() * 360);
        Double phi = Math.toRadians(r.nextDouble() * 180 - 90);

        double x1 = randomRadius * Math.cos(theta) * Math.sin(phi);
        double z1 = randomRadius * Math.cos(phi);

        return origin.add(x1, 0, z1);

    }

    public void giveSelector(Player player) {
        Inventory inv = player.getInventory();
        if (!inv.contains(sel.getTool())) {
            if (hasEmptySlots(inv)) {
                inv.setItem(getEmptySlots(inv).get(0), sel.getTool());
                player.sendMessage(ChatColor.DARK_AQUA + "Obtained FarmData Selection Tool");
            }
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "You Already Have A FarmData Selector");
        }
    }

    public void giveCatcher(Player player) {
        Long time = config.getFav().getLong("Delays.CatcherCooldown");
        if (player.hasPermission(FarmPermissions.BYPASS_FARM_CATCHER_COOLDOWN.toString()) || player.isOp()) {
            catcherObtainTime.remove(player.getUniqueId());
        }
        if (!catcherObtainTime.containsKey(player.getUniqueId())) {
            Inventory inv = player.getInventory();
            if (hasEmptySlots(inv)) {
                inv.setItem(getEmptySlots(inv).get(0), sel.getCatcher());
                player.sendMessage(ChatColor.DARK_AQUA + "You Have Been given (" + sel.getCatcher().getAmount()
                        + ") Catchers");
                catcherObtainTime.put(player.getUniqueId(), time);
                applyCatcherCooldown(player);
            }
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "This Command Has A" + ChatColor.GOLD + " (" + ChatColor.GOLD + time
                    + ") " + ChatColor.DARK_AQUA + "Second Cooldown");
        }
    }

    public void giveCatcher(Player player, Player target) {
        Long time = config.getFav().getLong("Delays.CatcherCooldown");
        if (player.hasPermission(FarmPermissions.BYPASS_FARM_CATCHER_COOLDOWN.toString()) || player.isOp()) {
            catcherObtainTime.remove(player.getUniqueId());
        }
        if (!catcherObtainTime.containsKey(player.getUniqueId())) {
            Inventory inv = target.getInventory();
            if (hasEmptySlots(inv)) {
                inv.setItem(getEmptySlots(inv).get(0), sel.getCatcher());
                player.sendMessage(ChatColor.DARK_AQUA + "You Have Been given " + ChatColor.GOLD
                        + " (" + sel.getCatcher().getAmount() + ") " + ChatColor.DARK_AQUA + "Catchers By " + player.getName());
                catcherObtainTime.put(player.getUniqueId(), time);
                applyCatcherCooldown(player);
            }
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "This Command Has A" + ChatColor.GOLD + " (" + ChatColor.GOLD + time
                    + ") " + ChatColor.DARK_AQUA + "Second Cooldown");
        }
    }

    public void applyCatcherCooldown(Player player) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        taskID_02.put(player.getUniqueId(), scheduler.scheduleSyncRepeatingTask(FavFarms.getInstance(), () -> {
            if (catcherObtainTime.containsKey(player.getUniqueId())) {
                if (catcherObtainTime.get(player.getUniqueId()) != 0) {
                    catcherObtainTime.put(player.getUniqueId(), catcherObtainTime.get(player.getUniqueId()) - 1);
                } else {
                    catcherObtainTime.remove(player.getUniqueId());
                    for (Integer taskId : taskID_02.values()) {
                        if (taskID_02.get(player.getUniqueId()).equals(taskId)) {
                            Bukkit.getServer().getScheduler().cancelTask(taskId);
                        }
                    }
                    player.sendMessage(ChatColor.DARK_AQUA + "You Can Now Receive Another Set of Catchers");
                }
            }
        }, 0, 20));
    }

    public void giveCatcherResetItem(Player player) {
        Inventory inv = player.getInventory();
        if (hasEmptySlots(inv)) {
            inv.setItem(getEmptySlots(inv).get(0), FarmItems.getInstance().createResetCatcherCooldown());
        }
    }

    public void removeCatcherCooldown(Player player) {
        if (catcherObtainTime.containsKey(player.getUniqueId())) {
            catcherObtainTime.remove(player.getUniqueId());
            player.getInventory().remove(FarmItems.getInstance().createResetCatcherCooldown());
            player.sendMessage(ChatColor.DARK_AQUA + "Your Cooldown For Receiving Catchers Has Been Removed");
        } else {
            player.sendMessage(ChatColor.DARK_AQUA + "You Cannot Use This Item If You Don't Have A Cooldown " +
                    "For Receiving Catchers");
        }
    }

    public void giveChanceModifierItem(Player player) {
        Inventory inv = player.getInventory();
        if (hasEmptySlots(inv)) {
            inv.setItem(getEmptySlots(inv).get(0), fItems.createChanceModifier(player));
        }
    }

    public void increaseCatchRate(Player player, ItemStack item) {
        UUID playerUUID = player.getUniqueId();
        if (hasFarm(playerUUID)) {
            catchRateTime.put(playerUUID, getIncreasedTime(item));
            increasedCatchRate.put(playerUUID, getIncreasedRate(item));
            player.sendMessage(ChatColor.DARK_AQUA + "You Catch Rate Chances Have Been Increased By " + getIncreasedRate(item));
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            taskID_03.put(playerUUID, scheduler.scheduleSyncRepeatingTask(FavFarms.getInstance(), () -> {
                if (catchRateTime.containsKey(playerUUID)) {
                    if (catchRateTime.get(playerUUID) != 0) {
                        catchRateTime.put(playerUUID, catchRateTime.get(playerUUID) - 1);
                    } else {
                        catchRateTime.remove(playerUUID);
                        increasedCatchRate.remove(playerUUID);
                        for (Integer taskId : taskID_03.values()) {
                            if (taskID_03.get(playerUUID).equals(taskId)) {
                                Bukkit.getServer().getScheduler().cancelTask(taskId);
                            }
                        }
                        player.sendMessage(ChatColor.DARK_AQUA + "Your Catch Rate Has Been Reset To Normal");
                    }
                }
            }, 0, 20));
        }
    }

    public Long getIncreasedTime(ItemStack item) {
        List<String> lore = item.getItemMeta().getLore();
        Long time = 0L;
        for (String str : lore) {
            if (str.contains("Minutes")) {
                String trim = ChatColor.stripColor(str).trim();
                String val = trim.replaceAll("[^0-9.]", "");
                time = Long.valueOf(val);
            }
        }
        return time;
    }

    public Double getIncreasedRate(ItemStack item) {
        List<String> lore = item.getItemMeta().getLore();
        Double amount = 0.0;
        for (String str : lore) {
            if (str.contains("Increase")) {
                String trim = ChatColor.stripColor(str).trim();
                String val = trim.replaceAll("[^0-9.]", "");
                amount = Double.parseDouble(val) / 100;
            }
        }
        return amount;
    }

    public Entity getAnimalFromItem(ItemStack item, World world) {
        ItemMeta meta = item.getItemMeta();
        UUID uuid = null;
        for (String id : meta.getLore()) {
            if (id.contains("UUID")) {
                uuid = UUID.fromString(id.substring(8));
            }
        }
        if (uuid != null) {
            return getAnimalFromUUID(uuid, world);
        }
        return null;
    }

    public String getFarmFromItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String farmName = "";
        if (meta.hasDisplayName()) {
            farmName = meta.getDisplayName().substring(7);
        }
        return farmName;
    }

    public boolean isAnimalInvItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        for (String id : meta.getLore()) {
            if (id.contains("UUID")) {
                return true;
            }
        }
        return false;
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
        animal.teleport(getFarmSpawn(player, getFarmHomeForAnimal(animal)));
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

    public String getFarmHomeForAnimal(Animals animal) {
        String name = "";
        if (animal.hasMetadata("Home")) {
            for (MetadataValue val : animal.getMetadata("Home")) {
                name =  val.asString();
            }
        }
        return name;
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
            int blocksCurrent = config.getPlayerData().getInt("PlayerData." + player.getUniqueId() + ".FarmBlocks");
            int blocksReceived = config.getFav().getInt("Size.Amount");
            int blocksCombined = blocksCurrent + blocksReceived;
            level.put(player.getUniqueId(), value);
            config.getPlayerData().set("PlayerData." + player.getUniqueId() + ".FarmBlocks", blocksCombined);
            player.sendMessage(ChatColor.DARK_AQUA + "Leveled Up To " + ChatColor.GOLD + getLevel(player)
                    + ChatColor.DARK_AQUA + " And Received " + ChatColor.GOLD + blocksReceived + " FarmData Blocks");
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

    public void calculateCatchRate(Animals animal, Player player) {
        double rand = Math.random() * 100;
        double chance = getCatchRate(animal);
        if (increasedCatchRate.containsKey(player.getUniqueId())) {
            chance = getCatchRate(animal) + increasedCatchRate.get(player.getUniqueId());
        }
        if (chance > rand) {
            caught.put(animal, true);
            assignRandomExperience(player, animal, rand);
        } else {
            caught.put(animal, false);
        }
    }

    public boolean hasTameUsages(Player player) {
        return getTameUsages(player) != 0;
    }

    public boolean hasReplenishUsages(Player player) {
        return getReplenishUsage(player) != 0;
    }

    public boolean hasStyleChangeUsages(Player player) {
        return getStyleChangeUsage(player) != 0;
    }

    public Integer getTameUsages(Player player) {
        Integer defaultUsages = config.getFav().getInt("Usages.Tame");
        if (!tameUsage.containsKey(player.getUniqueId())) {
            tameUsage.put(player.getUniqueId(), defaultUsages);
        }
        return tameUsage.get(player.getUniqueId());
    }

    public Integer getReplenishUsage(Player player) {
        Integer defaultUsages = config.getFav().getInt("Usages.Replenish");
        if (!ReplenishUsage.containsKey(player.getUniqueId())) {
            ReplenishUsage.put(player.getUniqueId(), defaultUsages);
        }
        return ReplenishUsage.get(player.getUniqueId());
    }

    public Integer getStyleChangeUsage(Player player) {
        Integer defaultStyleChanges = config.getFav().getInt("Usages.StyleChanges");
        if (!StyleChangeUsage.containsKey(player.getUniqueId())) {
            StyleChangeUsage.put(player.getUniqueId(), defaultStyleChanges);
        }
        return StyleChangeUsage.get(player.getUniqueId());
    }

    public void setTameUsages(Player player, Integer amount) {
        tameUsage.put(player.getUniqueId(), amount);
    }

    public void setReplenishUsages(Player player, Integer amount) {
        ReplenishUsage.put(player.getUniqueId(), amount);
    }

    public void setStyleChangeUsages(Player player, Integer amount) {
        ReplenishUsage.put(player.getUniqueId(), amount);
    }

    public void addTameUsages(Player player, Integer amount) {
        Integer sum = tameUsage.get(player.getUniqueId()) + amount;
        tameUsage.put(player.getUniqueId(), sum);
    }

    public void addReplenishUsage(Player player, Integer amount) {
        Integer sum = ReplenishUsage.get(player.getUniqueId()) + amount;
        ReplenishUsage.put(player.getUniqueId(), sum);
    }

    public void addStyleChangeUsage(Player player, Integer amount) {
        Integer sum = StyleChangeUsage.get(player.getUniqueId()) + amount;
        StyleChangeUsage.put(player.getUniqueId(), sum);
    }

    public Integer calculateExpGain(Animals animal) {
        Random rand = new Random();
        int least = getExpGainedLeast(animal);
        int most = getExpGainedMost(animal);
        return rand.nextInt(most - least) + least;
    }

    public void goToFarmHome(Player player, String name) {
        UUID playerUUID = player.getUniqueId();
        if (hasFarm(playerUUID)) {
            if (farmExist(name)) {
                if (isFarmOwner(player, name)) {
                    Location spawn = getFarmSpawn(player, name);
                    player.teleport(spawn);
                    player.sendMessage(ChatColor.DARK_AQUA + "You Have Teleported To " + name + "'s Farm Spawn");
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have Access To This Farm");
                }
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "The Farm " + ChatColor.GOLD + name
                        + ChatColor.DARK_AQUA + " Does Not Exist");
            }
        }
    }

    public void goToFarmOther(Player player, Player other, String name) {
        UUID playerUUID = other.getUniqueId();
        if (hasFarm(playerUUID)) {
            if (farmExist(name)) {
                Location spawn = getFarmSpawn(other, name);
                player.teleport(spawn);
                player.sendMessage(ChatColor.DARK_AQUA + "You Have Teleported To " + ChatColor.GOLD + other.getName()
                        + ChatColor.DARK_AQUA + "'s Farm " + ChatColor.GOLD + name + ChatColor.DARK_AQUA + " Spawn");
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "The Farm " + ChatColor.GOLD + name
                        + ChatColor.DARK_AQUA + " Does Not Exist");
            }
        }
    }

    public void setToggleSnowBlower(Player player, Animals animal) {
        if (toggleSnowBlower.containsKey(animal)) {
            if (toggleSnowBlower.get(animal)) {
                toggleSnowBlower.put(animal, false);
                player.sendMessage(ChatColor.AQUA + "Toggled SnowBlower Off For " + animal.getName());
            } else {
                toggleSnowBlower.put(animal, true);
                player.sendMessage(ChatColor.AQUA + "Toggled SnowBlower On For " + animal.getName());
            }
        } else {
            toggleSnowBlower.put(animal, true);
            player.sendMessage(ChatColor.AQUA + "Toggled SnowBlower On For " + animal.getName());
        }
    }

    public boolean isToggledSnowBlower(Animals animal) {
        return toggleSnowBlower.get(animal);
    }

    public void setToggleSprinkler(Player player, Animals animal) {
        if (toggleSprinkler.containsKey(animal)) {
            if (toggleSprinkler.get(animal)) {
                toggleSprinkler.put(animal, false);
                player.sendMessage(ChatColor.AQUA + "Toggled Sprinkler Off For " + animal.getName());
            } else {
                toggleSprinkler.put(animal, true);
                player.sendMessage(ChatColor.AQUA + "Toggled Sprinkler On For " + animal.getName());
            }
        } else {
            toggleSprinkler.put(animal, true);
            player.sendMessage(ChatColor.AQUA + "Toggled Sprinkler On For " + animal.getName());
        }
    }

    public boolean isToggledSprinkler(Animals animal) {
        return toggleSprinkler.get(animal);
    }

    public void setToggleShield(Player player, Animals animal) {
        if (toggleShield.containsKey(animal)) {
            if (toggleShield.get(animal)) {
                toggleShield.put(animal, false);
                player.sendMessage(ChatColor.AQUA + "Toggled Shield Off For " + animal.getName());
            } else {
                toggleShield.put(animal, true);
                player.sendMessage(ChatColor.AQUA + "Toggled Shield On For " + animal.getName());
            }
        } else {
            toggleShield.put(animal, true);
            player.sendMessage(ChatColor.AQUA + "Toggled Shield On For " + animal.getName());
        }
    }

    public boolean isToggledShield(Animals animal) {
        return toggleShield.get(animal);
    }

    public void setToggleExplosive(Player player, Animals animal) {
        if (toggleExplosive.containsKey(animal)) {
            if (toggleExplosive.get(animal)) {
                toggleExplosive.put(animal, false);
                player.sendMessage(ChatColor.AQUA + "Toggled Explosive Off For " + animal.getName());
            } else {
                toggleExplosive.put(animal, true);
                player.sendMessage(ChatColor.AQUA + "Toggled Explosive On For " + animal.getName());
            }
        } else {
            toggleExplosive.put(animal, true);
            player.sendMessage(ChatColor.AQUA + "Toggled Explosive On For " + animal.getName());
        }
    }

    public boolean isToggledExplosive(Animals animal) {
        return toggleExplosive.get(animal);
    }

    public void setFollowing(Player player, Animals animal) {
        following.put(player, animal);
        follow(player, animal);
        player.sendMessage(ChatColor.GOLD + animal.getName() + ChatColor.DARK_AQUA + " Is Now Following You");
    }

    public void removeFollowing(Player player, Animals animal) {
        following.remove(player, animal);
        release(player, animal);
        player.sendMessage(ChatColor.GOLD + animal.getName() + ChatColor.DARK_AQUA + " Is No Longer Following You");
    }

    @SuppressWarnings("unused")
    public Collection<Animals> getFollowing(Player player) {
        if (following.containsKey(player)) {
            return following.get(player);
        }
        return null;
    }

    public boolean isFollowing(Player player, Animals animal) {
        return (following.get(player).contains(animal));
    }

    public void release(Player player, Animals animal) {
        if (isFollowing(player, animal)) {
            Bukkit.getServer().getScheduler().cancelTask(taskID_04.get(player));
            taskID_04.remove(player);
            FarmNavigation.getInstance().deNavigate(animal);
        }
    }

    public void follow(Player player, Animals animal) {
        if (isFollowing(player, animal)) {
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            taskID_04.put(player, scheduler.scheduleSyncRepeatingTask(FavFarms.getInstance(), () -> {
                if (getDistance(player.getLocation(), animal.getLocation()) > 20) {
                    animal.teleport(checkSafeLocation(player));
                } else {
                    FarmNavigation.getInstance().navigate(animal, getFrontOfPlayer(player), 0.7);
                    animal.getEyeLocation().setYaw(player.getEyeLocation().getYaw());
                }
            }, 0, 7));
        }
    }

    public Double getDistance(Location locFirst, Location locSecond) {
        return locFirst.distance(locSecond);
    }

    public Location getFrontOfPlayer(Player player) {
        Location eyelocation = player.getEyeLocation();
        Vector vec = player.getLocation().getDirection().multiply(2);

        return eyelocation.add(vec);
    }

    public Location checkSafeLocation(Player player) {
        World world = player.getWorld();
        Location loc = getRandomLocFromRad(player);
        Block block = world.getBlockAt(loc);
        if (block.getType().isSolid()) {
            do {
                block = world.getBlockAt(loc);
                loc.add(0, 1, 0);
            } while (!block.getType().isSolid());
            return loc;
        }
        return loc;
    }

    public Location getRandomLocFromRad(Player player) {
        World world = player.getWorld();

        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();

        Location origin = new Location(world, x, y, z);

        Random r = new Random();

        Double randomRadius = r.nextDouble() * 3;
        Double theta =  Math.toRadians(r.nextDouble() * 360);
        Double phi = Math.toRadians(r.nextDouble() * 180 - 90);

        double x1 = randomRadius * Math.cos(theta) * Math.sin(phi);
        double z1 = randomRadius * Math.cos(phi);

        return origin.add(x1, 0, z1);

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

    public boolean hasCaught(Animals animal) {
        return caught.get(animal);
    }

    public boolean worldContainsAnimal(Animals animal, World world) {
        return world.getEntities().contains(animal);
    }

    /*
    public boolean isObtainable(Animals animal) {
        return obtainable.get(animal);
    }
    */

    public Integer getID() {
        return config.getFarms().getInt("Amount");
    }

    public void setID() {
        ID = getID() + 1;
    }

    public void saveAnimals(Player player) {
        if (animalsMap != null) {
            if (hasFarm(player.getUniqueId())) {
                for (UUID ownerId : animalsMap.keySet()) {
                    for (UUID animalId : animalsMap.get(ownerId)) {
                        Animals animal = (Animals) getAnimalFromUUID(animalId, player.getWorld());
                        if (worldContainsAnimal(animal, player.getWorld())) {
                            if (animal.hasMetadata("Home")) {
                                String home = animal.getMetadata("Home").get(0).asString();
                                Integer exp = animal.getMetadata("Experience").get(0).asInt();
                                Integer level = animal.getMetadata("Level").get(0).asInt();
                                List<String> listAbility = new ArrayList<>();
                                for (String ability : splitAbilitiesList(animal)) {
                                    listAbility.add(ability);
                                }
                                config.getAnimals().set("Animals." + ownerId.toString() + "." + home + "." + animal.getUniqueId()
                                        .toString() + ".Name", animal.getName());
                                config.getAnimals().set("Animals." + ownerId.toString() + "." + home + "." + animal.getUniqueId()
                                        .toString() + ".Home", home);
                                config.getAnimals().set("Animals." + ownerId.toString() + "." + home + "." + animal.getUniqueId()
                                        .toString() + ".Experience", exp);
                                config.getAnimals().set("Animals." + ownerId.toString() + "." + home + "." + animal.getUniqueId()
                                        .toString() + ".Level", level);
                                config.getAnimals().set("Animals." + ownerId.toString() + "." + home + "." + animal.getUniqueId()
                                        .toString() + ".Abilities", listAbility);
                                config.saveAnimals();
                            }
                        } else {
                            removeFarmAnimal(animal);
                        }
                    }
                }
            }
        }
    }

    public List<String> splitAbilitiesList(Animals animal) {
        List<String> values = new ArrayList<>();
        if (animal.hasMetadata("Abilities")) {
            for (MetadataValue ability : animal.getMetadata("Abilities")) {
                String split[] = ability.asString().split(",");
                for (String str : split) {
                    values.add(str.replaceAll("[^a-zA-Z_]", "").trim());
                }
            }
        }
        return values;
    }

    public void loadAnimals(Player player) {
        String ownerId = player.getUniqueId().toString();
        if (config.getAnimals().get("Animals." + player.getUniqueId()) != null) {
            for (String home : config.getAnimals().getConfigurationSection("Animals." + ownerId).getKeys(false)) {
                for (String animalId : config.getAnimals().getConfigurationSection("Animals." + ownerId + "."
                        + home).getKeys(false)) {
                    if (getAnimalFromUUID(UUID.fromString(animalId), player.getWorld()) != null) {
                        Animals animal = (Animals) getAnimalFromUUID(UUID.fromString(animalId), player.getWorld());
                        if (worldContainsAnimal(animal, player.getWorld())) {
                            int exp = config.getAnimals().getInt("Animals." + ownerId + "." + home + "." + animalId + ".Experience");
                            int level = config.getAnimals().getInt("Animals." + ownerId + "." + home + "." + animalId + ".Level");
                            List<String> abilities = new ArrayList<>();
                            if (config.getAnimals().getStringList("Animals." + ownerId + "." + home + "."
                                    + animalId + ".Abilities") != null) {
                                abilities = config.getAnimals().getStringList("Animals." + ownerId + "." + home + "."
                                        + animalId + ".Abilities");
                            }
                            animal.setMetadata("Home", new FixedMetadataValue(FavFarms.getInstance(), home));
                            animal.setMetadata("Experience", new FixedMetadataValue(FavFarms.getInstance(), exp));
                            animal.setMetadata("Level", new FixedMetadataValue(FavFarms.getInstance(), level));
                            if (!abilities.isEmpty()) {
                                for (String skill : abilities) {
                                    if (animal.hasMetadata("Abilities")) {
                                        animal.setMetadata("Abilities", new FixedMetadataValue(FavFarms.getInstance(), abilities));
                                    } else {
                                        animal.setMetadata("Abilities", new FixedMetadataValue(FavFarms.getInstance(), skill));
                                    }
                                }
                            }
                            animalsMap.put(player.getUniqueId(), animal.getUniqueId());
                        } else {
                            FavFarms.getInstance().getLogger().info("Animal Was Not Found In This World");
                            removeAnimalDirect(UUID.fromString(ownerId), UUID.fromString(animalId), home);
                        }
                    } else {
                        FavFarms.getInstance().getLogger().info("Animal Returned Null");
                        removeAnimalDirect(UUID.fromString(ownerId), UUID.fromString(animalId), home);
                    }
                }
            }
        }
    }

    public void saveFarmMail(Player player) {
        if (!farmMail.get(player.getUniqueId()).isEmpty()) {
            config.getFarmMail().set("Mail." + player.getUniqueId().toString(), farmMail.get(player.getUniqueId()));
            config.saveFarmMail();
        }
    }

    public void  clearMail(Player player) {
        List<String> mail = farmMail.get(player.getUniqueId());
        mail.clear();
        farmMail.put(player.getUniqueId(), mail);
        player.sendMessage(ChatColor.DARK_AQUA + "You Have Cleared Your Mail.");
        saveFarmMail(player);
    }

    public void loadFarmMail(Player player) {
        List<String> mail = new ArrayList<>();
        for (String key : config.getFarmMail().getConfigurationSection("Mail." + player.getUniqueId()).getKeys(false)) {
            String string = config.getFarmMail().getString("Mail." + key);
            mail.add(string);
            farmMail.put(UUID.fromString(key), mail);
        }
    }

    public void saveFarmLevel(Player player) {
        if (config.getPlayerData().get("PlayerData") != null) {
            UUID uuid = player.getUniqueId();
            config.getPlayerData().set("PlayerData." + uuid + ".Level", getLevel(player));
            config.savePlayerData();
        }
    }

    public void loadFarmLevel(Player player) {
        if (config.getPlayerData().get("PlayerData") != null) {
            UUID uuid = player.getUniqueId();
            UUID playerUUID = UUID.fromString(config.getPlayerData().getString("PlayerData." + uuid + ".OwnerUUID"));
            int tempLevel = config.getPlayerData().getInt("PlayerData." + uuid + ".Level");
            level.put(playerUUID, tempLevel);
        }
    }

    public void saveFarmExp(Player player) {
        if (config.getPlayerData().get("PlayerData") != null) {
            UUID uuid = player.getUniqueId();
            config.getPlayerData().set("PlayerData." + uuid + ".Experience", getExp(player));
            config.savePlayerData();
        }
    }

    public void loadFarmExp(Player player) {
        if (config.getPlayerData().get("PlayerData") != null) {
            UUID uuid = player.getUniqueId();
            int tempExp = config.getPlayerData().getInt("PlayerData." + uuid + ".Experience");
            UUID playerUUID = UUID.fromString(config.getPlayerData().getString("PlayerData." + uuid + ".OwnerUUID"));
            experience.put(playerUUID, tempExp);
        }
    }

    public List<String> getFarmsForPlayer(Player player) {
        List<String> names = new ArrayList<>();

        if (config.getFarms().getConfigurationSection("Farms") != null) {
            for (String key : config.getFarms().getConfigurationSection("Farms").getKeys(false)) {
                if (config.getFarms().getString("Farms." + key + ".Creator").equalsIgnoreCase(player.getUniqueId().toString())) {
                    names.add(key);
                }
            }
        } else {
            return null;
        }
        return names;
    }

    public String getMail(Player player) {
        StringBuilder sb = new StringBuilder();
        if (farmMail.get(player.getUniqueId()).isEmpty()) {
            sb.append(ChatColor.DARK_AQUA).append("You Do Not Have Any FarmData Mail");
        } else {
            sb.append("Mail: ");
            for (String mail : farmMail.get(player.getUniqueId())) {
                sb.append(ChatColor.translateAlternateColorCodes('&', mail)).append("\n");
            }
        }
        return sb.toString();
    }

    public String getFirstFarmWithSpace(Player player) {
        List<String> farmsWithEmptySpace = new ArrayList<>();
        for (String name : getFarmsForPlayer(player)) {
            if (hasSpace(player.getUniqueId(), name)) {
                farmsWithEmptySpace.add(name);
            }
        }
        return farmsWithEmptySpace.get(0);
    }

    public void removeAnimalDirect(UUID playerUUID, UUID animalUUID, String home) {
        String name = config.getAnimals().getString("Animals." + playerUUID + "." + home + "." + animalUUID + ".Name");
        config.getAnimals().set("Animals." + playerUUID + "." + home + "." + animalUUID, null);
        getPlayerFromUUID(playerUUID).sendMessage(ChatColor.DARK_AQUA + "Your Animal " + ChatColor.GOLD + name
                + ChatColor.DARK_AQUA + " Has Disappeared From This World");
        config.saveAnimals();
    }

    public Vector getMinimum(Location loc) {
        Vector minVec;

        String key = getFarmFromLocation(loc);
        int x = config.getFarms().getInt("Farms." + key + ".Min.X");
        int y = config.getFarms().getInt("Farms." + key + ".Min.Y");
        int z = config.getFarms().getInt("Farms." + key + ".Min.Z");

        minVec = new Vector(x, y, z);

        return minVec;
    }

    public Vector getMaximum(Location loc) {
        Vector maxVec;

        String key = getFarmFromLocation(loc);
        int x = config.getFarms().getInt("Farms." + key + ".Max.X");
        int y = config.getFarms().getInt("Farms." + key + ".Max.Y");
        int z = config.getFarms().getInt("Farms." + key + ".Max.Z");

        maxVec = new Vector(x, y, z);

        return maxVec;
    }

    public String getFarmFromLocation(Location location) {
        String name = "";

        int locX = location.getBlockX();
        int locY = location.getBlockY();
        int locZ = location.getBlockZ();

        for (String key : config.getFarms().getConfigurationSection("Farms").getKeys(false)) {
            int x = config.getFarms().getInt("Farms." + key + ".Min.X");
            int y = config.getFarms().getInt("Farms." + key + ".Min.Y");
            int z = config.getFarms().getInt("Farms." + key + ".Min.Z");

            int x1 = config.getFarms().getInt("Farms." + key + ".Max.X");
            int y1 = config.getFarms().getInt("Farms." + key + ".Max.Y");
            int z1 = config.getFarms().getInt("Farms." + key + ".Max.Z");

            Vector minVec = new Vector(x, y, z);
            Vector maxVec = new Vector(x1, y1, z1);

            if (locX >= minVec.getBlockX() && locX <= maxVec.getBlockX() || locX <= minVec.getBlockX()
                    && locX >= maxVec.getBlockX()) {
                if (locZ >= minVec.getBlockZ() && locZ <= maxVec.getBlockZ() || locZ <= minVec.getBlockZ()
                        && locZ >= maxVec.getBlockZ()) {
                    if (locY >= minVec.getBlockY() && locY <= maxVec.getBlockY() || locY <= minVec.getBlockY()
                            && locY >= maxVec.getBlockY()) {
                        name = key;
                    }
                }
            }
        }
        return name;
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

    ItemStack getItemForFarm(String name) {
        return FarmItems.getInstance().createFarmsListItem(name);
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

    public void assignRandomExperience(Player player, Animals animal, double chance) {
        Random rand = new Random();

        int min = 0;
        int max = 0;

        if (chance < 22) {
            min = 48000;
            max = 56000;
        }
        if (chance < 48 && chance > 22) {
            min = 36000;
            max = 48000;
        }
        if (chance < 64 && chance > 48) {
            min = 24000;
            max = 36000;
        }
        if (chance > 64) {
            min = 8000;
            max = 24000;
        }

        int result = rand.nextInt(max - min) + min;
        animal.setMetadata("Experience", new FixedMetadataValue(FavFarms.getInstance(), result));
        assignLevel(animal);
        assignAbility(player, animal);
        player.sendMessage(ChatColor.DARK_AQUA + "Assigning Values To " + ChatColor.GOLD + animal.getName());
    }

    public void assignLevel(Animals animal) {
        if (animal.hasMetadata("Experience")) {
            Random rand = new Random();
            int exp = animal.getMetadata("Experience").get(0).asInt();
            int result = 1;
            if (exp < 12000) {
                int min = 1;
                int max = 12;

                result = rand.nextInt(max - min) + min;
            }
            if (exp > 12000 && exp < 24000) {
                int min = 13;
                int max = 22;

                result = rand.nextInt(max - min) + min;
            }
            if (exp > 24000 && exp < 36000) {
                int min = 23;
                int max = 29;

                result = rand.nextInt(max - min) + min;
            }
            if (exp > 36000 && exp < 48000) {
                int min = 30;
                int max = 47;

                result = rand.nextInt(max - min) + min;
            }
            if (exp > 48000) {
                int min = 48;
                int max = 56;

                result = rand.nextInt(max - min) + min;
            }
            animal.setMetadata("Level", new FixedMetadataValue(FavFarms.getInstance(), result));
        }
    }

    public void assignAbility(Player player, Animals animal) {
        if (animal.hasMetadata("Level")) {
            int animalLevel = animal.getMetadata("Level").get(0).asInt();
            List<Abilities> abilities = new ArrayList<>();
            if (animal instanceof PolarBear) {
                Abilities snwblr = Abilities.SNOW_BLOWER;
                Abilities spklr = Abilities.SPRINKLER;
                Abilities shld = Abilities.SHIELD;
                Abilities xplsv = Abilities.EXPLOSIVE;
                if (animalLevel >= 32) {
                    abilities.add(snwblr);
                    abilities.add(spklr);
                    abilities.add(shld);
                    abilities.add(xplsv);
                    animal.setMetadata("Abilities", new FixedMetadataValue(FavFarms.getInstance(), abilities));
                } else {
                    animal.setMetadata("Abilities", new FixedMetadataValue(FavFarms.getInstance(), spklr));
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "Failed To Assign A Level To " + animal.getName());
        }
    }

    @SuppressWarnings("unused")
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
        return config.getFarms().getConfigurationSection("Farms." + name) != null;
    }

    public Integer getCount(Player player) {
        if (!count.containsKey(player)) {
            count.put(player, 0);
        }
        return count.get(player);
    }

    public void addCount(Player player, Integer amount) {
        int initial = count.get(player);
        count.put(player, initial + amount);
    }

    public void subtractCount(Player player, Integer amount) {
        int initial = count.get(player);
        count.put(player, amount - initial);
    }

    public void saveSavTimers() {
        if (!recentCreateTime.isEmpty()) {
            for (Entry<UUID, Long> entry : recentCreateTime.entrySet()) {
                UUID playerUUID = entry.getKey();
                Long remaining = recentCreateTime.get(playerUUID);

                config.getSavTimers().set("Tasks." + playerUUID.toString() + ".Remaining.RecentCreateTime", remaining);
                config.saveSavTimers();
            }
        }
        if (!catcherObtainTime.isEmpty()) {
            for (Entry<UUID, Long> entry : catcherObtainTime.entrySet()) {
                UUID playerUUID = entry.getKey();
                Long remaining = catcherObtainTime.get(playerUUID);

                config.getSavTimers().set("Tasks." + playerUUID.toString() + ".Remaining.CatcherObtainTime", remaining);
                config.saveSavTimers();
            }
        }
        if (!catchRateTime.isEmpty()) {
            for (Entry<UUID, Long> entry : catchRateTime.entrySet()) {
                UUID playerUUID = entry.getKey();
                Long remaining = catchRateTime.get(playerUUID);

                config.getSavTimers().set("Tasks." + playerUUID.toString() + ".Remaining.CatchRateTime", remaining);
                config.saveSavTimers();
            }
        }
    }

    public void loadSavTimers() {
        if (config.getSavTimers().getString("Tasks.") != null) {
            for (OfflinePlayer allPl : Bukkit.getOfflinePlayers()) {
                if (config.getSavTimers().get(allPl.getUniqueId() + ".Remaining.RecentCreateTime") != null) {
                    Long remaining = config.getSavTimers().getLong(allPl.getUniqueId() + ".Remaining.RecentCreateTime");
                    recentCreateTime.put(allPl.getUniqueId(), remaining);

                    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                    taskID_01.put(allPl.getUniqueId(), scheduler.scheduleSyncRepeatingTask(FavFarms.getInstance(), () -> {
                        if (recentCreateTime.get(allPl.getUniqueId()) != 0) {
                            recentCreateTime.put(allPl.getUniqueId(), recentCreateTime.get(allPl.getUniqueId()) - 1);
                        } else {
                            recentCreateTime.remove(allPl.getUniqueId());
                            for (Integer taskId : taskID_01.values()) {
                                if (taskID_01.get(allPl.getUniqueId()).equals(taskId)) {
                                    Bukkit.getServer().getScheduler().cancelTask(taskId);
                                }
                            }
                            Bukkit.getServer().getPlayer(allPl.getUniqueId()).sendMessage(ChatColor.DARK_AQUA
                                    + "You Can Now Create A FarmData Again");
                        }
                    }, 0, 20));
                }
            }
            for (OfflinePlayer allPl : Bukkit.getOfflinePlayers()) {
                if (config.getSavTimers().get(allPl.getUniqueId() + ".Remaining.CatcherObtainTime") != null) {
                    Long remaining = config.getSavTimers().getLong(allPl.getUniqueId() + ".Remaining.CatcherObtainTime");
                    catcherObtainTime.put(allPl.getUniqueId(), remaining);

                    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                    taskID_02.put(allPl.getUniqueId(), scheduler.scheduleSyncRepeatingTask(FavFarms.getInstance(), () -> {
                        if (catcherObtainTime.get(allPl.getUniqueId()) != 0) {
                            catcherObtainTime.put(allPl.getUniqueId(), catcherObtainTime.get(allPl.getUniqueId()) - 1);
                        } else {
                            catcherObtainTime.remove(allPl.getUniqueId());
                            for (Integer taskId : taskID_02.values()) {
                                if (taskID_02.get(allPl.getUniqueId()).equals(taskId)) {
                                    Bukkit.getServer().getScheduler().cancelTask(taskId);
                                }
                            }
                            Bukkit.getServer().getPlayer(allPl.getUniqueId()).sendMessage(ChatColor.DARK_AQUA
                                    + "You Can Now Receive Another Set of Catchers");
                        }
                    }, 0, 20));
                }
            }
            for (OfflinePlayer allPl : Bukkit.getOfflinePlayers()) {
                if (config.getSavTimers().get(allPl.getUniqueId() + ".Remaining.CatchRateTime") != null) {
                    Long remaining = config.getSavTimers().getLong(allPl.getUniqueId() + ".Remaining.CatchRateTime");
                    catchRateTime.put(allPl.getUniqueId(), remaining);

                    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                    taskID_03.put(allPl.getUniqueId(), scheduler.scheduleSyncRepeatingTask(FavFarms.getInstance(), () -> {
                        if (catchRateTime.get(allPl.getUniqueId()) != 0) {
                            catchRateTime.put(allPl.getUniqueId(), catchRateTime.get(allPl.getUniqueId()) - 1);
                        } else {
                            catchRateTime.remove(allPl.getUniqueId());
                            for (Integer taskId : taskID_03.values()) {
                                if (taskID_03.get(allPl.getUniqueId()).equals(taskId)) {
                                    Bukkit.getServer().getScheduler().cancelTask(taskId);
                                }
                            }
                            Bukkit.getServer().getPlayer(allPl.getUniqueId()).sendMessage(ChatColor.DARK_AQUA
                                    + "Your Catch Rate Has Been Reset To Normal");
                        }
                    }, 0, 20));
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public boolean isNumeric(String str) {
        try
        {
            int amount = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
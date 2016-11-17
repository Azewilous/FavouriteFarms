package io.github.favfarms.listener;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import io.github.favfarms.FavFarms;
import io.github.favfarms.ability.AbilityDisplay;
import io.github.favfarms.ability.AbilityItems;
import io.github.favfarms.configuration.FarmConfig;
import io.github.favfarms.farm.FarmMethods;
import io.github.favfarms.item.FarmItems;
import io.github.favfarms.permission.FarmPermissions;
import io.github.favfarms.select.SelectionTool;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BlockVector;

import java.util.HashMap;

import static org.bukkit.Bukkit.getServer;

/**
 * FavFarms Created by Awesome Red on 8/24/2016.
 */
public class FarmHandler implements Listener {

    FavFarms farms;

    public FarmHandler(FavFarms farms) {
        this.farms = farms;
    }

    FarmMethods method = FarmMethods.getInstance();
    SelectionTool tool = SelectionTool.getInstance();
    FarmItems FItems = FarmItems.getInstance();
    FarmConfig config = FarmConfig.getInstance();
    AbilityItems AItems = AbilityItems.getInstance();

    HashMap<Player, Location> blockLocFirst = new HashMap<>();
    HashMap<Player, Location> blockLocSecond = new HashMap<>();
    HashMap<Player, BlockVector> blockVecFirst = new HashMap<>();
    HashMap<Player, BlockVector> blockVecSecond = new HashMap<>();


    @SuppressWarnings("unused")
    @EventHandler
    public void playerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (event.getHand() == EquipmentSlot.HAND) {
            if (entity instanceof Animals) {
                Animals animal = (Animals) entity;
                if (method.isCheckingAnimals(player)) {
                    method.checkAnimal(player, animal);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack selTool = tool.getTool();
        ItemStack catchTool = tool.getCatcher();
        ItemStack resetTool = FItems.createResetCatcherCooldown();
        ItemStack increaseChanceItem = FItems.createChanceModifier(player);
        ItemStack item = event.getItem();
        if (item == null) {
            if (event.getAction() == Action.LEFT_CLICK_AIR) {
                if (method.hasFarm(player.getUniqueId())) {
                    for (Animals animal : method.toggleSnowBlower.keySet()) {
                        if (method.isToggledSnowBlower(animal)) {
                            AbilityDisplay.getInstance().calculateLine(animal, player, Particle.SNOWBALL);
                        }
                    }
                    for (Animals animal : method.toggleSprinkler.keySet()) {
                        if (method.isToggledSprinkler(animal)) {
                            AbilityDisplay.getInstance().calculateLine(animal, player, Particle.WATER_SPLASH);
                        }
                    }
                    for (Animals animal : method.toggleShield.keySet()) {
                        if (method.isToggledShield(animal)) {
                            AbilityDisplay.getInstance().calculateContinuousSphere(animal, Particle.WATER_BUBBLE, 5);
                        }
                    }
                    for (Animals animal : method.toggleExplosive.keySet()) {
                        if (method.isToggledExplosive(animal)) {
                            AbilityDisplay.getInstance().calculateArc(animal, player, Particle.FLAME);
                        }
                    }
                }
            }
        }
        if (item != null) {
            if (event.getHand() == EquipmentSlot.HAND) {
                if (item.isSimilar(resetTool)) {
                    if (item.getItemMeta().getDisplayName().equalsIgnoreCase(resetTool.getItemMeta().getDisplayName())) {
                        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                            method.removeCatcherCooldown(player);
                        }
                    }
                }
                if (item.isSimilar(increaseChanceItem)) {
                    if (item.getItemMeta().getDisplayName().equalsIgnoreCase(increaseChanceItem.getItemMeta().getDisplayName())) {
                        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                            method.increaseCatchRate(player, item);
                        }
                    }
                }
                if (item.isSimilar(catchTool)) {
                    if (item.getItemMeta().getDisplayName().equalsIgnoreCase(tool.getCatcher().getItemMeta().getDisplayName())) {
                        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
                            method.capture.add(player);
                        }
                    }
                }

                Plugin pluginGP = getServer().getPluginManager().getPlugin("GriefPrevention");
                if (pluginGP != null) {
                    if (GriefPrevention.instance.config_claims_modificationTool != null) {
                        if (item.getType() == GriefPrevention.instance.config_claims_modificationTool) {
                            if (event.getHand() == EquipmentSlot.HAND) {
                                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                    method.intersectsWithFarm(event.getClickedBlock().getLocation(), player);
                                }
                            }
                        }
                    }
                }
                Plugin pluginWE = getServer().getPluginManager().getPlugin("WorldEdit");
                if (pluginWE != null) {
                    WorldEditPlugin we = (WorldEditPlugin) pluginWE;
                    if (item.getType() == Material.WOOD_AXE) {
                        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            Selection selection = we.getSelection(player);
                            if (selection != null) {
                                Location minLoc = selection.getMinimumPoint();
                                Location maxLoc = selection.getMaximumPoint();
                                if (minLoc != null && maxLoc != null) {
                                    if (method.intersectsWithFarm(minLoc, maxLoc, player)) {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                if (selTool != null && catchTool != null) {
                    if (item.isSimilar(selTool)) {
                        if (item.hasItemMeta()) {
                            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                                if (player.hasPermission(FarmPermissions.COMMAND_START_FARM.toString()) || player.isOp()) {
                                    if (method.getCount(player) == 0) {
                                        World world = event.getClickedBlock().getLocation().getWorld();
                                        double x = event.getClickedBlock().getLocation().getBlockX();
                                        double y = event.getClickedBlock().getLocation().getBlockY();
                                        double z = event.getClickedBlock().getLocation().getBlockZ();
                                        blockLocFirst.put(player, new Location(world, x, y, z));
                                        blockVecFirst.put(player, new BlockVector(x, y, z));
                                        player.sendMessage(ChatColor.BLUE + "Selected First Block @ " + "x:" + x + " y:" + y
                                                + " z:" + z);
                                        method.addCount(player, 1);
                                        player.sendMessage(ChatColor.AQUA + "Left Click Another Block");
                                    } else if (method.getCount(player) == 1) {
                                        World world = event.getClickedBlock().getLocation().getWorld();
                                        double x = event.getClickedBlock().getLocation().getBlockX();
                                        double y = event.getClickedBlock().getLocation().getBlockY();
                                        double z = event.getClickedBlock().getLocation().getBlockZ();
                                        blockLocSecond.put(player, new Location(world, x, y, z));
                                        blockVecSecond.put(player, new BlockVector(x, y, z));
                                        player.sendMessage(ChatColor.BLUE + "Selected Second Block @ " + "x:" + x + " y:" + y
                                                + " z:" + z);
                                        method.calculateFarmSize(player, blockVecFirst.get(player), blockVecSecond.get(player));
                                        if (method.isValidFarmSize(player)) {
                                            if (method.isValidFarmLocation(player, blockVecFirst.get(player)
                                                    , blockVecSecond.get(player))) {
                                                method.subtractCount(player, 1);
                                            }
                                        }
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have Permission To Use This Item");
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @SuppressWarnings("unused")
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball) {
            Snowball snowball = (Snowball) event.getDamager();
            Entity hitBySnowball = event.getEntity();
            ProjectileSource shooter = snowball.getShooter();
            if (shooter instanceof Player) {
                Player player = (Player) shooter;
                if (method.capture.contains(player)) {
                    if (hitBySnowball.getType() == method.getEntityType(hitBySnowball)) {
                        Animals animal = (Animals) hitBySnowball;
                        if (method.hasFarm(player.getUniqueId())) {
                            event.setCancelled(true);
                            method.addFarmAnimal(animal, player);
                        } else {
                            player.sendMessage(ChatColor.DARK_AQUA + " You Do Not Own A Farm");
                        }
                    }
                }
            }
        }
    }

    /*
    @SuppressWarnings("unused")
    @EventHandler
    public void interactEntityEvent (PlayerInteractEntityEvent event) {
        ItemStack catchTool = tool.getCatcher();
        if (event.getRightClicked() instanceof Item) {
            ItemStack item = ((Item) event.getRightClicked()).getItemStack();
            if (item.isSimilar(catchTool)) {
                if (!(method.isObtainable())) {
                    event.setCancelled(true);
                    getServer().broadcastMessage("Is Not Obtainable");
                }
            }
        }
    }
    */

    @SuppressWarnings("unused")
    @EventHandler
    public void playerJoinServer(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (method.hasFarm(player.getUniqueId())) {
            if (config.getAnimals().get("Animals") != null) {
                method.loadAnimals(player);
            }
            if (config.getFarms().get("Farms") != null) {
                method.loadFarmExp(player);
                method.loadFarmLevel(player);
            }
            if (config.getFarmMail().get("Mail") != null) {
                method.loadFarmMail(player);
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        if (method.hasFarm(player.getUniqueId())) {
            if (inv.getName().equalsIgnoreCase(method.getPlayerFarmsInv(player).getName())) {
                if (method.isInTransfer(player)) {
                    method.cancelTransfer(player);
                }
            }
        }
    }

    @SuppressWarnings({"unused", "deprecation"})
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        World world = player.getWorld();
        ItemStack clicked = event.getCurrentItem();
        Inventory inventory = event.getInventory();
        if (clicked != null) {
            if (clicked.hasItemMeta()) {
                if (clicked.getItemMeta().hasLore()) {
                    if (inventory.getName().equals(FarmMethods.getInstance().getPlayerFarmsInv(player).getName())) {
                        event.setCancelled(true);
                        if (clicked.isSimilar(FItems.createFarmsListItem(method.getFarmFromItem(clicked)))) {
                            if (method.isInTransfer(player)) {
                                method.transferAnimal(player, method.getFarmFromItem(clicked));
                            }
                        }
                    }
                    if (method.isAnimalInvItem(clicked)) {
                        if (clicked.isSimilar(FItems.createReturnItem((Animals) method.getAnimalFromItem(clicked, world)))) {
                            event.setCancelled(true);
                            if (inventory.getName().equals(FarmMethods.getInstance().getAnimalInv(player, clicked).getName())) {
                                method.openFarmInv(player);
                            } else if (inventory.getName().equals(FarmMethods.getInstance().getModifyInv(player, clicked).getName())) {
                                method.openFarmInv(player);
                            }
                        }
                        if (inventory.getName().equals(method.getFarmInv(player).getName())) {
                            event.setCancelled(true);
                            if (clicked.isSimilar(FItems.createEgg((Animals) method.getAnimalFromItem(clicked, world)))) {
                                method.openAnimalInv(player, clicked);
                            }
                        } else if (inventory.getName().equals(method.getAnimalInv(player, clicked).getName())) {
                            event.setCancelled(true);
                            //Teleports Animal To Player
                            if (clicked.isSimilar(FItems.createTeleportHereItem((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                method.teleportAnimalToPlayer(player, clicked);
                            }
                            //Disables Animals AI
                            if (clicked.isSimilar(FItems.createFreezeAnimalItem((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                method.freezeAnimal(clicked, player);
                            }
                            //Opens The Animals Modification InventoryGSON
                            if (clicked.isSimilar(FItems.createModificationItem((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                method.openModifyInv(player, clicked);
                            }
                            //Tames An Animal
                            if (clicked.isSimilar(FItems.createTameItem((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                if (method.hasTameUsages(player)) {
                                    method.tameAnimal(clicked, player);
                                    method.openAnimalInv(player, clicked);
                                }
                            }
                            //Replenishes A Sheep's Fur
                            if (clicked.isSimilar(FItems.createSheepFurReset((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                if (method.hasReplenishUsages(player)) {
                                    method.resetSheepFur(clicked, world);
                                }
                            }
                            //Transfer Animal To Another Farm
                            if ((clicked.isSimilar(FItems.createTransferBetweenFarms((Animals) method
                                    .getAnimalFromItem(clicked, world), player)))) {
                                method.setInTransfer(player, clicked);
                                method.openPlayerFarmsInv(player);
                            }
                            //Teleports An Animal Home IE. Player Spawn
                            if (clicked.isSimilar(FItems.createHomeItem((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                method.teleportAnimalHome(player, clicked);
                            }
                            //Teleports Player To An Animal
                            if (clicked.isSimilar(FItems.createTeleportToItem((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                method.teleportPlayerToAnimal(player, clicked);
                            }
                            //Opens Animals Abilities
                            if (clicked.isSimilar(FItems.createAbilityListItem((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                method.openAbilityInv(player, clicked);
                            }
                            //Set An Animal To Follow Player
                            if (clicked.isSimilar(FItems.createAnimalFollowItem((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                if (method.isFollowing(player, (Animals) method.getAnimalFromItem(clicked, world))) {
                                    method.removeFollowing(player, (Animals) method.getAnimalFromItem(clicked, world));
                                } else {
                                    method.setFollowing(player, (Animals) method.getAnimalFromItem(clicked, world));
                                }
                            }
                        } else if (method.getModifyInv(player, clicked) != null && inventory.getName()
                                .equals(method.getModifyInv(player, clicked).getName())) {
                            event.setCancelled(true);
                            if (method.hasStyleChangeUsages(player)) {
                                //Looks Confusing, It Just Runs A Lot Of Checks To Prevent Bugs
                                if (clicked.isSimilar(FItems.createOcelotChangeItem((Animals)
                                        method.getAnimalFromItem(clicked, world), method.getOcelotTypeFromString(clicked)))
                                        || clicked.isSimilar(FItems.createHorseChangeItem((Animals)
                                        method.getAnimalFromItem(clicked, world), method.getHorseVariantFromString(clicked)))) {
                                    if (method.getAnimalFromItem(clicked, world) instanceof Ocelot) {
                                        method.setOcelotType(clicked, player);
                                    } else if (method.getAnimalFromItem(clicked, world) instanceof Horse) {
                                        method.setHorseVariant(clicked, player);
                                    }
                                }
                                if (clicked.isSimilar(FItems.createHorseStyleItem((Animals)
                                        method.getAnimalFromItem(clicked, world), method.getHorseStyleFromString(clicked)))) {
                                    if (method.getAnimalFromItem(clicked, world) instanceof Horse) {
                                        method.setHorseStyle(clicked, player);
                                    }
                                }
                                if (clicked.isSimilar(FItems.createHorseColorItem((Animals)
                                        method.getAnimalFromItem(clicked, world), method.getHorseColorFromString(clicked)))) {
                                    if (method.getAnimalFromItem(clicked, world) instanceof Horse) {
                                        method.setHorseColor(clicked, player);
                                    }
                                }
                                if (clicked.isSimilar(FItems.createAnimalColorItem((Animals)
                                        method.getAnimalFromItem(clicked, world), method.getColorFromString(clicked)))) {
                                    method.colorAnimal(clicked, player, method.getColorFromString(clicked));
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have Any Style Change Usages");
                            }
                        } else if (inventory.getName().equals(method.getAbilityInv(player, clicked).getName())) {
                            event.setCancelled(true);
                            if (clicked.isSimilar(AItems.createSnowBlowerDisplay((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                method.setToggleSnowBlower(player, (Animals) method.getAnimalFromItem(clicked, world));
                            } else if (clicked.isSimilar(AItems.createSprinklerDisplay((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                method.setToggleSprinkler(player, (Animals) method.getAnimalFromItem(clicked, world));
                            } else if (clicked.isSimilar(AItems.createShieldDisplay((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                method.setToggleShield(player, (Animals) method.getAnimalFromItem(clicked, world));
                            } else if (clicked.isSimilar(AItems.createExplosiveDisplay((Animals) method
                                    .getAnimalFromItem(clicked, world)))) {
                                method.setToggleExplosive(player, (Animals) method.getAnimalFromItem(clicked, world));
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Animals) {
            Animals animal = (Animals) entity;
            if (method.isFarmAnimal(animal.getUniqueId())) {
                method.removeFarmAnimal((Animals) entity);
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack selTool = tool.getTool();
        if (selTool != null && player.getInventory().getItemInMainHand() != null) {
            if (player.getInventory().getItemInMainHand().isSimilar(selTool)) {
                event.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void commandSend(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage();
        Player player = event.getPlayer();
        if (cmd.startsWith("/farm")) {
            if (config.getFav().getBoolean("CommandCooldown.Enabled")) {
                if (!player.hasPermission(FarmPermissions.BYPASS_FARM_COMMAND_COOLDOWN.toString())) {
                    if (!method.isDelayedCommand(player)) {
                        method.delayCommand(player, config.getFav().getLong("CommandCooldown.Time"));
                    } else {
                        player.sendMessage(ChatColor.DARK_AQUA + "On Cooldown For " + ChatColor.GOLD + method.getDelayedLeft(player)
                                + ChatColor.DARK_AQUA + " More Seconds");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
package io.github.favfarms.executor;

import io.github.favfarms.FavFarms;
import io.github.favfarms.configuration.FarmText;
import io.github.favfarms.farm.FarmMethods;
import io.github.favfarms.permission.FarmPermissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * FavFarms Created by Awesome Red on 8/24/2016.
 */
public class FarmExecutor implements CommandExecutor {

    private FavFarms farms;

    public FarmExecutor(FavFarms farms) {
        this.farms = farms;
    }

    private FarmText text = FarmText.getInstance();
    private FarmMethods method = FarmMethods.getInstance();

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (label.equalsIgnoreCase("farm")) {
                if (args.length == 0) {
                    if (sender instanceof ConsoleCommandSender) {
                        sender.sendMessage(text.getInfo());
                    } else if (sender.hasPermission(FarmPermissions.COMMAND_FARM.toString()) || sender.isOp()) {
                        sender.sendMessage(text.getInfo());
                    } else {
                        sender.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                + FarmPermissions.COMMAND_FARM.toString());
                    }
                }
                if (sender instanceof Player) {
                    Player player = ((Player) sender).getPlayer();
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("reload")) {
                            if (player.hasPermission(FarmPermissions.COMMAND_RELOAD.toString()) || player.isOp()) {
                                method.reloadFavFarms();
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                        + FarmPermissions.COMMAND_RELOAD.toString());
                            }
                        } else if (args[0].equalsIgnoreCase("update")) {
                            if (player.isOp()) {
                                FavFarms.update();
                            } else {
                                player.sendMessage(ChatColor.DARK_RED + "Must Be An OP To Run This Command");
                            }
                        } else if (args[0].equalsIgnoreCase("list")) {
                            if (player.hasPermission(FarmPermissions.COMMAND_LIST_FARMS.toString()) || player.isOp()) {
                                method.listFarms(player);
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                        + FarmPermissions.COMMAND_LIST_FARMS.toString());
                            }
                        } else if (args[0].equalsIgnoreCase("tool")) {
                            if (player.hasPermission(FarmPermissions.COMMAND_TOOL_SELF.toString()) || player.isOp()) {
                                method.giveSelector(player);
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                        + FarmPermissions.COMMAND_TOOL_SELF.toString());
                            }
                        } else if (args[0].equalsIgnoreCase("catcher")) {
                            if (player.hasPermission(FarmPermissions.COMMAND_OBTAIN_CATCHER.toString()) || player.isOp()) {
                                method.giveCatcher(player);
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                        + FarmPermissions.COMMAND_OBTAIN_CATCHER.toString());
                            }
                        } else if (args[0].equalsIgnoreCase("show")) {
                            if (player.hasPermission(FarmPermissions.COMMAND_SHOW_SELF.toString()) || player.isOp()) {
                                method.highlightCorners(player);
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                        + FarmPermissions.COMMAND_SHOW_SELF.toString());
                            }
                        } else if (args[0].equalsIgnoreCase("inv")) {
                            if (player.hasPermission(FarmPermissions.COMMAND_INV_SELF.toString()) || player.isOp()) {
                                method.openFarmInv(player);
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                        + FarmPermissions.COMMAND_INV_SELF.toString());
                            }
                        } else if (args[0].equalsIgnoreCase("info")) {
                            if (player.hasPermission(FarmPermissions.COMMAND_INFO_SELF.toString()) || player.isOp()) {
                                method.getFarmInfo(player);
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                        + FarmPermissions.COMMAND_INFO_SELF.toString());
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_AQUA + "Not A Valid Farm Plugin Command");
                        }
                    }
                    if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("start")) {
                            if (player.hasPermission(FarmPermissions.COMMAND_START_FARM.toString()) || player.isOp()) {
                                if (method.isSelection(player)) {
                                    if (args[1] != null && !(args[1].equals(" "))) {
                                        method.createFarmData(player, args[1]);
                                    } else {
                                        player.sendMessage(ChatColor.DARK_AQUA + "Farm Name Cannot Be Empty");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "There is no selection");
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                        + FarmPermissions.COMMAND_START_FARM.toString());
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (player.hasPermission(FarmPermissions.COMMAND_REMOVE_FARM.toString()) || player.isOp()) {
                                if (args[1] != null && !(args[1].equals(" "))) {
                                    method.removeFarm(player, args[1]);
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "Farm Name Cannot Be Empty");
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                        + FarmPermissions.COMMAND_REMOVE_FARM.toString());
                            }
                        } else if (args[0].equalsIgnoreCase("info")) {
                            if (player.hasPermission(FarmPermissions.COMMAND_INFO_OTHERS.toString()) || player.isOp()) {
                                if (args[1] != null && farms.getServer().getPlayer(args[1]) != null) {
                                    Player target = farms.getServer().getPlayer(args[1]);
                                    method.getFarmInfo(player, target.getUniqueId());
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "Player" + args[1] + " Not Found");
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                        + FarmPermissions.COMMAND_INFO_OTHERS.toString());
                            }
                        } else if (args[0].equalsIgnoreCase("set")) {
                            if (args[1].equalsIgnoreCase("spawn")) {
                                if (player.hasPermission(FarmPermissions.COMMAND_SET_SPAWN.toString()) || player.isOp()) {
                                    method.setFarmSpawn(player);
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                            + FarmPermissions.COMMAND_SET_SPAWN.toString());
                                }
                            }
                        } else if (args[0].equalsIgnoreCase("inv")) {
                            if (player.hasPermission(FarmPermissions.COMMAND_INV_OTHER.toString()) || player.isOp()) {
                                if (args[1] != null && farms.getServer().getPlayer(args[1]) != null) {
                                    Player target = farms.getServer().getPlayer(args[1]);
                                    method.openFarmInv(player, target);
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "Player" + args[1] + " Not Found");
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                        + FarmPermissions.COMMAND_INV_OTHER.toString());
                            }
                        } else if (args[0].equalsIgnoreCase("animals")) {
                            if (player.hasPermission(FarmPermissions.COMMAND_FARM_ANIMALS.toString()) || player.isOp()) {
                                if (args[1] != null && farms.getServer().getPlayer(args[1]) != null) {
                                    Player target = farms.getServer().getPlayer(args[1]);
                                    method.getFarmAnimals(target.getUniqueId(), target.getWorld());
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "Player" + args[1] + " Not Found");
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                        + FarmPermissions.COMMAND_FARM_ANIMALS.toString());
                            }
                        } else if (args[0].equalsIgnoreCase("mail")) {
                            if (args[1].equalsIgnoreCase("check")) {
                                player.sendMessage(method.getMail(player));
                            } else if (args[1].equalsIgnoreCase("clear")) {
                                method.clearMail(player);
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_AQUA + "Not A Valid Farm Plugin Command");
                        }
                    } else if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("give")) {
                            if (args[1].equalsIgnoreCase("reset")) {
                                if (player.hasPermission(FarmPermissions.COMMAND_OBTAIN_RESET.toString()) || player.isOp()) {
                                    if (args[2] != null && farms.getServer().getPlayer(args[2]) != null) {
                                        Player target = farms.getServer().getPlayer(args[2]);
                                        method.giveCatcherResetItem(target);
                                    } else {
                                        player.sendMessage(ChatColor.DARK_AQUA + "Player" + args[2] + " Not Found");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                            + FarmPermissions.COMMAND_OBTAIN_RESET.toString());
                                }
                            } else if (args[1].equalsIgnoreCase("catcher")) {
                                if (player.hasPermission(FarmPermissions.COMMAND_OBTAIN_CATCHER_OTHER.toString()) || player.isOp()) {
                                    if (args[2] != null && farms.getServer().getPlayer(args[2]) != null) {
                                        Player target = farms.getServer().getPlayer(args[2]);
                                        method.giveCatcher(player, target);
                                    } else {
                                        player.sendMessage(ChatColor.DARK_AQUA + "Player" + args[2] + " Not Found");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                            + FarmPermissions.COMMAND_OBTAIN_CATCHER_OTHER.toString());
                                }
                            } else if (args[1].equalsIgnoreCase("increase")) {
                                if (args[2] != null && farms.getServer().getPlayer(args[2]) != null) {
                                    Player target = farms.getServer().getPlayer(args[2]);
                                    method.giveChanceModifierItem(target);
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "Player" + args[2] + " Not Found");
                                }
                            }
                        }
                    } else if(args.length == 4) {
                        if (args[0].equalsIgnoreCase("set")) {
                            if (args[1].equalsIgnoreCase("tames")) {
                                if (player.hasPermission(FarmPermissions.COMMAND_SET_TAMES.toString()) || player.isOp()) {
                                    if (args[2] != null && farms.getServer().getPlayer(args[2]) != null) {
                                        Player target = farms.getServer().getPlayer(args[2]);
                                        if (args[3] != null && method.isNumeric(args[3])) {
                                            Integer amount = Integer.parseInt(args[3]);
                                            method.setTameUsages(target, amount);
                                        } else {
                                            player.sendMessage(ChatColor.DARK_AQUA + "The Numbers Cannot Be Null And Must " +
                                                    "Be An Integer");
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.DARK_AQUA + "Player" + args[2] + " Not Found");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                            + FarmPermissions.COMMAND_SET_TAMES.toString());
                                }
                            } else if (args[1].equalsIgnoreCase("replenishes")) {
                                if (player.hasPermission(FarmPermissions.COMMAND_SET_REPLENISHES.toString()) || player.isOp()) {
                                    if (args[2] != null && farms.getServer().getPlayer(args[2]) != null) {
                                        Player target = farms.getServer().getPlayer(args[2]);
                                        if (args[3] != null && method.isNumeric(args[3])) {
                                            Integer amount = Integer.parseInt(args[3]);
                                            method.setReplenishUsages(target, amount);
                                        } else {
                                            player.sendMessage(ChatColor.DARK_AQUA + "The Numbers Cannot Be Null And Must " +
                                                    "Be An Integer");
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.DARK_AQUA + "Player" + args[2] + " Not Found");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                            + FarmPermissions.COMMAND_SET_REPLENISHES.toString());
                                }
                            } else if (args[1].equalsIgnoreCase("stylechanges")) {
                                if (player.hasPermission(FarmPermissions.COMMAND_SET_STYLECHANGES.toString()) || player.isOp()) {
                                    if (args[2] != null && farms.getServer().getPlayer(args[2]) != null) {
                                        Player target = farms.getServer().getPlayer(args[2]);
                                        if (args[3] != null && method.isNumeric(args[3])) {
                                            Integer amount = Integer.parseInt(args[3]);
                                            method.setStyleChangeUsages(target, amount);
                                        } else {
                                            player.sendMessage(ChatColor.DARK_AQUA + "The Numbers Cannot Be Null And Must " +
                                                    "Be An Integer");
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.DARK_AQUA + "Player" + args[2] + " Not Found");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                            + FarmPermissions.COMMAND_SET_STYLECHANGES.toString());
                                }
                            }
                        } else if (args[0].equalsIgnoreCase("add")) {
                            if (args[1].equalsIgnoreCase("tames")) {
                                if (player.hasPermission(FarmPermissions.COMMAND_ADD_TAMES.toString()) || player.isOp()) {
                                    if (args[2] != null && farms.getServer().getPlayer(args[2]) != null) {
                                        Player target = farms.getServer().getPlayer(args[2]);
                                        if (args[3] != null && method.isNumeric(args[3])) {
                                            Integer amount = Integer.parseInt(args[3]);
                                            method.addTameUsages(target, amount);
                                        } else {
                                            player.sendMessage(ChatColor.DARK_AQUA + "The Numbers Cannot Be Null And Must " +
                                                    "Be An Integer");
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.DARK_AQUA + "Player" + args[2] + " Not Found");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                            + FarmPermissions.COMMAND_ADD_TAMES.toString());
                                }
                            } else if (args[1].equalsIgnoreCase("replenishes")) {
                                if (player.hasPermission(FarmPermissions.COMMAND_ADD_REPLENISHES.toString()) || player.isOp()) {
                                    if (args[2] != null && farms.getServer().getPlayer(args[2]) != null) {
                                        Player target = farms.getServer().getPlayer(args[2]);
                                        if (args[3] != null && method.isNumeric(args[3])) {
                                            Integer amount = Integer.parseInt(args[3]);
                                            method.addReplenishUsage(target, amount);
                                        } else {
                                            player.sendMessage(ChatColor.DARK_AQUA + "The Numbers Cannot Be Null And Must " +
                                                    "Be An Integer");
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.DARK_AQUA + "Player" + args[2] + " Not Found");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                            + FarmPermissions.COMMAND_ADD_REPLENISHES.toString());
                                }
                            } else if (args[1].equalsIgnoreCase("stylechange")) {
                                if (player.hasPermission(FarmPermissions.COMMAND_ADD_STYLECHANGES.toString()) || player.isOp()) {
                                    if (args[2] != null && farms.getServer().getPlayer(args[2]) != null) {
                                        Player target = farms.getServer().getPlayer(args[2]);
                                        if (args[3] != null && method.isNumeric(args[3])) {
                                            Integer amount = Integer.parseInt(args[3]);
                                            method.addStyleChangeUsage(target, amount);
                                        } else {
                                            player.sendMessage(ChatColor.DARK_AQUA + "The Numbers Cannot Be Null And Must " +
                                                    "Be An Integer");
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.DARK_AQUA + "Player" + args[2] + " Not Found");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.DARK_AQUA + "You Do Not Have The Permission "
                                            + FarmPermissions.COMMAND_ADD_STYLECHANGES.toString());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}

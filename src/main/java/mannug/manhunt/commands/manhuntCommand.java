package mannug.manhunt.commands;

import mannug.manhunt.Gui.impl.manhuntGui;
import mannug.manhunt.ManhuntManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.StringUtil;

import java.util.*;

public class manhuntCommand implements CommandExecutor {
    private ManhuntManager manhuntManager;
    private BukkitTask task;
    public manhuntCommand(ManhuntManager manhuntManager){
        this.manhuntManager = manhuntManager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            manhuntManager.sendError(sender,"Only Players Can Use This Command");
            return true;
        }

        if(args.length == 0){
            manhuntManager.sendMessage(sender,"Opening Manhunt Selector!");
            manhuntManager.getGuiManager().setGui((Objects.requireNonNull(((Player) sender).getPlayer())),new manhuntGui(manhuntManager));
        }

        if(args.length >=1) {
            if (args[0].equalsIgnoreCase("start")) {
                if (!sender.hasPermission("Manhunt.start")) {
                    manhuntManager.sendError(sender, "You Don't Have Permission to Use This Command!");
                    return true;
                }
                if (manhuntManager.isStarted()) {
                    manhuntManager.sendError(sender, "Game is Already Started");
                    return true;
                }
                if (!manhuntManager.getHunters().isEmpty() && !manhuntManager.getSpeedrunners().isEmpty()) {
                    manhuntManager.setStarted(true);
                    manhuntManager.setStartTime(new Date());
                    manhuntManager.setGrease(0);
                    if (args.length >= 2) {
                        try {
                            manhuntManager.setGrease(Integer.parseInt(args[1]));
                        } catch (Exception e) {
                            manhuntManager.sendError(sender, args[1] + " is Not a Valid integer , Give a valid integer for Grace Period!");
                            return true;
                        }
                    }
                    World world = Bukkit.getWorld("world");
                    for (UUID player : manhuntManager.getSpeedrunners()) {
                        Player player1 = Bukkit.getPlayer(player);
                        player1.getInventory().clear();
                        player1.setGameMode(GameMode.SURVIVAL);
                        player1.setHealth(player1.getMaxHealth());
                        player1.teleport(world.getSpawnLocation());
                    }
                    for (UUID uuid : manhuntManager.getSpectators()) {
                        Bukkit.getPlayer(uuid).setGameMode(GameMode.SPECTATOR);
                        Bukkit.getPlayer(uuid).teleport(world.getSpawnLocation());
                    }
                    if (manhuntManager.getGrease() != 0) {
                        for (UUID uuid : manhuntManager.getHunters()) {
                            Player player1 = Bukkit.getPlayer(uuid);
                            player1.getInventory().clear();
                            player1.setGameMode(GameMode.SURVIVAL);
                            player1.setHealth(player1.getMaxHealth());
                            player1.teleport(world.getSpawnLocation());
                        }

                        task = Bukkit.getScheduler().runTaskTimer(manhuntManager.getManhunt(), () -> {
                            if (manhuntManager.getGrease() > 0) {
                                Bukkit.broadcastMessage(manhuntManager.getPrefix() + "Grace Period will end in " + manhuntManager.getGrease());
                                manhuntManager.setGrease(manhuntManager.getGrease() - 5);
                            } else {
                                for (UUID uuid : manhuntManager.getHunters()) {
                                    Player player1 = Bukkit.getPlayer(uuid);
                                    player1.getInventory().clear();
                                    player1.setGameMode(GameMode.SURVIVAL);
                                    player1.getInventory().addItem(manhuntManager.getCompass());
                                    player1.setHealth(player1.getMaxHealth());
                                    player1.teleport(world.getSpawnLocation());
                                }
                                Bukkit.broadcastMessage(manhuntManager.getPrefix() + "Game Has Been Started , Let The Battle Begin!");
                                task.cancel();
                            }
                        }, 0L, 100L);

                    } else {
                        for (UUID uuid : manhuntManager.getHunters()) {
                            Player player1 = Bukkit.getPlayer(uuid);
                            player1.getInventory().clear();
                            player1.setGameMode(GameMode.SURVIVAL);
                            player1.getInventory().addItem(manhuntManager.getCompass());
                            player1.setHealth(player1.getMaxHealth());
                            player1.teleport(world.getSpawnLocation());
                        }
                        Bukkit.broadcastMessage(manhuntManager.getPrefix() + "Game Has Been Started , Let The Battle Begin!");
                    }
                } else {
                    manhuntManager.sendError(sender, "Not Enough Hunters or Speedrunners To Start!");
                }
            }
            else if(args[0].equalsIgnoreCase("force")){
                if(args.length >= 3) {
                    if (!sender.hasPermission("Manhunt.force")) {
                        manhuntManager.sendError(sender, "You Don't Have Permission to Use This Command!");
                        return true;
                    }
                    if (Bukkit.getPlayer(args[1]) != null) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if (args[2].equalsIgnoreCase("spectator")) {
                            manhuntManager.addSpectator(player);
                            manhuntManager.sendError(sender, player.getDisplayName() + " is Now a Spectator!");
                        } else if (args[2].equalsIgnoreCase("hunter")) {
                            manhuntManager.addHunter(player);
                            manhuntManager.sendError(sender, player.getDisplayName() + " is Now a Hunter!");
                        } else if (args[2].equalsIgnoreCase("speedrunner")) {
                            manhuntManager.addSpeecrunner(player);
                            manhuntManager.sendError(sender, player.getDisplayName() + " is Now a Speedrunner!");
                        } else {
                            manhuntManager.sendError(sender, args[2] + " is Not a Valid Group!");
                            manhuntManager.sendError(sender, "Groups: spectator, hunter, speedrunner");
                            return true;
                        }

                    } else {
                        manhuntManager.sendError(sender, "Player " + args[1] + " is Not Online!");
                        return true;
                    }
                }
                else {
                    manhuntManager.sendError(sender,"Invalid Usage");
                    manhuntManager.sendWarn(sender,"Usage : /manhunt force <player> <group>");
                }
            }
            else if(args[0].equalsIgnoreCase("stop")){
                if(sender.hasPermission("Manhunt.stop")) {
                    if(manhuntManager.isStarted()){
                        manhuntManager.sendError(sender,"Game is Not Started!");
                        return true;
                    }
                    if (args.length > 2) {
                        if (args[1].equalsIgnoreCase("confirm")) {
                            manhuntManager.sendMessage(sender, "Ending Game!");
                            manhuntManager.endGame(((Player) sender).getDisplayName());
                        } else {
                            manhuntManager.sendError(sender, "Invalid Usage!");
                            manhuntManager.sendError(sender, "Uasge : /manhunt start confirm");
                        }
                    } else {
                        manhuntManager.sendError(sender, "Invalid Usage!");
                        manhuntManager.sendError(sender, "Uasge : /manhunt start confirm");
                    }
                }
                else {
                    manhuntManager.sendError(sender,"You Don't Have Permission To Use That Command");
                }
            }
            else {
                manhuntManager.sendError(sender,"Invalid Sub-Command");
                manhuntManager.sendWarn(sender,"SubCommands: start, stop, force");
            }
        }

        return true;
    }
}
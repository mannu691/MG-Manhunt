package mannug.manhunt.events;

import mannug.manhunt.Gui.Gui;
import mannug.manhunt.Gui.impl.PlayerSelector;
import mannug.manhunt.ManhuntManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class onPlayer implements Listener {
    private final ManhuntManager manhuntManager;
    public onPlayer(ManhuntManager manhuntManager){
        this.manhuntManager =manhuntManager;
    }
    @EventHandler
    public void onend(EntityDeathEvent e) {
        if(manhuntManager.isStarted()) {
            if (e.getEntity().getType().equals(EntityType.ENDER_DRAGON)) {
                manhuntManager.speedrunnerWon();
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (!e.hasItem()) return;
        if (!e.getItem().hasItemMeta()) return;
        if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Track Speedrunner")) {
            if (!manhuntManager.getHunters().contains(e.getPlayer().getUniqueId())) {
                manhuntManager.sendError(e.getPlayer(),"Only Hunters can Use This Compass!");
                return;
            }
            if (manhuntManager.getCompassRecord().containsKey(e.getPlayer())) {
                Player player = e.getPlayer();
                Player target = manhuntManager.getCompassRecord().get(e.getPlayer());
                if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    if (e.getPlayer().getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                        if (!target.getWorld().getEnvironment().equals(World.Environment.NORMAL) && !target.getWorld().equals(player.getWorld())) {
                            manhuntManager.sendError(player, "Speedrunner is Not In OverWorld!");
                            return;
                        }
                        player.setCompassTarget(manhuntManager.getCompassRecord().get(e.getPlayer()).getLocation());
                        player.sendMessage(ChatColor.GOLD + "Compass is Poiting to " + manhuntManager.getCompassRecord().get(player).getDisplayName());

                    } else if (e.getPlayer().getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                        if (manhuntManager.getConfig().getBoolean("compass-work-in-nether")) {
                            manhuntManager.sendError(player, "Compass Doesn't work in Nether!");
                            return;
                        }
                        if (!target.getWorld().getEnvironment().equals(World.Environment.NETHER) && !target.getWorld().equals(player.getWorld())) {
                            manhuntManager.sendError(player, "Speedrunner is Not In Nether!");
                            return;
                        }
                        player.setCompassTarget(manhuntManager.getCompassRecord().get(e.getPlayer()).getLocation());
                        player.sendMessage(ChatColor.GOLD + "Compass is Poiting to " + manhuntManager.getCompassRecord().get(player).getDisplayName());

                    } else if (e.getPlayer().getWorld().getEnvironment().equals(World.Environment.THE_END)) {
                        if (manhuntManager.getConfig().getBoolean("compass-work-in-end")) {
                            manhuntManager.sendError(player, "Compass Doesn't work in End!");
                            return;
                        }
                        if (!target.getWorld().getEnvironment().equals(World.Environment.THE_END) && !target.getWorld().equals(player.getWorld())) {
                            manhuntManager.sendError(player, "Speedrunner is Not In End!");
                            return;
                        }
                        player.setCompassTarget(manhuntManager.getCompassRecord().get(e.getPlayer()).getLocation());
                        player.sendMessage(ChatColor.GOLD + "Compass is Poiting to " + manhuntManager.getCompassRecord().get(player).getDisplayName());
                    }
                }
                else {
                    if (manhuntManager.getSpeedrunners().size() >= 1) {
                        manhuntManager.getGuiManager().setGui(e.getPlayer(), new PlayerSelector(manhuntManager));
                    } else {
                        manhuntManager.sendError(e.getPlayer(),"There is no Speedrunner!");
                    }
                }
            }
            else {
                if (manhuntManager.getSpeedrunners().size() >= 1) {
                    manhuntManager.getGuiManager().setGui(e.getPlayer(), new PlayerSelector(manhuntManager));
                } else {
                    manhuntManager.sendError(e.getPlayer(),"There is No Speedrunner!");
                }
            }
        }
    }
    @EventHandler
    public void onInventory(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;
        Player player = (Player) e.getWhoClicked();
        Gui gui = manhuntManager.getGuiManager().getOpenGui(player);
        if (gui == null) return;
        Gui newGui = gui.handleClick(player, e.getCurrentItem(), e.getView());
        e.getView().close();
        e.setCancelled(true);
        if (newGui != null) {
            manhuntManager.getGuiManager().setGui(player, gui);
        }

    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        if (manhuntManager.getGuiManager().getOpenGui((Player) e.getPlayer()) == null) return;
        if (e.getInventory().equals(manhuntManager.getGuiManager().getOpenGui((Player) e.getPlayer()).getInventory())) {
            manhuntManager.getGuiManager().getOpenGui((Player) e.getPlayer()).onOpen(((Player) e.getPlayer()), e.getInventory());
        }
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent e) {
        if (manhuntManager.getGuiManager().getOpenGui((Player) e.getWhoClicked()) == null) return;
        if (e.getInventory().equals(manhuntManager.getGuiManager().getOpenGui((Player) e.getWhoClicked()).getInventory())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        manhuntManager.getGuiManager().removeGui(player);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (manhuntManager.getHunters().contains(e.getPlayer().getUniqueId())) {
            e.getPlayer().getInventory().addItem(manhuntManager.getCompass());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (manhuntManager.isStarted()) {
            UUID uuid = e.getEntity().getPlayer().getUniqueId();
            Player player = e.getEntity().getPlayer();
            if (manhuntManager.getSpeedrunners().contains(uuid)) {
                player.sendMessage(ChatColor.GREEN+e.getEntity().getPlayer().getDisplayName()+ChatColor.GOLD+" Died!");
                manhuntManager.getSpeedrunners().remove(e.getEntity().getPlayer().getUniqueId());
                if (manhuntManager.getSpeedrunners().size() == 0) {
                    manhuntManager.hunterWon();
                }
            }
            if (manhuntManager.getHunters().contains(uuid)) {
                manhuntManager.addDeath(1);
                player.sendMessage(ChatColor.BLUE+e.getEntity().getPlayer().getDisplayName()+ChatColor.GOLD+" Died!");
                e.getDrops().removeIf(itemStack -> itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Track Speedrunner") && itemStack.getType().equals(Material.COMPASS));
            }

        }
    }
}
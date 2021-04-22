package mannug.manhunt.Gui.impl;

import mannug.manhunt.Gui.Gui;
import mannug.manhunt.ManhuntManager;
import mannug.manhunt.Utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class manhuntGui implements Gui {
    private final ManhuntManager manhuntManager;
    public manhuntGui(ManhuntManager manhuntManager){
        this.manhuntManager = manhuntManager;
    }
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null,27,getName());
        ItemBuilder sp = new ItemBuilder(Material.ENDER_EYE);
        sp.setName(ChatColor.GREEN+"Speedrunner");
        sp.setLore("Beat The Hunter and Kill EnderDragon!");
        ItemBuilder hunter = new ItemBuilder(Material.COMPASS);
        hunter.setName(ChatColor.RED+"Hunter");
        hunter.setLore("Kill The Speedrunner Before He Kills EnderDragon!");
        ItemBuilder spectator = new ItemBuilder(Material.PLAYER_HEAD);
        spectator.setName(ChatColor.YELLOW+"Spectator");
        spectator.setLore("Spectate Your Friends!");
        int i =0;
        while (i != 27){
            inventory.setItem(i,new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).setName(" ").toItemStack());
            i+=1;
        }
        inventory.setItem(11,sp.toItemStack());
        inventory.setItem(13,hunter.toItemStack());
        inventory.setItem(15,spectator.toItemStack());
        return inventory;
    }

    @Override
    public String getName() {
        return "Manhunt Group Selector";
    }

    @Override
    public Gui handleClick(Player player, ItemStack itemStack, InventoryView inventoryView) {
        if(itemStack.getType().equals(Material.GREEN_STAINED_GLASS_PANE)) return null;
        if(itemStack.getType().equals(Material.ENDER_EYE)){
            if(manhuntManager.getSpeedrunners().contains(player.getUniqueId())){
                Bukkit.broadcastMessage(manhuntManager.getPrefix()+player.getDisplayName()+" is Now a Speedrunner!");
                player.getInventory().addItem(manhuntManager.getCompass());
                return null;
            }
            if(manhuntManager.isStarted()){
                player.sendMessage(manhuntManager.getPrefix()+ChatColor.RED+"You Can't Switch Groups After Game Is Started!");
                return null;
            }
            manhuntManager.addSpeecrunner(player);
            Bukkit.broadcastMessage(manhuntManager.getPrefix()+player.getDisplayName()+" is Now a Speedrunner!");
        }
        if(itemStack.getType().equals(Material.COMPASS)){
            if(manhuntManager.getHunters().contains(player.getUniqueId())){
                Bukkit.broadcastMessage(manhuntManager.getPrefix()+player.getDisplayName()+" is Now a Hunter!");
                return null;
            }
            if(manhuntManager.isStarted()){
                player.sendMessage(manhuntManager.getPrefix()+ChatColor.RED+"You Can't Switch Groups After Game Is Started!");
                return null;
            }
            manhuntManager.addHunter(player);
            Bukkit.broadcastMessage(manhuntManager.getPrefix()+player.getDisplayName()+" is Now a Hunter!");
            player.getInventory().addItem(manhuntManager.getCompass());
        }
        if(itemStack.getType().equals(Material.PLAYER_HEAD)){
            if(manhuntManager.getSpectators().contains(player.getUniqueId())){
                Bukkit.broadcastMessage(manhuntManager.getPrefix()+player.getDisplayName()+" is Now a Spectator!");
                return null;
            }
            if(manhuntManager.getSpeedrunners().size() == 0){
                manhuntManager.hunterWon();
            }
            manhuntManager.addSpectator(player);
            Bukkit.broadcastMessage(manhuntManager.getPrefix()+player.getDisplayName()+" is Now a Spectator!");
            return null;
        }
        return null;
    }

    @Override
    public boolean isInventory(InventoryView view) {
        if(view.getTitle().equalsIgnoreCase(getName())) return true;
        return false;
    }

    @Override
    public Gui onOpen(Player player, Inventory inventory) {
        if(manhuntManager.getSpeedrunners().contains(player)){
            ItemBuilder sp = new ItemBuilder(Material.ENDER_EYE);
            sp.setName(ChatColor.GREEN+"Speedrunner");
            sp.setLore("Beat The Hunter and Kill EnderDragon!");
            inventory.setItem(11,sp.toItemStack());
        }
        else if(manhuntManager.getHunters().contains(player)){
            ItemBuilder hunter = new ItemBuilder(Material.COMPASS);
            hunter.setName(ChatColor.RED+"Hunter");
            hunter.setLore("Kill The Speedrunner Before He Kills EnderDragon!");
            inventory.setItem(13,hunter.toItemStack());
        }
        else if(manhuntManager.getSpectators().contains(player)){
            ItemBuilder spectator = new ItemBuilder(Material.PLAYER_HEAD);
            spectator.setName(ChatColor.YELLOW+"Spectator");
            spectator.setLore("Spectate Your Friends!");
            inventory.setItem(15,spectator.toItemStack());
        }
        return null;
    }
}

package mannug.manhunt.Gui.impl;

import mannug.manhunt.Gui.Gui;
import mannug.manhunt.Manhunt;
import mannug.manhunt.ManhuntManager;
import mannug.manhunt.Utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;
import java.util.UUID;

public class PlayerSelector implements Gui {
    private ManhuntManager manhuntManager;
    public PlayerSelector(ManhuntManager manhuntManager){
        this.manhuntManager = manhuntManager;
    }
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null,27,getName());
        ItemBuilder itemBuilder = new ItemBuilder(Material.PLAYER_HEAD);
        for(UUID player: manhuntManager.getSpeedrunners()){
            inventory.addItem(itemBuilder.setSkullOwner(Bukkit.getPlayer(player).getDisplayName()).setName(Bukkit.getPlayer(player).getDisplayName()).toItemStack());
        }
        return inventory;
    }

    @Override
    public String getName() {
        return "Player Selector";
    }

    @Override
    public Gui handleClick(Player player, ItemStack itemStack, InventoryView inventoryView) {
        if(!inventoryView.getTopInventory().contains(itemStack)) return null;
        if(!itemStack.getType().equals(Material.PLAYER_HEAD)) return null;
        if(!Objects.requireNonNull(Bukkit.getPlayer(itemStack.getItemMeta().getDisplayName())).isOnline()) return null;
        manhuntManager.getCompassRecord().put(player,Bukkit.getPlayer(itemStack.getItemMeta().getDisplayName()));
        player.sendMessage(manhuntManager.getPrefix()+"Compass is Now Pointing at "+itemStack.getItemMeta().getDisplayName());
        return null;
    }

    @Override
    public boolean isInventory(InventoryView view) {
        return view.getTitle().equalsIgnoreCase(getName());
    }

    @Override
    public Gui onOpen(Player player, Inventory inventory) {
        int i = 0;
        for(ItemStack itemStack : inventory.getContents()){
            if(itemStack.getType().equals(Material.PLAYER_HEAD)){
                if(manhuntManager.getCompassRecord().get(player).equals(Objects.requireNonNull(((SkullMeta) itemStack.getItemMeta()).getOwningPlayer()).getPlayer())){
                    inventory.setItem(i,new ItemBuilder(itemStack).addGlint().toItemStack());
                    return null;
                }
            }
            i +=1;
        }
        return null;
    }
}

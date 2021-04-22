package mannug.manhunt.Gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public interface Gui {
    Inventory getInventory();

    String getName();

    Gui handleClick(Player player, ItemStack itemStack, InventoryView inventoryView);

    boolean isInventory(InventoryView view);
    Gui onOpen(Player player,Inventory inventory);

}

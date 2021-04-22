package mannug.manhunt.Gui;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GuiManager {
    private final Map<Player, Gui> playerGuiMap;

    public GuiManager() {
        this.playerGuiMap = new HashMap<>();
    }

    public Gui getOpenGui(Player player) {
        return playerGuiMap.get(player);
    }

    public void setGui(Player player, Gui gui) {
        player.closeInventory();
        playerGuiMap.put(player, gui);
        player.openInventory(gui.getInventory());
    }

    public void removeGui(Player player) {
        playerGuiMap.remove(player);
    }
}

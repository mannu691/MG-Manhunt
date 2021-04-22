package mannug.manhunt;

import mannug.manhunt.commands.manhuntCommand;
import mannug.manhunt.commands.manhuntTab;
import mannug.manhunt.events.onPlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Manhunt extends JavaPlugin {
    private ManhuntManager manhuntManager;
    private Configuration config;
    @Override
    public void onEnable() {
        this.manhuntManager = new ManhuntManager(this);
        getServer().getPluginManager().registerEvents(new onPlayer(manhuntManager),this);
        getCommand("manhunt").setTabCompleter(new manhuntTab());
        getCommand("manhunt").setExecutor(new manhuntCommand(manhuntManager));
        this.saveDefaultConfig();
        this.config = getConfig();
        this.saveConfig();
    }

    public Configuration getConfigu() {
        return config;
    }
}

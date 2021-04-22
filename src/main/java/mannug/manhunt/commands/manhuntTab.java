package mannug.manhunt.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class manhuntTab implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        final List<String> commands = new ArrayList<>();
        if(args.length == 1) {
            if (sender.hasPermission("Manhunt.start")) commands.add("start");
            if (sender.hasPermission("Manhunt.stop")) commands.add("stop");
            if (sender.hasPermission("Manhunt.force")) commands.add("force");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }else if(args.length == 2){
            if (args[0].equalsIgnoreCase("force") && sender.hasPermission("Manhunt.force")){
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        commands.add(player.getDisplayName());
                    }
                    StringUtil.copyPartialMatches(args[1], commands, completions);
            }
            else if (args[0].equalsIgnoreCase("stop") && sender.hasPermission("Manhunt.stop")){
                    commands.add("confirm");
                    StringUtil.copyPartialMatches(args[1], commands, completions);
            }
        }
        else if(args.length == 3){
            if (args[0].equalsIgnoreCase("force") && sender.hasPermission("Manhunt.force")) {
                    commands.add("speedrunner");
                    commands.add("hunter");
                    commands.add("spectator");
                    StringUtil.copyPartialMatches(args[2], commands, completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }
}

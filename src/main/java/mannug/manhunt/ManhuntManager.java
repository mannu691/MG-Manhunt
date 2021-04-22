package mannug.manhunt;

import mannug.manhunt.Gui.GuiManager;
import mannug.manhunt.Utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ManhuntManager {
    private final Manhunt manhunt;
    private final List<UUID> hunters;
    private final List<UUID> speedrunners;
    private final List<UUID> spectators;
    private final String prefix;
    private boolean isStarted;
    private final GuiManager guiManager;
    private int deaths;
    private Date startTime;
    private final ItemStack compass;
    private final Map<Player,Player> compassRecord;
    private int grease;
    private Configuration config;
    public ManhuntManager(Manhunt manhunt){
        this.manhunt = manhunt;
        this.hunters = new ArrayList<>();
        this.speedrunners = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.prefix = ChatColor.GOLD+"["+ChatColor.DARK_AQUA+"Manhunt"+ChatColor.GOLD+"]"+ChatColor.GRAY+": "+ChatColor.GOLD;
        this.isStarted = false;
        this.guiManager = new GuiManager();
        this.deaths = 0;
        this.compass = new ItemBuilder(Material.COMPASS).setName(ChatColor.GOLD+"Track Speedrunner").setLore("Right-Click To Track Selected Speedrunner","Left-Click to Select Speedrunner").toItemStack();
        this.compassRecord = new HashMap<>();
        this.grease = 0;
        this.config = manhunt.getConfigu();
    }
    public Manhunt getManhunt() {
        return manhunt;
    }
    public void addHunter(Player player){
        if(!hunters.contains(player.getUniqueId())){
            spectators.remove(player.getUniqueId());
            speedrunners.remove(player.getUniqueId());
            hunters.add(player.getUniqueId());
        }
    }
    public void addSpeecrunner(Player player){
        if(!speedrunners.contains(player.getUniqueId())){
            spectators.remove(player.getUniqueId());
            hunters.remove(player.getUniqueId());
            speedrunners.add(player.getUniqueId());
        }
    }
    public void addSpectator(Player player){
        if(!spectators.contains(player.getUniqueId())){
            hunters.remove(player.getUniqueId());
            speedrunners.remove(player.getUniqueId());
            spectators.add(player.getUniqueId());
        }
    }
    public void removeSpectator(Player player){
        spectators.remove(player.getUniqueId());
    }
    public void removeHunter(Player player){
        hunters.remove(player.getUniqueId());
    }
    public void addDeath(int i){
        this.deaths +=i;
    }
    public void removeSpeedrunner(Player player){
        speedrunners.remove(player.getUniqueId());
    }
    public List<UUID> getHunters() {
        return hunters;
    }

    public List<UUID> getSpeedrunners() {
        return speedrunners;
    }

    public List<UUID> getSpectators() {
        return spectators;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public int getDeaths() {
        return deaths;
    }

    public Date getStartTime() {
        return startTime;
    }

    public ItemStack getCompass() {
        return compass;
    }

    public Map<Player, Player> getCompassRecord() {
        return compassRecord;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getGrease() {
        return grease;
    }

    public void setGrease(int grease) {
        this.grease = grease;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    /*
    ====================Messages Manager==================

     */
    public void sendMessage(Player player,String message){
        player.sendMessage(prefix+message);
    }
    public void sendError(Player player,String message){
        player.sendMessage(ChatColor.GRAY+"("+ChatColor.RED+"!"+ChatColor.GRAY+") "+ChatColor.RED+message);
    }
    public void sendWarn(Player player,String message){
        player.sendMessage(ChatColor.GRAY+"("+ChatColor.YELLOW+"!"+ChatColor.GRAY+") "+message);
    }
    public void sendMessage(CommandSender player, String message){
        player.sendMessage(prefix+message);
    }
    public void sendError(CommandSender player,String message){
        player.sendMessage(ChatColor.GRAY+"("+ChatColor.RED+"!"+ChatColor.GRAY+") "+ChatColor.RED+message);
    }
    public void sendWarn(CommandSender player,String message){
        player.sendMessage(ChatColor.GRAY+"("+ChatColor.YELLOW+"!"+ChatColor.GRAY+") "+message);
    }

    /*
        ================Winner Announce=============
      */
    public void hunterWon(){
        for(Player player1: Bukkit.getOnlinePlayers()){
            player1.sendMessage(ChatColor.AQUA+"------------------------------");
            player1.sendMessage(prefix+ChatColor.BLUE+"Hunters Have Won The Game!");
            player1.sendMessage(prefix+"Total Hunters: "+hunters.size());
            player1.sendMessage(prefix+"Total Speedrunners: "+speedrunners.size());
            player1.sendMessage(prefix+"Hunter Deaths: "+deaths);
            player1.sendMessage(prefix+"TimeTaken: "+getTime(startTime,new Date()));
            player1.sendMessage(ChatColor.AQUA+"------------------------------");
        }
        reset();
    }
    public void endGame(String s){
        for(Player player1: Bukkit.getOnlinePlayers()){
            player1.sendMessage(ChatColor.AQUA+"------------------------------");
            player1.sendMessage(prefix+ChatColor.BLUE+"Game Has Been Ended");
            player1.sendMessage(prefix+s+" Ended The Game!");
            player1.sendMessage(ChatColor.AQUA+"------------------------------");
        }
        reset();
    }
    public void speedrunnerWon(){
        for(Player player1: Bukkit.getOnlinePlayers()){
            player1.sendMessage(ChatColor.AQUA+"------------------------------");
            player1.sendMessage(prefix+ChatColor.GREEN+"Speedrunner Have Won The Game!");
            player1.sendMessage(prefix+"Total Hunters: "+hunters.size());
            player1.sendMessage(prefix+"Total Speedrunners: "+speedrunners.size());
            player1.sendMessage(prefix+"Hunter Deaths: "+deaths);
            player1.sendMessage(prefix+"TimeTaken: "+getTime(startTime,new Date()));
            player1.sendMessage(ChatColor.AQUA+"------------------------------");
        }
        reset();
    }
    public void reset(){
        this.hunters.clear();
        this.speedrunners.clear();
        this.spectators.clear();
        this.isStarted = false;
        this.deaths =0;
        this.compassRecord.clear();
    }
    public String getTime(Date started,Date ended){
        long difference_In_Time = ended.getTime() - started.getTime();
        long s = (difference_In_Time / 1000) % 60;
        long m = (difference_In_Time / (1000 * 60)) % 60;
        long h = (difference_In_Time / (1000 * 60 * 6)) % 24;
        return ""+h+ChatColor.BLUE+":"+ChatColor.GOLD+m+ChatColor.BLUE+":"+ChatColor.GOLD+s;
    }

    public Configuration getConfig() {
        return config;
    }
}

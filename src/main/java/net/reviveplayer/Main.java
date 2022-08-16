package net.reviveplayer;

import lombok.Getter;
import net.minecraft.world.scores.ScoreboardTeam;
import net.reviveplayer.events.BleedingEvent;
import net.reviveplayer.events.ReviveEvent;
import net.reviveplayer.listener.PlayerEvents;
import net.reviveplayer.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_19_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    @Getter
    public static boolean usePapi = false;
    @Getter
    private static int bleedingTime;
    @Getter
    private static Scoreboard scoreboard;
    @Getter
    private static Team team;
    @Getter
    private static Map<Player,Team> oldTeam = new HashMap<>();


    @Override
    public void onEnable() {
        instance = this;
        scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        setUpConfig();
        Util.loadMessages();
        start();
    }

    private void start(){
        Objects.requireNonNull(this.getCommand("defeat")).setExecutor(new Command(this));
        Objects.requireNonNull(this.getCommand("revive")).setExecutor(new PrincipalCommand());
        Objects.requireNonNull(this.getCommand("revive")).setTabCompleter(new PrincipalCommand());
        this.enableMenssage();
        bleedingTime = this.getConfig().getInt("time");
        //checkPapi();
        registerListener();
        newTeam();
    }

    public void newReloadConfig(){
        this.reloadConfig();
        Util.loadMessages();
        bleedingTime = this.getConfig().getInt("time");
    }

    public static void addPlayerTeam(Player player){

        player.setDisplayName(Main.format(Util.getMessage("chat_prefix") + player.getName()));
        scoreboard.getTeams().forEach((team) ->{
            if (team.hasPlayer(player) || team.hasEntry(player.getUniqueId().toString())){
                oldTeam.put(player,team);
                team.removePlayer(player);
                team.removeEntry(player.getUniqueId().toString());
            }
        });
        team.addEntry(player.getUniqueId().toString());
        team.addPlayer(player);
    }

    public static void removePlayerTeam(Player player){
        team.removeEntry(player.getUniqueId().toString());
        player.setDisplayName(Main.format(player.getName()));
        team.removePlayer(player);

        oldTeam.forEach((p,team) ->{
            if (p.equals(player)){
                team.addPlayer(player);
                team.removeEntry(player.getUniqueId().toString());
            }
        });
    }

    public static void removePlayerTeam(OfflinePlayer player){
        team.removeEntry(player.getUniqueId().toString());
        team.removePlayer(player);

        oldTeam.forEach((p,team) ->{
            if (p.equals(player)){
                team.addPlayer(player);
                team.removeEntry(player.getUniqueId().toString());
            }
        });
    }

    private void newTeam(){
        for (Team t : scoreboard.getTeams()) if (t.getName().equals("bleeding_team")){
            team = scoreboard.getTeam("bleeding_team");
            return;
        }
        team = scoreboard.registerNewTeam("bleeding_team");
        team.setColor(ChatColor.RED);

    }

    public void setUpConfig(){
        File config = new File(this.getDataFolder(), "config.yml");
        String path = config.getPath();

        if (!config.exists()){
            this.getConfig().options().copyDefaults(true);
            this.saveDefaultConfig();
        }
    }


    private void registerListener(){
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerEvents(),this);
    }

    private void enableMenssage(){
        Bukkit.getConsoleSender().sendMessage(format("&c------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(format("&c------&4 Revive Player is &l&eloaded&c------"));
        Bukkit.getConsoleSender().sendMessage(format("&c-----------------&av:"+this.getConfig().getString("ver")+"&c------------------"));
        Bukkit.getConsoleSender().sendMessage(format("&c------------------------------------"));
    }


/*
    private void checkPapi(){
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            usePapi = true;
            config.set("enable_papi",true);
        } else {
            getLogger().log(Level.INFO,"Could not find PlaceholderAPI! Is set false in config file");
            usePapi = false;
            config.set("enable_papi",false);
        }
    }*/

    @Override
    public void onDisable() {
        if (!BleedingEvent.getBleedingPlayers().isEmpty()){
            for (Player player : BleedingEvent.getBleedingPlayers().keySet()){
                if (player.isOnline())
                    ReviveEvent.notSave(player);
            }
        }
        if (!team.getPlayers().isEmpty()){
            team.getPlayers().forEach(Main::removePlayerTeam);
        }

    }


    public static String format(String text){return ChatColor.translateAlternateColorCodes('&',text);}


}

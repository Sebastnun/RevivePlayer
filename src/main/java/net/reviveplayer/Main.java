package net.reviveplayer;

import dev.sergiferry.playernpc.api.NPCLib;
import lombok.Getter;
import net.reviveplayer.listener.PlayerEvents;
import net.reviveplayer.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    @Getter
    public static boolean usePapi = false;
    private static int bleedingTime;


    @Override
    public void onEnable() {
        instance = this;
        setUpConfig();
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
        Util.loadMessages();
    }

    public void newReloadConfig(){
        this.reloadConfig();
        bleedingTime = this.getConfig().getInt("time");
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
        Bukkit.getConsoleSender().sendMessage(format("&c-------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(format("&c------&4 Revive Player is &l&eloaded&c-------"));
        Bukkit.getConsoleSender().sendMessage(format("&c-------------------------------------"));
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
        // Plugin shutdown logic
    }


    public static String format(String text){return ChatColor.translateAlternateColorCodes('&',text);}


    public static int getBleedingTime() {
        return bleedingTime;
    }
}

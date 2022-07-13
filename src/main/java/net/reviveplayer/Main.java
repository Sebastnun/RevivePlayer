package net.reviveplayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    private static Main instance;
    private final FileConfiguration config = this.getConfig();
    public static boolean usePapi = false;


    @Override
    public void onEnable() {
        instance = this;
        start();
    }

    private void start(){
        this.saveDefaultConfig();
        this.enableMenssage();
        checkPapi();
    }

    private void enableMenssage(){
        Bukkit.getConsoleSender().sendMessage(format("&c------------------------------------------"));
        Bukkit.getConsoleSender().sendMessage(format("&c--&4 Revive Player is &l&eloaded&c--"));
        Bukkit.getConsoleSender().sendMessage(format("&c-----------------------------------------"));
    }


    private void checkPapi(){
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            usePapi = true;
            config.set("enable_papi",true);
        } else {
            getLogger().log(Level.INFO,"Could not find PlaceholderAPI! Is set false in config file");
            usePapi = false;
            config.set("enable_papi",false);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static Main getInstance(){
        return instance;
    }
    public static String format(String text){return ChatColor.translateAlternateColorCodes('&',text);}




}

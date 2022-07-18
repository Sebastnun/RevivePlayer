package net.reviveplayer.util;

import net.reviveplayer.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class Util {

    private static final Map<Player, String> localeSettings = new HashMap<>();
    private static final Map<String, Map<String, String>> messages = new HashMap<>();
    public static List<String> files = new ArrayList<>();

    public static String getMessage(String messName) {
        String locale = Main.getInstance().getConfig().getString("lang");
        return messages.getOrDefault(locale, messages.get("en")).getOrDefault(messName, "Message " + messName + " not set!");
    }

    public static String getMessage(String locale,String messName) {
        return messages.getOrDefault(locale, messages.get("en")).getOrDefault(messName, "Message " + messName + " not set!");
    }

    public static String getLocale(Player p) {
        return localeSettings.get(p);
    }

    public static void setLocale(Player p, String string) {
        localeSettings.remove(p);
        if (!files.contains(string)) {
            p.sendMessage(Util.getMessage(Util.getLocale(p), "LocaleNoExist"));
        } else {
            localeSettings.put(p, string);
            p.sendMessage(Util.getMessage(Util.getLocale(p), "LocaleSet"));
            Main.getInstance().getConfig().set(p.getUniqueId().toString(), string);
            Main.getInstance().saveConfig();
        }
    }

    public static void removePlayer(Player p) {
        localeSettings.remove(p);
    }

    public static void loadMessages() {
        File langFolder = new File(Main.getInstance().getDataFolder() + "/locales");
        if (!langFolder.exists()) {
            boolean s = langFolder.mkdir();
        }
        File enFile = new File(langFolder, "en.yml");
        File esFile = new File(langFolder, "es.yml");
        try {
            if (!enFile.exists()) {
                InputStream in = Main.getInstance().getResource("en.yml");
                assert in != null;
                Files.copy(in, enFile.toPath());
            }
            if (!esFile.exists()) {
                InputStream in = Main.getInstance().getResource("es.yml");
                assert in != null;
                Files.copy(in, esFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (File file : Objects.requireNonNull(langFolder.listFiles())) {
            Map<String, String> localeMessages = new HashMap<>();

            FileConfiguration lang = YamlConfiguration.loadConfiguration(file);
            for (String key : lang.getKeys(false)) {
                for (String messName : Objects.requireNonNull(lang.getConfigurationSection(key)).getKeys(false )) {
                    String message = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(lang.getString(key + "." + messName)));
                    localeMessages.put(messName, message);
                }
            }
            String fileName = file.getName().split(".yml")[0];
            messages.put(fileName, localeMessages);
            files.add(fileName);
            Bukkit.getConsoleSender().sendMessage(Main.format("[RevivePlayer] &a"+file.getName() + " is loaded!"));
        }
    }
}

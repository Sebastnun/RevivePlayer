package net.reviveplayer;

import net.reviveplayer.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrincipalCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)){
            if (args[0].equals("reload")){
                Bukkit.getConsoleSender().sendMessage(Main.format(Util.getMessage("reload")));
                Main.getInstance().newReloadConfig();
            }else{
                Bukkit.getConsoleSender().sendMessage(Main.format(Util.getMessage("error_reload")));
            }
            return true;
        } else {
            if (player.hasPermission("reviveplayer.reload")){
                if (args[0].equals("reload")){
                    player.sendMessage(Main.format(Util.getMessage("reload")));
                    Main.getInstance().newReloadConfig();
                }else{
                    player.sendMessage(Main.format(Util.getMessage("error_reload")));
                }
            }
            return true;
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1){
            List<String> reload = new ArrayList<>();
            reload.add("reload");
            return reload;
        }
        return null;
    }
}

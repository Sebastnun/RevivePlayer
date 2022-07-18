package net.reviveplayer;

import net.reviveplayer.events.BleedingEvent;
import net.reviveplayer.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Command implements CommandExecutor {

    public Command(Main main){
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)){
            Bukkit.getConsoleSender().sendMessage(Main.format("&cYou arent player"));
            return false;
        }else {
            if (!BleedingEvent.bleedingPlayers.containsKey(player)){
                player.sendMessage(Main.format(Util.getMessage("error_you_arent_bleeding")));
            }else {
                player.sendMessage(Main.format(Util.getMessage("accept_destiny")));
                player.setHealth(0);
            }
            return true;
        }
    }
}

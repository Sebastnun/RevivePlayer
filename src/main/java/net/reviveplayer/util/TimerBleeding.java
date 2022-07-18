package net.reviveplayer.util;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.reviveplayer.Main;
import net.reviveplayer.events.BleedingEvent;
import net.reviveplayer.events.ReviveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TimerBleeding {
    private static final Main main = Main.getInstance();
    private static int time;
    @Getter @Setter
    private static int timeRes;
    @Getter
    private final Player player;
    BukkitRunnable runnable;

    public TimerBleeding(Player player){
        this.player = player;
        if (!BleedingEvent.npcPlayers.containsKey(player))return;
        time = 0;
        timeRes = Main.getBleedingTime();
        runnable = new BukkitRunnable() {

            @Override
            public void run() {
                if (time <=Main.getBleedingTime()){
                    BaseComponent[] component =
                            new ComponentBuilder(Main.format(Util.getMessage("time_left").replace("%timer%",getTime(timeRes)))).create();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);

                    time++;
                    timeRes--;
                }else {
                    player.setHealth(0);
                }
            }
        };

        runnable.runTaskTimer(main,0,20);
    }



    public void stopCountdown(){
        runnable.cancel();
    }

    public static String getTime(int duration) {
        String string = "";
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        if (duration / 60 / 60 / 24 >= 1) {
            duration -= duration / 60 / 60 / 24 * 60 * 60 * 24;
        }
        if (duration / 60 / 60 >= 1) {
            hours = duration / 60 / 60;
            duration -= duration / 60 / 60 * 60 * 60;
        }
        if (duration / 60 >= 1) {
            minutes = duration / 60;
            duration -= duration / 60 * 60;
        }
        if (duration >= 1)
            seconds = duration;
        if (hours <= 9) {
            string = string + "0" + hours + ":";
        } else {
            string = string + hours + ":";
        }
        if (minutes <= 9) {
            string = string + "0" + minutes + ":";
        } else {
            string = string + minutes + ":";
        }
        if (seconds <= 9) {
            string = string + "0" + seconds;
        } else {
            string = string + seconds;
        }
        return string;
    }



}

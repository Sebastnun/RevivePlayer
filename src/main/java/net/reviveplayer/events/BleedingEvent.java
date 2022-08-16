package net.reviveplayer.events;

import lombok.Getter;
import net.minecraft.world.scores.ScoreboardTeam;
import net.reviveplayer.Main;
import net.reviveplayer.util.TimerBleeding;
import net.reviveplayer.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class BleedingEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Player player;
    private boolean cancelled;
    @Getter
    private static final HashMap<Player,TimerBleeding> bleedingPlayers = new HashMap<>();

    public BleedingEvent(Player p){
        this.player = p;
        Objects.requireNonNull(player.getPlayer()).setHealth(Main.getInstance().getConfig().getInt("bleeding_health_level"));
        player.getPlayer().setFoodLevel(Main.getInstance().getConfig().getInt("bleeding_food_level"));
        player.playSound(player, Sound.ENTITY_PLAYER_HURT,10,0);
        player.setGliding(true);
        player.setFreezeTicks(0);
        player.setGlowing(true);
        player.setInvulnerable(true);

        Collection<PotionEffect> effects = new ArrayList<>();
        effects.add(new PotionEffect(PotionEffectType.SLOW,Main.getBleedingTime()*20,3, false,false,false));
        effects.add(new PotionEffect(PotionEffectType.WEAKNESS,Main.getBleedingTime()*20,3, false,false,false));
        effects.add(new PotionEffect(PotionEffectType.SLOW_DIGGING,Main.getBleedingTime()*20,3, false,false,false));

        player.addPotionEffects(effects);
        Main.addPlayerTeam(player);

        String chatMe = Util.getMessage("player_chat_menssage").replace("%player_bleeding%", player.getName());

        for (Player player1 : Bukkit.getOnlinePlayers()){
            player1.sendMessage(Main.format(chatMe));
        }
        TimerBleeding timer = new TimerBleeding(player);
        bleedingPlayers.put(player, timer);
        player.sendMessage(Main.format(Util.getMessage("help_message")));
    }

    @Override
    public boolean isCancelled(){
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){return handlers;}







}

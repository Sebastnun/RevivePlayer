package net.reviveplayer.events;

import lombok.Getter;
import net.reviveplayer.Main;
import net.reviveplayer.listener.PlayerEvents;
import net.reviveplayer.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ReviveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Player playerBleeding;
    @Getter
    private final Player playerSavior;
    private boolean cancelled;


    public ReviveEvent(Player playerBleeding, Player playerSave){
        this.playerBleeding = playerBleeding;
        this.playerSavior = playerSave;
        ReviveEvent.notSave(playerBleeding);
        playerSavior.sendMessage(Main.format(Util.getMessage("player_savior").replace("%player_bleeding%",playerBleeding.getDisplayName())));
        for (Player p : Bukkit.getOnlinePlayers()){
            if (p != playerSavior){
                p.sendMessage(Main.format(Util.getMessage("player_savior")
                        .replace("%player_bleeding%",playerBleeding.getDisplayName())
                        .replace("%player_savior%",playerSavior.getDisplayName())));
            }
        }
    }

    public static void notSave(Player player){
        if (!BleedingEvent.getBleedingPlayers().containsKey(player))return;
        BleedingEvent.getBleedingPlayers().get(player).stopCountdown();
        BleedingEvent.getBleedingPlayers().remove(player);
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        Main.removePlayerTeam(player);
        player.setGlowing(false);
        player.setInvulnerable(false);
        player.setFreezeTicks(0);
        player.setGliding(false);
        player.setGameMode(GameMode.SURVIVAL);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){return handlers;}

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}

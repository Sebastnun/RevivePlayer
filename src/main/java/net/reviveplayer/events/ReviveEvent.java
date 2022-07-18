package net.reviveplayer.events;

import lombok.Getter;
import net.reviveplayer.Main;
import net.reviveplayer.util.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
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
        BleedingEvent.npcPlayers.get(playerBleeding).destroy();
        this.playerBleeding.setGameMode(GameMode.SURVIVAL);
        this.playerBleeding.setFreezeTicks(0);
        this.playerBleeding.teleport(BleedingEvent.npcPlayers.get(playerBleeding).getLocation());
        BleedingEvent.bleedingPlayers.get(playerBleeding).stopCountdown();
        BleedingEvent.bleedingPlayers.remove(playerBleeding);
        BleedingEvent.npcPlayers.remove(playerBleeding);

        playerSavior.sendMessage(Main.format(Util.getMessage("player_savior").replace("%player_bleeding%",playerBleeding.getDisplayName())));

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

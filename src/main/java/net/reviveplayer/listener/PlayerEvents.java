package net.reviveplayer.listener;

import dev.sergiferry.playernpc.api.NPC;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.server.level.EntityPlayer;
import net.reviveplayer.Main;
import net.reviveplayer.events.BleedingEvent;
import net.reviveplayer.events.ReviveEvent;
import net.reviveplayer.util.BleedingEntity;
import net.reviveplayer.util.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class PlayerEvents implements Listener {

    private final Main main = Main.getInstance();

    @EventHandler
    private void onJoin(PlayerJoinEvent e){
        for (Player p : BleedingEvent.npcPlayers.keySet()){
            BleedingEvent.npcPlayers.get(p).addPlayer(e.getPlayer());
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(EntityDamageEvent e){
        if (!(e.getEntity() instanceof Player player))return;
        if (e.isCancelled())return;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID) && main.getConfig().getBoolean("kill_enable"))return;
        if (((player.getHealth() - e.getFinalDamage()) <= 0))
        {
            e.setCancelled(true);
            Bukkit.getPluginManager().callEvent(new BleedingEvent(player));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void moveEvent(PlayerMoveEvent e){
        Player player = e.getPlayer();
        if (!BleedingEvent.bleedingPlayers.containsKey(player))return;
        /*Location locNPC = BleedingEvent.npcPlayers.get(player).getLocation();
        Location loc = new Location(locNPC.getWorld(),locNPC.getX(),locNPC.getY(),locNPC.getZ(),
                player.getLocation().getYaw(),player.getLocation().getPitch());
        player.teleport(loc);*/
        e.setCancelled(true);
    }


    @EventHandler
    private void bleedingDeath(PlayerDeathEvent e){
        Player player = e.getEntity();
        if (!BleedingEvent.bleedingPlayers.containsKey(player))return;
        if(Objects.requireNonNull(player.getLocation().getWorld()).isGameRule("keepInventory") && !main.getConfig().getBoolean("keep_inventory")){
            e.setKeepInventory(false);
            for (ItemStack item : player.getInventory()){
                if (item==null)continue;
                player.getLocation().getWorld().dropItem(player.getLocation(),item);
            }
            e.setKeepLevel(false);
            //if (e.getDroppedExp()>0)
            (player.getWorld().spawn(player.getLocation(),ExperienceOrb.class)).setExperience(e.getDroppedExp());
        }

        e.setDeathMessage(Main.format(Util.getMessage("player_dead").replace("%player_bleeding%",player.getName())));
        notSave(player);
    }



    @EventHandler(priority = EventPriority.MONITOR)
    private void onInteractEvent(NPC.Events.Interact e){
        Player playerBody = e.getNPC().getPlayer();
        e.getNPC().hide();
        Bukkit.getPluginManager().callEvent(new ReviveEvent(playerBody,e.getPlayer()));
    }


    @EventHandler(priority = EventPriority.MONITOR)
    private void onExit(PlayerQuitEvent e){
        if (!BleedingEvent.bleedingPlayers.containsKey(e.getPlayer()))return;
        e.getPlayer().setHealth(0);
        notSave(e.getPlayer());
    }

    private void notSave(Player player){
        if (!BleedingEvent.bleedingPlayers.containsKey(player))return;
        BleedingEvent.bleedingPlayers.get(player).stopCountdown();
        BleedingEvent.npcPlayers.get(player).destroy();
        BleedingEvent.bleedingPlayers.remove(player);
        BleedingEvent.npcPlayers.remove(player);
        player.setFreezeTicks(0);
        player.setGameMode(GameMode.SURVIVAL);
    }
}

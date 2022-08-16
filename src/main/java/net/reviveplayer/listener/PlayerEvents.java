package net.reviveplayer.listener;

import net.minecraft.world.level.ExplosionDamageCalculatorEntity;
import net.reviveplayer.Main;
import net.reviveplayer.events.BleedingEvent;
import net.reviveplayer.events.ReviveEvent;
import net.reviveplayer.util.Util;
import org.bukkit.*;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class PlayerEvents implements Listener {

    private final Main main = Main.getInstance();


    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(EntityDamageEvent e){
        if (!(e.getEntity() instanceof Player player))return;
        if (e.isCancelled())return;
        if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID) && main.getConfig().getBoolean("kill_enable"))return;
        if (BleedingEvent.getBleedingPlayers().containsKey(player)){
            e.setCancelled(true);
        }
        if (e.isCancelled())return;
        if (((player.getHealth() - e.getFinalDamage()) <= 0) && !BleedingEvent.getBleedingPlayers().containsKey(player)) {
            if (player.getInventory().getItemInMainHand().equals(new ItemStack(Material.TOTEM_OF_UNDYING)) ||
                    player.getInventory().getItemInOffHand().equals(new ItemStack(Material.TOTEM_OF_UNDYING)))
                return;
            e.setCancelled(true);
            Bukkit.getPluginManager().callEvent(new BleedingEvent(player));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void targetEvent(EntityTargetEvent e){
        if (e.getTarget() instanceof Player player){
            if (BleedingEvent.getBleedingPlayers().containsKey(player)){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void interactFire(PlayerInteractEvent e){
        if (!BleedingEvent.getBleedingPlayers().containsKey(e.getPlayer()))return;
        if (Objects.equals(e.getItem(), new ItemStack(Material.FIREWORK_ROCKET)))
            e.setCancelled(true);
    }





    @EventHandler
    private void bleedingDeath(PlayerDeathEvent e){
        Player player = e.getEntity();
        if (!BleedingEvent.getBleedingPlayers().containsKey(player))return;
        notSave(player);
        e.setDeathMessage(Main.format(Util.getMessage("player_dead").replace("%player_bleeding%",player.getName())));
    }



    @EventHandler(priority = EventPriority.MONITOR)
    private void onInteractEvent(PlayerInteractEntityEvent e){
        if (!(e.getRightClicked() instanceof Player playerBody))return;
        if (!BleedingEvent.getBleedingPlayers().containsKey(playerBody))return;
        for (Player p :BleedingEvent.getBleedingPlayers().keySet())
            if (e.getPlayer().equals(p))
                return;
        Bukkit.getPluginManager().callEvent(new ReviveEvent(playerBody,e.getPlayer()));
    }


    @EventHandler(priority = EventPriority.MONITOR)
    private void onExit(PlayerQuitEvent e){
        if (!BleedingEvent.getBleedingPlayers().containsKey(e.getPlayer()))return;
        e.getPlayer().setHealth(0);
        ReviveEvent.notSave(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void swimming(EntityToggleGlideEvent e){
        if (!(e.getEntity() instanceof Player))return;
        if (!BleedingEvent.getBleedingPlayers().containsKey(((Player) e.getEntity()).getPlayer()))return;
        e.setCancelled(true);
    }

    private static void notSave(Player player){
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
}

package net.reviveplayer.events;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import dev.sergiferry.playernpc.nms.minecraft.NMSEntityPlayer;
import dev.sergiferry.spigot.nms.craftbukkit.NMSCraftPlayer;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerInteractManager;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.reviveplayer.Main;
import net.reviveplayer.util.BleedingEntity;
import net.reviveplayer.util.TimerBleeding;
import net.reviveplayer.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class BleedingEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Player player;
    private boolean cancelled;
    public static HashMap<Player,TimerBleeding> bleedingPlayers = new HashMap<>();
    public static HashMap<Player,NPC.Global> npcPlayers = new HashMap<>();

    public BleedingEvent(Player p){
        this.player = p;
        NPC.Global body = BleedingEntity.spawn(player);
        Objects.requireNonNull(player.getPlayer()).setHealth(6);
        player.getPlayer().setFoodLevel(6);
        player.playSound(player, Sound.ENTITY_PLAYER_HURT,10,0);
        player.setGameMode(GameMode.SPECTATOR);
        player.setFreezeTicks((Main.getBleedingTime() * 20)*2);

        Location locP = player.getLocation();
        Location loc = new Location(locP.getWorld(),locP.getX(),locP.getY(),locP.getZ(),-90,-90);
        player.teleport(loc);
        String chatMe = Util.getMessage("player_chat_menssage").replace("%player_bleeding%", player.getName());

        for (Player player1 : Bukkit.getOnlinePlayers()){
            player1.sendMessage(Main.format(chatMe));
        }
        npcPlayers.put(player,body);
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



    private static String[] getSkinPlayerName(String name) {
        org.bukkit.entity.Player player = Bukkit.getServer().getPlayer(name);
        if (Bukkit.getServer().getOnlineMode() && player != null) {
            return getSkinGameProfile(player);
        } else {
            try {
                return getSkinMojangServer(getUUID(name));
            } catch (Exception var3) {
                return NPC.Skin.getSteveSkin().getData();
            }
        }
    }

    private static String[] getSkinGameProfile(org.bukkit.entity.Player player) {
        try {
            EntityPlayer p = NMSCraftPlayer.getEntityPlayer(player);
            GameProfile profile = NMSEntityPlayer.getGameProfile(p);
            Property property = profile.getProperties().get("textures").iterator().next();
            String texture = property.getValue();
            String signature = property.getSignature();
            return new String[]{texture, signature};
        } catch (Exception var6) {
            return NPC.Skin.getSteveSkin().getData();
        }
    }

    private static String[] getSkinMojangServer(String uuid) throws IOException {
        URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
        InputStreamReader reader2 = new InputStreamReader(url2.openStream());
        JsonObject property = (new JsonParser()).parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        String texture = property.get("value").getAsString();
        String signature = property.get("signature").getAsString();
        return new String[]{texture, signature};
    }

    private static String getUUID(String name) throws IOException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        InputStreamReader reader = new InputStreamReader(url.openStream());
        return (new JsonParser()).parse(reader).getAsJsonObject().get("id").getAsString();
    }





}

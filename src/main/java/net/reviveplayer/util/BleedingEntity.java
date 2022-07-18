package net.reviveplayer.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import dev.sergiferry.playernpc.nms.minecraft.NMSEntityPlayer;
import dev.sergiferry.spigot.nms.craftbukkit.NMSCraftPlayer;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import net.reviveplayer.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class BleedingEntity {



    public static NPC.Global spawn(Player player){
        EntityPlayer craftPlayer = ((CraftPlayer)player).getHandle();

        /*NPC.Personal body = NPCLib.getInstance().generatePersonalNPC(player, Main.getInstance(),
                player.getDisplayName()+"_bleeding", player.getLocation());*/
        NPC.Global body = NPCLib.getInstance().generateGlobalNPC(Main.getInstance(),player.getDisplayName()+"_bleeding",
                player.getLocation());
        body.setPose(NPC.Pose.SLEEPING);
        body.setSkin(new NPC.Skin(getSkinPlayerName(player.getName())));
        body.setShowOnTabList(false);
        body.setGlowing(true,NPC.Color.RED);
        body.setText(Main.format("Bleeding "+player.getName()));
        //body.create(player);
        for (Player p : Bukkit.getOnlinePlayers()) body.addPlayer(p);
        body.teleport(player);
        body.show();
        return body;
    }


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

    /*
    public static void spawn(Player player){
        EntityPlayer craftPlayer = ((CraftPlayer)player).getHandle();

        Property textures = (Property)craftPlayer.getBukkitEntity().getProfile().getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(),player.getName());
        gameProfile.getProperties().put("textures",new Property("textures", textures.getValue(), textures.getSignature()));


        EntityPlayer entity = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld)player.getWorld()).getHandle(),
                gameProfile,
                null
        );

        entity.teleportTo(((CraftWorld)player.getWorld()).getHandle(),player.getLocation().getX(),
                player.getLocation().getY(),player.getLocation().getZ(),player.getLocation().getPitch(),
                player.getLocation().getYaw(),PlayerTeleportEvent.TeleportCause.PLUGIN);

        Location bed = player.getLocation().add(1,0,0);
        entity.e(new BlockPosition(bed.getX(),bed.getY(),bed.getZ()));


        ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard)Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(),
                player.getName());
        team.b(ScoreboardTeamBase.EnumNameTagVisibility.b);
        PacketPlayOutScoreboardTeam score1 = PacketPlayOutScoreboardTeam.a(team);
        PacketPlayOutScoreboardTeam score2 = PacketPlayOutScoreboardTeam.a(team,true);
        PacketPlayOutScoreboardTeam score3 = PacketPlayOutScoreboardTeam.a(team,entity.getBukkitEntity().getName(),
                PacketPlayOutScoreboardTeam.a.a);

        entity.set
    }*/



}

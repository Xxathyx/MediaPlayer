package fr.xxathyx.mediaplayer.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.sound.SoundPlayer;
import io.netty.buffer.Unpooled;

/** 
* The AudioUtil class serves as an utility class in order to perform audio actions to {@link Player}.
* For the moment it only contains a static method, see {@link #stopAudio(Player)}, more methods will be added
* further.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class AudioUtil {
	
	/** 
	* Stops a sound effect that is actually played to a player, packets will be used for the following versions:
	* v1_8_R1, v1_8_R2, v1_8_R3, v1_9_R1, v1_9_R2, otherwise for further versions the Minecraft stopsound command
	* will be called from Console.
	* 
	* <p>This is the same as calling {@link SoundPlayer#stopSound(Player)}.
	* 
	* @param player The player hearing a sound.
	*/
	
    public static void stopAudio(Player player, String sound) {
    	
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        
        if(serverVersion.equals("v1_9_R2")) {
        	
        	Bukkit.broadcastMessage("it equals");
        	
        	net.minecraft.server.v1_9_R2.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer)player).getHandle();
        	net.minecraft.server.v1_9_R2.PacketDataSerializer packet = new net.minecraft.server.v1_9_R2.PacketDataSerializer(Unpooled.buffer()).a("");
        	net.minecraft.server.v1_9_R2.PacketPlayOutCustomPayload packetPlayOutCustomPayload = new net.minecraft.server.v1_9_R2.PacketPlayOutCustomPayload("MC|StopSound", packet);
        	entityPlayer.playerConnection.sendPacket(packetPlayOutCustomPayload);
        	return;
        }
        if(serverVersion.equals("v1_9_R1")) {
        	net.minecraft.server.v1_9_R1.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer)player).getHandle();
        	net.minecraft.server.v1_9_R1.PacketDataSerializer packet = new net.minecraft.server.v1_9_R1.PacketDataSerializer(Unpooled.buffer()).a("");
        	net.minecraft.server.v1_9_R1.PacketPlayOutCustomPayload packetPlayOutCustomPayload = new net.minecraft.server.v1_9_R1.PacketPlayOutCustomPayload("MC|StopSound", packet);
        	entityPlayer.playerConnection.sendPacket(packetPlayOutCustomPayload);
        	return;
        }
        if(serverVersion.equals("v1_8_R3")) {
        	net.minecraft.server.v1_8_R2.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer)player).getHandle();
        	net.minecraft.server.v1_8_R2.PacketDataSerializer packet = new net.minecraft.server.v1_8_R2.PacketDataSerializer(Unpooled.buffer()).a("");
        	net.minecraft.server.v1_8_R2.PacketPlayOutCustomPayload packetPlayOutCustomPayload = new net.minecraft.server.v1_8_R2.PacketPlayOutCustomPayload("MC|StopSound", packet);
        	entityPlayer.playerConnection.sendPacket(packetPlayOutCustomPayload);
        	return;
        }
        if(serverVersion.equals("v1_8_R2")) {
        	net.minecraft.server.v1_8_R2.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer)player).getHandle();
        	net.minecraft.server.v1_8_R2.PacketDataSerializer packet = new net.minecraft.server.v1_8_R2.PacketDataSerializer(Unpooled.buffer()).a("");
        	net.minecraft.server.v1_8_R2.PacketPlayOutCustomPayload packetPlayOutCustomPayload = new net.minecraft.server.v1_8_R2.PacketPlayOutCustomPayload("MC|StopSound", packet);
        	entityPlayer.playerConnection.sendPacket(packetPlayOutCustomPayload);
        	return;
        }
        if(serverVersion.equals("v1_7_R4")) {
        	net.minecraft.server.v1_7_R4.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer)player).getHandle();
        	net.minecraft.server.v1_7_R4.PacketPlayOutCustomPayload packetPlayOutCustomPayload = new net.minecraft.server.v1_7_R4.PacketPlayOutCustomPayload("MC|StopSound", Unpooled.buffer(0).array());
        	entityPlayer.playerConnection.sendPacket(packetPlayOutCustomPayload);
        	return;
        }
        if(serverVersion.equals("v1_7_R3")) {
        	net.minecraft.server.v1_7_R3.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer)player).getHandle();
        	net.minecraft.server.v1_7_R3.PacketPlayOutCustomPayload packetPlayOutCustomPayload = new net.minecraft.server.v1_7_R3.PacketPlayOutCustomPayload("MC|StopSound", Unpooled.buffer(0).array());
        	entityPlayer.playerConnection.sendPacket(packetPlayOutCustomPayload);
        	return;
        }
        if(serverVersion.equals("v1_7_R2")) {
        	net.minecraft.server.v1_7_R2.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer)player).getHandle();
        	net.minecraft.server.v1_7_R2.PacketPlayOutCustomPayload packetPlayOutCustomPayload = new net.minecraft.server.v1_7_R2.PacketPlayOutCustomPayload("MC|StopSound", Unpooled.buffer(0).array());
        	entityPlayer.playerConnection.sendPacket(packetPlayOutCustomPayload);
        	return;
        }
        if(serverVersion.equals("v1_7_R1")) {
        	net.minecraft.server.v1_7_R1.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer)player).getHandle();
        	net.minecraft.server.v1_7_R1.PacketPlayOutCustomPayload packetPlayOutCustomPayload = new net.minecraft.server.v1_7_R1.PacketPlayOutCustomPayload("MC|StopSound", Unpooled.buffer(0).array());
        	entityPlayer.playerConnection.sendPacket(packetPlayOutCustomPayload);
        	return;
        }
        
        player.stopSound("");
        
    	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound " + player.getName());
    }
}
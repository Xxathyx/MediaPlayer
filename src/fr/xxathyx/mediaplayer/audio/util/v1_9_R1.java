package fr.xxathyx.mediaplayer.audio.util;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.util.AudioUtil;
import io.netty.buffer.Unpooled;

public class v1_9_R1 implements AudioUtil {

	@Override
	public void stopAudio(Player player, String sound) {
    	net.minecraft.server.v1_9_R1.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer)player).getHandle();
    	net.minecraft.server.v1_9_R1.PacketDataSerializer packet = new net.minecraft.server.v1_9_R1.PacketDataSerializer(Unpooled.buffer()).a("");
    	net.minecraft.server.v1_9_R1.PacketPlayOutCustomPayload packetPlayOutCustomPayload = new net.minecraft.server.v1_9_R1.PacketPlayOutCustomPayload("MC|StopSound", packet);
    	entityPlayer.playerConnection.sendPacket(packetPlayOutCustomPayload);
	}
}
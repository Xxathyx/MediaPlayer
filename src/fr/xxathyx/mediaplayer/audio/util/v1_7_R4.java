package fr.xxathyx.mediaplayer.audio.util;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.util.AudioUtil;
import io.netty.buffer.Unpooled;

public class v1_7_R4 implements AudioUtil {

	@Override
	public void stopAudio(Player player, String sound) {
    	net.minecraft.server.v1_7_R4.EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer)player).getHandle();
    	net.minecraft.server.v1_7_R4.PacketPlayOutCustomPayload packetPlayOutCustomPayload = new net.minecraft.server.v1_7_R4.PacketPlayOutCustomPayload("MC|StopSound", Unpooled.buffer(0).array());
    	entityPlayer.playerConnection.sendPacket(packetPlayOutCustomPayload);
	}
}
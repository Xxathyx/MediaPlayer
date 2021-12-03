package fr.xxathyx.mediaplayer.actionbar;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.util.ActionBar;

/**
 * The v1_8_R1 class implements {@link ActionBar}, it can only be defined once if
 * the server is running under this version, see {@link ActionBarVersion#getActionBar()}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2021-08-23 
 */

public class v1_8_R1 implements ActionBar {
	
	@Override
	public void send(Player player, String text) {
        ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer)player).getHandle().playerConnection.sendPacket(new net.minecraft.server.v1_8_R1.PacketPlayOutChat(
        		net.minecraft.server.v1_8_R1.ChatSerializer.a("{\"text\": \"" + text + "\"}"), (byte) 2));
	}
}
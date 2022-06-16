package fr.xxathyx.mediaplayer.actionbar;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.util.ActionBar;

/**
 * The v1_19_R1 class implements {@link ActionBar}, it can only be defined once if
 * the server is running under this version, see {@link ActionBarVersion#getActionBar()}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2021-12-03 
 */

public class v1_19_R1 implements ActionBar {
	
	@Override
    public void send(Player player, String text) {
		((org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer)player).getHandle().b.a(new net.minecraft.network.protocol.game.PacketPlayOutChat(
				net.minecraft.network.chat.IChatBaseComponent.ChatSerializer.a( "{\"text\": \"" + text + "\"}"), net.minecraft.network.chat.ChatMessageType.c, player.getUniqueId()));
	}
}
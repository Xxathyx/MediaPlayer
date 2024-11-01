package fr.xxathyx.mediaplayer.actionbar;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.util.ActionBar;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * The v1_18_R1 class implements {@link ActionBar}, it can only be defined once if
 * the server is running under this version, see {@link ActionBarVersion#getActionBar()}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2021-12-03 
 */

public class v1_18_R1 implements ActionBar {
	
	private boolean isPaper = false;
	
	public v1_18_R1() {
		try {
	        try {Class.forName("com.destroystokyo.paper.ParticleBuilder"); isPaper = true;}catch (ClassNotFoundException ignored) {}
		}catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public void send(Player player, String text) {
		if(isPaper) {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
		}else {
			((org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer)player).getHandle().b.a(new net.minecraft.network.protocol.game.PacketPlayOutChat(
					net.minecraft.network.chat.IChatBaseComponent.ChatSerializer.a( "{\"text\": \"" + text + "\"}"), net.minecraft.network.chat.ChatMessageType.c, player.getUniqueId()));
		}
	}
}
package fr.xxathyx.mediaplayer.actionbar;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.util.ActionBar;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * The v1_19_R3 class implements {@link ActionBar}, it can only be defined once if
 * the server is running under this version, see {@link ActionBarVersion#getActionBar()}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2023-06-14
 */

public class v1_19_R3 implements ActionBar {
	
	private boolean isPaper = false;
	
	public v1_19_R3() {
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
			((org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer)player).getHandle().b.a(
					new net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket(
							net.minecraft.network.chat.IChatBaseComponent.ChatSerializer.a( "{\"text\": \"" + text + "\"}")));
		}
	}
}
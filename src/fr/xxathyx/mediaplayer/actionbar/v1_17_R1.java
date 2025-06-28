package fr.xxathyx.mediaplayer.actionbar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.util.ActionBar;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * The v1_17_R1 class implements {@link ActionBar}, it can only be defined once if
 * the server is running under this version, see {@link ActionBarVersion#getActionBar()}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2021-08-23 
 */

public class v1_17_R1 implements ActionBar {
	
	private final Method method = net.minecraft.server.network.PlayerConnection.class.getMethods()[60];
	
	private boolean isPaper = false;
	
	public v1_17_R1() {
		method.setAccessible(true);
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
			try {
				method.invoke(((org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer)player).getHandle().b,
						new net.minecraft.network.protocol.game.PacketPlayOutChat(net.minecraft.network.chat.IChatBaseComponent.ChatSerializer.a( "{\"text\": \"" + text + "\"}"),
								net.minecraft.network.chat.ChatMessageType.c, player.getUniqueId()));
			}catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
}
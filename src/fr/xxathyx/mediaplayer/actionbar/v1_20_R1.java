package fr.xxathyx.mediaplayer.actionbar;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.util.ActionBar;
import net.minecraft.server.network.PlayerConnection;

/**
 * The v1_20_R1 class implements {@link ActionBar}, it can only be defined once if
 * the server is running under this version, see {@link ActionBarVersion#getActionBar()}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2023-06-15
 */

public class v1_20_R1 implements ActionBar {
		
	@Override
    public void send(Player player, String text) {		
		try {
			((PlayerConnection) ((org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer)player).getHandle().getClass().getFields()[0].get(
					((org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer)player).getHandle())).a(new net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket(
					net.minecraft.network.chat.IChatBaseComponent.ChatSerializer.a( "{\"text\": \"" + text + "\"}")));
		}catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
package fr.xxathyx.mediaplayer.actionbar;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.util.ActionBar;

/**
 * The v1_19_R3 class implements {@link ActionBar}, it can only be defined once if
 * the server is running under this version, see {@link ActionBarVersion#getActionBar()}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2021-06-14
 */

public class v1_19_R3 implements ActionBar {
	
	@Override
    public void send(Player player, String text) {
		((org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer)player).getHandle().b.a(
				new net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket(
						net.minecraft.network.chat.IChatBaseComponent.ChatSerializer.a( "{\"text\": \"" + text + "\"}")));
	}
}
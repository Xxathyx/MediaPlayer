package fr.xxathyx.mediaplayer.actionbar;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.util.ActionBar;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * The v1_21_R6 class implements {@link ActionBar}, it can only be defined once if
 * the server is running under this version, see {@link ActionBarVersion#getActionBar()}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2025-10-03
 */

public class v1_21_R6 implements ActionBar {
		
	@Override
    public void send(Player player, String text) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
	}
}
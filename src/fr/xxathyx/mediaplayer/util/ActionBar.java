package fr.xxathyx.mediaplayer.util;

import org.bukkit.entity.Player;

/**
 * Interface to a ActionBar, see {@link ActionBarVersion} and {@link fr.xxathyx.mediaplayer.actionbar}.
 * Contains only one method {@link #send(Player, String)}.
 */

public interface ActionBar {
	
    /**
     * Send a message through the player Action Bar.
     * 
     * <p>This is the same as calling {@link org.bukkit.entity.Player.Spigot#sendMessage(net.md_5.bungee.api.chat.BaseComponent, net.md_5.bungee.api.chat.TextComponent)}
     * on the servers that are running under spigot with the latest Minecraft versions.
     *
     * @param player The targeted player of the message.
     * @param text The message that will be sent to the targeted player.
     */
	
    public abstract void send(Player player, String text);
}
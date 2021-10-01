package fr.xxathyx.mediaplayer.notification;

import java.util.UUID;

import org.bukkit.Bukkit;

import fr.xxathyx.mediaplayer.group.Group;
import fr.xxathyx.mediaplayer.sound.SoundPlayer;
import fr.xxathyx.mediaplayer.sound.SoundType;

/** 
* The Notification class is about a notification system, have one constructor and a single
* method {@link #send(Group, String[])}, can be used across the plugin to send notifications.
* Rests on a {@link Group} messaging system.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class Notification {
		
	private NotificationType notificationType;
	private boolean ping;
	
	/**
	* Constructor for Notification class, creates an Notification variable according
	* to a {@link NotificationType}.
	* 
	* <p>The ping sound can be disabled with the boolean ping parameter.
	* 
	* @param notificationType The notification message type.
	* @param ping Send a ping sound effect or not.
	*/
	
	public Notification(NotificationType notificationType, boolean ping) {
		this.notificationType = notificationType;
		this.ping = ping;
	}
	
	/**
	* Sends a message to a targeted {@link Group} according to a {@link NotificationType} passed
	* earlier in the constructor.
	* 
	* @param group The targeted group to the message.
	* @param args The arguments that can replace values in a {@link NotificationType#toString(String[])}.
	*/
	
	public void send(Group group, String args[]) {
		for(UUID uuid : group.getPlayers()) {
			Bukkit.getPlayer(uuid).sendMessage(notificationType.toString(args));
			if(ping) {
				SoundPlayer.playSound(Bukkit.getPlayer(uuid), SoundType.NOTIFICATION_UP);
			}
		}
	}
}
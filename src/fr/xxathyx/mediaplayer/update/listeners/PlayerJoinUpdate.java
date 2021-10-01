package fr.xxathyx.mediaplayer.update.listeners;

import java.io.IOException;
import java.net.UnknownHostException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.group.Group;
import fr.xxathyx.mediaplayer.notification.Notification;
import fr.xxathyx.mediaplayer.notification.NotificationType;
import fr.xxathyx.mediaplayer.update.Updater;

/** 
* The PlayerJoinUpdate class implements {@link Listener}, it consist
* of a single event method {@link #onJoin(PlayerJoinEvent)}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class PlayerJoinUpdate implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private final Configuration configuration = new Configuration();
	private final Updater updater = new Updater();
	
    /**
     * Called whenever a {@link Player} having mediaplayer.permission.admin permission joins
     * the server, it will alert him using the {@link Notification} system three seconds after
     * the connection if the plugin is outdated.
     * 
     * @param Event Instance of {@link PlayerJoinEvent}.
     */
	
	public void onJoin(PlayerJoinEvent event) throws UnknownHostException, IOException {
		
		if(event.getPlayer().hasPermission("mediaplayer.permission.admin")) {		
			if(updater.isOutdated()) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
					event.getPlayer().sendMessage(configuration.plugin_outdated());
					new Notification(NotificationType.PLUGIN_OUTDATED, true).send(new Group("mediaplayer.permission.admin"), new String[] { "" });     
				}, 60L);
			}
		}
	}
}
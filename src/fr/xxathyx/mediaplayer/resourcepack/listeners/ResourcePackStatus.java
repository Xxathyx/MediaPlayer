package fr.xxathyx.mediaplayer.resourcepack.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import fr.xxathyx.mediaplayer.Main;

/** 
* The ResourcePackStatus class implements {@link Listener}, it consist
* of a single event method {@link #onResourcepackStatusEvent(PlayerResourcePackStatusEvent)}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class ResourcePackStatus implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
    /**
     * Called whenever a {@link Player} update is ressource-pack application.
     * see Bukkit documentation : {@link PlayerResourcePackStatusEvent}. This
     * is used to know if a play successully downloaded a ressource-pack.
     *
     * @param event Instance of {@link PlayerResourcePackStatusEvent}.
     */
	
    @EventHandler
    public void onResourcepackStatusEvent(PlayerResourcePackStatusEvent event) {
    	if(event.getStatus().equals(Status.SUCCESSFULLY_LOADED)) plugin.getPlayersScreens().put(event.getPlayer().getUniqueId(), null);
    }
}
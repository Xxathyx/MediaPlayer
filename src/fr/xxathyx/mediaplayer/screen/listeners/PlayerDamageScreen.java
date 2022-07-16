package fr.xxathyx.mediaplayer.screen.listeners;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;

import fr.xxathyx.mediaplayer.Main;

/** 
* The PlayerDamageScreen class implements {@link Listener}, it consist
* of a single event method {@link #onDamage(HangingBreakEvent)}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class PlayerDamageScreen implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
    /**
     * Called whenever a {@link Player} break a {@link ItemFrame}. See Bukkit
     * documentation : {@link HangingBreakEvent}. This is used to prevent
     * from player to destroy screen-frames.
     *
     * @param event Instance of {@link HangingBreakEvent}.
     */
	
	@EventHandler
	public void onDamage(HangingBreakEvent event) {
		
		if(event.getEntity() instanceof ItemFrame) {
        	if(plugin.getScreensFrames().containsKey((ItemFrame)event.getEntity())) {
				event.setCancelled(true);
        	}
		}
	}
}
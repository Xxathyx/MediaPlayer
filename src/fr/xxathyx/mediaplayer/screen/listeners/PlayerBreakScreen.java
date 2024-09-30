package fr.xxathyx.mediaplayer.screen.listeners;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.xxathyx.mediaplayer.Main;

/** 
* The PlayerBreakScreen class implements {@link Listener}, it consist
* of a single event method {@link #onBreak(BlockBreakEvent)}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class PlayerBreakScreen implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
    /**
     * Called whenever a {@link Player} break a block. See Bukkit documentation
     * : {@link BlockBreakEvent}. This is used to prevent
     * from player to destroy screen-blocks.
     *
     * @param event Instance of {@link BlockBreakEvent}.
     */
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(plugin.getScreensBlocks().containsKey(event.getBlock())) {
			event.setCancelled(true);
		}
	}
	
    /**
     * Called whenever a {@link Entity} break a frame. See Bukkit documentation
     * : {@link EntityDamageByEntityEvent}. This is used to prevent
     * from player to destroy screen-frames.
     *
     * @param event Instance of {@link EntityDamageByEntityEvent}.
     */
	
	@EventHandler
	public void onDamageFrame(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof ItemFrame) {
			if(plugin.getScreensFrames().containsKey((ItemFrame)event.getEntity())) event.setCancelled(true);
		}
	}
}
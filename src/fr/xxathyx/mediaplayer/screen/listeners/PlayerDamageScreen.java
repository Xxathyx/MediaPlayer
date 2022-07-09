package fr.xxathyx.mediaplayer.screen.listeners;

import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;

import fr.xxathyx.mediaplayer.Main;

public class PlayerDamageScreen implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	@EventHandler
	public void onDamage(HangingBreakEvent event) {
		
		if(event.getEntity() instanceof ItemFrame) {
        	if(plugin.getScreensFrames().containsKey((ItemFrame)event.getEntity())) {
				event.setCancelled(true);
        	}
		}
	}
}
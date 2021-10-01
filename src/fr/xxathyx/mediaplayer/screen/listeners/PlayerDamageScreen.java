package fr.xxathyx.mediaplayer.screen.listeners;

import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.screen.Screen;

public class PlayerDamageScreen implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof ItemFrame) {
			
        	Material material = Material.MAP;
        	if(!plugin.isLegacy()) material = Material.FILLED_MAP;
        	
			if(((ItemFrame)event.getEntity()).getItem().getType() == material) {
				for(Screen screen : plugin.getRegisteredScreens()) {
					if(screen.getFrames().contains((ItemFrame) event.getEntity())) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
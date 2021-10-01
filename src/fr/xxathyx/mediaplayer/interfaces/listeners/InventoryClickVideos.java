package fr.xxathyx.mediaplayer.interfaces.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.interfaces.Interfaces;
import fr.xxathyx.mediaplayer.video.Video;

/** 
* The InventoryClickVideos class implements {@link Listener}, it consist
* of a single event method {@link #onClick(InventoryClickEvent)}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class InventoryClickVideos implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Interfaces interfaces = new Interfaces();
	
    /**
     * Called whenever a {@link Player} clicks in an inventory, specially in
     * videos panel, see {@link Interfaces#getVideos()}, see also Bukkit documentation
     * : {@link InventoryClickEvent}. This is used to listen for selected video to be
     * opened in a video panel, see {@link Interfaces#getVideoPanel(Video)}.
     * 
     * @param Event Instance of {@link InventoryClickEvent}.
     */
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		if(event.getView().getTitle().equals(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Videos (" + plugin.getRegisteredVideos().size() + ")")) {
						
			event.setCancelled(true);
						
	        if(event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
	            return;
	        }
	        if(event.getCurrentItem() == null) {
	        	return;
	        }
	        if(event.getCurrentItem().getType() == Material.AIR) {
	            return;
	        }
	        if(!event.getCurrentItem().hasItemMeta()) {
	            return;
	        }
	        if(!event.getCurrentItem().getItemMeta().hasDisplayName()) {
	            return;
	        }
	        if(event.getSlot() > 54) {
	        	return;
	        }
	        
	        event.getWhoClicked().closeInventory();        
	        plugin.getVideoPanels().put(event.getWhoClicked().getUniqueId(), plugin.getRegisteredVideos().get(event.getSlot()));
	        
	        event.getWhoClicked().openInventory(interfaces.getVideoPanel(plugin.getRegisteredVideos().get(event.getSlot())));
		}
	}
}
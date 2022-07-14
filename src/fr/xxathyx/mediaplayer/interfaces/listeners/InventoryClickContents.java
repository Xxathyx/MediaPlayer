package fr.xxathyx.mediaplayer.interfaces.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.interfaces.Interfaces;
import fr.xxathyx.mediaplayer.items.ItemStacks;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.screen.content.Content;
import fr.xxathyx.mediaplayer.video.player.VideoPlayer;

/** 
* The InventoryClickContents class implements {@link Listener}, it consist
* of a single event method {@link #onClick(InventoryClickEvent)}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-09 
*/

public class InventoryClickContents implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private final Configuration configuration = new Configuration();
	
	private final Interfaces interfaces = new Interfaces();
	private final ItemStacks items = new ItemStacks();
	
    /**
     * Called whenever a {@link Player} clicks in an inventory, specially in
     * contents panel, see {@link Interfaces#getContents()}, see also Bukkit documentation
     * : {@link InventoryClickEvent}.
     * 
     * @param Event Instance of {@link InventoryClickEvent}.
     */
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		if(event.getView().getTitle().contains(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Contents")) {
			
			if(!event.getWhoClicked().hasPermission("mediaplayer.permission.admin")) {
				event.getWhoClicked().closeInventory();
				return;
			}
			
			event.setCancelled(true);
						
	        if(event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;
	        if(event.getCurrentItem() == null) return;
	        if(event.getCurrentItem().getType() == Material.AIR) return;
	        if(!event.getCurrentItem().hasItemMeta()) return;
	        if(!event.getCurrentItem().getItemMeta().hasDisplayName()) return;
	        if(event.getSlot() > 54) return;
	        
	        Screen screen = plugin.getContentsPanels().get(event.getWhoClicked().getUniqueId());
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.previous().getItemMeta().getDisplayName())) {
	        	
	        	if(plugin.getContentsPages().get(event.getWhoClicked().getUniqueId())-1 < 0) {
	        		event.getWhoClicked().sendMessage(configuration.no_page_left());
	        		return;
	        	}
	        	
	        	plugin.getContentsPages().replace(event.getWhoClicked().getUniqueId(), plugin.getContentsPages().get(event.getWhoClicked().getUniqueId())-1);
		        event.getWhoClicked().closeInventory();
				event.getWhoClicked().openInventory(interfaces.getContents(screen, plugin.getContentsPages().get(event.getWhoClicked().getUniqueId())));
				plugin.getContentsPanels().put(event.getWhoClicked().getUniqueId(), screen);
				return;
	        }
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.refresh().getItemMeta().getDisplayName())) {      	
		        event.getWhoClicked().closeInventory();
				event.getWhoClicked().openInventory(interfaces.getContents(screen, plugin.getContentsPages().get(event.getWhoClicked().getUniqueId())));
				plugin.getContentsPanels().put(event.getWhoClicked().getUniqueId(), screen);
				return;
	        }
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.next().getItemMeta().getDisplayName())) {
	        	
	        	if(plugin.getContentsPages().get(event.getWhoClicked().getUniqueId())+1 > (int) (plugin.getRegisteredScreens().size() / 45)) {
	        		event.getWhoClicked().sendMessage(configuration.no_page_left());
	        		return;
	        	}
	        	
	        	plugin.getContentsPages().replace(event.getWhoClicked().getUniqueId(), plugin.getContentsPages().get(event.getWhoClicked().getUniqueId())+1);
		        event.getWhoClicked().closeInventory();
				event.getWhoClicked().openInventory(interfaces.getContents(screen, plugin.getContentsPages().get(event.getWhoClicked().getUniqueId())));
				plugin.getContentsPanels().put(event.getWhoClicked().getUniqueId(), screen);
				return;
	        }
	        
	        if(event.getCurrentItem().getType() == Material.ITEM_FRAME) {
	        	
	        	Content content = screen.getContents().get(event.getSlot() + plugin.getContentsPages().get(event.getWhoClicked().getUniqueId())*45);
	        	screen.setContent(content);
	        	
				screen.display();
				event.getWhoClicked().closeInventory();
				plugin.getVideoPlayers().put(event.getWhoClicked().getUniqueId(), new VideoPlayer(screen));	
	        	
		        event.getWhoClicked().closeInventory();
	        	plugin.getContentsPages().replace(event.getWhoClicked().getUniqueId(), 0);
	        }
	        
	        if(event.getCurrentItem().getType() == Material.PAINTING) {
	        	
	        	Content content = screen.getContents().get(event.getSlot() + plugin.getContentsPages().get(event.getWhoClicked().getUniqueId())*45);
	        	screen.setContent(content);
	        	
		        event.getWhoClicked().closeInventory();
	        	plugin.getContentsPages().replace(event.getWhoClicked().getUniqueId(), 0);
	        }
		}
	}
}
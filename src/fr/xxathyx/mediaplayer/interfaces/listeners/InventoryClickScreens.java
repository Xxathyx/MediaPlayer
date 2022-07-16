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

/** 
* The InventoryClickScreens class implements {@link Listener}, it consist
* of a single event method {@link #onClick(InventoryClickEvent)}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-09 
*/

public class InventoryClickScreens implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private final Configuration configuration = new Configuration();
	
	private final Interfaces interfaces = new Interfaces();
	private final ItemStacks items = new ItemStacks();
	
    /**
     * Called whenever a {@link Player} clicks in an inventory, specially in
     * screens panel, see {@link Interfaces#getScreens()}, see also Bukkit documentation
     * : {@link InventoryClickEvent}. This is used to listen for selected screen to be
     * opened in a screen panel, see {@link Interfaces#getScreenPanel(Screen)}.
     * 
     * @param event Instance of {@link InventoryClickEvent}.
     */
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		if(event.getView().getTitle().contains(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Screens")) {
			
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
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.previous().getItemMeta().getDisplayName())) {
	        	
	        	if(plugin.getScreensPages().get(event.getWhoClicked().getUniqueId())-1 < 0) {
	        		event.getWhoClicked().sendMessage(configuration.no_page_left());
	        		return;
	        	}
	        	
	        	plugin.getScreensPages().replace(event.getWhoClicked().getUniqueId(), plugin.getScreensPages().get(event.getWhoClicked().getUniqueId())-1);
		        event.getWhoClicked().closeInventory();
				event.getWhoClicked().openInventory(interfaces.getScreens(plugin.getScreensPages().get(event.getWhoClicked().getUniqueId())));
				return;
	        }
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.refresh().getItemMeta().getDisplayName())) {      	
		        event.getWhoClicked().closeInventory();
				event.getWhoClicked().openInventory(interfaces.getScreens(plugin.getScreensPages().get(event.getWhoClicked().getUniqueId())));
				return;
	        }
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.next().getItemMeta().getDisplayName())) {
	        	
	        	if(plugin.getScreensPages().get(event.getWhoClicked().getUniqueId())+1 > (int) (plugin.getRegisteredScreens().size() / 45)) {
	        		event.getWhoClicked().sendMessage(configuration.no_page_left());
	        		return;
	        	}
	        	
	        	plugin.getScreensPages().replace(event.getWhoClicked().getUniqueId(), plugin.getScreensPages().get(event.getWhoClicked().getUniqueId())+1);
		        event.getWhoClicked().closeInventory();
				event.getWhoClicked().openInventory(interfaces.getScreens(plugin.getScreensPages().get(event.getWhoClicked().getUniqueId())));
				return;
	        }
	        
	        if(event.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS) {
	        	
		        event.getWhoClicked().closeInventory();        
		        plugin.getScreenPanels().put(event.getWhoClicked().getUniqueId(), plugin.getRegisteredScreens().get(event.getSlot() +
		        		plugin.getScreensPages().get(event.getWhoClicked().getUniqueId())*45));
		        		        
		        event.getWhoClicked().openInventory(interfaces.getScreenPanel(plugin.getRegisteredScreens().get(event.getSlot() +
		        		plugin.getScreensPages().get(event.getWhoClicked().getUniqueId())*45)));
		        
	        	plugin.getScreensPages().replace(event.getWhoClicked().getUniqueId(), 0);
	        }
		}
	}
}
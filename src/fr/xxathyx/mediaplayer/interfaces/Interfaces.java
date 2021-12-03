package fr.xxathyx.mediaplayer.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.items.ItemStacks;
import fr.xxathyx.mediaplayer.video.Video;

/** 
* The Interfaces class is a contener, storing thtought methods the differents {@link Inventory},
* used across the plugin, such as {@link #getVideos()} for example.
*  
* <p>There is no special constructor, Interfaces class can be instantiated from everywhere granting
* an access to the interfaces inventories. Technical inventories part is managed by {@link fr.xxathyx.mediaplayer.interfaces.listeners}
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class Interfaces {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
	private final ItemStacks items = new ItemStacks();
	
	/** 
	* Gets an {@link Inventory} containing all {@link Main#getRegisteredVideos()} on a {@link ItemStack}
	* aspect.
	*
	* @throws FileNotFoundException When a video configuration-file ins't found.
	* @throws IOException When failed or interrupted I/O operations occurs.
	* @return An inventory containing registered videos.
	*/
	
	public Inventory getVideos(int index) {
		
		Inventory videos = Bukkit.createInventory(null, 54, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Videos (" + plugin.getRegisteredVideos().size() + ")");
		
		ArrayList<ArrayList<Video>> packets = new ArrayList<>();
		ArrayList<Video> packet = new ArrayList<>();
		
		int first = 45;
		if(plugin.getRegisteredVideos().size() < 45) first = plugin.getRegisteredVideos().size();
		
		packet.addAll(plugin.getRegisteredVideos().subList(0, first));
		packets.add(packet);
				
		if(plugin.getRegisteredVideos().size() > 45) {
			
			packet = new ArrayList<>();
			
			for(int i = 45; i < plugin.getRegisteredVideos().size(); i++) {
				if(i % 45 == 0) {	
					packet = new ArrayList<>();
					packets.add(packet);
				}			
				packets.get(packets.size()-1).add(plugin.getRegisteredVideos().get(i));
			}
		}
		
		packet = packets.get(index);
		
		for(Video video : packet) {
						
			ItemStack item = new ItemStack(Material.ITEM_FRAME, 1);
			ItemMeta item_meta = item.getItemMeta();
		    
			item_meta.setDisplayName(ChatColor.GREEN + video.getName());
			
			List<String> lore = new ArrayList<>();
			
			lore.add(video.getDescription());
			lore.add("");
			lore.add(ChatColor.DARK_AQUA + "frame-rate: " + ChatColor.BOLD + ChatColor.AQUA + video.getFrameRate() + ChatColor.DARK_AQUA + " FPS.");
			lore.add(ChatColor.DARK_AQUA + "frames: " + ChatColor.BOLD + ChatColor.AQUA + video.getTotalFrames() + ChatColor.DARK_AQUA + ".");
			lore.add(ChatColor.DARK_AQUA + "format: " + ChatColor.BOLD  + ChatColor.AQUA + video.getFormat() + "" + ChatColor.DARK_AQUA + ".");
			lore.add(ChatColor.DARK_AQUA + "quality: " + ChatColor.BOLD + ChatColor.AQUA + video.getWidth() + "x" + video.getHeight() + ChatColor.DARK_AQUA + ".");
			lore.add(ChatColor.DARK_AQUA + "duration: " + ChatColor.BOLD + ChatColor.AQUA + video.getDuration() + ChatColor.DARK_AQUA + ".");
			lore.add(ChatColor.DARK_AQUA + "views: " + ChatColor.BOLD + ChatColor.AQUA + video.getViews() + ChatColor.DARK_AQUA + ".");
			lore.add(ChatColor.DARK_AQUA + "speed: " + ChatColor.BOLD + ChatColor.AQUA + video.getSpeed() + "x" + ChatColor.DARK_AQUA + ".");
			lore.add(ChatColor.DARK_AQUA + "size: " + ChatColor.BOLD + ChatColor.AQUA + video.getSize() + ChatColor.DARK_AQUA + ".");
			lore.add(ChatColor.DARK_AQUA + "creation: " + ChatColor.BOLD + ChatColor.AQUA + video.getCreation() + ChatColor.DARK_AQUA + ".");
			lore.add("");
			lore.add(ChatColor.DARK_AQUA + "status: " + video.getStatus());
			
			if(video.isRestricted()) {
				lore.add("");
				lore.add(ChatColor.RED + "age-limit: " + configuration.age_limit_warning());
			}
			
			if(!video.isCompatible()) {
				lore.add("");
				lore.add(configuration.incompatible());
			}
			
			item_meta.setLore(lore);
			item.setItemMeta(item_meta);
						
			videos.addItem(item);
		}
		
		videos.setItem(45, items.glass());
	    videos.setItem(46, items.glass());
	    videos.setItem(47, items.glass());
	    videos.setItem(51, items.glass());
	    videos.setItem(52, items.glass());
	    videos.setItem(53, items.glass());
	    videos.setItem(48, items.previous());
	    videos.setItem(49, items.refresh());
	    videos.setItem(50, items.next());
		
		return videos;
	}
	
	/** 
	* Gets an {@link Inventory} containing videos tools, see {@link ItemStacks}.
	*
	* @param video The video that the panel is about.
	* @return An inventory panel containing videos tools.
	*/
	
	public Inventory getVideoPanel(Video video) {
		
		Inventory panel = Bukkit.createInventory(null, 27, ChatColor.LIGHT_PURPLE + "Panel (" + video.getIndex() + ")");
        
		panel.setItem(11, items.play());
		panel.setItem(13, items.load());
		panel.setItem(15, items.delete());
		
		return panel;
	}
}
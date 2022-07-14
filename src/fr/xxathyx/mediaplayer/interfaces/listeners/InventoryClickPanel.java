package fr.xxathyx.mediaplayer.interfaces.listeners;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.ffmpeg.Ffmpeg;
import fr.xxathyx.mediaplayer.interfaces.Interfaces;
import fr.xxathyx.mediaplayer.items.ItemStacks;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.sound.SoundPlayer;
import fr.xxathyx.mediaplayer.sound.SoundType;
import fr.xxathyx.mediaplayer.tasks.TaskSyncLoadScreens;
import fr.xxathyx.mediaplayer.video.Video;
import fr.xxathyx.mediaplayer.video.instance.VideoInstance;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/** 
* The InventoryClickPanel class implements {@link Listener}, it consist
* of a single event method {@link #onClick(InventoryClickEvent)}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class InventoryClickPanel implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
	private final Ffmpeg ffmpeg = new Ffmpeg();
	
	private final Interfaces interfaces = new Interfaces();
	private final ItemStacks items = new ItemStacks();
	
    /**
     * Called whenever a {@link Player} clicks in an inventory, specially in a
     * video panel, see {@link Interfaces#getVideoPanel(Video)}, see also Bukkit
     * documentation : {@link InventoryClickEvent}. This is used to list for
     * requests to perform actions such as {@link Video#load()}.
     * 
     * @throws IOException When failed or interrupted I/O operations occurs.
     * @param Event Instance of {@link InventoryClickEvent}.
     */
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		if(plugin.getVideoPanels().containsKey(event.getWhoClicked().getUniqueId())) {
			
			if(!event.getWhoClicked().hasPermission("mediaplayer.permission.admin")) {
				event.getWhoClicked().closeInventory();
				return;
			}
			
			Video video = plugin.getVideoPanels().get(event.getWhoClicked().getUniqueId());
			Player player = (Player) event.getWhoClicked();
			
			event.setCancelled(true);
			
	        if(event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;
	        if(event.getCurrentItem() == null) return;
	        if(event.getCurrentItem().getType() == Material.AIR) return;
	        if(!event.getCurrentItem().hasItemMeta()) return;
	        if(!event.getCurrentItem().getItemMeta().hasDisplayName()) return;
	        if(event.getSlot() > 54) return;
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.play().getItemMeta().getDisplayName())) {
				
	    		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

	    			@Override
	    			public void run() {
	    	        	if(!video.isLoaded()) {
	    					player.sendMessage(configuration.video_not_loaded(video.getName()));
	    					SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
	    					return;
	    				}
	    			}
	    		});
	        	
	        	if(plugin.getPlayingVideos().size() <= configuration.maximum_playing_videos()) {
	        		
	        		UUID uuid = UUID.randomUUID();
	        		VideoInstance videoInstance = new VideoInstance(video, new File(video.getInstancesFolder(), uuid.toString() + ".yml"));
	        		
	        		videoInstance.createConfiguration(video, uuid);
	        		
	        		if(plugin.getSelectedVideos().containsKey(player.getUniqueId())) {
	        			plugin.getSelectedVideos().replace(player.getUniqueId(), videoInstance);
	        		}
	        		
	        		plugin.getPlayingVideos().add(video.getName());	        		
	        		plugin.getSelectedVideos().put(player.getUniqueId(), videoInstance);
	        		
	        		player.sendMessage(configuration.video_selected(video.getName()));
	        		
					TextComponent dimension = new TextComponent(ChatColor.GRAY + "(Dimension: " + video.getVideoData().getMinecraftWidth() + "x" + video.getVideoData().getMinecraftHeight() + " -> " + ChatColor.BOLD + "/screen create " +
							video.getVideoData().getMinecraftWidth() + " " + video.getVideoData().getMinecraftHeight() + ChatColor.GRAY + ")");
					dimension.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "" + ChatColor.BOLD + "/" + ChatColor.GRAY +
							"screen create " + video.getVideoData().getMinecraftWidth() + " " + video.getVideoData().getMinecraftHeight()).create()));
					dimension.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/screen create " + video.getVideoData().getMinecraftWidth() + " " +
							video.getVideoData().getMinecraftHeight()));
					
					player.closeInventory();
					player.spigot().sendMessage(dimension);
					return;
	        	}
	        	player.sendMessage(configuration.too_much_playing());
	        	return;
	        }
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.load().getItemMeta().getDisplayName())) {
	        	
	    		if(!ffmpeg.isInstalled()) {
	    	        Bukkit.getLogger().warning("[MediaPlayer]: " + configuration.libraries_not_installed());
	    			return;
	    		}
	        	
	        	if(video.isLoaded()) {
	        		player.sendMessage(configuration.video_already_loaded(video.getName()));
					SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
	        		return;
	        	}
	        	
	        	if(!video.hasEnoughtSpace() && video.getVideoData().getRealTimeRendering()) {
	        		player.sendMessage(configuration.video_not_enought_space(video.getName()));
					SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
	        		return;
	        	}
	        	
	        	if(plugin.getLoadingVideos().size() <= configuration.maximum_loading_videos()) {
	        			        		
		        	video.load();
		        	
		        	player.sendMessage(configuration.video_load_requested());
		        	player.sendMessage(configuration.video_load_notice());
					SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
					
					player.closeInventory();
		        	return;
	        	} 
	        	player.sendMessage(configuration.too_much_loading());
	        	return;
	        }
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.delete().getItemMeta().getDisplayName())) {
	        	
				String name = video.getName();
	        	
				Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

					@Override
					public void run() {
						try {
							video.delete();
						}catch (IOException e) {
							e.printStackTrace();
						}
						player.sendMessage(configuration.video_deleted(name));
						SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
					}
				});
				player.closeInventory();
				return;
	        }
		}
		
		if(plugin.getScreenPanels().containsKey(event.getWhoClicked().getUniqueId())) {
			
			if(!event.getWhoClicked().hasPermission("mediaplayer.permission.admin")) {
				event.getWhoClicked().closeInventory();
				return;
			}
			
			Screen screen = plugin.getScreenPanels().get(event.getWhoClicked().getUniqueId());
			Player player = (Player) event.getWhoClicked();
			
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
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.switcher().getItemMeta().getDisplayName())) {			
				player.openInventory(interfaces.getContents(screen, 0));
				plugin.getContentsPages().put(player.getUniqueId(), 0);
				plugin.getContentsPanels().put(player.getUniqueId(), screen);
	        	return;
	        }
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.remote().getItemMeta().getDisplayName())) {
	        	
	        	if(!screen.getVideoName().equals("none")) {
		        	if(screen.isRunning()) {
			        	screen.setRunning(false);
						player.sendMessage(configuration.video_instance_pause(screen.getVideoName()));
						SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
						player.closeInventory();
						return;
		        	}
		        	screen.setRunning(true);
					player.sendMessage(configuration.video_instance_resume(screen.getVideoName()));
					SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
					player.closeInventory();
		        	return;
	        	}
	        }
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.teleport().getItemMeta().getDisplayName())) {
				player.teleport(screen.getLocation());
				player.sendMessage(configuration.screen_teleport(screen.getName()));
	        	return;
	        }
	        
	        if(event.getCurrentItem().getItemMeta().getDisplayName().equals(items.remove().getItemMeta().getDisplayName())) {
	        	
	        	String name = screen.getName();
	        	
				screen.delete();
				player.sendMessage(configuration.screen_deleted(name));
				SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
				player.closeInventory();
				
				new TaskSyncLoadScreens().runTask(plugin);
				return;
	        }
		}
	}
}
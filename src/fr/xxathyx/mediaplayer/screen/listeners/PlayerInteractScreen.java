package fr.xxathyx.mediaplayer.screen.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.api.listeners.PlayerInteractScreenEvent;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.interfaces.Interfaces;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.sound.SoundPlayer;
import fr.xxathyx.mediaplayer.sound.SoundType;
import fr.xxathyx.mediaplayer.util.FacingLocation;
import fr.xxathyx.mediaplayer.video.data.VideoData;

/** 
* The PlayerInteractScreen class implements {@link Listener}, it consist
* of a two event methods {@link #onInteract(PlayerInteractEntityEvent)},
* and {@link #onInteractAtScreen(PlayerInteractAtEntityEvent)}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class PlayerInteractScreen implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
	private final Interfaces interfaces = new Interfaces();
	
    /**
     * Called whenever a {@link Player} interact with an {@link Entity},
     * see Bukkit documentation : {@link PlayerInteractEntityEvent}. This
     * is used to detected manual player playing video from thumbnail-icon. 
     *
     * @param event Instance of {@link PlayerInteractEntityEvent}.
     */
	
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
    	
        if(event.getRightClicked() instanceof ItemFrame) {
        	
        	Material material = Material.MAP;
        	if(!plugin.isLegacy()) material = Material.FILLED_MAP;
        	        	
			if(((ItemFrame)event.getRightClicked()).getItem().getType() == material) {
				if(plugin.getScreensFrames().containsKey((ItemFrame) event.getRightClicked())) {
					
					Screen screen = plugin.getScreensFrames().get((ItemFrame) event.getRightClicked());
					
			        plugin.getScreenPanels().put(event.getPlayer().getUniqueId(), screen);
			        		        
			        event.getPlayer().openInventory(interfaces.getScreenPanel(screen));					
					event.setCancelled(true);
					
					if(screen.getVideoInstance() != null && !screen.getVideoName().equals("none")) {
						
						VideoData videoData = screen.getVideoInstance().getVideo().getVideoData();
												
						if(screen.getFrames().indexOf((ItemFrame) event.getRightClicked()) == (videoData.getMinecraftWidth()*videoData.getMinecraftHeight())/2) {
							if(!screen.isRunning()) {
								screen.setRunning(true);
								event.getPlayer().sendMessage(configuration.video_instance_started(screen.getVideoInstance().getVideo().getName(), String.valueOf(screen.getId())));
								SoundPlayer.playSound(event.getPlayer(), SoundType.NOTIFICATION_UP);
							}
						}
					}
				}
			}
        }
    }
    
    /**
     * Called whenever a {@link Player} interact with an {@link Entity},
     * see Bukkit documentation : {@link PlayerInteractAtEntityEvent}. This
     * is used to detected, and send a {@link PlayerInteractScreenEvent}.
     *
     * @param Event Instance of {@link PlayerInteractAtEntityEvent}.
     */
    
    @EventHandler
    public void onInteractAtScreen(PlayerInteractAtEntityEvent event) {
    	
        if(event.getRightClicked() instanceof ItemFrame) {
        	
        	Material material = Material.MAP;
        	if(!plugin.isLegacy()) material = Material.FILLED_MAP;
        	        	
			if(((ItemFrame)event.getRightClicked()).getItem().getType() == material) {
				if(plugin.getScreensFrames().containsKey((ItemFrame) event.getRightClicked())) {
					
					event.setCancelled(true);
					
					Screen screen = plugin.getScreensFrames().get((ItemFrame) event.getRightClicked());
					
					Player player = event.getPlayer();
					
					double width = screen.getWidth();
					double height = screen.getHeight();
					
					ItemFrame origin = screen.getFrames().get(screen.getWidth()*(screen.getHeight()-1));
					ItemFrame itemFrame = null;
					
					ArrayList<ItemFrame> frames = new ArrayList<>();	
											
					for(int j = 0; j < height; j++) {
						for(int i = 0; i < width; i++) {
							
							if(FacingLocation.getCardinalDirection(player).equals("N")) {									
								if(getNearbyEntities(origin.getLocation().add(i, j, 0), 0).toArray().length <= 0) return;
								itemFrame = (ItemFrame) getNearbyEntities(origin.getLocation().add(i, j, 0), 0).toArray()[0];
							}
							if(FacingLocation.getCardinalDirection(player).equals("E")) {
								if(getNearbyEntities(origin.getLocation().add(0, j, i), 0).toArray().length <= 0) return;
								itemFrame = (ItemFrame) getNearbyEntities(origin.getLocation().add(0, j, i), 0).toArray()[0];
							}
							if(FacingLocation.getCardinalDirection(player).equals("S")) {
								if(getNearbyEntities(origin.getLocation().add(-i, j, 0), 0).toArray().length <= 0) return;
								itemFrame = (ItemFrame) getNearbyEntities(origin.getLocation().add(-i, j, 0), 0).toArray()[0];
							}
							if(FacingLocation.getCardinalDirection(player).equals("W")) {
								if(getNearbyEntities(origin.getLocation().add(0, j, -i), 0).toArray().length <= 0) return;
								itemFrame = (ItemFrame) getNearbyEntities(origin.getLocation().add(0, j, -i), 0).toArray()[0];
							}									
							if(itemFrame != null) {
								frames.add(itemFrame);
							}
						}
					}
					
					int index = frames.indexOf((ItemFrame)event.getRightClicked());
									        
			        double i = 0, j = event.getClickedPosition().getY();
			        
					if(FacingLocation.getCardinalDirection(player).equals("N")) i = event.getClickedPosition().getX();
					if(FacingLocation.getCardinalDirection(player).equals("E")) i = event.getClickedPosition().getZ();
					if(FacingLocation.getCardinalDirection(player).equals("S")) i = event.getClickedPosition().getX();
					if(FacingLocation.getCardinalDirection(player).equals("W")) i = event.getClickedPosition().getZ();
			        
			        if(i >= 0) i = i + 0.375; if(j >= 0) j = j + 0.375;
			        
			        double cursorX = screen.getWidth()*128;
			        double cursorY = screen.getHeight()*128;
			        				        
			        if(!screen.getVideoName().equals("none")) {
			        					        	
				        cursorX = (index%width + i)*(screen.getVideo().getWidth()/width);
				        cursorY = (index/width + j)*(screen.getVideo().getHeight()/height);
				        
				        if(cursorX > screen.getVideo().getWidth()) cursorX = screen.getVideo().getWidth();
				        if(cursorY > screen.getVideo().getHeight()) cursorY = screen.getVideo().getHeight();
				        if(cursorX < 0) cursorX = 0; if(cursorY < 0) cursorY = 0;
			        }
					Bukkit.getPluginManager().callEvent(new PlayerInteractScreenEvent(event.getPlayer(), screen, (int) cursorX, (int) cursorY));
				}
			}
        }
    }
    
    /**
     * Gets the entities that are located in a specified radius from a {@link Location}.
     * 
     * <p>This method is used instead of the given one with the API, in order to support
     * older Minecraft versions.
     * 
     * @param location The center point.
     * @param radius The radius from the center.
     * @return The entities that are within the radius.
     */
	
	public Collection<Entity> getNearbyEntities(Location location, int radius) {
		
		if(plugin.isOld()) {
		    int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		    HashSet<Entity> radiusEntities = new HashSet < Entity > ();
		 
		    for(int chunkX = 0 - chunkRadius; chunkX <= chunkRadius; chunkX++) {
		        for(int chunkZ = 0 - chunkRadius; chunkZ <= chunkRadius; chunkZ++) {
		            int x = (int) location.getX(), y = (int) location.getY(), z = (int) location.getZ();
		            for(Entity entity : new Location(location.getWorld(), x + (chunkX * 16), y, z + (chunkZ * 16)).getChunk().getEntities()) {
		                if(entity.getLocation().distance(location) <= radius && entity.getLocation().getBlock() != location.getBlock())
		                    radiusEntities.add(entity);
		            }
		        }
		    }
		    return radiusEntities;
		}
		return location.getWorld().getNearbyEntities(location, radius, radius, radius);
	}
}
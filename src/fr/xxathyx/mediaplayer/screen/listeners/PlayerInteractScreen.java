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
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.sound.SoundPlayer;
import fr.xxathyx.mediaplayer.sound.SoundType;
import fr.xxathyx.mediaplayer.util.FacingLocation;
import fr.xxathyx.mediaplayer.video.data.VideoData;

public class PlayerInteractScreen implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if(event.getRightClicked() instanceof ItemFrame) {
        	
        	Material material = Material.MAP;
        	if(!plugin.isLegacy()) material = Material.FILLED_MAP;
        	        	
			if(((ItemFrame)event.getRightClicked()).getItem().getType() == material) {
				for(Screen screen : plugin.getRegisteredScreens()) {
					if(screen.getFrames().contains((ItemFrame) event.getRightClicked())) {
						
						VideoData videoData = screen.getVideoInstance().getVideo().getVideoData();
												
						if(screen.getFrames().indexOf((ItemFrame) event.getRightClicked()) == (videoData.getMinecraftWidth()*videoData.getMinecraftHeight())/2) {
							if(!screen.isRunning()) {
								screen.setRunning(true);
								event.getPlayer().sendMessage(configuration.video_instance_started(screen.getVideoInstance().getVideo().getName(), String.valueOf(screen.getId())));
								SoundPlayer.playSound(event.getPlayer(), SoundType.NOTIFICATION_UP);
							}
						}
						event.setCancelled(true);
					}
				}
			}
        }
    }
    
    @EventHandler
    public void onInteractAtScreen(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked() instanceof ItemFrame) {
        	
        	Material material = Material.MAP;
        	if(!plugin.isLegacy()) material = Material.FILLED_MAP;
        	        	
			if(((ItemFrame)event.getRightClicked()).getItem().getType() == material) {
				for(Screen screen : plugin.getRegisteredScreens()) {
					if(screen.getFrames().contains((ItemFrame) event.getRightClicked())) {
						
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
				        
				        double cursorX = (index%width + i)*(screen.getVideo().getWidth()/width);
				        double cursorY = (index/width + j)*(screen.getVideo().getHeight()/height);
				        
				        if(cursorX > screen.getVideo().getWidth()) cursorX = screen.getVideo().getWidth();
				        if(cursorY > screen.getVideo().getHeight()) cursorY = screen.getVideo().getHeight();
				        if(cursorX < 0) cursorX = 0; if(cursorY < 0) cursorY = 0;
				        
						Bukkit.getPluginManager().callEvent(new PlayerInteractScreenEvent(event.getPlayer(), screen, (int) cursorX, (int) cursorY));
						event.setCancelled(true);
					}
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
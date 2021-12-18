package fr.xxathyx.mediaplayer.video.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.image.listeners.PlayerInteractImage;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.sound.SoundPlayer;
import fr.xxathyx.mediaplayer.sound.SoundType;
import fr.xxathyx.mediaplayer.util.FacingLocation;
import fr.xxathyx.mediaplayer.video.instance.VideoInstance;
import fr.xxathyx.mediaplayer.video.player.VideoPlayer;

/** 
* The PlayerInteractVideo class implements {@link Listener}, it consist
* of a single event method {@link #interactVideo(PlayerInteractEntityEvent)}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class PlayerInteractVideo implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
    
    /**
     * Same as {@link PlayerInteractImage}, Called whenever a {@link Player}
     * interact with an {@link Entity}, see Bukkit documentation : {@link PlayerInteractEntityEvent}.
     * This is used to detected a player trying to assign a {@link Screen} to an existing {@link VideoInstance}.
     *
     * @param Event Instance of {@link PlayerInteractEntityEvent}.
     */
	
	@EventHandler
	public void interactVideo(PlayerInteractEntityEvent event) {
		
		if(event.getRightClicked() instanceof ItemFrame) {
						
			Player player = event.getPlayer();
						
			if(plugin.getSelectedVideos().containsKey(player.getUniqueId())) {
				
				event.setCancelled(true);
				
				VideoInstance videoInstance = plugin.getSelectedVideos().get(player.getUniqueId());
				
				ArrayList<ItemFrame> frames = new ArrayList<>();
				
				double c = 0;
				
				boolean up = false;
				boolean down = false;
				
		        if(plugin.getServerVersion().equals("v1_18_R1") || plugin.getServerVersion().equals("v1_17_R1") || plugin.getServerVersion().equals("v1_16_R3") || plugin.getServerVersion().equals("v1_16_R2") || plugin.getServerVersion().equals("v1_16_R1")) {
					if(Math.abs(player.getLocation().getPitch()) >= 60) {
						
						c = -1;
						
						if(player.getLocation().getPitch() <= -60) up = true;
						if(player.getLocation().getPitch() >= 60) down = true;
					}
		        }
						        
				for(int i = 0; i < videoInstance.getVideo().getVideoData().getMinecraftHeight(); i++) {
					for(int j = 0; j < videoInstance.getVideo().getVideoData().getMinecraftWidth(); j++) {
						
						ItemFrame itemFrame = null;
						
						double k = j;
						double y = -i;
												
				        if(plugin.getServerVersion().equals("v1_18_R1") || plugin.getServerVersion().equals("v1_17_R1") || plugin.getServerVersion().equals("v1_16_R3") || plugin.getServerVersion().equals("v1_16_R2") || plugin.getServerVersion().equals("v1_16_R1")) {
							if(Math.abs(player.getLocation().getPitch()) >= 60) {
								y = 0;
								if(k % videoInstance.getVideo().getVideoData().getMinecraftWidth() == 0) c++;
							}
				        }
						
						if(FacingLocation.getCardinalDirection(player).equals("N")) {
							if(getNearbyEntities(event.getRightClicked().getLocation().add(k, y, c), 0).toArray().length <= 0) {
								player.sendMessage(configuration.image_invalid_screen());
								SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
								return;
							}
							itemFrame = (ItemFrame) getNearbyEntities(event.getRightClicked().getLocation().add(k, y, c), 0).toArray()[0];
							if(up) itemFrame.setRotation(Rotation.CLOCKWISE);
						}
						if(FacingLocation.getCardinalDirection(player).equals("E")) {
							
							if(getNearbyEntities(event.getRightClicked().getLocation().add(-c, y, k), 0).toArray().length <= 0) {
								player.sendMessage(configuration.image_invalid_screen());
								SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
								return;
							}
							itemFrame = (ItemFrame) getNearbyEntities(event.getRightClicked().getLocation().add(-c, y, k), 0).toArray()[0];
							if(down) itemFrame.setRotation(Rotation.CLOCKWISE_45);
							if(up) itemFrame.setRotation(Rotation.CLOCKWISE_45);
						}
						if(FacingLocation.getCardinalDirection(player).equals("S")) {
							
							if(getNearbyEntities(event.getRightClicked().getLocation().add(-k, y, -c), 0).toArray().length <= 0) {
								player.sendMessage(configuration.image_invalid_screen());
								SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
								return;
							}
							itemFrame = (ItemFrame) getNearbyEntities(event.getRightClicked().getLocation().add(-k, y, -c), 0).toArray()[0];
							if(down) itemFrame.setRotation(Rotation.CLOCKWISE);
						}
						if(FacingLocation.getCardinalDirection(player).equals("W")) {
							
							if(getNearbyEntities(event.getRightClicked().getLocation().add(c, y, -k), 0).toArray().length <= 0) {
								player.sendMessage(configuration.image_invalid_screen());
								SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
								return;
							}
							itemFrame = (ItemFrame) getNearbyEntities(event.getRightClicked().getLocation().add(c, y, -k), 0).toArray()[0];
							if(down) itemFrame.setRotation(Rotation.CLOCKWISE_135);
							if(up) itemFrame.setRotation(Rotation.CLOCKWISE_135);
						}							
						if(itemFrame != null) {
							frames.add(itemFrame);
						}
					}
				}
				
				if(up) {
					
					ArrayList<ItemFrame[]> sequences = new ArrayList<>();
					
					for(int i = 0; i < videoInstance.getVideo().getVideoData().getMinecraftWidth(); i++) {
						
						ItemFrame[] sequence = new ItemFrame[videoInstance.getVideo().getVideoData().getMinecraftHeight()];
						
						for(int j = 0; j < videoInstance.getVideo().getVideoData().getMinecraftHeight(); j++) {
							sequence[j] = frames.get(i + j * videoInstance.getVideo().getVideoData().getMinecraftWidth());
						}
						sequences.add(sequence);
					}
					
					Collections.reverse(sequences);
					
					for(ItemFrame[] sequence : sequences) {
						for(int i = 0; i < sequence.length; i++) {
							frames.set(sequences.indexOf(sequence) + i * videoInstance.getVideo().getVideoData().getMinecraftWidth(), sequence[i]);
						}
					}
				}
											    				
				if(frames.size() < videoInstance.getVideo().getVideoData().getMinecraftWidth()*videoInstance.getVideo().getVideoData().getMinecraftHeight()) {
					player.sendMessage(configuration.image_invalid_screen());
					SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
					return;
				}
				
		        boolean visible = false;
		        
		        if(plugin.getServerVersion().equals("v1_18_R1") || plugin.getServerVersion().equals("v1_17_R1") || plugin.getServerVersion().equals("v1_16_R3") || plugin.getServerVersion().equals("v1_16_R2") || plugin.getServerVersion().equals("v1_16_R1")) {
		        	visible = false;
		        }
		        
				for(int i = 0; i < frames.size(); i++) {
					if(plugin.getServerVersion().equals("v1_18_R1")) ((org.bukkit.craftbukkit.v1_18_R1.entity.CraftItemFrame) frames.get(i)).setVisible(visible);
					if(plugin.getServerVersion().equals("v1_17_R1")) ((org.bukkit.craftbukkit.v1_17_R1.entity.CraftItemFrame) frames.get(i)).setVisible(visible);
					if(plugin.getServerVersion().equals("v1_16_R3")) ((org.bukkit.craftbukkit.v1_16_R3.entity.CraftItemFrame) frames.get(i)).setVisible(visible);
					if(plugin.getServerVersion().equals("v1_16_R2")) ((org.bukkit.craftbukkit.v1_16_R2.entity.CraftItemFrame) frames.get(i)).setVisible(visible);
					if(plugin.getServerVersion().equals("v1_16_R1")) ((org.bukkit.craftbukkit.v1_16_R1.entity.CraftItemFrame) frames.get(i)).setVisible(visible);
				}
				
				Screen screen = new Screen(videoInstance, frames);
				screen.display();
				
				plugin.getVideoPlayers().put(player.getUniqueId(), new VideoPlayer(screen));								
				plugin.getRegisteredScreens().add(screen);
				
				plugin.getSelectedVideos().remove(player.getUniqueId());
				player.sendMessage(configuration.video_assigned(videoInstance.getVideo().getName()));
				SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
			}
		}
	}
	
    /**
     * Gets the entities {@link ItemFrame} that are located in a specified radius from a {@link Location}.
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
		                if(entity.getLocation().distance(location) <= radius && entity.getLocation().getBlock() != location.getBlock()) {
		                	if(entity.getType() == EntityType.ITEM_FRAME) {
			                    radiusEntities.add(entity);
		                	}
		                }
		            }
		        }
		    }
		    return radiusEntities;
		}
		return location.getWorld().getNearbyEntities(location, radius, radius, radius);
	}
}
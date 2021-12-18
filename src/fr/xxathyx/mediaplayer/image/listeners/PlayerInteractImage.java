package fr.xxathyx.mediaplayer.image.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.image.Image;
import fr.xxathyx.mediaplayer.items.ItemStacks;
import fr.xxathyx.mediaplayer.sound.SoundPlayer;
import fr.xxathyx.mediaplayer.sound.SoundType;
import fr.xxathyx.mediaplayer.util.FacingLocation;

/** 
* The PlayerInteractImage class implements {@link Listener}, it consist
* of a single event method {@link #interactImage(PlayerInteractEntityEvent)}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class PlayerInteractImage implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
	private final ItemStacks itemStacks = new ItemStacks();
	
    /**
     * Called whenever a {@link Player} interact with an {@link Entity},
     * see Bukkit documentation : {@link PlayerInteractEntityEvent}. This
     * is used to place/remove an image-poster. 
     *
     * @param Event Instance of {@link PlayerInteractEntityEvent}.
     */
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void interactImage(PlayerInteractEntityEvent event) {
		
		if(event.getRightClicked() instanceof ItemFrame) {
			
			Player player = event.getPlayer();
		    
			if(player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) {
				if(player.getItemInHand().hasItemMeta()) {
					if(player.getItemInHand().getItemMeta().getDisplayName() != null) {
						
						String name = player.getItemInHand().getItemMeta().getDisplayName();
						
						name = name.replace(ChatColor.GOLD + "" + ChatColor.BOLD, "");
						
						if(new Image(name).getFile().exists()) {
							
							event.setCancelled(true);
							
							Image image = new Image(name);
							
							ArrayList<ItemFrame> frames = new ArrayList<>();
							
							for(int i = 0; i < image.getHeight(); i++) {
								for(int j = 0; j < image.getWidth(); j++) {
									
									ItemFrame itemFrame = null;
									
									if(FacingLocation.getCardinalDirection(player).equals("N")) {
										
										if(getNearbyEntities(event.getRightClicked().getLocation().add(j, -i, 0), 0).toArray().length <= 0) {
											player.sendMessage(configuration.image_invalid_screen());
											SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
											return;
										}
										itemFrame = (ItemFrame) getNearbyEntities(event.getRightClicked().getLocation().add(j, -i, 0), 0).toArray()[0];
									}
									if(FacingLocation.getCardinalDirection(player).equals("E")) {
										
										if(getNearbyEntities(event.getRightClicked().getLocation().add(0, -i, j), 0).toArray().length <= 0) {
											player.sendMessage(configuration.image_invalid_screen());
											SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
											return;
										}
										itemFrame = (ItemFrame) getNearbyEntities(event.getRightClicked().getLocation().add(0, -i, j), 0).toArray()[0];
									}
									if(FacingLocation.getCardinalDirection(player).equals("S")) {
										
										if(getNearbyEntities(event.getRightClicked().getLocation().add(-j, -i, 0), 0).toArray().length <= 0) {
											player.sendMessage(configuration.image_invalid_screen());
											SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
											return;
										}
										itemFrame = (ItemFrame) getNearbyEntities(event.getRightClicked().getLocation().add(-j, -i, 0), 0).toArray()[0];
									}
									if(FacingLocation.getCardinalDirection(player).equals("W")) {
										
										if(getNearbyEntities(event.getRightClicked().getLocation().add(0, -i, -j), 0).toArray().length <= 0) {
											player.sendMessage(configuration.image_invalid_screen());
											SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
											return;
										}
										itemFrame = (ItemFrame) getNearbyEntities(event.getRightClicked().getLocation().add(0, -i, -j), 0).toArray()[0];
									}									
									if(itemFrame != null) {
										frames.add(itemFrame);
									}
								}
							}
							
							if(frames.size() < image.getIds().size()) {
								player.sendMessage(configuration.image_invalid_screen());
								SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
								return;
							}
							
							if(player.isSneaking()) {
								for(int i = 0; i < frames.size(); i++) {
									frames.get(i).setItem(new ItemStack(new ItemStack(Material.AIR, 1)));
								}
								player.sendMessage(configuration.image_removed(image.getName()));
								SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
								return;
							}
							
					        boolean visible = true;
					        
					        if(plugin.getServerVersion().equals("v1_18_R1") || plugin.getServerVersion().equals("v1_17_R1") || plugin.getServerVersion().equals("v1_16_R3") || plugin.getServerVersion().equals("v1_16_R2") || plugin.getServerVersion().equals("v1_16_R1")) {
					        	visible = false;
					        }
					        
							for(int i = 0; i < frames.size(); i++) {
								
								if(plugin.getServerVersion().equals("v1_18_R1")) ((org.bukkit.craftbukkit.v1_18_R1.entity.CraftItemFrame) frames.get(i)).setVisible(visible);
								if(plugin.getServerVersion().equals("v1_17_R1")) ((org.bukkit.craftbukkit.v1_17_R1.entity.CraftItemFrame) frames.get(i)).setVisible(visible);
								if(plugin.getServerVersion().equals("v1_16_R3")) ((org.bukkit.craftbukkit.v1_16_R3.entity.CraftItemFrame) frames.get(i)).setVisible(visible);
								if(plugin.getServerVersion().equals("v1_16_R2")) ((org.bukkit.craftbukkit.v1_16_R2.entity.CraftItemFrame) frames.get(i)).setVisible(visible);
								if(plugin.getServerVersion().equals("v1_16_R1")) ((org.bukkit.craftbukkit.v1_16_R1.entity.CraftItemFrame) frames.get(i)).setVisible(visible);
								
								frames.get(i).setItem(itemStacks.getMap(image.getIds().get(i)));
							}
							
							player.sendMessage(configuration.image_placed(image.getName()));
							SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
						}
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
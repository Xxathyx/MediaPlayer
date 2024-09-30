package fr.xxathyx.mediaplayer.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.scheduler.BukkitRunnable;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.items.ItemStacks;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.screen.part.Part;

/** 
* The TaskSyncLoadScreens class extends {@link BukkitRunnable} have no special constructors, have an
* method, see {@link #run()}. As named, the task is used to load screen configurations. The task is
* runned for example on {@link Main#onEnable()}. The task is runned synchronously from the main thread
* this is not a thread-safe task since the screen is parametered to run on start and it is accessing sensible
* {@link Bukkit} API content.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-04
*/

public class TaskSyncLoadScreens extends BukkitRunnable {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
	private final ItemStacks items = new ItemStacks();
	
	/**
	* Runs a task that will load and register screens contained in {@link Configuration#getScreensFolder()}
	* to {@link Main#getRegisteredScreens()}.
	*/
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		
		plugin.getTasks().add(getTaskId());
		
		File[] files = configuration.getScreensFolder().listFiles(File::isDirectory);
		
		plugin.getRegisteredScreens().clear();
		plugin.getScreensBlocks().clear(); plugin.getScreensFrames().clear();
		
		for(File file : files) {
			
			File screenConfiguration = new File(configuration.getScreensFolder() + "/" + file.getName(), file.getName() + ".yml");
			
			Screen screen = new Screen(screenConfiguration);
						
			String entityName = "item_frame";
			if(configuration.glowing_screen_frames_support()) entityName = "glow_item_frame";
			
			ArrayList<ItemFrame> frames = new ArrayList<>();
			ArrayList<Location> existant = new ArrayList<>();
			
			for(Part part : screen.getParts()) {
				
				Location location = part.getItemFrameLocation();
				
				Chunk chunk = location.getChunk();
				if(chunk.isLoaded()) chunk.setForceLoaded(true); chunk.load();
				
				List<Entity> entities = Arrays.asList(chunk.getEntities());
												
				if(!part.getBlock().getType().equals(screen.getBlockType())) {
					part.getBlock().setType(screen.getBlockType());
				}
				ItemFrame itemFrame = part.getItemFrame();
				if(itemFrame == null) {
					
					for(Entity entity : entities) {	
						if(entity.getLocation().distance(location)<0.01) {
							plugin.getScreensFrames().put((ItemFrame) entity, screen);
							frames.add((ItemFrame) entity);
							existant.add(entity.getLocation());
						}						
					}
				}else plugin.getScreensFrames().put(itemFrame, screen);
				plugin.getScreensBlocks().put(part.getBlock(), screen);
			}
			
			ArrayList<Location> replace = new ArrayList<>();
			
			for(Part part : screen.getParts()) {
				
				Location frameLocation = part.getItemFrameLocation();
				boolean contains = false;
				
				for(Location location : existant) {
					if(location.distance(frameLocation) == 0) contains = true;
				}
				
				if(contains == false) replace.add(frameLocation);
			}
						
			for(Location location : replace) {
				if(getNearbyEntities(location, 0).toArray().length <= 0) {
					ItemFrame frame = (ItemFrame) screen.getLocation().getWorld().spawnEntity(location, EntityType.fromName(entityName));
					frames.add(frame);
					plugin.getScreensFrames().put(frame, screen);
				}
			}
			
			screen.loadThumbnail();
			
			if(!plugin.isReloaded()) {
				for(int i = 0; i < frames.size(); i++) {
					if(frames.get(i) != null) {
						if(frames.get(i).getItem().getType().equals(Material.AIR)) {
							frames.get(i).setItem(items.getMap(screen.getIds()[i]));
						}
					}
				}
			}else {
				for(int i = 0; i < screen.getFrames().size(); i++) {
					if(screen.getFrames().get(i) != null) {
						if(screen.getFrames().get(i).getItem().getType().equals(Material.AIR)) {
							screen.getFrames().get(i).setItem(items.getMap(screen.getIds()[i]));
						}
					}
				}
			}
						
			if(plugin.isReloaded()) {			
				frames = new ArrayList<>();
				for(Part part : screen.getParts()) {
					ItemFrame frame = part.getItemFrame();
					frames.add(frame);
					plugin.getScreensFrames().put(frame, screen);
				}
			}
			
			screen.setFrames(frames);
			
			plugin.getRegisteredScreens().add(screen);
		}
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + ChatColor.GRAY + "Screens successfully registered. (" + plugin.getRegisteredScreens().size() + ")");
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
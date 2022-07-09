package fr.xxathyx.mediaplayer.screen.part;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.screen.Screen;

public class Part {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private FileConfiguration fileconfiguration;
	private File file;
	
	public Part(File file) {
		this.file = file;
	}
	
	public void createConfiguration(UUID uuid, Block block, ItemFrame itemFrame, boolean glowing, boolean visible, int id) {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.set("screen.uuid", uuid.toString());
		fileconfiguration.set("screen.part-id", id);
		
		fileconfiguration.set("block.type", block.getType().toString());
		fileconfiguration.set("block.location.world", block.getLocation().getWorld().getName());
		fileconfiguration.set("block.location.x", block.getLocation().getX());
		fileconfiguration.set("block.location.y", block.getLocation().getY());
		fileconfiguration.set("block.location.z", block.getLocation().getZ());
		
		fileconfiguration.set("item-frame.glowing", glowing);
		fileconfiguration.set("item-frame.visible", visible);
		fileconfiguration.set("item-frame.location.world", itemFrame.getLocation().getWorld().getName());
		fileconfiguration.set("item-frame.location.x", itemFrame.getLocation().getX());
		fileconfiguration.set("item-frame.location.y", itemFrame.getLocation().getY());
		fileconfiguration.set("item-frame.location.z", itemFrame.getLocation().getZ());
		fileconfiguration.set("item-frame.location.facing", itemFrame.getFacing().toString());
		
		try {
			fileconfiguration.save(file);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getConfigFile() {
		
		fileconfiguration = new YamlConfiguration();
		
		try {
			fileconfiguration.load(file);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return fileconfiguration;
    }
	
	public File getFile() {
		return file;
	}
	
	public Screen getScreen() {
		return new Screen(UUID.fromString(getConfigFile().getString("screen.part-id")));
	}
	
	public int getId() {
		return getConfigFile().getInt("screen.part-id");
	}
	
	public boolean isGlowing() {
		return getConfigFile().getBoolean("item-frame.glowing");
	}
	
	public boolean isVisible() {
		return getConfigFile().getBoolean("item-frame.visible");
	}
	
	public Block getBlock() {
		return new Location(Bukkit.getWorld(getConfigFile().getString("block.location.world")), getConfigFile().getDouble("block.location.x"), getConfigFile().getDouble("block.location.y"),
				getConfigFile().getDouble("block.location.z")).getBlock();
	}
	
	public Location getItemFrameLocation() {
				
		return new Location(Bukkit.getWorld(getConfigFile().getString("item-frame.location.world")), getConfigFile().getDouble("item-frame.location.x"),
				getConfigFile().getDouble("item-frame.location.y"), getConfigFile().getDouble("item-frame.location.z"));
	}
	
	public ItemFrame getItemFrame() {
		
		Location location = new Location(Bukkit.getWorld(getConfigFile().getString("item-frame.location.world")), getConfigFile().getDouble("item-frame.location.x"),
				getConfigFile().getDouble("item-frame.location.y"), getConfigFile().getDouble("item-frame.location.z"));
		
		if(getNearbyEntities(location, 0).toArray().length <= 0) {
			return null;
		}
		return (ItemFrame) getNearbyEntities(location, 0).toArray()[0];
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
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

/** 
* The Part class is essential for the screen structure, it consists
* in regrouping {@link ItemFrame} and {@link Block} into a single
* object for simplicity. This class has only one constructor.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class Part {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private FileConfiguration fileconfiguration;
	private File file;
	
	/**
	* Constructor for Part class, creates an {@link Part} variable according
	* to a part configuration-file, this is used during first time screen creation,
	* see {@link Screen#createConfiguration(String, Location)}.
	* 
	* @param file The screen configuration-file.
	*/
	
	public Part(File file) {
		this.file = file;
	}
	
	/**
	* Creates a part configuration-file according to {@link Screen} informations.
	* 
	* <p>This is used in {@link Screen#createConfiguration(String, Location)}.
	* 
	* @param uuid The part unique-id.
	* @param block The part {@link Block}.
	* @param itemFrame The part {@link ItemFrame}.
	* @param glowing Whether a part {@link ItemFrame} is glowing.
	* @param visible Whether a part {@link ItemFrame} is visible.
	* @param id The specific part-id.
	*/
	
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
	
    /**
     * Gets an FileConfiguration instance of the part configuration-file,
     * wich grant access to the configuration data.
     * 
     * <p> This method isn't usable directly, its used on class getters method
     * such as {@link #getUUID()} to access data.
     *
     * @return FileConfiguration instance of the part configuration-file.
     */
	
	public FileConfiguration getConfigFile() {
		
		fileconfiguration = new YamlConfiguration();
		
		try {
			fileconfiguration.load(file);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return fileconfiguration;
    }
	
	/**
	* Gets the part configuration-file accoding to the values passed earlier in the constructors.
	* 
	* <p>The configuration file does not need to exist, specially if the {@link #createConfiguration(File)}
	* will be called next.
	* 
	* @return The part configuration-file.
	*/
	
	public File getFile() {
		return file;
	}
	
	/**
	* Gets the {@link Screen} that the part is belonging to.
	* 
	* @return The {@link Screen} that the part is belonging to.
	*/
	
	public Screen getScreen() {
		return new Screen(UUID.fromString(getConfigFile().getString("screen.uuid")));
	}
	
	/**
	* Gets the speicific part-id.
	* 
	* @return The part-id.
	*/
	
	public int getId() {
		return getConfigFile().getInt("screen.part-id");
	}
	
	/**
	* Gets whether the part {@link ItemFrame} is glowing.
	* 
	* @return Whether the part {@link ItemFrame} is glowing.
	*/
	
	public boolean isGlowing() {
		return getConfigFile().getBoolean("item-frame.glowing");
	}
	
	/**
	* Gets whether the part {@link ItemFrame} is visible.
	* 
	* @return Whether the part {@link ItemFrame} is visible.
	*/
	
	public boolean isVisible() {
		return getConfigFile().getBoolean("item-frame.visible");
	}
	
	/**
	* Gets the part {@link Block}.
	* 
	* @return The part {@link Block}.
	*/
	
	public Block getBlock() {
		return new Location(Bukkit.getWorld(getConfigFile().getString("block.location.world")), getConfigFile().getDouble("block.location.x"), getConfigFile().getDouble("block.location.y"),
				getConfigFile().getDouble("block.location.z")).getBlock();
	}
	
	/**
	* Gets the part {@link ItemFrame} {@link Location}.
	* 
	* @return The part {@link ItemFrmae} location.
	*/
	
	public Location getItemFrameLocation() {
				
		return new Location(Bukkit.getWorld(getConfigFile().getString("item-frame.location.world")), getConfigFile().getDouble("item-frame.location.x"),
				getConfigFile().getDouble("item-frame.location.y"), getConfigFile().getDouble("item-frame.location.z"));
	}
	
	/**
	* Gets the part {@link ItemFrame} itselft.
	* 
	* @return The part {@link ItemFrame} itselft.
	*/
	
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
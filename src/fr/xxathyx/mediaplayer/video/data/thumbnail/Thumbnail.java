package fr.xxathyx.mediaplayer.video.data.thumbnail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.xxathyx.mediaplayer.image.renderer.ImageRenderer;

/** 
* The Thumbnail class is used to manage videos thumbnail, this used during
* video loading, see {@link TaskAsyncLoadVideos#run()}, the class has only
* one constructor and a few methods.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class Thumbnail {
		
	private FileConfiguration fileconfiguration;
	private File file;
	
	/**
	* Main constructor for Thumbnail class, creates an Thumbnail variable according to a
	* {@link BufferdImage} file.
	* 
	* @param file The thumbnail file.
	*/
	
	public Thumbnail(File file) {
		this.file = file;
	}
	
	/** 
	* Creates a thumbnail configuration-file, according to {@link BufferedImage} thumbnail.
	*
	* @param imageRenderer The image-renderer tool, see {@link ImageRenderer}.
	*/
	
	public void create(ImageRenderer imageRenderer) {
				
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.set("image.path", file.getPath());    	
		fileconfiguration.set("image.world", Bukkit.getWorlds().get(0).getName());
		fileconfiguration.set("image.ids", imageRenderer.getIds());
		
		try {
			fileconfiguration.save(new File(file.getParentFile() + "/data/maps/", new File(file.getParentFile() + "/data/maps/").listFiles().length + ".yml"));
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Gets the thumbnail as a {@link BufferedImage} variable.
	*
	* @throws IOException When failed or interrupted I/O operations occurs.
	* @return The thumbnail as a {@link BufferedImage} variable.
	*/
	
	public BufferedImage getBufferedImage() throws IOException {
		return ImageIO.read(new File(getPath()));
	}
	
	/**
	* Gets the thumbnail-file path.
	*
	* @return The thumbnail-file path.
	*/
	
	public String getPath() {
		return getConfigFile().getString("image.path");
	}
	
	/**
	* Gets the world where the map-ids where stored.
	* 
	* <p>Saving and using maps from external worlds will
	* not work.
	* 
	* @return The world where the map-ids where stored.
	*/
	
	public World getWorld() {
		return Bukkit.getWorld(getConfigFile().getString("image.world"));
	}
	
	/**
	* Gets the Minecraft map-ids of the thumbnail, used for creating the thumbnail.
	* 
	* @return The Minecraft map-ids of the thumbnail.
	*/
	
	public List<Integer> getIds() {
		return getConfigFile().getIntegerList("image.ids");
	}
	
	
    /**
     * Gets an FileConfiguration instance of the {@link Thumbnail} configuration-file,
     * wich grant access to the configuration data.
     * 
     * <p> This method isn't usable directly, its used on class getters method
     * such as {@link #getPath()} to access data.
     *
     * @return FileConfiguration instance of the thumbnail configuration-file.
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
	* Gets the thumbnail configuration-file accoding to the values passed earlier in the constructors.
	* 
	* <p>The configuration file does not need to exist, specially if the {@link #createConfiguration(File)}
	* will be called next.
	* 
	* @return The thumbnail configuration-file.
	*/
	
	public File getFile() {
		return file;
	}
}
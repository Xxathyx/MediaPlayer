package fr.xxathyx.mediaplayer.video.instance;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.video.Video;

/** 
* The VideoInstance class is used in {@link Screen} in order to represent a video that
* is runned. The class have only two constructors.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class VideoInstance {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private FileConfiguration fileconfiguration;
	private File file;
	
	private Video video;
	private Screen screen;
	
	private boolean linked = false;
	
	/**
	* Constructor for VideoInstance class, creates an VideoInstance variable according
	* to a original {@link Video}, and the VideoInstance configuration-file.
	* 
	* @param video The original video.
	* @param file The VideoInstance configuration-file.
	*/
	
	public VideoInstance(Video video, File file) {
		this.video = video;
		this.file = file;
	}
	
	/**
	* Constructor for VideoInstance class, creates an VideoInstance variable according
	* to a video instance configuration-file.
	* 
	* @param file The content configuration-file.
	*/
	
	public VideoInstance(File file) {
		this.file = file;
	}
	
	/**
	* Creates a VideoInstance configuration-file according to a {@link Video}, and a unique-id specific
	* to the video instance.
	* 
	* @param video The video related to the video instance.
	* @param uuid The video instance unique-id.
	*/
	
	public void createConfiguration(Video video, UUID uuid) {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.set("video-instance.name", video.getName());
		
		fileconfiguration.set("video-instance.uuid", uuid.toString());
		fileconfiguration.set("video-instance.screen", "none");
		
		try {
			fileconfiguration.save(file);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Sets the video instance screen, in the configuration-file and here.
	* 
	* @param screen The screen that will support the video.
	*/
	
	public void setScreen(Screen screen) {
		
		this.screen = screen;
		
		fileconfiguration = new YamlConfiguration();
		
		try {
			fileconfiguration.load(file);
			fileconfiguration.set("video-instance.screen", screen.getUUID().toString());
			fileconfiguration.save(file);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Gets an FileConfiguration instance of the video instance configuration-file,
     * wich grant access to the configuration data.
     * 
     * <p> This method isn't usable directly, its used on class getters method
     * such as {@link #getName()} to access data.
     *
     * @return FileConfiguration instance of the video instance configuration-file.
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
	* Gets the video instance configuration-file accoding to the values passed earlier in the constructors.
	* 
	* <p>The configuration file does not need to exist, specially if the {@link #createConfiguration(File)}
	* will be called next.
	* 
	* @return The video instance configuration-file.
	*/
	
	public File getFile() {
		return file;
	}
	
	/**
	* Gets the video instance unique-id.
	* 
	* @return The video instance unique-id.
	*/
	
	public UUID getUUID() {
		return UUID.fromString(getConfigFile().getString("video-instance"));
	}
	
	/**
	* Gets the video instance attached screen.
	* 
	* @return The video instance attached screen.
	*/
	
	public Screen getScreen() {
		
		if(screen != null) return screen;
		
		for(Screen screen : plugin.getRegisteredScreens()) {
			if(screen.getUUID().toString().equals(getUUID().toString())) {
				this.screen = screen;
				return screen;
			}
		}
		return new Screen(getUUID());
	}
	
	/**
	* Gets the original video.
	* 
	* @return The original video.
	*/
	
	public Video getVideo() {
		
		if(video != null) return video;
		
		Video video = new Video(getConfigFile().getString("video-instance.name"));
		this.video = video;
		
		return video;
	}
	
	/**
	* Gets whether the video instance has been linked to a {@link Screen}.
	* 
	* @return Whether the video instance has been linked to a {@link Screen}.
	*/
	
	public boolean isLinked() {
		return linked;
	}
}
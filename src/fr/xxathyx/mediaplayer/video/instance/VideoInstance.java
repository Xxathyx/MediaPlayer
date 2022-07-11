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

public class VideoInstance {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private FileConfiguration fileconfiguration;
	private File file;
	
	private Video video;
	private Screen screen;
	
	private boolean linked = false;
	
	public VideoInstance(Video video, File file) {
		this.video = video;
		this.file = file;
	}
	
	public VideoInstance(File file) {
		this.file = file;
	}
	
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
	
	public void setScreen(Screen screen) {
		
		fileconfiguration = new YamlConfiguration();
		
		try {
			fileconfiguration.load(file);
			fileconfiguration.set("video-instance.screen", screen.getUUID().toString());
			fileconfiguration.save(file);
		}catch (IOException | InvalidConfigurationException e) {
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
	
	public UUID getUUID() {
		return UUID.fromString(getConfigFile().getString("video-instance"));
	}
	
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
	
	public Video getVideo() {
		
		if(video != null) return video;
		
		Video video = new Video(getConfigFile().getString("video-instance.name"));
		this.video = video;
		
		return video;
	}
	
	public boolean isLinked() {
		return linked;
	}
}
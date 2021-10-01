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

import fr.xxathyx.mediaplayer.image.ImageRenderer;

public class Thumbnail {
		
	private FileConfiguration fileconfiguration;
	private File file;
	
	public Thumbnail(File file) {
		this.file = file;
	}
	
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
	
	public BufferedImage getBufferedImage() throws IOException {
		return ImageIO.read(new File(getPath()));
	}
	
	public String getPath() {
		return getConfigFile().getString("image.path");
	}
	
	public World getWorld() {
		return Bukkit.getWorld(getConfigFile().getString("image.world"));
	}
	
	public List<Integer> getIds() {
		return getConfigFile().getIntegerList("image.ids");
	}
	
	public File getFile() {
		return file;
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
}
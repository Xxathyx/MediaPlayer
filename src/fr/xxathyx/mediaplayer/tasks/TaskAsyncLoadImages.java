package fr.xxathyx.mediaplayer.tasks;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.image.Image;
import fr.xxathyx.mediaplayer.image.ImageRenderer;
import fr.xxathyx.mediaplayer.image.helpers.ImageHelper;

import org.bukkit.World;

/** 
* The TaskAsyncLoadImages class extends {@link BukkitRunnable} have no special
* constructors, have an method, see {@link #run()}. As named, the task is used to load images
* themselves and their configurations, see {@link Image}. The task is runned on {@link Main#onEnable()}.
* The task is runned asynchronously from the main thread for I/O opperations, since it uses re-rendering,
* loading images using the {@link World} can causes some problems, but again its widely stable.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class TaskAsyncLoadImages extends BukkitRunnable {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	/**
	* Runs a task that will load the {@link Image} themselves and their configurations in order to be
	* again displayed in Minecraft.
	* 
	* <p> <strong>Note: </strong>The images are re-rendered again according to their {@link Image#getPath()},
	* so if the same named file does change, the changes will be applied.
	*/
	
	@Override
	public void run() {
		
		File[] files = new File(plugin.getDataFolder() + "/images/maps/").listFiles();
		
		for(File file : files) {
			try {
				Image image = new Image(file);
				BufferedImage bufferedImage = ImageHelper.getImage(image.getPath());
				
				ImageRenderer imageRenderer = new ImageRenderer(bufferedImage);
				
				imageRenderer.calculateDimensions();
				imageRenderer.splitImages();
				
				MapView map;
				
				for(int j = 0; j < imageRenderer.getBufferedImages().length; j++) {
					
					map = plugin.getMapUtil().getMapView(image.getIds().get(j));
					map = new ImageRenderer(imageRenderer.getBufferedImages()[j]).resetRenderers(map);
					
					map.setScale(MapView.Scale.FARTHEST);
					if(!plugin.isLegacy()) map.setUnlimitedTracking(false);
					map.addRenderer(new ImageRenderer(imageRenderer.getBufferedImages()[j]));
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + ChatColor.GRAY + "Images successfully updated. (" + new File(plugin.getDataFolder() + "/images/maps/").listFiles().length + ")");
	}
}
package fr.xxathyx.mediaplayer.tasks;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.scheduler.BukkitRunnable;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
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
		
		plugin.getScreensBlocks().clear();
		plugin.getScreensFrames().clear();
		
		for(File file : files) {
			
			File screenConfiguration = new File(configuration.getScreensFolder() + "/" + file.getName(), file.getName() + ".yml");
			
			Screen screen = new Screen(screenConfiguration);
						
			String entityName = "item_frame";
			if(configuration.glowing_screen_frames_support()) entityName = "glow_item_frame";
			
			ArrayList<ItemFrame> frames = new ArrayList<>();
			
			for(Part part : screen.getParts()) {
				
				Chunk chunk = part.getItemFrameLocation().getChunk();
				
				if(chunk.isLoaded()) chunk.setForceLoaded(true); chunk.load();
				
				if(part.getBlock().getType() != screen.getBlockType()) part.getBlock().setType(screen.getBlockType());
				if(part.getItemFrame() != null) part.getItemFrame().setPersistent(true);
				if(part.getItemFrame() == null) screen.getLocation().getWorld().spawnEntity(part.getItemFrameLocation(), EntityType.fromName(entityName));
				
				plugin.getScreensBlocks().put(part.getBlock(), screen);
				plugin.getScreensFrames().put(part.getItemFrame(), screen);
				
				frames.add(part.getItemFrame());
			}
			
			screen.loadThumbnail();
			
			plugin.getRegisteredScreens().add(screen);
		}
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + ChatColor.GRAY + "Screens successfully registered. (" + plugin.getRegisteredScreens().size() + ")");
	}
}
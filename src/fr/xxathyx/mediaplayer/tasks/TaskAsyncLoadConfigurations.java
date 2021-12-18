package fr.xxathyx.mediaplayer.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.scheduler.BukkitRunnable;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.group.Group;
import fr.xxathyx.mediaplayer.notification.Notification;
import fr.xxathyx.mediaplayer.notification.NotificationType;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.util.Format;
import fr.xxathyx.mediaplayer.video.Video;
import fr.xxathyx.mediaplayer.video.data.VideoData;
import fr.xxathyx.mediaplayer.video.instance.VideoInstance;

/** 
* The TaskAsyncLoadConfigurations class extends {@link BukkitRunnable} have no special
* constructors, have an method, see {@link #run()}. As named, the task is used to load video
* configurations. The task is runned for example on {@link Main#onEnable()}. The task is runned
* asynchronously from the main thread for I/O opperations, this is a thread-safe task since the
* video isn't parametered to run on start and it isn't accessing sensible {@link Bukkit} API content.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class TaskAsyncLoadConfigurations extends BukkitRunnable {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
	/**
	* Runs a task that will load and register videos contained in {@link Configuration#getVideosFolder()}
	* to {@link Main#getRegisteredVideos()}.
	*/
	
	@Override
	public void run() {
		
		File[] files = configuration.getVideosFolder().listFiles();
		
		plugin.getRegisteredVideos().clear();
		
		for(File file : files) {
			if(!file.isDirectory()) {
				
				File videoConfiguration = new File(configuration.getVideosFolder() + "/" + FilenameUtils.removeExtension(file.getName()), 
						FilenameUtils.removeExtension(file.getName()) + ".yml");
				
				if(Format.getCompatibleFormats().contains(FilenameUtils.getExtension(file.getName()))) {
					if(!videoConfiguration.exists()) {
						try {
							new Video(videoConfiguration).createConfiguration(file);
						}catch (IOException | IllegalArgumentException | InvalidConfigurationException e) {
							e.printStackTrace();
						}
					}
					
					Video video = new Video(videoConfiguration);
					
					if(video.getFormat().equals("m3u8")) {
						
						File[] decodedFrames = video.getFramesFolder().listFiles();
						
						for(int i = 0; i < decodedFrames.length; i++) {
							
					        File oldfile = decodedFrames[i];
					        File newfile = new File(video.getFramesFolder(), i + ".jpg");

					        oldfile.renameTo(newfile);
						}
					}
					
					if(video.isLoaded()) {
						
						VideoData videoData = video.getVideoData();
						
						Bukkit.getScheduler().runTask(plugin, new Runnable() {
							@Override
							public void run() {
								videoData.loadThumbnail();
							}
						});
													
						if(videoData.getRunOnStartup()) {
							
							Screen screen = new Screen(new VideoInstance(video), new ArrayList<>());
							screen.setRunning(true);
							screen.display();
							
							plugin.getRegisteredScreens().add(screen);
						}
					}else {
						try {
							video.setLoaded(false);
						}catch (IOException | InvalidConfigurationException e) {
							e.printStackTrace();
						}
					}
					plugin.getRegisteredVideos().add(video);
				}
			}
		}
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + ChatColor.GRAY + "Videos successfully registered. (" + plugin.getRegisteredVideos().size() + ")");
        new Notification(NotificationType.VIDEOS_RELOADED, true).send(new Group("mediaplayer.permission.admin"), new String[] { "" }, true);       
	}
}
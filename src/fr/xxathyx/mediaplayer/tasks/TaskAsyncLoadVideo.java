package fr.xxathyx.mediaplayer.tasks;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.scheduler.BukkitRunnable;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.group.Group;
import fr.xxathyx.mediaplayer.image.renderer.ImageRenderer;
import fr.xxathyx.mediaplayer.map.colors.MapColorPalette;
import fr.xxathyx.mediaplayer.notification.Notification;
import fr.xxathyx.mediaplayer.notification.NotificationType;
import fr.xxathyx.mediaplayer.resourcepack.ResourcePack;
import fr.xxathyx.mediaplayer.system.SystemType;
import fr.xxathyx.mediaplayer.util.GIFUtil;
import fr.xxathyx.mediaplayer.util.ImageUtil;
import fr.xxathyx.mediaplayer.video.Video;
import fr.xxathyx.mediaplayer.video.data.VideoData;
import fr.xxathyx.mediaplayer.video.data.cache.Cache;

/** 
* The TaskAsyncLoadVideo class extends {@link BukkitRunnable} has a single constructor
* and method, see {@link #run()}. As named, the task is used to load video, see
* {@link Video#load()}. The task is runned asynchronously from the main thread for I/O
* opperations, this shouldn't cause problems to be runned asynchronously until video
* thumbnail creation but again its widely stable.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class TaskAsyncLoadVideo extends BukkitRunnable {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
		
    private Video video;
    
	/**
	* Constructor for TaskAsyncLoadVideo class, creates an TaskAsyncLoadVideo variable
	* according to a {@link Video}.
	* 
	* @param video The video that is going to be load.
	*/
    
    public TaskAsyncLoadVideo(Video video) {
        this.video = video;
    }
    
	/**
	* Runs a task that will load the {@link Video} passed earlier in the constructor. Loading
	* a video can take time according to the video lenght and their options, see {@link Video#getSize()}.
	* 
	* The estimated time shown is a magnified average, it shouldn't be trusted anymore.
	*/
    
	@SuppressWarnings("deprecation")
	public void run() {
		
		plugin.getTasks().add(getTaskId());
		
        long time = System.currentTimeMillis();
                
        new Notification(NotificationType.VIDEO_PROCESSING_FRAMES_STARTING, true).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName() }, true);
        new Notification(NotificationType.VIDEO_PROCESSING_ESTIMATED_TIME, false).send(new Group("mediaplayer.permission.admin"), new String[] { String.valueOf(Math.round((video.getVideoFile().length()*Math.pow(10, -6)))) }, true);
        
        String framesExtension = video.getFramesExtension();
        int framesCount = video.getFramesFolder().listFiles().length;
                
        VideoData videoData = new VideoData(video); 
        
        if(framesCount < video.getTotalFrames()) {
        	
        	if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.LINUX) || fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.OTHER)) {
        		if(configuration.plugin_force_permissions()) {
                	try {
        				Runtime.getRuntime().exec("chmod -R 777 " + FilenameUtils.separatorsToUnix(plugin.getFfmpeg().getLibraryFile().getAbsolutePath())).waitFor();
        			}catch (InterruptedException | IOException e) {
        				e.printStackTrace();
        			}
        		}
        	}
        	
    		String[] videoCommand = {FilenameUtils.separatorsToUnix(plugin.getFfmpeg().getLibraryFile().getAbsolutePath())
    				, "-hide_banner",
    				"-loglevel", "error",
    				"-i", FilenameUtils.separatorsToUnix(video.getVideoFile().getAbsolutePath()),
    				"-start_number", String.valueOf(framesCount),
    				"-q:v", "0",
    				FilenameUtils.separatorsToUnix(new File(video.getFramesFolder().getPath(), "%d.jpg").getAbsolutePath())};
            
            ProcessBuilder videoProcessBuilder = new ProcessBuilder(videoCommand);
                     
            try {
    			Process process = videoProcessBuilder.inheritIO().start();
    			plugin.getProcess().add(process);
    			process.waitFor();
    		}catch (IOException | InterruptedException e) {
    			e.printStackTrace();
    		}
            
            if(configuration.verify_files_on_load()) {
            	
            	int count = 0;
            	int total = video.getTotalFrames();
            	
                while(count < total) {
                	
                	File previous = new File(video.getFramesFolder(), String.valueOf(count-1) + "." + video.getFramesExtension());
                	File next = new File(video.getFramesFolder(), count + "." + video.getFramesExtension());
                	
                	if(!next.exists() && previous.exists()) {
    					try {
    						ImageIO.write(ImageIO.read(previous), video.getFramesExtension().replace(".", ""), next);
    					}catch (IOException e) {
    						e.printStackTrace();
    					}
                	}
                	count++;
                }
            }
        }
        
        new Notification(NotificationType.VIDEO_PROCESSING_FRAMES_FINISHED, true).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName() }, true);        
        new Notification(NotificationType.VIDEO_PROCESSING_AUDIO_STARTING, false).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName() }, true);
        
		
        if(!video.getFormat().equalsIgnoreCase("m3u8")) {
            if(!video.getFormat().equalsIgnoreCase("gif")) {
            	if(video.hasAudio()) {
            		String[] audioCommand = {FilenameUtils.separatorsToUnix(plugin.getFfmpeg().getLibraryFile().getAbsolutePath()),
            				"-hide_banner",
            				"-loglevel", "error",
            				"-i", FilenameUtils.separatorsToUnix(video.getVideoFile().getAbsolutePath()),
            				"-f", "ogg",
            				"-ab", "192000",
            				"-vn", FilenameUtils.separatorsToUnix(new File(video.getAudioFolder(), video.getAudioFolder().listFiles().length + ".ogg").getAbsolutePath())};
                	
                    ProcessBuilder audioProcessBuilder = new ProcessBuilder(audioCommand);
                    
                    try {
                    	Process process = audioProcessBuilder.inheritIO().start();
		    			plugin.getProcess().add(process);
            			process.waitFor();
            		}catch (IOException | InterruptedException e) {
            			e.printStackTrace();
            		}
                    new ResourcePack().create(video);
            	}
            }else {
            	try {
    				GIFUtil.split(video.getVideoFile(), video.getFramesFolder());
    			}catch (IOException e) {
    				e.printStackTrace();
    			}
            }
        }
        
        new Notification(NotificationType.VIDEO_PROCESSING_AUDIO_FINISHED, true).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName() }, true);
        new Notification(NotificationType.VIDEO_PROCESSING_MINECRAFT_STARTING, false).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName() }, true);
		                
        try {
        	if(!videoData.getThumbnail().exists()) {
        		
    	        videoData.createThumbnail();		        	        
        		
        		Bukkit.getScheduler().runTask(plugin, new Runnable() {
					@Override
					public void run() {
		    			try {
			    			videoData.createMaps();
						}catch (IOException e) {
							e.printStackTrace();
						}
					}
        		});
    			ImageRenderer imageRenderer = new ImageRenderer(ImageIO.read(video.getVideoData().getThumbnail()));
    			imageRenderer.calculateDimensions();
				
				video.setMinecraftWidth(imageRenderer.columns);
    			video.setMinecraftHeight(imageRenderer.lines);
        	}
        	
        	if(configuration.detect_duplicated_frames()) {
    			if(!new File(video.getFramesFolder(), "duplicated.txt").exists()) {
    				
    				new File(video.getFramesFolder(), "duplicated.txt").createNewFile();
    				
    			    FileWriter fileWriter = new FileWriter(video.getFramesFolder().getPath() + "/duplicated.txt", true);
    			    
    			    final double max = configuration.ressemblance_to_skip();
    			    final int total = video.getTotalFrames()-1;
    			    
    			    for(int i = 0; i < total; i++) {
    			    	
    			    	BufferedImage original = ImageIO.read(new File(video.getFramesFolder(), i + framesExtension));
    			    	BufferedImage next = ImageIO.read(new File(video.getFramesFolder(), (i+1) + framesExtension));
    			    			    			    	
    			    	if(ImageUtil.getResemblance(original, next) > max) {
    			    		fileWriter.write((i+1) + "\n");
    			    	}
    			    }
    			    fileWriter.close();	
    			}
        	} 
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
                
        if(!videoData.getRealTimeRendering()) {
        	
        	int total = video.getTotalFrames();
        	int count = videoData.getCacheFolder().listFiles().length;
        	
        	ImageRenderer imageRenderer;
        	
        	while(count < total) {
    			try {
    				imageRenderer = new ImageRenderer(ImageIO.read(new File(video.getFramesFolder(), count + framesExtension)));
    	    		imageRenderer.calculateDimensions();
    	    		imageRenderer.splitImages();
    				
    	    		if(!video.isCacheCompressed()) {
    	    			
        				File file = new File(videoData.getCacheFolder(), String.valueOf(count));
        				file.mkdir();
        	    		
        				for(int j = 0; j < imageRenderer.getBufferedImages().length; j++) {
        					new Cache(new File(file, String.valueOf(j) + ".cache")).createCache(imageRenderer.getBufferedImages()[j]);
        				}
    	    		}else {
    	    			
    	    			FileOutputStream fout = new FileOutputStream(video.getVideoData().getCacheFolder() + "/" + String.valueOf(count) + ".zip");
    	    			ZipOutputStream zout = new ZipOutputStream(fout);
    	    			
    	    			for(int j = 0; j < imageRenderer.getBufferedImages().length; j++) {
    	    			    ZipEntry ze = new ZipEntry(String.valueOf(j) + ".cache");
    	    			    zout.putNextEntry(ze);
    	    			    zout.write(MapColorPalette.convertImage(imageRenderer.getBufferedImages()[j]));
    	    			    zout.closeEntry();
    	    			}
    	    			zout.close();
    	    			fout.close();
    	    		}
    	    		
    				count++;
    			}catch (IOException | InvalidConfigurationException e) {
    				e.printStackTrace();
    			}
        	}
        	
        	if(configuration.verify_files_on_load()) {
        		
            	count = 0;
            	
            	while(count < total) {
        			try {
        				
        				File previous = new File(videoData.getCacheFolder(), String.valueOf(count-1));
        				File next = new File(videoData.getCacheFolder(), String.valueOf(count));
        				
        				if(!next.exists() && previous.exists()) {
        					
            				imageRenderer = new ImageRenderer(ImageIO.read(new File(video.getFramesFolder(), String.valueOf(count-1) + framesExtension)));
            	    		imageRenderer.calculateDimensions();
            	    		imageRenderer.splitImages();
        					
            				for(int j = 0; j < imageRenderer.getBufferedImages().length; j++) {
            					new Cache(new File(next, String.valueOf(j) + ".cache")).createCache(imageRenderer.getBufferedImages()[j]);
            				}        	    		
        				}
        				count++;
        			}catch (IOException | InvalidConfigurationException e) {
        				e.printStackTrace();
        			}
            	}
        	}
    		
        	try {
				video.setLoaded(true);
			}catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
        	
        	if(!videoData.getRealTimeRendering()) {
                if(configuration.frames_delete_on_loaded()) {
            		try {
    					FileUtils.deleteDirectory(video.getFramesFolder());
    					video.getFramesFolder().mkdir();
    				}catch (IOException e) {
    					e.printStackTrace();
    				}
                }
        	}
        	
            if(configuration.video_delete_on_loaded()) {
            	video.getVideoFile().delete();
            }
        }
        
        new Notification(NotificationType.VIDEO_PROCESSING_MINECRAFT_FINISHED, true).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName() }, true);
        new Notification(NotificationType.VIDEO_PROCESSING_FINISHED, false).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName(), String.valueOf(Math.round(((System.currentTimeMillis() - time) / 1000)/60)) }, true);
		
        plugin.getLoadingVideos().remove(video.getName());
        
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + ChatColor.GRAY + video.getName() + " successfully loaded.");
    }
}
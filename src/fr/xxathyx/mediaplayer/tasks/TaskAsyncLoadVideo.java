package fr.xxathyx.mediaplayer.tasks;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.scheduler.BukkitRunnable;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.group.Group;
import fr.xxathyx.mediaplayer.image.ImageRenderer;
import fr.xxathyx.mediaplayer.notification.Notification;
import fr.xxathyx.mediaplayer.notification.NotificationType;
import fr.xxathyx.mediaplayer.util.AWTUtil;
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
	*/
    
	public void run() {
    	
        long time = System.currentTimeMillis();
        
        new Notification(NotificationType.VIDEO_PROCESSING_FRAMES_STARTING, true).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName() });
        new Notification(NotificationType.VIDEO_PROCESSING_ESTIMATED_TIME, false).send(new Group("mediaplayer.permission.admin"), new String[] { String.valueOf(Math.round((video.getVideoFile().length()*Math.pow(10, -6)))) });
        
        String framesExtension = video.getFramesExtension(); 
        
        if(!video.getFormat().equalsIgnoreCase("gif")) {
    		for(int i = video.getFramesFolder().listFiles().length; i < video.getTotalFrames(); i++) {	
    	        try {
    				ImageIO.write(AWTUtil.toBufferedImage(FrameGrab.getFrameFromFile(video.getVideoFile(), i)), "jpg", new File(video.getFramesFolder(), i + ".jpg"));
    			}catch (IOException | JCodecException e) {
    				e.printStackTrace();
    			}
    		}
        }else {
        	try {
				GIFUtil.split(video.getVideoFile(), video.getFramesFolder());
			}catch (IOException e) {
				e.printStackTrace();
			}
        }
		
        new Notification(NotificationType.VIDEO_PROCESSING_FRAMES_FINISHED, true).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName() });        
        new Notification(NotificationType.VIDEO_PROCESSING_AUDIO_STARTING, false).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName() });
        
        if(!video.getFormat().equalsIgnoreCase("gif")) {
        	
    		AudioAttributes audioAttributes = new AudioAttributes();
    		audioAttributes.setCodec("vorbis");
    		audioAttributes.setBitRate(new Integer(128000));
    		audioAttributes.setChannels(new Integer(2));
    		audioAttributes.setSamplingRate(new Integer(44100));
    		
    		EncodingAttributes encodingAttributes = new EncodingAttributes();
    		encodingAttributes.setFormat("ogg");
    		encodingAttributes.setAudioAttributes(audioAttributes);
    		
    		try {
    			new Encoder().encode(video.getVideoFile(), new File(video.getAudioFolder(), video.getAudioFolder().listFiles().length + ".ogg"), encodingAttributes);
    		}catch (IllegalArgumentException | EncoderException e) {
    			e.printStackTrace();
    		}
        }
        		
        new Notification(NotificationType.VIDEO_PROCESSING_AUDIO_FINISHED, true).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName() });
        new Notification(NotificationType.VIDEO_PROCESSING_MINECRAFT_STARTING, false).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName() });
		        
        VideoData videoData = new VideoData(video);
                
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
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
        
        if(!videoData.getRealTimeRendering()) {
        	
        	int total = video.getTotalFrames();
        	int count = videoData.getCacheFolder().listFiles().length;
        	
        	ImageRenderer imageRenderer;
        	
        	while(count < total) {
    			try {
    				File file = new File(videoData.getCacheFolder(), String.valueOf(count));
    				file.mkdir();
    				
    				imageRenderer = new ImageRenderer(ImageIO.read(new File(video.getFramesFolder(), count + framesExtension)));
    	    		imageRenderer.calculateDimensions();
    	    		imageRenderer.splitImages();
    						    		
    				for(int j = 0; j < imageRenderer.getBufferedImages().length; j++) {
    					new Cache(new File(file, String.valueOf(j) + ".cache")).createCache(imageRenderer.getBufferedImages()[j]);
    				}
    				count++;
    			}catch (IOException | InvalidConfigurationException e) {
    				e.printStackTrace();
    			}
        	}
    				
            if(configuration.video_delete_on_loaded()) {
            	video.getVideoFile().delete();
            }
        }
		
        new Notification(NotificationType.VIDEO_PROCESSING_MINECRAFT_FINISHED, true).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName() });
        new Notification(NotificationType.VIDEO_PROCESSING_FINISHED, false).send(new Group("mediaplayer.permission.admin"), new String[] { video.getName(), String.valueOf(Math.round(((System.currentTimeMillis() - time) / 1000)/60)) });
		
        plugin.getLoadingVideos().remove(video.getName());
        
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + ChatColor.GRAY + video.getName() + " successfully loaded.");
    }
}
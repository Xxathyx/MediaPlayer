package fr.xxathyx.mediaplayer.source;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;

import com.google.common.io.Files;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.image.renderer.ImageRenderer;
import fr.xxathyx.mediaplayer.video.Video;
import fr.xxathyx.mediaplayer.video.data.VideoData;

/** 
* The Source class is used to display custom sources on screen, this could be very useful
* making an other plugin that would displaying something else from a regular video files,
* such for instance a WebBrowser. This class has only one constructor.
* 
* <p>See tutorial on Github main page project.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class Source {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private Video video;
	
	private String name;
	
	private double framerate;
	private int frames;
	
	private int width;
	private int height;
	
	private boolean audio;
	private boolean looping;
	private boolean showInformations;
	private boolean showFPS;
	
	private BufferedImage thumbnail;
	
	private int index = 0;
	
	/**
	* Main constructor for Source class, creates an Source variable according to the source settings.
	* 
	* @param name The source name.
	* @param framerate The source frame-rate.
	* @param width The source width.
	* @param height The source height.
	* @param audio Whether the source has audio, even if it has audio, its isn't support currently
	* @param looping Whether the source is gonna loop.
	* @param showInformations Whether informations would be displayed during the running.
	* @param showFPS Whether current frame-rate would be displayed during the running.
	* @param thumbnail The source thumbnail.
	*/
	
	public Source(String name, double framerate, int frames, int width, int height, boolean audio, boolean looping, boolean showInformations, boolean showFPS, BufferedImage thumbnail) {
		this.name = name;
		this.framerate = framerate;
		this.frames = frames;
		this.width = width;
		this.height = height;
		this.audio = audio;
		this.looping = looping;
		this.showInformations = showInformations;
		this.showFPS = showFPS;
		this.thumbnail = thumbnail;
	}
	
	/** 
	* Creates a {@link Source} variable, this method shall be called after passing arguments in the
	* earlier constructor.
	*
	* @return The source itself.
	*/
	
	public Source create() {
		
		Video videoSource = new Video(this);
		video = videoSource;
		
		try {
			videoSource.createConfiguration();
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		plugin.getRegisteredVideos().add(videoSource);
		return this;
	}
	
	/** 
	* Loads a {@link Source}, this method shall be called after the creates one, see {@link #create()}.
	*/
	
	public void load() {
		
		VideoData videoData = video.getVideoData();
		
    	if(!videoData.getThumbnail().exists()) {
    		
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
    		
	        try {
				createThumbnail();
				
				ImageRenderer imageRenderer = new ImageRenderer(ImageIO.read(video.getVideoData().getThumbnail()));
				imageRenderer.calculateDimensions();
				
				video.setMinecraftWidth(imageRenderer.columns);
				video.setMinecraftHeight(imageRenderer.lines);
				
			}catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}	        	        
    	}
	}
	
	/**
	* Creates the source thumbnail, according to earlier passed {@link BufferedImage} in the constructor.
	* 
	* <p>The thumbnail appearance changes if the source thumbnail file change.
	*/
	
	public void createThumbnail() throws IOException {
		
		BufferedImage frame = thumbnail;
		
		Image background = ImageIO.read(Main.class.getResource("resources/background.png"));	
		Image play = ImageIO.read(Main.class.getResource("resources/play.png"));
		
		BufferedImage bufferedPlay = (BufferedImage) play;
		BufferedImage bufferedBackground = (BufferedImage) background;
		
		Image resizedBackground = background.getScaledInstance((int) Math.round(bufferedBackground.getWidth()*((double) frame.getWidth()/bufferedBackground.getWidth())),
				(int) Math.round(bufferedBackground.getHeight()*((double) frame.getHeight()/bufferedBackground.getHeight())), Image.SCALE_DEFAULT);
		
		frame.getGraphics().drawImage(resizedBackground, 0, 0, null);
		frame.getGraphics().drawImage(play, (frame.getWidth()/2)-bufferedPlay.getWidth()/2, (frame.getHeight()/2)-bufferedPlay.getHeight()/2, null);
		
		ImageIO.write(frame, video.getFramesExtension().replace(".", ""), video.getVideoData().getThumbnail());
	}
	
	/**
	* Adds a new frame to be displayed on the screen, using a {@link BufferedFrame} instance.
	* 
	* @param frame The {@link BufferedFrame} to be added
	*/
	
	public void add(BufferedImage frame) {
		try {
			ImageIO.write(frame, "jpg", new File(video.getFramesFolder(), String.valueOf(index) + ".jpg"));
			index++;
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Adds a new frame to be displayed on the screen, using a frame {@link File}.
	* 
	* @param file The frame-file to be added
	*/
	
	public void add(File frame) {
		try {
			Files.copy(frame, new File(video.getFramesFolder(), index + "." + FilenameUtils.getExtension(frame.getName())));
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Gets the source as a {@link Video} variable.
	* 
	* <p>All {@link Video} fonctionnality couldn't work correctly
	* 
	* @return The source as a {@link Video} variable.
	*/
	
	public Video getVideo() {
		return video;
	}
	
	/**
	* Gets the source name passed earlier in the constructor.
	* 
	* @return The source source name.
	*/
	
	public String getName() {
		return name;
	}
	
	/**
	* Gets the source frame-rate.
	* 
	* @return The source frame-rate.
	*/
	
	public double getFramerate() {
		return framerate;
	}
	
	/**
	* Gets the total source frames count, the amount is passed earlier in the constructor.
	* 
	* @return The total source frames count.
	*/
	
	public int getTotalFrames() {
		return frames;
	}
	
	/**
	* Gets the source width.
	* 
	* @return The source width
	*/
	
	public int getWidth() {
		return width;
	}
	
	/**
	* Gets the source height.
	* 
	* @return The source height
	*/
	
	public int getHeight() {
		return height;
	}
	
	/**
	* Gets whether the source has audio.
	* 
	* @return Whether the source has audio.
	*/
	
	public boolean hasAudio() {
		return audio;
	}
	
	/**
	* Gets whether the source is loooing.
	* 
	* @return Whether the source is loooing.
	*/
	
	public boolean isLooping() {
		return looping;
	}
	
	/**
	* Gets whether informations would be displayed during the running.
	* 
	* @return Whether informations would be displayed during the running.
	*/
	
	public boolean showInformations() {
		return showInformations;
	}
	
	/**
	* Gets whether current frame-rate would be displayed during the running.
	* 
	* @return Whether current frame-rate would be displayed during the running.
	*/
	
	public boolean showFPS() {
		return showFPS;
	}
	
	/**
	* Gets the source thumbnail as a {@link BufferedImage} variable.
	* 
	* @return The source thumbnail as a {@link BufferedImage} variable.
	*/
	
	public BufferedImage getTumbnail() {
		return thumbnail;
	}
}
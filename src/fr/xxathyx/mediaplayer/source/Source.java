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
	
	public Source create() {
		
		Video videoSource = new Video(this);
		this.video = videoSource;
		
		try {
			videoSource.createConfiguration();
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		plugin.getRegisteredVideos().add(videoSource);
		return this;
	}
	
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
	
	public void add(BufferedImage frame) {
		try {
			ImageIO.write(frame, "jpg", new File(video.getFramesFolder(), String.valueOf(index) + ".jpg"));
			index++;
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void add(File frame) {
		try {
			Files.copy(frame, new File(video.getFramesFolder(), index + "." + FilenameUtils.getExtension(frame.getName())));
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Video getVideo() {
		return video;
	}
	
	public String getName() {
		return name;
	}
	
	public double getFramerate() {
		return framerate;
	}
	
	public int getTotalFrames() {
		return frames;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean hasAudio() {
		return audio;
	}
	
	public boolean isLooping() {
		return looping;
	}
	
	public boolean showInformations() {
		return showInformations;
	}
	
	public boolean showFPS() {
		return showFPS;
	}
	
	public BufferedImage getTumbnail() {
		return thumbnail;
	}
}
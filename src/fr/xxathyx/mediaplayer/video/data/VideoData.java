package fr.xxathyx.mediaplayer.video.data;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.image.helpers.ImageHelper;
import fr.xxathyx.mediaplayer.image.renderer.ImageRenderer;
import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadConfigurations;
import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadVideo;
import fr.xxathyx.mediaplayer.util.ImageUtil;
import fr.xxathyx.mediaplayer.video.Video;
import fr.xxathyx.mediaplayer.video.data.cache.Cache;
import fr.xxathyx.mediaplayer.video.data.thumbnail.Thumbnail;

/** 
* The VideoData class is an essential part for completing {@link Video} and the video
* feature, it is managing all content that is related to Minecraft. Does have one
* constructor.
* 
* <p> Can be called from {@link Video#getVideoData()}.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class VideoData {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private Video video;
	private ArrayList<Integer> duplicated = new ArrayList<>();	
	
	/**
	* Constructor for VideoData class, creates an VideoData variable according
	* to a {@link Video}.
	* 
	* <p> <strong>Note: </strong>Every time the class instantiated the duplicated frames
	* list is loaded, so do not call {@link Video#getVideoData()} every time for
	* no reason, instead store a new {@link VideoData} variable to avoid performances
	* issues. 
	* 
	* <p>This is the same as calling {@link Video#getVideoData()};
	* 
	* @param video The video that the data is from.
	*/
	
	public VideoData(Video video) {
		this.video = video;
	    if(new File(video.getFramesFolder().getPath() + "/duplicated.txt").exists()) 
	    	duplicated = (ArrayList<Integer>) bufferReaderToArrayList(video.getFramesFolder().getPath() + "/duplicated.txt");
	}
	
	/**
	* Creates the dedicated maps according to the main video thumbnail. The
	* method is used in {@link TaskAsyncLoadVideo} while loading Minecraft content.
	* 
	* @throws IOException When failed or interrupted I/O operations occurs.
	*/
	
	public void createMaps() throws IOException {
		
		ImageRenderer imageRenderer = new ImageRenderer(ImageIO.read(getThumbnail()));		
		imageRenderer.createMaps(Bukkit.getWorlds().get(0));
		
		new Thumbnail(getThumbnail()).create(imageRenderer);
	}
	
	/**
	* Creates the main video thumbnail. The method is used in {@link TaskAsyncLoadVideo} while loading
	* Minecraft content.
	* 
	* <p>The thumbnail appearance changes if the video is restricted, see {@link Video#isRestricted()}.
	* 
	* @throws IOException When failed or interrupted I/O operations occurs.
	*/
	
	public void createThumbnail() throws IOException {
			
		int random = (int) Math.floor(Math.random()*((video.getTotalFrames()-1)-1+1)+1);
		
		File file = new File(video.getFramesFolder(), random + video.getFramesExtension());
				
		BufferedImage frame = ImageIO.read(file);
		
		Image background = ImageIO.read(Main.class.getResource("resources/background.png"));	
		Image play = ImageIO.read(Main.class.getResource("resources/play.png"));
				
		if(video.isRestricted()) {
			play = ImageIO.read(Main.class.getResource("resources/restricted.png"));
			ImageUtil.blur(frame);
		}
		
		BufferedImage bufferedPlay = (BufferedImage) play;
		BufferedImage bufferedBackground = (BufferedImage) background;
		
		Image resizedBackground = background.getScaledInstance((int) Math.round(bufferedBackground.getWidth()*((double) frame.getWidth()/bufferedBackground.getWidth())),
				(int) Math.round(bufferedBackground.getHeight()*((double) frame.getHeight()/bufferedBackground.getHeight())), Image.SCALE_DEFAULT);
		
		frame.getGraphics().drawImage(resizedBackground, 0, 0, null);
		frame.getGraphics().drawImage(play, (frame.getWidth()/2)-bufferedPlay.getWidth()/2, (frame.getHeight()/2)-bufferedPlay.getHeight()/2, null);
		
		ImageIO.write(frame, video.getFramesExtension().replace(".", ""), getThumbnail());
	}
	
	/**
	* Loads the {@link #getThumbnail()} in order to be again displayed in Minecraft, this method
	* is called on {@link TaskAsyncLoadConfigurations} if the video is loaded.
	* 
	* <p> <strong>Note: </strong>The thumbnail is re-rendered again according to their
	* {@link Thumbnail#getFile()}, so if the same named file does change, the changes
	* will be applied.
	*/
	
	public void loadThumbnail() {
		try {
			if(getMapsFolder().listFiles().length > 0) {
				
				Thumbnail thumbnail = getMaps();
				BufferedImage bufferedImage = ImageHelper.getImage(thumbnail.getPath());
				
				ImageRenderer imageRenderer = new ImageRenderer(bufferedImage);
				
				imageRenderer.calculateDimensions();
				imageRenderer.splitImages();
				
				MapView map;
				
				for(int i = 0; i < imageRenderer.getBufferedImages().length; i++) {
					map = plugin.getMapUtil().getMapView(thumbnail.getIds().get(i));
					map = new ImageRenderer(imageRenderer.getBufferedImages()[i]).resetRenderers(map);
					
					map.setScale(MapView.Scale.FARTHEST);
					if(!plugin.isLegacy()) map.setUnlimitedTracking(false);
					map.addRenderer(new ImageRenderer(imageRenderer.getBufferedImages()[i]));
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Gets the video that the data is from, the video variable is passed
	* earlier in the constructor.
	* 
	* @return The video that the data is from.
	*/
	
	public Video getVideo() {
		return video;
	}
	
	/**
	* Gets whether the video should be rendered in realtime or use a {@link Cache} system.
	* 
	* <p> <strong>Note: </strong>The realtime rendering shall be used to receive and render
	* frames from a live stream for instance, or if the video is to long to be stored with
	* the {@link Cache} system. Rendering and sending frames in realtime should be done
	* asynchronously from the main one, but considering this option to not use a {@link Cache}
	* system could result in a slight performance decrease.
	*/
	
	public boolean getRealTimeRendering() {
		return video.getConfigFile().getBoolean("video.real-time-rendering");
	}
	
	/**
	* Gets whether the video skipping frames would be only duplicated ones.
	* 
	* @return Whether the video skipping frames would be only duplicated ones.
	*/
	
	public boolean getSkipDuplicatedFrames() {
		return video.getConfigFile().getBoolean("video.skip-duplicated-frames");
	}
	
	/**
	* Gets whether the video informations will be sent to viewers.
	* 
	* @return Whether the video informations will be sent to viewers.
	*/
	
	public boolean getShowInformations() {
		return video.getConfigFile().getBoolean("video.show-informations");
	}
	
	/**
	* Gets whether the sent informations would contains the amount of frame
	* per second.
	* 
	* @return Whether the sent informations would contains the amount of frame
	* per second.
	*/
	
	public boolean getShowFPS() {
		return video.getConfigFile().getBoolean("video.show-fps");
	}
	
	/**
	* Gets whether a video should be launched on the plugin statup.
	* 
	* @return Whether a video should be launched on the plugin statup.
	*/
	
	public boolean getRunOnStartup() {
		return video.getConfigFile().getBoolean("video.run-on-startup");
	}
	
	/**
	* Gets the Minecraft video width in blocks-count.
	* 
	* @return The Minecraft video width in blocks-count.
	*/
	
	public int getMinecraftWidth() {
		return video.getConfigFile().getInt("video.minecraft-width");
	}
	
	/**
	* Gets the Minecraft video height in blocks-count.
	* 
	* @return The Minecraft video height in blocks-count.
	*/
	
	public int getMinecraftHeight() {
		return video.getConfigFile().getInt("video.minecraft-height");
	}
	
	/**
	* Gets an {@link ArrayList} containing duplicated frames.
	* 
	* @return The duplicated frames.
	*/
	
	public ArrayList<Integer> getDuplicated() {
		return duplicated;
	}
	
	/**
	* Gets the video main thumbnail, corresponding to the dedicated video maps.
	* 
	* @return The main video thumbnail.
	*/
	
	public Thumbnail getMaps() {
		return new Thumbnail(getMapsFolder().listFiles()[0]);
	}
	
	/**
	* Gets the folder containing the dedicated maps for the video main thumbnail.
	* 
	* @return The folder containing the dedicated maps for the video main thumbnail.
	*/
	
	public File getMapsFolder() {
		return new File(video.getDataFolder() + "/maps/");
	}
	
	/**
	* Gets the folder containing the maps cache.
	* 
	* @return The folder containing the maps cache.
	*/
	
	public File getCacheFolder() {
		return new File(video.getDataFolder() + "/cache/");
	}
	
	/**
	* Gets the folder containing the audio ressource packs.
	* 
	* @return The folder containing the audio ressource packs.
	*/
	
	public File getResourcePacksFolder() {
		return new File(video.getDataFolder() + "/resourcepacks/");
	}
	
	/**
	* Gets the video main thumbnail file.
	*
	* <p>Expect it to return an Portable Network Graphics (png) file for .GIF videos.
	* 
	* @return The video main thumbnail file.
	*/
	
	public File getThumbnail() {
		return new File(video.getFile().getParent(), "thumbnail" + video.getFramesExtension());
	}
	
	/**
	* Converts a List from {@link BufferedReader} into a ArrayList containing all duplicated frames.
	* 
	* @return An ArrayList containing all duplicated frames
	*/
	
    public List<Integer> bufferReaderToArrayList(String path) {
        return bufferReaderToList(path, new ArrayList<>());
    }
    
	/**
	* Gets a List containing all duplicated frames, readed in the duplicated text-document file.
	* 
	* @return An List containing all duplicated frames
	*/
    
    public List<Integer> bufferReaderToList(String path, List<Integer> list) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            
            String line;
            
            while((line = bufferedReader.readLine()) != null) {
                list.add(Integer.parseInt(line));
            }
            bufferedReader.close();
        }catch( IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
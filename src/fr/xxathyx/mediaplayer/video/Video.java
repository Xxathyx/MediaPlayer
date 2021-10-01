package fr.xxathyx.mediaplayer.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.interfaces.Interfaces;
import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadConfigurations;
import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadVideo;
import fr.xxathyx.mediaplayer.util.Format;
import fr.xxathyx.mediaplayer.video.data.VideoData;
import fr.xxathyx.mediaplayer.video.data.cache.Cache;

/** 
* The Video class is essential in the good functioning of things, its used
* everywhere across the plugin, it constitue a core part. A video instance
* bridge the gap between the original video file and the ressource. The video
* class has three constructors.
* 
* Registered videos can be found here {@link Main#getRegisteredVideos()}.
* 
* <p>{@link Video} corresponds to an video configuration file, for each video
* that can be registered there have an configuration that is used as we can see
* here {@link TaskAsyncLoadConfiguration}. In the YAML configuration-file are
* related all informations about the video itself.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class Video {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
	
	private File file;
	private FileConfiguration fileconfiguration;
	
	/**
	* Constructor for Video class, creates an Video variable according
	* to a live-stream {@link URL} url.
	* 
	* @param name The live-stream direct url.
	*/
	
	public Video(URL url) {
		
	}
	
	/**
	* Constructor for Video class, creates an Video variable according
	* to a {@link String} video name.
	* 
	* @param name The video name without extension.
	*/
	
	public Video(String name) {
		this.file = new File(plugin.getDataFolder(), "/videos/" + name + "/" + name + ".yml");
	}
	
	/**
	* Constructor for Video class, creates an Video variable according
	* to a video index {@link Integer}.
	* 
	* <p>Does search for that index in {@link Main#getRegisteredVideos()}.
	* 
	* @param index The video index.
	*/
	
	public Video(int index) {
		file = plugin.getRegisteredVideos().get(index).getFile();
	}
	
	/**
	* Constructor for Video class, creates an Video variable according
	* to a video configuration-file {@link File}.
	* 
	<p>Passed {@link File} can be non-existent.
	* 
	* @param file The video configuration-file.
	*/
	
	public Video(File file) {
		this.file = file;
	}
	
	/**
	* Creates a video configuration-file accoding to a original video file.
	* 
	* @param file The original video file.
	* 
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void createConfiguration(File videoFile) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		LocalDateTime date = LocalDateTime.now();
		String format = FilenameUtils.getExtension(videoFile.getName());
				
		int originalWidth = 0;
		int originalHeight = 0;
		
		double framerate = 0;
		double duration = 0;
		
		int frames = 0;
				
		if(Format.getCompatibleFormats().contains(format)) {
			
			if(format.equalsIgnoreCase("mp4") || format.equalsIgnoreCase("mov") || format.equalsIgnoreCase("m4v")) {
				
				DemuxerTrack videoTrack =  MP4Demuxer.createMP4Demuxer(NIOUtils.readableFileChannel(videoFile.getAbsolutePath())).getVideoTrack();
				
		        originalWidth = videoTrack.getMeta().getVideoCodecMeta().getSize().getWidth();
		        originalHeight = videoTrack.getMeta().getVideoCodecMeta().getSize().getHeight();
		        
		        framerate = Math.round(videoTrack.getMeta().getTotalFrames()/videoTrack.getMeta().getTotalDuration());
		        
		        duration = videoTrack.getMeta().getTotalDuration();
		        frames = videoTrack.getMeta().getTotalFrames();
			}
			
			if(format.equalsIgnoreCase("gif")) {
				
			    ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
			    reader.setInput(ImageIO.createImageInputStream(new FileInputStream(videoFile)), false);
			    
			    BufferedImage bufferedImage = reader.read(0);
			    
			    originalWidth = bufferedImage.getWidth();
			    originalHeight = bufferedImage.getHeight();
			    
			    frames = reader.getNumImages(true);
			    framerate = 20;
			    
			    duration = frames*framerate;
			}
		}
        
		fileconfiguration.set("video.name", FilenameUtils.removeExtension(file.getName()));
		fileconfiguration.set("video.description", "&a/video " + FilenameUtils.removeExtension(file.getName()) + " set description <message>");
		fileconfiguration.set("video.file-video-path", videoFile.getPath());
		fileconfiguration.set("video.enable-audio", true);
		fileconfiguration.set("video.file-audio-path", getAudioFolder().getPath());
		fileconfiguration.set("video.audio-volume", 1.0);
		fileconfiguration.set("video.frames-folder", getFramesFolder().getPath());
		fileconfiguration.set("video.frame-rate", framerate);
		fileconfiguration.set("video.frames", frames);
		fileconfiguration.set("video.format", FilenameUtils.getExtension(videoFile.getName()));
		fileconfiguration.set("video.width", originalWidth);
		fileconfiguration.set("video.height", originalHeight);
		fileconfiguration.set("video.duration", decimalFormat.format(duration/3600) + "h");
		fileconfiguration.set("video.speed", 1.0);
		fileconfiguration.set("video.size", decimalFormat.format(videoFile.length()*Math.pow(10, -6)) + " Mo");
		fileconfiguration.set("video.age-limit", false);
		fileconfiguration.set("video.looping", false);
		fileconfiguration.set("video.creation", date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear() + " " + date.getHour() + ":" + date.getMinute() + ":" + date.getSecond());
		fileconfiguration.set("video.data-folder", getDataFolder().getPath());
		fileconfiguration.set("video.real-time-rendering", false);
		fileconfiguration.set("video.skip-duplicated-frames", false);
		fileconfiguration.set("video.show-informations", true);
		fileconfiguration.set("video.show-fps", true);
		fileconfiguration.set("video.run-on-startup", false);
		fileconfiguration.set("video.minecraft-width", 0);
		fileconfiguration.set("video.minecraft-height", 0);
		fileconfiguration.set("video.views", 0);
		fileconfiguration.set("video.index", plugin.getRegisteredVideos().size());
		
		fileconfiguration.save(file);
		
		getFramesFolder().mkdir();
		getAudioFolder().mkdir();
		getDataFolder().mkdir();
				
		new File(file.getParent() + "/data/maps/").mkdir();
		new File(file.getParent() + "/data/cache/").mkdir();
		new File(file.getParent() + "/data/resourcepacks/").mkdir();
	}
	
	/**
	* Sets whether the video audio will be played.
	* 
	* @param enableaudio Whether the video audio will be played.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setEnableAudio(boolean enableaudio) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.enable-audio", enableaudio);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets the video volume for being played.
	* 
	* <p> <strong>Note: </strong>The volume parameter is defined from 0 to 1.0,
	* with 0 lowest and 1.0 highest. However if the volume value is above 1.0, then the
	* hearing sound radius will be increased.
	* 
	* @param volume The volume parameter from 0 to 1.0 and above 1.0 for higher radius.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setVolume(double volume) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.audio-volume", volume);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets the displayed video description.
	* 
	* <p>The video description is by default displayed in the showing informations while playing
	* the video, and the {@link Interfaces#getVideos()} interface.
	* 
	* @param description The video description.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setDescription(String description) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.description", description);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets the video displaying speed.
	* 
	* <p> <strong>Note: </strong>A video reload is necessary to see these changes
	* for the audio part.
	* 
	* @param speed The video displaying speed.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setSpeed(double speed) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.speed", speed);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets the video in restricted mode, the thumbnail appearance will change.
	* 
	* <p> <strong>Note: </strong>A video reload is necessary to see these changes.
	* 
	* @param restricted Whether the video would be in restricted method.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setRestricted(boolean restricted) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.age-limit", restricted);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets whether the video will be repeated when it ends.
	* 
	* @param looping Whether the video will be repeated when it ends.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setLooping(boolean looping) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.looping", looping);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets whether the video should be rendered in realtime or use a {@link Cache} system.
	* 
	* <p> <strong>Note: </strong>The realtime rendering shall be used to receive and render
	* frames from a live stream for instance, or if the video is to long to be stored with
	* the {@link Cache} system. Rendering and sending frames in realtime should be done
	* asynchronously from the main one, but considering this option to not use a {@link Cache}
	* system could result in a slight performance decrease.
	* 
	* @param realtimerendering Whether the video should be rendered in realtime.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setRealTimeRendering(boolean realtimerendering) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.real-time-rendering", realtimerendering);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets whether the video skipping frames would be only duplicated ones.
	* 
	* @param skipduplicatedframes Whether the video skipping frames would be only duplicated ones.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setSkipDuplicatedFrames(boolean skipduplicatedframes) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.skip-duplicated-frames", skipduplicatedframes);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets whether the video informations will be sent to viewers.
	* 
	* @param showinformations Vhether the video informations will be sent to viewers.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setShowInformations(boolean showinformations) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.show-informations", showinformations);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets whether the sent informations would contains the amount of frame
	* per second.
	* 
	* @param showfps whether the sent informations would contains the amount of frame
	* per second.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setShowFPS(boolean showfps) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.show-fps", showfps);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets whether a video should be launched on the plugin statup.
	* 
	* @param runonstartup Whether a video should be launched on the plugin statup.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setRunOnStartup(boolean runonstartup) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.run-on-startup", runonstartup);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets the Minecraft video width in blocks-count.
	* 
	* @param minecraftWidth The Minecraft video width in blocks-count.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setMinecraftWidth(int minecraftWidth) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.minecraft-width", minecraftWidth);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets the Minecraft video height in blocks-count.
	* 
	* @param minecraftHeight The Minecraft video height in blocks-count.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setMinecraftHeight(int minecraftHeight) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.minecraft-height", minecraftHeight);
		fileconfiguration.save(file);
	}
	
	/**
	* Sets the total amount of views, views corresponds to The number of times a video
	* has been viewed
	* 
	* @param views The number of views.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void setViews(int views) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set("video.views", views);
		fileconfiguration.save(file);
	}
	
	/**
	* Runs a task that will load the {@link Video}. Loading a video can take time according to
	* the video lenght and their options, see {@link #getSize()}.
	* 
	* <p>This is the same as calling {@link TaskAsyncLoadVideo}. 
	*/
	public void load() {
		plugin.getLoadingVideos().add(getName());
		new TaskAsyncLoadVideo(this).runTaskAsynchronously(plugin);
	}
	
	/**
	* Unloads a loaded video.
	*  
	* <p> <strong>Note: </strong> This method shall be called asynchronously from the main
	* thread, since its thread-safe and performs a lot of I/O opperations.
	* 
	* @throws IOException When failed or interrupted I/O operations occurs.
	*/
	
	public void unload() throws IOException {
		
		getVideoData().getThumbnail().delete();
		
		FileUtils.deleteDirectory(getFramesFolder());
		FileUtils.deleteDirectory(getDataFolder());
		FileUtils.deleteDirectory(getAudioFolder());

		getFramesFolder().mkdir();
		getDataFolder().mkdir();
		getAudioFolder().mkdir();
				
		new File(file.getParent() + "/data/cache/").mkdir();
		new File(file.getParent() + "/data/maps/").mkdir();
		new File(file.getParent() + "/data/resourcepacks/").mkdir();
		
	    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + ChatColor.GRAY + getName() + " successfully unloaded.");
	}
	
	/**
	* Deletes a video and her video folder.
	*  
	* <p> <strong>Note: </strong> This method shall be called asynchronously from the main
	* thread, since its thread-safe and performs a lot of I/O opperations.
	* 
	* @throws IOException When failed or interrupted I/O operations occurs.
	*/
	
	public void delete() throws IOException {
		
		getVideoFile().delete();
		FileUtils.deleteDirectory(file.getParentFile());
		
		new TaskAsyncLoadConfigurations().runTaskAsynchronously(plugin);
	}
	
	/**
	* Gets whether the video format is compatible.
	* 
	* @return Whether the video format is compatible
	*/
	
	public boolean isCompatible() {
		return Format.getCompatibleFormats().contains(getFormat());
	}
	
	/**
	* Gets whether the video is loaded.
	*  
	* <p> <strong>Note: </strong> This method shall be called asynchronously from the main
	* thread, since its thread-safe and performs a lot of I/O opperations.
	* 
	* @return Whether the video is loaded.
	*/
	
	public boolean isLoaded() {
		
		if(getFramesFolder().listFiles().length >= getTotalFrames()) {
			
			if(getFormat().equalsIgnoreCase("gif")) {
				if(!getVideoData().getRealTimeRendering()) {
					if(getVideoData().getCacheFolder().listFiles().length >= getTotalFrames()) {
						return true;
					}return false;
				}return true;
			}
			
			if(getAudioFolder().listFiles().length > 0) {
				if(!getVideoData().getRealTimeRendering()) {
					if(getVideoData().getCacheFolder().listFiles().length >= getTotalFrames()) {
						return true;
					}return false;
				}return true;
			}
		}
		return false;
	}
	
	/**
	* Gets whether two videos are equals.
	*  
	* <p> <strong>Note: </strong> This method checks if two videos are equals
	* using their file lenght, the video names don't matter.
	* 
	* @return Whether two videos are equals.
	*/
	
	public boolean equals(Video video) {
		if(getVideoFile().length() == video.getVideoFile().length()) {
			return true;
		}
		return false;
	}
	
	/**
	* Gets the folder containing all video frames.
	*  
	* <p>The folder is empty until the video is loaded.
	* 
	* @return The folder containing all video frames.
	*/
	
	public File getFramesFolder() {
		return new File(file.getParent() + "/frames/");
	}
	
	
	/**
	* Gets the folder containing all audio files.
	*  
	* <p>The folder is empty until the video is loaded.
	* 
	* @return The folder containing all audio files.
	*/
	
	public File getAudioFolder() {
		return new File(file.getParent() + "/audio/");
	}
	
	/**
	* Gets the folder containing all related Minecraft data about the video.
	*  
	* <p>The folder is empty until the video is loaded.
	* 
	* @return The folder ccontaining all related Minecraft video data.
	*/
	
	public File getDataFolder() {
		return new File(file.getParent() + "/data/");
	}
	
	/**
	* Gets the video name without extension.
	* 
	* @return The video name without extension.
	*/
	
	public String getName() {
		return getConfigFile().getString("video.name");
	}
	
	/**
	* Gets the video description with the alternate color codes.
	* 
	* @return The video description with the alternate color codes.
	*/
	
	public String getDescription() {
		return ChatColor.translateAlternateColorCodes('&', getConfigFile().getString("video.description"));
	}
	
	/**
	* Get the original video file.
	* 
	* <p>The original video file path is stored in the video configuration-file.
	* 
	* @return The original video file.
	*/
	
	public File getVideoFile() {
		return new File(getConfigFile().getString("video.file-video-path"));
	}
	
	/**
	* Gets whether the video have audio enabled.
	* 
	* @return Whether the video have audio enabled.
	*/
	
	public boolean isAudioEnabled() {
		return getConfigFile().getBoolean("video-enable-audio");
	}
		
	/**
	* Gets an {@link List} of the audio files contained in the audio folder, see {@link getAudioFolder}.
	* 
	* <p>The returned List is empty until the video is loaded.
	* 
	* @return The folder containing audio files.
	*/
	
	public List<File> getAudioFiles() {
		return Arrays.asList(getAudioFolder().listFiles());
	}
	
	/**
	* Gets the video audio intensity. 
	*  
	* @return The video audio intensity
	*/
	
	public double getVolume() {
		return getConfigFile().getDouble("video.audio-volume");
	}
	
	/**
	* Gets the video number of frames per second.
	*  
	* @return The video number of frames per second.
	*/
	
	public int getFrameRate() {
		return getConfigFile().getInt("video.frame-rate");
	}
	
	/**
	* Gets the total number of frames contained in the video.
	*  
	* @return The total number of frames contained in the video.
	*/
	
	public int getTotalFrames() {
		return getConfigFile().getInt("video.frames");
	}
	
	/**
	* Gets the video width.
	*  
	* @return The video width.
	*/
	
	public int getWidth() {
		return getConfigFile().getInt("video.width");
	}
	
	/**
	* Gets the video height.
	*  
	* @return The video heigt.
	*/
	
	public int getHeight() {
		return getConfigFile().getInt("video.height");
	}
	
	/**
	* Gets the video duration in hours.
	*  
	* @return The video duration in hours.
	*/
	
	public String getDuration() {
		return getConfigFile().getString("video.duration");
	}
		
	/**
	* Gets the write speed of the device, about how many bytes are wrote within a second,
	* for a {@link Video} configuration-file for instance.
	* 
	* <p>This method is used to know how long load a video will take.
	* 
	* @return The write speed of the device.
	*/
	
	public double getSpeed() {
		return getConfigFile().getInt("video.speed");
	}
	
	/**
	* Gets the video format as a {@link String} without the dot after the name.
	*  
	* @return The video format.
	*/
	
	public String getFormat() {
		return getConfigFile().getString("video.format");
	}
	
	/**
	* Gets the video length in megabyte as a {@link String}.
	* 
	* <p> <strong>Note: </strong>The video length value can be retrieve using
	* the following calculations: getVideoFile().length()*Math.pow(10, -6)
	* 
	* @return The video length in megabyte.
	*/
	
	public String getSize() {
		return getConfigFile().getString("video.size");
	}
	
	/**
	* Gets whether the video is in restricted mode.
	* 
	* <p>In restricted mode, the thumbnail appear different.
	* 
	* @return The video length in megabyte.
	*/
	
	public boolean isRestricted() {
		return getConfigFile().getBoolean("video.age-limit");
	}
	
	/**
	* Gets whether the video will be repeated when it ends.
	* 
	* @return Whether the video will be repeated when it ends.
	*/
	
	public boolean isLoopping() {
		return getConfigFile().getBoolean("video.looping");
	}
	
	/**
	* Gets creation date of the video as a single {@link String} using
	* the following european* schema : dd-mm-yyyyy hh:mm:ss.
	* 
	* @return The video creation date.
	*/
	
	public String getCreation() {
		return getConfigFile().getString("video.creation");
	}
	
	/**
	* Gets the total number of times the video has been viewed.
	* 
	* @return The total number of times the video has been viewed.
	*/
	
	public int getViews() {
		return getConfigFile().getInt("video.views");
	}
	
	/**
	* Gets the video index created has.
	* 
	* <p> <strong>Note: </strong>The video index is the one used at
	* the time of the configuration-file creation, the index does not
	* change if a video is deleted in later, it is not reliable to use
	* it as a video valid index.
	* 
	* @return The video index created has.
	*/
	
	public int getIndex() {
		return getConfigFile().getInt("video.index");
	}
	
	/**
	* Gets the video frames extension.
	* 
	* <p> <strong>Note: </strong>The frames extension for GIF files aren't
	* the same as for common videos files. Portable Network Graphics (png)
	* are used for GIF instead of the default JPG extension for videos. 
	* 
	* @return The video frames extension.
	*/
	
	public String getFramesExtension() {
		if(getFormat().equalsIgnoreCase("gif")) return ".png";
		return ".jpg";
	}
	
	/**
	* Gets a {@link String} status short message on whether a video is loaded.
	* 
	* <p>This method is used for {@link Interfaces#getVideos()}.
	* 
	* @return A status short message on whether a video is loaded.
	*/
	
	public String getStatus() {
		if(isLoaded()) return ChatColor.GREEN + "CHARGEE";
		return ChatColor.RED + "PAS CHARGEE";
	}
	
	/**
	* Gets whether a {@link VideoData} has enought space to be load on the partition.
	* 
	* @return Whether the device has enought space to load a video on.
	*/
	
	public boolean hasEnoughtSpace() {

		long required = getVideoFile().length()*3;
		if(!getVideoData().getRealTimeRendering()) required = required + ((Math.round((getWidth()*getHeight())/128))*16384*getTotalFrames()); 
		
		if(getVideoFile().getUsableSpace() > required) return true;
		return false;
	}
	
	/**
	* Gets a {@link VideoData} instance for the current video where its called from.
	* 
	* <p> <strong>Note: </strong>Every time the class instantiated the duplicated frames
	* list is loaded, so do not call {@link Video#getVideoData()} every time for
	* no reason, instead store a new {@link VideoData} variable to avoid performances
	* issues. 
	* 
	* @return A video instance for the current video where its called from.
	*/
	
	public VideoData getVideoData() {
		return new VideoData(this);
	}
	
    /**
     * Gets an FileConfiguration instance of the video configuration-file,
     * wich grant access to the configuration data.
     * 
     * <p> This method isn't usable directly, its used on class getters method
     * such as {@link #getName()} to access data.
     *
     * @return FileConfiguration instance of the video configuration-file.
     */
	
	public FileConfiguration getConfigFile() {
		
		fileconfiguration = new YamlConfiguration();
		
		try {
			fileconfiguration.load(file);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return fileconfiguration;
    }
	
	/**
	* Gets the video configuration-file accoding to the values passed earlier in the constructors.
	* 
	* <p>The configuration file does not need to exist, specially if the {@link #createConfiguration(File)}
	* will be called next.
	* 
	* @return The video configuration-file.
	*/
	
	public File getFile() {
		return file;
	}
}
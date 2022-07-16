package fr.xxathyx.mediaplayer.screen;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.io.Files;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.group.Group;
import fr.xxathyx.mediaplayer.image.helpers.ImageHelper;
import fr.xxathyx.mediaplayer.image.renderer.ImageRenderer;
import fr.xxathyx.mediaplayer.items.ItemStacks;
import fr.xxathyx.mediaplayer.map.colors.MapColorPalette;
import fr.xxathyx.mediaplayer.notification.Notification;
import fr.xxathyx.mediaplayer.notification.NotificationType;
import fr.xxathyx.mediaplayer.screen.content.Content;
import fr.xxathyx.mediaplayer.screen.content.ContentType;
import fr.xxathyx.mediaplayer.screen.part.Part;
import fr.xxathyx.mediaplayer.screen.settings.ScreenSettings;
import fr.xxathyx.mediaplayer.stream.Stream;
import fr.xxathyx.mediaplayer.util.ImageUtil;
import fr.xxathyx.mediaplayer.video.Video;
import fr.xxathyx.mediaplayer.video.data.VideoData;
import fr.xxathyx.mediaplayer.video.instance.VideoInstance;
import fr.xxathyx.mediaplayer.tasks.TaskSyncLoadScreens;
import fr.xxathyx.mediaplayer.util.FacingLocation;

/** 
* The Screen class is essential in the good functioning of things, its used
* across the plugin as a support for various content, it constitue a support part.
* The video class has three constructors.
* 
* Registered screens can be found here {@link Main#getRegisteredScreens()}.
* 
* <p>{@link Screen} corresponds to an screen configuration file, for each screen
* that can be registered they have an configuration that is used as we can see
* here {@link TaskSyncLoadScreens}. In the YAML configuration-file are
* related all informations about the screen itself.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class Screen {

	private final Main plugin = Main.getPlugin(Main.class);
	
	private final Configuration configuration = new Configuration();
	private final ItemStacks itemStacks = new ItemStacks();
	
	private FileConfiguration fileconfiguration;
	
	private File file;
	private UUID uuid;
	
	private ScreenSettings settings;
	private int id;
	
	private Video video;
	private Stream stream;
	private VideoData videoData;
	
	private Content content;
	private VideoInstance videoInstance;
	
	private boolean running = false;
	private boolean linked = true;
		
	public int[] tasks = {Bukkit.getScheduler().getPendingTasks().size(), Bukkit.getScheduler().getPendingTasks().size()+1};
		
	private int[] ids;
	
	private int width = -1;
	private int height = -1;
	
	private ItemFrame lowest;
	private ItemFrame highest;
	
	private ArrayList<Content> contents = new ArrayList<>();
	private ArrayList<Part> parts = new ArrayList<>();
	
	private ArrayList<ItemFrame> frames = new ArrayList<>();
	private ArrayList<Block> blocks = new ArrayList<>();
	
	private ArrayList<UUID> listeners = new ArrayList<>();
	
	/**
	* Constructor for Screen class, creates an Screen variable according
	* to world objects, this is used for first time screen creation.
	* 
	* @param uuid The screen unique-id.
	* @param width The screen width.
	* @param height The screen height.
	* @param height The screen list of {@link ItemFrame}.
	* @param height The screen list of {@link Block}.
	*/
	
	public Screen(UUID uuid, int width, int height, ArrayList<ItemFrame> frames, ArrayList<Block> blocks) {
				
		this.file = new File(configuration.getScreensFolder() + "/" + uuid.toString(), uuid.toString() + ".yml");
		this.uuid = uuid;
		this.frames = frames;
		this.blocks = blocks;
		this.id = plugin.getRegisteredScreens().size();
		this.width = width;
		this.height = height;
	}
	
	/**
	* Constructor for Screen class, creates an Screen variable according
	* to a screen configuration-file, this is used during {@link TaskSyncLoadScreens#run()}.
	* 
	* @param file The screen configuration-file.
	*/
	
	public Screen(File file) {
		
		this.file = file;
		this.frames = getFrames();
		this.id = plugin.getRegisteredScreens().size();
		this.width = getWidth();
		this.height = getHeight();
	}
	
	/**
	* Constructor for Screen class, creates an Screen variable according
	* to a screen unique-id.
	* 
	* @param uuid The screen unique-id.
	*/
	
	public Screen(UUID uuid) {
		
		this.file = new File(configuration.getScreensFolder() + "/" + uuid.toString(), uuid.toString() + ".yml");
		this.frames = new ArrayList<>();
		this.id = plugin.getRegisteredScreens().size();
		this.width = getWidth();
		this.height = getHeight();
	}
	
	/**
	* Creates a screen configuration-file according to a original {@link Location} and a
	* facing location, see {@link FacingLocation#getCardinalDirection(org.bukkit.entity.LivingEntity)}.
	* 
	* <p>This is used on first time screen creation, see {@link #Screen(UUID, int, int, ArrayList, ArrayList)}.
	* 
	* @param facingDirection The opposite facing direction of frames, or the facing direction of the player
	* who placed the screen.
	* @param location The original location of the screen, take the value of the original first {@link ItemFrame}.
	*/
	
	public void createConfiguration(String facingDirection, Location location) {
				
		LocalDateTime date = LocalDateTime.now();
				
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.set("screen.name", "screen");
		fileconfiguration.set("screen.uuid", uuid.toString());
		fileconfiguration.set("screen.creation", date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear() + " " + date.getHour() + ":" + date.getMinute() + ":" + date.getSecond());
		fileconfiguration.set("screen.block-type", blocks.get(0).getType().toString());
		fileconfiguration.set("screen.glowing", false);
				
		fileconfiguration.set("screen.width", width);
		fileconfiguration.set("screen.height", height);
		
		fileconfiguration.set("screen.location.world", location.getWorld().getName());
		fileconfiguration.set("screen.location.x", location.getBlockX());
		fileconfiguration.set("screen.location.y", location.getBlockY());
		fileconfiguration.set("screen.location.z", location.getBlockZ());
		fileconfiguration.set("screen.location.facing", facingDirection);
				
		fileconfiguration.set("screen.video.name", "none");
		fileconfiguration.set("screen.video.instance", "none");
		fileconfiguration.set("screen.last-frame", 0);
		fileconfiguration.set("screen.contents-folder", new File(configuration.getScreensFolder() + "/" + uuid + "/contents/").getAbsolutePath());
		fileconfiguration.set("screen.parts-folder", new File(configuration.getScreensFolder() + "/" + uuid + "/parts/").getAbsolutePath());
		fileconfiguration.set("screen.parts-count", width*height);
		fileconfiguration.set("screen.thumbnail-path", new File(configuration.getScreensFolder() + "/" + uuid + "/thumbnail", "thumbnail.jpg").getAbsolutePath());
		fileconfiguration.set("screen.ids", 0);
								
		try {
			fileconfiguration.save(file);
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		new File(configuration.getScreensFolder() + "/" + uuid + "/contents/").mkdir();
		getThumbnail().getParentFile().mkdir();
		
		ArrayList<ItemFrame> sorted = new ArrayList<>();
		ItemFrame origin = frames.get(height-1);
				
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {			
				if(facingDirection.equals("N")) if(getNearbyEntities(origin.getLocation().add(j, -i, 0), 0).toArray().length > 0) sorted.add((ItemFrame) getNearbyEntities(origin.getLocation().add(j, -i, 0), 0).toArray()[0]);
				if(facingDirection.equals("E")) if(getNearbyEntities(origin.getLocation().add(0, -i, -j), 0).toArray().length > 0) sorted.add((ItemFrame) getNearbyEntities(origin.getLocation().add(0, -i, -j), 0).toArray()[0]);
				if(facingDirection.equals("S")) if(getNearbyEntities(origin.getLocation().add(-j, -i, 0), 0).toArray().length > 0) sorted.add((ItemFrame) getNearbyEntities(origin.getLocation().add(-j, -i, 0), 0).toArray()[0]);
				if(facingDirection.equals("W")) if(getNearbyEntities(origin.getLocation().add(0, -i, j), 0).toArray().length > 0) sorted.add((ItemFrame) getNearbyEntities(origin.getLocation().add(0, -i, j), 0).toArray()[0]);							
			}
		}
		
		frames = sorted;
				
		if(facingDirection.equals("E") || facingDirection.equals("W")) Collections.reverse(frames);
		
		for(int i = 0; i < blocks.size(); i++) new Part(new File(getPartsFolder(), i + ".yml")).createConfiguration(uuid, blocks.get(i), frames.get(i), configuration.glowing_screen_frames_support(), configuration.visible_screen_frames_support(), i);
		createThumbnail();
		for(int i = 0; i < getFrames().size(); i++) getFrames().get(i).setItem(new ItemStacks().getMap(getIds()[i]));
	}
	
    /**
     * Gets an FileConfiguration instance of the screen configuration-file,
     * wich grant access to the configuration data.
     * 
     * <p> This method isn't usable directly, its used on class getters method
     * such as {@link #getName()} to access data.
     *
     * @return FileConfiguration instance of the screen configuration-file.
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
	* Gets the screen configuration-file accoding to the values passed earlier in the constructors.
	* 
	* <p>The configuration file does not need to exist, specially if the {@link #createConfiguration(File)}
	* will be called next.
	* 
	* @return The screen configuration-file.
	*/
	
	public File getFile() {
		return file;
	}
	
	/**
	* Gets the screen name, each screen can have a name, even the same one.
	* 
	* <p>This name can be change with manual editing the screen configuration-file.
	* 
	* @return The screen name.
	*/
	
	public String getName() {
		return getConfigFile().getString("screen.name");
	}
	
	/**
	* Gets the screen unique-id.
	* 
	* @return The screen unique-id.
	*/
	
	public UUID getUUID() {
		return UUID.fromString(getConfigFile().getString("screen.uuid"));
	}
	
	/**
	* Gets creation date of the screen as a single {@link String} using
	* the following european* schema : dd-mm-yyyyy hh:mm:ss.
	* 
	* @return The screen creation date.
	*/
	
	public String getCreation() {
		return getConfigFile().getString("screen.creation");
	}
	
	/**
	* Gets the block-type used to build the screen by a non-hand way if it hasn't
	* been changed it would be the same as {@link Configuration#screen_block()}
	* material name.
	* 
	* @return The block-type used the create the screen.
	*/
	
	public Material getBlockType() {
		return Material.getMaterial(getConfigFile().getString("screen.block-type"));
	}
	
	/**
	* Gets whether the {@link ItemFrame} of the screen are glowing, it should
	* if it hasn't been changed return {@link Configration#glowing_screen_frames_support()}.
	* 
	* <p> <strong>Note: </strong>Glowing {@link ItemFrame} only exists
	* on earlier version of the game, since >1.17.
	* 
	* @return Whether the screen frames are glowing.
	*/
	
	public boolean isGlowing() {
		return getConfigFile().getBoolean("screen.glowing");
	}
	
	/**
	* Gets the screen width.
	* 
	* <p>The {@link #width} equals to zero, will result in variable obtention from configuration-file.
	* 
	* @return The screen width.
	*/
	
	public int getWidth() {
		if(width > 0) return width;
		return getConfigFile().getInt("screen.width");
	}
	
	/**
	* Gets the screen height.
	* 
	* <p>The {@link #height} equals to zero, will result in variable obtention from configuration-file.
	* 
	* @return The screen height.
	*/
	
	public int getHeight() {
		if(height > 0) return height;
		return getConfigFile().getInt("screen.height");
	}
	
	/**
	* Gets the screen spatial lowest {@link ItemFrame}, origin is the first frame to be placed.
	* 
	* <p> <strong>Note: </strong>Could return a null variable, if the screen hasn't been instancied
	* from first time creation constructor.
	* 
	* @return The spatial lowest {@link ItemFrame}.
	*/
	
	public ItemFrame getLowestFrame() {
		return lowest;
	}
	
	/**
	* Gets the screen spatial highest {@link ItemFrame}, origin is the first frame to be placed.
	* 
	* <p> <strong>Note: </strong>Could return a null variable, if the screen hasn't been instancied
	* from first time creation constructor.
	* 
	* @return The spatial highest {@link ItemFrame}.
	*/
	
	public ItemFrame getHighestFrame() {
		return highest;
	}
	
	/**
	* Gets the screen location based on {@link ItemFrame} location, the first frame, or key-frame
	* to be placed.
	* 
	* @return The screen location.
	*/
	
	public Location getLocation() {
		
		World world = Bukkit.getWorld(getConfigFile().getString("screen.location.world"));
		
		int x = getConfigFile().getInt("screen.location.x");
		int y = getConfigFile().getInt("screen.location.y");
		int z = getConfigFile().getInt("screen.location.z");
		
		return new Location(world, x, y, z);
	}
	
	/**
	* Gets the screen facing direction.
	* 
	* <p>Screen frames are facing the opposite direction.
	* 
	* @return The screen facing direction.
	*/
	
	public String getFacingLocation() {
		return getConfigFile().getString("screen.location.facing");
	}
	
	/**
	* Gets the name of the last, or actually played {@link Video}, that has been played one the screen.
	* 
	* <p>The case where no video were played, will return a 'none' {@link String}.
	* 
	* @return The name of last played, or actually played {@link Video}.
	*/
	
	public String getVideoName() {
		return getConfigFile().getString("screen.video.name");
	}
	
	/**
	* Gets the last, or actually played {@link Video} of the screen.
	* 
	* <p> <strong>Note: </strong>Could return a null variable, if the screen hasn't been played
	* any video from his first time creation.
	* 
	* @return The last, or actually played {@link Video}.
	*/
	
	public Video getVideo() {
		if(video != null) return video;
		return new Video(getConfigFile().getString("screen.video.name"));
	}
	
	/**
	* Gets the {@link VideoInstance} of the {@link Video} that is played.
	* 
	* <p>Case where no video are actually played, it will return the VideoInstance,
	* of the last played video.
	* 
	* @return The {@link VideoInstance} of the played {@link Video}.
	*/
	
	public VideoInstance getVideoInstance() {
		if(videoInstance != null) return videoInstance;
		videoInstance = new VideoInstance(getVideo(), new File(getVideo().getInstancesFolder(), getConfigFile().getString("screen.video.instance") + ".yml"));
		return videoInstance;
	}
	
	/**
	* Gets the {@link Content} that is actually played.
	* 
	* <p> <strong>Note: </strong>Could return a null variable, if no content
	* is actually played.
	* 
	* @return The {@link Content} that is actually played.
	*/
	
	public Content getContent() {
		return content;
	}
	
	/**
	* Gets the last-frame index of the last played video.
	* 
	* <p>The last-frame will change if the plugin shutdown, without finishing
	* displaying the entire video, this is used to resume the video.
	* 
	* @return The last-frame index of the last played video.
	*/
	
	public int getLastFrame() {
		return getConfigFile().getInt("screen.last-frame");
	}
	
	/**
	* Gets the folder containing all registered screen content.
	*  
	* <p>The folder is empty until a content has been played.
	* 
	* @return The folder containing all screen registered content.
	*/
	
	public File getContentsFolder() {
		return new File(configuration.getScreensFolder() + "/" + getUUID() + "/contents/");
	}
	
	/**
	* Gets the folder containing all screen parts.
	*  
	* <p>The folder is empty until {@link #createConfiguration(String, Location)}.
	* 
	* @return The folder containing all screen parts.
	*/
	
	public File getPartsFolder() {
		return new File(configuration.getScreensFolder() + "/" + getUUID() + "/parts/");
	}
	
	/**
	* Gets the screen thumbnail-file, this file could be changed.
	* 
	* @return The screen thumbnail-file.
	*/
	
	public File getThumbnail() {
		return new File(configuration.getScreensFolder() + "/" + getUUID() + "/thumbnail/", "thumbnail.png");
	}
	
	/**
	* Gets the screen thumbnail-file path, path of {@link #getThumbnail()}.
	* 
	* @return The screen thumbnail-file path.
	*/
	
	public String getThumbnailPath() {
		return getConfigFile().getString("screen.thumbnail-path");
	}
	
	/**
	* Gets an list of {@link Integer} corresponding to the ids wich were used to render
	* the thumbnail in Minecraft.
	* 
	* <p>Those ids will be next used to display content.
	* 
	* @return A list of the screen frames map-ids.
	*/
	
	@SuppressWarnings("unchecked")
	public int[] getIds() {
		if(ids != null) return ids;
		ids = ArrayUtils.toPrimitive(Arrays.stream(((List<Integer>) getConfigFile().getList("screen.ids")).toArray()).map(Object::toString).map(Integer::valueOf).toArray(Integer[]::new));
		return ids;
	}
	
	/**
	* Gets screen frames, as list of {@link ItemFrame}.
	* 
	* @return A list of the {@link ItemFrame}, composing the screen.
	*/
	
	public ArrayList<ItemFrame> getFrames() {		
		if(!frames.isEmpty()) return frames;		
		for(int i = 0; i < width*height; i++) frames.add(new Part(new File(getPartsFolder(), i + ".yml")).getItemFrame());		
		return frames;
	}
	
	/**
	* Gets screen blocks, as list of {@link Block}.
	* 
	* @return A list of the {@link Block}, composing the screen structure.
	*/
	
	public ArrayList<Block> getBlocks() {
		if(!blocks.isEmpty()) return blocks;
		for(int i = 0; i < width*height; i++) blocks.add(new Part(new File(getPartsFolder(), i + ".yml")).getBlock());
		return blocks;
	}
	
	/**
	* Gets actual time-left of the played video, as a {@link String}.
	* 
	* <p>Could return null, if no videos were played.
	* 
	* @return actual time-left of the played video, as a {@link String}.
	*/
	
	public String getTimeLeft(int frame) {
		return settings.count + "/" + settings.total;
	}
	
	/**
	* Gets actual time-left of the played video, as a {@link String}.
	* 
	* <p>Could return null, if no videos were played.
	* 
	* @return actual time-left of the played video, as a {@link String}.
	*/
	
	public String getStatus() {
		if(isRunning()) return ChatColor.GREEN + "RUNNING: " + ChatColor.DARK_GREEN + settings.name;
		return ChatColor.RED + "NOT RUNNING";
	}
	
	/**
	* Gets {@link ScreenSettings} of the played video to the screen.
	* 
	* @return {@link ScreenSettings} of the played video.
	*/
	
	public ScreenSettings getSettings() {
		return settings;
	}
	
	/**
	* Gets the screen-id, between all other screens, this could be the
	* same as {@link Main#getRegisteredScreens()} index of this screen.
	* 
	* <p>Screens deleted or removed from this list, will result in the id
	* to be non-fiable, so more often ignore this specific screen-id, and
	* use {@link #getUUID()}.
	* 
	* @return The screen-id.
	*/
	
	public int getId() {
		return id;
	}
	
	/**
	* Gets whether a video is currently being runned on this screen.
	* 
	* <p> <strong>Note: </strong>Return false, if the video has been linked,
	* see {@link #isLinked()}, only true when the video is being currently played.
	* 
	* @return Whether the a video is currently being runned.
	*/
	
	public boolean isRunning() {
		return running;
	}
	
	/**
	* Gets whether a video has been linked to this screen.
	* 
	* @return Whether a video has been linked to the screen.
	*/
	
	public boolean isLinked() {
		return linked;
	}
	
	/**
	* Gets screen instance itself, used for sub-class tasks.
	* 
	* <p>Equivalent to this.
	* 
	* @return Screen instance itself
	*/
	
	public Screen getScreen() {
		return this;
	}
	
	/**
	* Gets screen contents, as list of {@link Content}.
	* 
	* <p>Return list could be empty if no content were actually
	* played on the screen one a time at least.
	* 
	* @return A list of the {@link Content} registered on the screen.
	*/
	
	public ArrayList<Content> getContents() {	
		File[] files = getContentsFolder().listFiles();
		if(files.length == contents.size()) return contents;
		contents.clear();
		for(File file : files) if(!file.isDirectory()) contents.add(new Content(file));
		return contents;
	}
	
	/**
	* Gets screen parts, as list of {@link Part}.
	* 
	* @return A list of the {@link Part}, composing the screen.
	*/
	
	public ArrayList<Part> getParts() {	
		if(!parts.isEmpty()) return parts;
		for(int i = 0; i < width*height; i++) parts.add(new Part(new File(getPartsFolder(), i + ".yml")));
		return parts;
	}
	
	/**
	* Sets the screen current video, this is used when playing a video into the screen for the first time.
	* 
	* @param videoInstance An instance of the video to be played on the screen.
	* @param frames The screen frames, same as {@link #getFrames()} if correctly filled.
	*/
	
	public void setVideo(VideoInstance videoInstance, ArrayList<ItemFrame> frames) {
		
		Video video = videoInstance.getVideo();
		
		this.frames = frames;
		this.settings = new ScreenSettings(video);
		this.id = plugin.getRegisteredScreens().size();
		this.video = video;
		this.videoData = video.getVideoData();
		this.videoInstance = videoInstance;
		this.ids = getIds();
		this.width = video.getVideoData().getMinecraftWidth();
		this.height = video.getVideoData().getMinecraftHeight();
		
		try {
			
			fileconfiguration = new YamlConfiguration();
			fileconfiguration.load(file);
			fileconfiguration.set("screen.video.name", video.getName());
			fileconfiguration.save(file);
			
			Files.copy(videoData.getThumbnail(), getThumbnail());
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Sets the screen content, this is used when switching to a other screen registered content.
	* 
	* <p>Content can be video, or image.
	* 
	* @param content The new content to be put on the screen.
	*/
	
	public void setContent(Content content) {
		
		if(isRunning()) end();
		
		if(content.getType() == ContentType.IMAGE) {
			
			if(video != null) end();
			
			fr.xxathyx.mediaplayer.image.Image image = content.getImage();
			
			this.frames = getFrames();
			this.content = content;
			this.ids = getIds();
			this.width = image.getWidth();
			this.height = image.getHeight();
			
			ArrayList<ItemFrame> frames = new ArrayList<>();
			ItemFrame keyframe = getFrames().get(content.getKeyFrame());
			
			for(int i = 0; i < image.getHeight(); i++) {
				for(int j = 0; j < image.getWidth(); j++) {
					
					ItemFrame itemFrame = null;
					
					if(getFacingLocation().equals("N")) {
						if(getNearbyEntities(keyframe.getLocation().add(j, -i, 0), 0).toArray().length > 0) {
							itemFrame = (ItemFrame) getNearbyEntities(keyframe.getLocation().add(j, -i, 0), 0).toArray()[0];;
						}
					}
					if(getFacingLocation().equals("E")) {
						if(getNearbyEntities(keyframe.getLocation().add(0, -i, j), 0).toArray().length > 0) {
							itemFrame = (ItemFrame) getNearbyEntities(keyframe.getLocation().add(0, -i, j), 0).toArray()[0];
						}
					}
					if(getFacingLocation().equals("S")) {
						if(getNearbyEntities(keyframe.getLocation().add(-j, -i, 0), 0).toArray().length > 0) {
							itemFrame = (ItemFrame) getNearbyEntities(keyframe.getLocation().add(-j, -i, 0), 0).toArray()[0];
						}
					}
					if(getFacingLocation().equals("W")) {
						if(getNearbyEntities(keyframe.getLocation().add(0, -i, -j), 0).toArray().length > 0) {
							itemFrame = (ItemFrame) getNearbyEntities(keyframe.getLocation().add(0, -i, -j), 0).toArray()[0];
						}
					}									
					if(itemFrame != null) {
						frames.add(itemFrame);
					}
				}
			}
			for(int i = 0; i < frames.size(); i++) frames.get(i).setItem(itemStacks.getMap(image.getIds().get(i)));
			return;
		}
		
		if(content.getType() == ContentType.VIDEO) {
			
			Video video = content.getVideo();
			
			this.frames = getFrames();
			this.settings = new ScreenSettings(video);
			this.id = plugin.getRegisteredScreens().size();
			this.video = video;
			this.videoData = video.getVideoData();
			this.ids = getIds();
			this.width = video.getVideoData().getMinecraftWidth();
			this.height = video.getVideoData().getMinecraftHeight();
		}
		
		try {
			
			fileconfiguration = new YamlConfiguration();
			fileconfiguration.load(file);
			fileconfiguration.set("screen.video.name", video.getName());
			fileconfiguration.save(file);
			
			Files.copy(videoData.getThumbnail(), getThumbnail());
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Creates the screen thumbnail.
	* 
	* <p>The thumbnail appearance changes if the screen {@link #getThumbnail()} file change.
	*/
	
	public void createThumbnail() {
		
		try {
			Image base = ImageIO.read(Main.class.getResource("resources/default.png"));
			BufferedImage bufferedBase = (BufferedImage) base;
								
			Image resizedBase = base.getScaledInstance((int) Math.round(bufferedBase.getWidth()*((double) width*128/bufferedBase.getWidth())),
					(int) Math.round(bufferedBase.getHeight()*((double) height*128/bufferedBase.getHeight())), Image.SCALE_DEFAULT);
						
			ImageIO.write(ImageUtil.convertToBufferedImage(resizedBase), "png", getThumbnail());
			
			ImageRenderer imageRenderer = new ImageRenderer(ImageIO.read(getThumbnail()));		
			imageRenderer.createMaps(Bukkit.getWorlds().get(0));
			
			ids = ArrayUtils.toPrimitive(Arrays.stream(imageRenderer.getIds().toArray()).map(Object::toString).map(Integer::valueOf).toArray(Integer[]::new));
			
			fileconfiguration = new YamlConfiguration();
			fileconfiguration.load(file);
			fileconfiguration.set("screen.ids", imageRenderer.getIds());
			fileconfiguration.save(file);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Loads the {@link #getThumbnail()} in order to be again displayed in Minecraft, this method
	* is called on {@link TaskSyncLoadScreens}.
	* 
	* <p> <strong>Note: </strong>The thumbnail is re-rendered again according to their
	* {@link #getThumbnail()} file, so if the same named file does change, the changes
	* will be applied on restart.
	*/
	
	public void loadThumbnail() {
		
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					if(getIds().length > 0) {
						
						BufferedImage bufferedImage = ImageHelper.getImage(getThumbnail().getPath());
						
						ImageRenderer imageRenderer = new ImageRenderer(bufferedImage);
						
						imageRenderer.calculateDimensions();
						imageRenderer.splitImages();
						
						MapView map;
						
						for(int i = 0; i < imageRenderer.getBufferedImages().length; i++) {
							map = plugin.getMapUtil().getMapView(getIds()[i]);
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
		});
	}
	
	/**
	* Removes the screen structure according to {@link #getBlocks()}.
	* 
	* <p>Actually don't removes {@link ItemFrame} too.
	*/
	
	public void remove() {
		for(int i = 0; i < getBlocks().size(); i++) {
			getFrames().get(i).remove();
			getBlocks().get(i).setType(Material.AIR);
		}
	}
	
	/**
	* Deletes the screen files, and structure in game, see {@link #remove()}.
	*/
	
	public void delete() {
		
		plugin.getRegisteredScreens().remove(this);
		remove();
		
		try {
			FileUtils.deleteDirectory(getFile().getParentFile());
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Screen display method, this method is used to display videos, this is the plugin key-part.
	*/
	
	@SuppressWarnings("deprecation")
	public void display() {
		
		plugin.getTasks().add(tasks[0]); plugin.getTasks().add(tasks[1]);
		
		loadThumbnail();
		
		if(!frames.isEmpty()) for(int i = 0; i < frames.size(); i++) frames.get(i).setItem(itemStacks.getMap(ids[i]));
				
		if(video.isStreamed()) {
			
			stream = new Stream(video);
			stream.update();
			
			tasks[0] = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {  
				@Override
				public void run() {
					settings.total = settings.total + video.getTotalFrames();
				}
			}, (int) (Math.round(video.getTotalFrames()/video.getFrameRate())*20), (int) (Math.round(video.getTotalFrames()/video.getFrameRate())*20));
		}
				
		tasks[1] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		    
			@Override
			public void run() {
							
				Collection<Entity> entities = getNearbyEntities(frames.get(ids.length/2).getLocation(), configuration.maximum_distance_to_receive());
				
				for(Entity entity : entities) {
					if(entity.getType() == EntityType.PLAYER) {
						
						Player player = ((Player)entity);
						
						if(!listeners.contains(player.getUniqueId())) {
						    player.setResourcePack(new String("http://" + configuration.plugin_free_audio_server_address() + "/mediaplayer/users/" + configuration.free_audio_server_token() + "/" + video.getName() + ".zip").replace(" ", "%20"));
							listeners.add(player.getUniqueId());
						}
					}
				}
				
				if(!running) {
					
					ArrayList<UUID> ready = new ArrayList<>();
					
					if(System.currentTimeMillis() - settings.time >= 1000) {
						
	                	settings.time = System.currentTimeMillis();
						
						for(UUID uuid : listeners) {
							if(!plugin.getPlayersScreens().containsKey(uuid)) {
								new Notification(NotificationType.WAITING_PLAYER, false).send(new Group(listeners), new String[] {Bukkit.getPlayer(uuid).getName()}, false);
							}else if(!ready.contains(uuid)){
								ready.add(uuid);
							}
						}
						if(ready.size() == listeners.size()) new Notification(NotificationType.EVERYONE_READY, false).send(new Group(listeners), null, false);
					}
				}
				
				if(running) {
					
					for(UUID uuid : listeners) {
						
						if(plugin.getPlayersScreens().containsKey(uuid)) {
							if(plugin.getPlayersScreens().get(uuid) == null) {
								
								plugin.getPlayersScreens().replace(uuid, getScreen());
								Player player = Bukkit.getPlayer(uuid);
								
								for(int i = 0; i < video.getAudioChannels(); i++) player.playSound(player.getLocation(), "mediaplayer." + i, 10, 1);
							}
						}
					}
					
					if(settings.count <= settings.total) {
						if(settings.missed == settings.differencial) {
							settings.missed = 0;
							settings.max = settings.differencial/2;
							if(settings.framerate == 20) settings.max = 1;
						}
		                
		                if(System.currentTimeMillis() - settings.time >= 1000) {		                	
	                		if(settings.fps < settings.framerate) {
                				if(settings.skipDuplicatedFrames) {
                					if(videoData.getDuplicated().contains(settings.count)) {
                						settings.count++;
                					}
                				}else {
		                			settings.count = settings.count + (settings.framerate-settings.fps);
                				}
	                		}
		                	
		                	if(settings.showInformations) {
								String message = ChatColor.GREEN + "" + ChatColor.BOLD + settings.name + ": " + ChatColor.GREEN + getTimeLeft(settings.count) + ", " + settings.description;
								if(settings.showFPS) {
									message = message + ChatColor.GREEN + " - " + ChatColor.BOLD + settings.fps + ChatColor.GREEN + " FPS";
								}
			                	for(Player player : Bukkit.getOnlinePlayers()) plugin.getActionBar().send(player, message);
		                	}
		                	settings.fps = 0;
		                	settings.time = System.currentTimeMillis();
		                }
						
		                settings.max = (int) Math.round(settings.max*(settings.framerate/20)*settings.speed);
		                		                
						for(int i = 0; i < settings.max; i++) {
							if(settings.count < settings.total && settings.fps < settings.framerate) {
								
								BukkitTask[] bukkitTask = {null};
								bukkitTask[0] = Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
																		
									@Override
									public void run() {
														
										plugin.getTasks().add(bukkitTask[0].getTaskId());
										
										if(settings.realtimeRendering) {
											
											try {
												
												BufferedImage frame = ImageIO.read(new File(video.getFramesFolder(), settings.count + settings.framesExtension));
												
												ImageRenderer imageRenderer = new ImageRenderer(frame);
							    	    		imageRenderer.calculateDimensions();
							    	    		imageRenderer.splitImages();
							    	    									    	    		
							    	    		byte[] buffer;
							    	    		
							    	    		for(int j = 0; j < ids.length; j++) {
							    	    			
													buffer = MapColorPalette.convertImage(imageRenderer.getBufferedImages()[j]);
													
													for(Entity entity : entities) {
														if(entity.getType() == EntityType.PLAYER) plugin.getMapUtil().update((Player)entity, ids[j], buffer);
													}
							    	    		}
											}catch (IOException e) {
												e.printStackTrace();
											}
										}else {
											byte[] buffer;
											
											for(int j = 0; j < ids.length; j++) {
												try {
													
													buffer = FileUtils.readFileToByteArray(new File(videoData.getCacheFolder() + "/" + settings.count + "/", String.valueOf(j) + ".cache"));
													
													for(Entity entity : entities) {
														if(entity.getType() == EntityType.PLAYER) plugin.getMapUtil().update((Player)entity, ids[j], buffer);
													}
												}catch (IOException e) {
													e.printStackTrace();
												}
											}
											buffer = null;
										}
									}
								});
								settings.count++;
								settings.missed++;
								settings.fps++;
							}
						}
						settings.max = 1;
					}else {
						if(video.isLoopping()) settings.count = 0;
						if(!video.isStreamed()) end();
					}
				}
			}
		}, 0L, 0L);
	}
	
	/**
	* Pausing the currently played video.
	* 
	* <p>This is the same as passing {@link running} variable to false.
	*/
	
	public void pause() {
		running = false;
	}
	
	/**
	* Resuming the currently played video.
	* 
	* <p>This is the same as passing {@link running} variable to true.
	*/
	
	public void resume() {
		running = true;
	}
	
	/**
	* Called whenever a video has finished to be displayed.
	* 
	* <p>Don't fire if a video is designed to loop.
	*/
	
	public void end() {
		
		Bukkit.getScheduler().cancelTask(tasks[0]);
		Bukkit.getScheduler().cancelTask(tasks[1]);
		
		running = false;
				
		videoData.loadThumbnail();
		
		Object[] ids = videoData.getMaps().getIds().toArray();
		
		for(int i = 0; i < frames.size(); i++) {
			frames.get(i).setItem(itemStacks.getMap((int) ids[i]));
		}
		
		for(UUID uuid : listeners) { for(int i = 0; i < video.getAudioChannels(); i++) plugin.getAudioUtil().stopAudio(Bukkit.getPlayer(uuid), "mediaplayer." + i); }
		
		plugin.getPlayingVideos().remove(video.getName());
		
		if(configuration.remove_screen_on_end()) {
			remove();
		}
	}
	
	/**
	* Sets the current screen settings to display a video.
	* 
	* @param screenSettings The screen settings adapted to a video.
	*/
	
	public void setSettings(ScreenSettings screenSettings) {
		this.settings = screenSettings;
	}
	
	/**
	* Sets whether the current video will be runned or not, this method
	* is used with the /video start command, to set {@link running} to
	* true.
	* 
	* @param running Whether the current video will be runned or not.
	*/
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	/**
	* A way to manually set the screen spatial lowest {@link ItemFrame}.
	* 
	* @param lowest The screen lowest {@link ItemFrame} to be set.
	*/
	
	public void setLowestFrame(ItemFrame lowest) {
		this.lowest = lowest;
	}
	
	/**
	* A way to manually set the screen spatial highest {@link ItemFrame}.
	* 
	* @param highest The screen highest {@link ItemFrame} to be set.
	*/
	
	public void setHighestFrame(ItemFrame highest) {
		this.highest = highest;
	}
	
	/**
	* A way to manually set the screen frames.
	* 
	* @param frames The screen frames as a list of {@link ItemFrame}.
	*/
	
	public void setFrames(ArrayList<ItemFrame> frames) {
		this.frames = frames;
	}
	
    /**
     * Gets the entities that are located in a specified radius from a {@link Location}.
     * 
     * <p>This method is used instead of the given one with the API, in order to support
     * older Minecraft versions.
     * 
     * @param location The center point.
     * @param radius The radius from the center.
     * @return The entities that are within the radius.
     */
	
	public Collection<Entity> getNearbyEntities(Location location, int radius) {
		
		if(plugin.isOld()) {
		    int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		    HashSet<Entity> radiusEntities = new HashSet < Entity > ();
		 
		    for(int chunkX = 0 - chunkRadius; chunkX <= chunkRadius; chunkX++) {
		        for(int chunkZ = 0 - chunkRadius; chunkZ <= chunkRadius; chunkZ++) {
		            int x = (int) location.getX(), y = (int) location.getY(), z = (int) location.getZ();
		            for(Entity entity : new Location(location.getWorld(), x + (chunkX * 16), y, z + (chunkZ * 16)).getChunk().getEntities()) {
		                if(entity.getLocation().distance(location) <= radius && entity.getLocation().getBlock() != location.getBlock())
		                    radiusEntities.add(entity);
		            }
		        }
		    }
		    return radiusEntities;
		}
		return location.getWorld().getNearbyEntities(location, radius, radius, radius);
	}
}
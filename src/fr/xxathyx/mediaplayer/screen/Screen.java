package fr.xxathyx.mediaplayer.screen;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import fr.xxathyx.mediaplayer.notification.Notification;
import fr.xxathyx.mediaplayer.notification.NotificationType;
import fr.xxathyx.mediaplayer.screen.part.Part;
import fr.xxathyx.mediaplayer.screen.settings.ScreenSettings;
import fr.xxathyx.mediaplayer.stream.Stream;
import fr.xxathyx.mediaplayer.util.ImageUtil;
import fr.xxathyx.mediaplayer.video.Video;
import fr.xxathyx.mediaplayer.video.data.VideoData;
import fr.xxathyx.mediaplayer.video.instance.VideoInstance;

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
	private VideoInstance videoInstance;
	
	private boolean running = false;
	private boolean linked = true;
		
	public int[] tasks = {Bukkit.getScheduler().getPendingTasks().size(), Bukkit.getScheduler().getPendingTasks().size()+1};
		
	private int[] ids;
	
	private int width;
	private int height;
	
	private ItemFrame lowest;
	private ItemFrame highest;
	
	private ArrayList<Part> parts = new ArrayList<>();
	
	private ArrayList<ItemFrame> frames = new ArrayList<>();
	private ArrayList<Block> blocks = new ArrayList<>();
	
	private ArrayList<UUID> listeners = new ArrayList<>();
		
	public Screen(UUID uuid, int width, int height, ArrayList<ItemFrame> frames, ArrayList<Block> blocks) {
				
		this.file = new File(configuration.getScreensFolder() + "/" + uuid.toString(), uuid.toString() + ".yml");
		this.uuid = uuid;
		this.frames = frames;
		this.blocks = blocks;
		this.id = plugin.getRegisteredScreens().size();
		this.width = width;
		this.height = height;
	}
	
	public Screen(File file) {
		
		this.file = file;
		this.frames = getFrames();
		this.id = plugin.getRegisteredScreens().size();
		this.width = getWidth();
		this.height = getHeight();
	}
	
	public Screen(UUID uuid) {
		
		this.file = new File(configuration.getScreensFolder() + "/" + uuid.toString(), uuid.toString() + ".yml");
		this.frames = new ArrayList<>();
		this.id = plugin.getRegisteredScreens().size();
		this.width = getWidth();
		this.height = getHeight();
	}
	
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
		fileconfiguration.set("screen.parts-folder", new File(configuration.getScreensFolder() + "/" + uuid + "/parts/").getAbsolutePath());
		fileconfiguration.set("screen.parts-count", width*height);
		fileconfiguration.set("screen.thumbnail-path", new File(configuration.getScreensFolder() + "/" + uuid + "/thumbnail", "thumbnail.jpg").getAbsolutePath());
		fileconfiguration.set("screen.ids", 0);
								
		try {
			fileconfiguration.save(file);
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		getThumbnail().getParentFile().mkdir();
		
		ArrayList<ItemFrame> sorted = new ArrayList<>();
		ItemFrame origin = frames.get(height-1);
				
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {			
				if(facingDirection.equals("N")) if(getNearbyEntities(origin.getLocation().add(j, -i, 0), 0).toArray().length >= 0) sorted.add((ItemFrame) getNearbyEntities(origin.getLocation().add(j, -i, 0), 0).toArray()[0]);
				if(facingDirection.equals("E")) if(getNearbyEntities(origin.getLocation().add(0, -i, j), 0).toArray().length >= 0) sorted.add((ItemFrame) getNearbyEntities(origin.getLocation().add(0, -i, j), 0).toArray()[0]);
				if(facingDirection.equals("S")) if(getNearbyEntities(origin.getLocation().add(-j, -i, 0), 0).toArray().length >= 0) sorted.add((ItemFrame) getNearbyEntities(origin.getLocation().add(-j, -i, 0), 0).toArray()[0]);
				if(facingDirection.equals("W")) if(getNearbyEntities(origin.getLocation().add(0, -i, -j), 0).toArray().length >= 0) sorted.add((ItemFrame) getNearbyEntities(origin.getLocation().add(0, -i, -j), 0).toArray()[0]);							
			}
		}
		
		frames = sorted;
				
		for(int i = 0; i < blocks.size(); i++) new Part(new File(getPartsFolder(), i + ".yml")).createConfiguration(uuid, blocks.get(i), frames.get(i), false, false, i);
		createThumbnail();
		for(int i = 0; i < getFrames().size(); i++) getFrames().get(i).setItem(new ItemStacks().getMap(getIds()[i]));
	}
	
	public FileConfiguration getConfigFile() {
		
		fileconfiguration = new YamlConfiguration();
		
		try {
			fileconfiguration.load(file);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return fileconfiguration;
    }
	
	public File getFile() {
		return file;
	}
	
	public String getName() {
		return getConfigFile().getString("screen.name");
	}
	
	public UUID getUUID() {
		return UUID.fromString(getConfigFile().getString("screen.uuid"));
	}
	
	public String getCreation() {
		return getConfigFile().getString("screen.creation");
	}
	
	public Material getBlockType() {
		return Material.getMaterial(getConfigFile().getString("screen.block-type"));
	}
	
	public boolean isGlowing() {
		return getConfigFile().getBoolean("screen.glowing");
	}
	
	public int getWidth() {
		if(width > 0) return width;
		return getConfigFile().getInt("screen.width");
	}
	
	public int getHeight() {
		if(height > 0) return height;
		return getConfigFile().getInt("screen.height");
	}
	
	public ItemFrame getLowestFrame() {
		return lowest;
	}
	
	public ItemFrame getHighestFrame() {
		return highest;
	}
	
	public Location getLocation() {
		
		World world = Bukkit.getWorld(getConfigFile().getString("screen.location.world"));
		
		int x = getConfigFile().getInt("screen.location.x");
		int y = getConfigFile().getInt("screen.location.y");
		int z = getConfigFile().getInt("screen.location.z");
		
		return new Location(world, x, y, z);
	}
	
	public String getFacingLocation() {
		return getConfigFile().getString("screen.location.facing");
	}
	
	public String getVideoName() {
		return getConfigFile().getString("screen.video.name");
	}
	
	public Video getVideo() {
		if(video != null) return video;
		return new Video(getConfigFile().getString("screen.video.name"));
	}
	
	public VideoInstance getVideoInstance() {
		
		if(videoInstance != null) return videoInstance;
		
		videoInstance = new VideoInstance(getVideo(), new File(getVideo().getInstancesFolder(), getConfigFile().getString("screen.video.instance") + ".yml"));
		return videoInstance;
	}
	
	public int getLastFrame() {
		return getConfigFile().getInt("screen.last-frame");
	}
	
	public File getPartsFolder() {
		return new File(configuration.getScreensFolder() + "/" + getUUID() + "/parts/");
	}
	
	public File getThumbnail() {
		return new File(configuration.getScreensFolder() + "/" + getUUID() + "/thumbnail/", "thumbnail.png");
	}
	
	public String getThumbnailPath() {
		return getConfigFile().getString("screen.thumbnail-path");
	}
	

	@SuppressWarnings("unchecked")
	public int[] getIds() {
		if(ids != null) return ids;
		ids = ArrayUtils.toPrimitive(Arrays.stream(((List<Integer>) getConfigFile().getList("screen.ids")).toArray()).map(Object::toString).map(Integer::valueOf).toArray(Integer[]::new));
		return ids;
	}
	
	public ArrayList<ItemFrame> getFrames() {		
		if(!frames.isEmpty()) return frames;		
		for(int i = 0; i < width*height; i++) frames.add(new Part(new File(getPartsFolder(), i + ".yml")).getItemFrame());		
		return frames;
	}
	
	public ArrayList<Block> getBlocks() {
		if(!blocks.isEmpty()) return blocks;
		for(int i = 0; i < width*height; i++) blocks.add(new Part(new File(getPartsFolder(), i + ".yml")).getBlock());
		return blocks;
	}
	
	public String getTimeLeft(int frame) {
		return settings.count + "/" + settings.total;
	}
	
	public String getStatus() {
		if(isRunning()) return ChatColor.GREEN + "RUNNING: " + ChatColor.DARK_GREEN + settings.name;
		return ChatColor.RED + "NOT RUNNING";
	}
	
	public ScreenSettings getSettings() {
		return settings;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public boolean isLinked() {
		return linked;
	}
	
	public Screen getScreen() {
		return this;
	}
	
	public ArrayList<Part> getParts() {	
		if(!parts.isEmpty()) return parts;
		for(int i = 0; i < width*height; i++) parts.add(new Part(new File(getPartsFolder(), i + ".yml")));
		return parts;
	}
	
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
			Files.copy(videoData.getThumbnail(), getThumbnail());
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
	
	public void remove() {
		for(int i = 0; i < getBlocks().size(); i++) {
			getFrames().get(i).remove();
			getBlocks().get(i).setType(Material.AIR);
		}
	}
	
	public void delete() {
		
		plugin.getRegisteredScreens().remove(this);
		remove();
		
		try {
			FileUtils.deleteDirectory(getFile().getParentFile());
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
							    	    			
													buffer = com.bergerkiller.bukkit.common.map.MapColorPalette.convertImage(imageRenderer.getBufferedImages()[j]);
													
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
	
	public void pause() {
		running = false;
	}
	
	public void resume() {
		running = true;
	}
	
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
	
	public void setSettings(ScreenSettings screenSettings) {
		this.settings = screenSettings;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public void setLowestFrame(ItemFrame lowest) {
		this.lowest = lowest;
	}
	
	public void setHighestFrame(ItemFrame highest) {
		this.highest = highest;
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
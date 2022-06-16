package fr.xxathyx.mediaplayer.screen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.image.renderer.ImageRenderer;
import fr.xxathyx.mediaplayer.items.ItemStacks;
import fr.xxathyx.mediaplayer.screen.settings.ScreenSettings;
import fr.xxathyx.mediaplayer.stream.Stream;
import fr.xxathyx.mediaplayer.util.AudioUtil;
import fr.xxathyx.mediaplayer.video.Video;
import fr.xxathyx.mediaplayer.video.data.VideoData;
import fr.xxathyx.mediaplayer.video.instance.VideoInstance;

public class Screen {

	private final Main plugin = Main.getPlugin(Main.class);
	
	private final Configuration configuration = new Configuration();
	private final ItemStacks itemStacks = new ItemStacks();
	
	private ScreenSettings settings;
	private int id;
	
	private Video video;
	private Stream stream;
	private VideoData videoData;
	private VideoInstance videoInstance;
	
	private boolean running = false;
	private boolean linked = true;
	
	public int[] tasks = {Bukkit.getScheduler().getPendingTasks().size(), Bukkit.getScheduler().getPendingTasks().size()+1};
		
	private final int[] ids;
	
	private int width;
	private int height;
		
	private ItemFrame lowest;
	private ItemFrame highest;
	
	private ArrayList<ItemFrame> frames = new ArrayList<>();
	private ArrayList<Block> blocks = new ArrayList<>();
	
	private ArrayList<UUID> listeners = new ArrayList<>();
	
	public Screen(VideoInstance videoInstance, ArrayList<ItemFrame> frames) {
		
		Video video = videoInstance.getVideo();
		
		this.frames = frames;
		this.settings = new ScreenSettings(video);
		this.id = plugin.getRegisteredScreens().size();
		this.video = video;
		this.videoData = video.getVideoData();
		this.videoInstance = videoInstance;
		this.ids = ArrayUtils.toPrimitive(Arrays.stream(video.getVideoData().getMaps().getIds().toArray()).map(Object::toString).map(Integer::valueOf).toArray(Integer[]::new));
		this.width = video.getVideoData().getMinecraftWidth();
		this.height = video.getVideoData().getMinecraftHeight();
	}
	
	public void remove() {
		for(int i = 0; i < blocks.size(); i++) {
			frames.get(i).remove();
			blocks.get(i).setType(Material.AIR);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void display() {
		
		plugin.getTasks().add(tasks[0]);
		plugin.getTasks().add(tasks[1]);
		
		if(!frames.isEmpty()) {
			for(int i = 0; i < frames.size(); i++) {
				frames.get(i).setItem(itemStacks.getMap(ids[i]));
			}
		}
				
		if(video.isStreamed()) {
			
			stream = new Stream(video);
			
			try {
				stream.update();
			}catch (IOException e) {
				e.printStackTrace();
			}
			
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
								
				if(running) {
					
					Collection<Entity> entities = getNearbyEntities(frames.get(ids.length/2).getLocation(), configuration.maximum_distance_to_receive());
					
					if(video.isAudioEnabled()) {
						for(Entity entity : entities) {
							if(entity.getType() == EntityType.PLAYER) {
								
								Player player = ((Player)entity);
								
								if(!listeners.contains(player.getUniqueId())) {
								    //player.setResourcePack("free hosting url");
									listeners.add(player.getUniqueId());
								}
							}
						}
						
						for(UUID uuid : listeners) {
							
							if(plugin.getPlayersScreens().containsKey(uuid)) {
								if(plugin.getPlayersScreens().get(uuid) == null) {
									
									plugin.getPlayersScreens().replace(uuid, getScreen());
									Player player = Bukkit.getPlayer(uuid);
									
									for(int i = 0; i < video.getAudioChannels(); i++) player.playSound(player.getLocation(), "mediaplayer." + i, 10, 1);
								}
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
                					System.out.println(settings.framerate-settings.fps + " frames added");
		                			settings.count = settings.count + (settings.framerate-settings.fps);
                				}
	                		}
		                	
		                	if(settings.showInformations) {
								String message = ChatColor.GREEN + "" + ChatColor.BOLD + settings.name + ": " + ChatColor.GREEN + getTimeLeft(settings.count) + ", " + settings.description;
								if(settings.showFPS) {
									message = message + ChatColor.GREEN + " - " + ChatColor.BOLD + settings.fps + ChatColor.GREEN + " FPS";
								}
			                	for(Player player : Bukkit.getOnlinePlayers()) {
				                	plugin.getActionBar().send(player, message);
			                	}
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
														if(entity.getType() == EntityType.PLAYER) {
															plugin.getMapUtil().update((Player)entity, ids[j], buffer);
														}
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
														if(entity.getType() == EntityType.PLAYER) {
															plugin.getMapUtil().update((Player)entity, ids[j], buffer);
														}
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
						if(video.isLoopping()) {
							settings.count = 0;
						}
						
						if(!video.isStreamed()) {
							end();
						}
					}
				}
			}
		}, 0L, 0L);
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
	
	public void pause() {
		running = false;
	}
	
	public void resume() {
		running = true;
	}
	
	public void end() {
		
		Bukkit.getScheduler().cancelTask(tasks[0]);
		running = false;
				
		video.getVideoData().loadThumbnail();
		
		for(int i = 0; i < frames.size(); i++) {
			frames.get(i).setItem(itemStacks.getMap(ids[i]));
		}
		
		for(UUID uuid : listeners) {
			for(int i = 0; i < video.getAudioChannels(); i++) AudioUtil.stopAudio(Bukkit.getPlayer(uuid), "mediaplayer." + i);
		}
		
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
	
	public VideoInstance getVideoInstance() {
		return videoInstance;
	}
	
	public Video getVideo() {
		return video;
	}
	
	public int[] getIds() {
		return ids;
	}
	
	public String getTimeLeft(int frame) {
		/*
		int leftFrames = video.getTotalFrames()-frame;
		int seconds = (int) Math.round((double) leftFrames/video.getFrameRate());
		
		int minutes = (int) Math.round((double) seconds/60);
		int hours = (int) Math.round((double) minutes/60);
		
		if(minutes < 1) {
			if(seconds == 1) {
				return String.valueOf(seconds) + " seconde";
			}
			return String.valueOf(seconds) + " secondes";
		}
		
		if(hours < 1) {
			if(minutes == 1) {
				return String.valueOf(minutes) + " minute";
			}
			return String.valueOf(minutes) + " minutes";
		}
		
		if(hours > 1) {
			if(hours == 1) {
				return String.valueOf(hours) + " heure";
			}
			return String.valueOf(hours) + " heures";
		}*/
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
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public ItemFrame getLowestFrame() {
		return lowest;
	}
	
	public ItemFrame getHighestFrame() {
		return highest;
	}
	
	public ArrayList<ItemFrame> getFrames() {
		return frames;
	}
	
	public ArrayList<Block> getBlocks() {
		return blocks;
	}
	
	public Screen getScreen() {
		return this;
	}
}
package fr.xxathyx.mediaplayer.video.commands;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.image.helpers.ImageHelper;
import fr.xxathyx.mediaplayer.interfaces.Interfaces;
import fr.xxathyx.mediaplayer.sound.SoundPlayer;
import fr.xxathyx.mediaplayer.sound.SoundType;
import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadConfigurations;
import fr.xxathyx.mediaplayer.video.Video;
import fr.xxathyx.mediaplayer.video.instance.VideoInstance;
import fr.xxathyx.mediaplayer.video.player.VideoPlayer;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/** 
* The VideoCommands class implements {@link CommandExecutor}, it grants an easy in
* game access to the plugin features concerning videos.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class VideoCommands implements CommandExecutor, TabCompleter {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
	private final Interfaces interfaces = new Interfaces();
	
    private final String[] commands = { "play", "load", "unload", "info", "delete", "start",  "stop", "pause", "resume", "download", "stop", "list" };
	
	/**
	* See Bukkit documentation : {@link CommandExecutor#onCommand(CommandSender, Command, String, String[])}
	* 
	* <p> Called every time the video commands are sent.
	*/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] arg3) {
		
		if(cmd.getName().equalsIgnoreCase("video")) {
			if(sender.hasPermission("mediaplayer.command.video")) {
								
				if(arg3.length >= 2) {
					if(arg3[0].equalsIgnoreCase("download")) {
						Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
							@Override
							public void run() {
								if(ImageHelper.isURL(arg3[1])) {
									try {
										URL url = new URL(arg3[1]);
										
										FileUtils.copyURLToFile(url, new File(configuration.getVideosFolder(), FilenameUtils.getName(url.getPath())));
																				
										new TaskAsyncLoadConfigurations().runTaskLaterAsynchronously(plugin, 20L);
										sender.sendMessage(configuration.video_downloaded(FilenameUtils.removeExtension(FilenameUtils.getName(url.getPath()))));
										return;
									}catch (IOException e) {
										e.printStackTrace();
									}
								}
								sender.sendMessage(configuration.invalid_url(arg3[1]));
								return;
							}
						});
						return true;
					}
										
					if(arg3[0].equalsIgnoreCase("live")) {
						Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
							@Override
							public void run() {
								if(ImageHelper.isURL(arg3[1])) {

									try {
										URL url = new URL(arg3[1]);
										UUID uuid = UUID.randomUUID();
										
										InputStream inputStream = null;
										OutputStream outputStream = null;
										
										inputStream = url.openStream();
										
									    File file = new File(configuration.getVideosFolder(), uuid.toString() + ".m3u8");
									    
									    outputStream = FileUtils.openOutputStream(file);
									    
									    IOUtils.copy(inputStream, outputStream);
									    
									    IOUtils.closeQuietly(inputStream);
									    IOUtils.closeQuietly(outputStream);
									    
									    plugin.getStreamsURL().put(uuid, url);
									    
										new TaskAsyncLoadConfigurations().runTaskLaterAsynchronously(plugin, 20L);
										sender.sendMessage("c'est dans la boite hbibi");
										return;
									}catch (IOException e) {
										e.printStackTrace();
									}	
								}
								sender.sendMessage(configuration.invalid_url(arg3[1]));
								return;
							}
						});
						return true;
					}
				}
				
				Player player = null;
				
				if(sender instanceof Player) {
					player = (Player) sender;
				}
				
				if(arg3.length == 1) {
					
					if(arg3[0].equalsIgnoreCase("help")) {
						sendHelp(sender, name);
						return true;
					}
					
					if(arg3[0].equalsIgnoreCase("play")) {
						sender.sendMessage(ChatColor.RED + "/video play <video>");
						return false;
					}
					
					if(arg3[0].equalsIgnoreCase("download")) {
						sender.sendMessage(ChatColor.RED + "/video download <url>");
						return false;
					}
					
					if(arg3[0].equalsIgnoreCase("load")) {
						sender.sendMessage(ChatColor.RED + "/video load <video>");
						return false;
					}
					
					if(arg3[0].equalsIgnoreCase("unload")) {
						sender.sendMessage(ChatColor.RED + "/video unload <video>");
						return false;
					}
					
					if(arg3[0].equalsIgnoreCase("info")) {
						sender.sendMessage(ChatColor.RED + "/video info <video>");
						return false;
					}
					
					if(arg3[0].equalsIgnoreCase("delete")) {
						sender.sendMessage(ChatColor.RED + "/video delete <video>");
						return false;
					}
										
					if(arg3[0].equalsIgnoreCase("list")) {
						
						if(plugin.getRegisteredVideos().isEmpty()) {
							sender.sendMessage(configuration.videos_empty_registered());
							return false;
						}
						
						String videosList = ChatColor.DARK_GREEN + configuration.available_videos() + ChatColor.GRAY + " (" + plugin.getRegisteredVideos().size() + ")" + "\n " + ChatColor.RESET;
						
						for(int i = 0; i < plugin.getRegisteredVideos().size(); i++) {
							
							int index = i + 1;
							
							String video = ChatColor.GREEN + plugin.getRegisteredVideos().get(i).getName() + "\n";
							videosList = (videosList + "\n" + ChatColor.GREEN + index + ". " +  video);
						}
						sender.sendMessage(videosList);
					    if(sender instanceof Player) player.sendMessage(configuration.videos_notice());
						return true;
					}
					
					if(sender instanceof Player) {
						if(plugin.getVideoPlayers().containsKey(player.getUniqueId())) {
							
							VideoPlayer videoPlayer = plugin.getVideoPlayers().get(player.getUniqueId());
							
							if(arg3[0].equalsIgnoreCase("start")) {
								
								//if(!Bukkit.getScheduler().isCurrentlyRunning(videoPlayer.getScreen().task[0])) videoPlayer.getScreen().setSettings(new ScreenSettings(videoPlayer.getScreen().getVideo())); videoPlayer.getScreen().display();
								
								videoPlayer.getScreen().setRunning(true);
								player.sendMessage(configuration.video_instance_started(videoPlayer.getScreen().getVideoInstance().getVideo().getName(), String.valueOf(videoPlayer.getScreen().getId())));
								if(sender instanceof Player) SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
								return true;
							}
							
							if(arg3[0].equalsIgnoreCase("stop")) {
								videoPlayer.getScreen().end();
								plugin.getVideoPlayers().remove(player.getUniqueId());
								player.sendMessage(configuration.video_instance_stopped(videoPlayer.getScreen().getVideoInstance().getVideo().getName()));
								return true;
							}
							
							if(arg3[0].equalsIgnoreCase("pause")) {
								
							}
							
							if(arg3[0].equalsIgnoreCase("resume")) {
								
							}
						}
					}
				}
				
				if(arg3.length == 2) {
										
					Video video = null;
					
					for(Video registeredVideo : plugin.getRegisteredVideos()) {
						if(registeredVideo.getName().equalsIgnoreCase(arg3[1])) {
							video = registeredVideo;
						}
					}
					
					if(video == null) {				
				        try { 
				            Integer.parseInt(arg3[1]); 
				        }catch (NumberFormatException e) { 
				        	sender.sendMessage(configuration.not_number());
				            return false;
				        }
				        
				        if(Integer.parseInt(arg3[1])-1 < 0) {
				        	sender.sendMessage(configuration.negative_number());
				        	return false;
				        }
				        
				        if(Integer.parseInt(arg3[1])-1 >= plugin.getRegisteredVideos().size()) {
				        	sender.sendMessage(configuration.video_invalid_index(arg3[1]));
				        	return false;
				        }
				        video = plugin.getRegisteredVideos().get(Integer.parseInt(arg3[1])-1);
					}
			        					
					if(video != null) {
						if(arg3[0].equalsIgnoreCase("play")) {
							
							if(!(sender instanceof Player)) {
								sender.sendMessage(configuration.invalid_sender());
								return false;
							}
							
							Player[] players = {player};
							Video[] videoTask = {video};
							
							Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

								@Override
								public void run() {
									
						        	if(!videoTask[0].isLoaded()) {
						        		players[0].sendMessage(configuration.video_not_loaded(videoTask[0].getName()));
										SoundPlayer.playSound(players[0], SoundType.NOTIFICATION_DOWN);
										return;
									}
						        	
						        	if(plugin.getPlayingVideos().size() <= configuration.maximum_playing_videos()) {
						        		
						        		if(plugin.getSelectedVideos().containsKey(players[0].getUniqueId())) {
						        			players[0].sendMessage(configuration.video_already_selected(videoTask[0].getName()));
											SoundPlayer.playSound(players[0], SoundType.NOTIFICATION_DOWN);
						        			return;
						        		}
						        		
						        		plugin.getPlayingVideos().add(videoTask[0].getName());
										
						        		VideoInstance videoInstance = new VideoInstance(videoTask[0]);
						        		
						        		plugin.getSelectedVideos().put(players[0].getUniqueId(), videoInstance);
						        		
						        		players[0].sendMessage(configuration.video_selected(videoTask[0].getName()));
						        		
										TextComponent dimension = new TextComponent(ChatColor.GRAY + "(Dimension: " + videoTask[0].getVideoData().getMinecraftWidth() + "x" + videoTask[0].getVideoData().getMinecraftHeight() + " -> " + ChatColor.BOLD + "/screen create " +
												videoTask[0].getVideoData().getMinecraftWidth() + " " + videoTask[0].getVideoData().getMinecraftHeight() + ChatColor.GRAY + ")");
										dimension.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "" + ChatColor.BOLD + "/" + ChatColor.GRAY +
												"screen create " + videoTask[0].getVideoData().getMinecraftWidth() + " " + videoTask[0].getVideoData().getMinecraftHeight()).create()));
										dimension.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/screen create " + videoTask[0].getVideoData().getMinecraftWidth() + " " +
												videoTask[0].getVideoData().getMinecraftHeight()));
										
										players[0].spigot().sendMessage(dimension);
						        		
										if(sender instanceof Player) players[0].sendMessage(configuration.videos_notice());
										SoundPlayer.playSound(players[0], SoundType.NOTIFICATION_UP);
										return;
						        	}
						        	players[0].sendMessage(configuration.too_much_playing());
						        	return;
								}
							});
							return true;
						}
						
						if(arg3[0].equalsIgnoreCase("load")) {
							
							Player[] players = {player};
							Video[] videoTask = {video};
							
							Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

								@Override
								public void run() {
									
									if(!videoTask[0].isLoaded()) {
										if(plugin.getLoadingVideos().size() <= configuration.maximum_loading_videos()) {
											if(!plugin.getLoadingVideos().contains(videoTask[0].getName())) {
												
									        	if(!videoTask[0].hasEnoughtSpace()) {
									        		sender.sendMessage(configuration.video_not_enought_space(videoTask[0].getName()));
													if(sender instanceof Player) SoundPlayer.playSound(players[0], SoundType.NOTIFICATION_DOWN);
									        		return;
									        	}
												
									        	videoTask[0].load();
												
									        	sender.sendMessage(configuration.video_load_requested());
										    	sender.sendMessage(configuration.video_load_notice());
										    	if(sender instanceof Player) players[0].sendMessage(configuration.videos_notice());
										    	if(sender instanceof Player) SoundPlayer.playSound(players[0], SoundType.NOTIFICATION_UP);
												return;
											}
											sender.sendMessage(configuration.video_already_loading(videoTask[0].getName()));
											return;
										}
										sender.sendMessage(configuration.too_much_loading());
										return;
									}
									sender.sendMessage(configuration.video_already_loaded(videoTask[0].getName()));
									if(sender instanceof Player) SoundPlayer.playSound(players[0], SoundType.NOTIFICATION_DOWN);
									return;
								}
							});
							return true;
						}
						
						if(arg3[0].equalsIgnoreCase("unload")) {
									
							Player[] players = {player};
							Video[] videoTask = {video};
							
							Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

								@Override
								public void run() {
									
									if(!videoTask[0].isLoaded()) {
										sender.sendMessage(configuration.video_not_loaded(videoTask[0].getName()));
										return;
									}
									
									try {
										videoTask[0].unload();
										sender.sendMessage(configuration.video_unloaded(videoTask[0].getName()));
										if(sender instanceof Player) SoundPlayer.playSound(players[0], SoundType.NOTIFICATION_UP);
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
							});
							return true;
						}
						
						if(arg3[0].equalsIgnoreCase("info")) {
							sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Informations: " + video.getName());
							sender.sendMessage("");
							sender.sendMessage(ChatColor.DARK_PURPLE + "name: " + ChatColor.LIGHT_PURPLE + video.getName());
							sender.sendMessage(ChatColor.DARK_PURPLE + "description: " + ChatColor.WHITE + video.getDescription());
							sender.sendMessage(ChatColor.DARK_PURPLE + "file-video-path: " + ChatColor.LIGHT_PURPLE + video.getVideoFile().getPath());
							sender.sendMessage(ChatColor.DARK_PURPLE + "stream: " + ChatColor.LIGHT_PURPLE + video.getVideoFile().getPath());
							sender.sendMessage(ChatColor.DARK_PURPLE + "stream-url: " + ChatColor.LIGHT_PURPLE + video.getVideoFile().getPath());
							sender.sendMessage(ChatColor.DARK_PURPLE + "enable-audio: " + ChatColor.LIGHT_PURPLE + Boolean.toString(video.isAudioEnabled()));
							sender.sendMessage(ChatColor.DARK_PURPLE + "audio-folder-path: " + ChatColor.LIGHT_PURPLE + video.getAudioFolder().getPath());
							sender.sendMessage(ChatColor.DARK_PURPLE + "audio-volume: " + ChatColor.LIGHT_PURPLE + video.getVolume());
							sender.sendMessage(ChatColor.DARK_PURPLE + "frames-folder: " + ChatColor.LIGHT_PURPLE + video.getFramesFolder().getPath());
							sender.sendMessage(ChatColor.DARK_PURPLE + "frame-rate: " + ChatColor.LIGHT_PURPLE + video.getFrameRate());
							sender.sendMessage(ChatColor.DARK_PURPLE + "frames: " + ChatColor.LIGHT_PURPLE + video.getTotalFrames());
							sender.sendMessage(ChatColor.DARK_PURPLE + "format: " + ChatColor.LIGHT_PURPLE + video.getFormat());
							sender.sendMessage(ChatColor.DARK_PURPLE + "width: " + ChatColor.LIGHT_PURPLE + video.getWidth());
							sender.sendMessage(ChatColor.DARK_PURPLE + "height: " + ChatColor.LIGHT_PURPLE + video.getHeight());
							sender.sendMessage(ChatColor.DARK_PURPLE + "duration: " + ChatColor.LIGHT_PURPLE + video.getDuration());
							sender.sendMessage(ChatColor.DARK_PURPLE + "speed: " + ChatColor.LIGHT_PURPLE + video.getSpeed());
							sender.sendMessage(ChatColor.DARK_PURPLE + "size: " + ChatColor.LIGHT_PURPLE + video.getSize());
							sender.sendMessage(ChatColor.DARK_PURPLE + "age-limit: " + ChatColor.LIGHT_PURPLE + Boolean.toString(video.isRestricted()));
							sender.sendMessage(ChatColor.DARK_PURPLE + "creation: " + ChatColor.LIGHT_PURPLE + video.getCreation());
							sender.sendMessage(ChatColor.DARK_PURPLE + "data-folder: " + ChatColor.LIGHT_PURPLE + video.getDataFolder().getPath());
							sender.sendMessage(ChatColor.DARK_PURPLE + "show-informations: " + ChatColor.LIGHT_PURPLE + Boolean.toString(video.getVideoData().getShowInformations()));
							sender.sendMessage(ChatColor.DARK_PURPLE + "show-informations: " + ChatColor.LIGHT_PURPLE + Boolean.toString(video.getVideoData().getShowFPS()));
							sender.sendMessage(ChatColor.DARK_PURPLE + "run-on-startup: " + ChatColor.LIGHT_PURPLE + Boolean.toString(video.getVideoData().getRunOnStartup()));
							sender.sendMessage(ChatColor.DARK_PURPLE + "views: " + ChatColor.LIGHT_PURPLE + video.getViews());
							sender.sendMessage(ChatColor.DARK_PURPLE + "index: " + ChatColor.LIGHT_PURPLE + video.getIndex());
							sender.sendMessage(ChatColor.DARK_PURPLE + "status: " + ChatColor.LIGHT_PURPLE + video.getStatus());
							return true;
						}
											
						if(arg3[0].equalsIgnoreCase("delete") | arg3[0].equalsIgnoreCase("del")) {
							
							Player[] players = {player};
							Video[] videoTask = {video};
							
							Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

								@Override
								public void run() {
									try {
										videoTask[0].delete();
									}catch (IOException e) {
										e.printStackTrace();
									}
									sender.sendMessage(configuration.video_deleted(name));
									if(sender instanceof Player) SoundPlayer.playSound(players[0], SoundType.NOTIFICATION_UP);
									if(sender instanceof Player) players[0].sendMessage(configuration.videos_notice());
								}
							});
							return true;
						}
					}
					sender.sendMessage(configuration.video_invalid(arg3[1]));
					return false;
				}
								
				if(arg3.length == 4) {
					
					if(arg3[1].equalsIgnoreCase("set")) {
						
						Video video = null;
						
						for(Video registeredVideo : plugin.getRegisteredVideos()) {
							if(registeredVideo.getName().equalsIgnoreCase(arg3[0])) {
								video = registeredVideo;
							}
						}
						
						if(video == null) {						
					        try { 
					            Integer.parseInt(arg3[0]); 
					        }catch (NumberFormatException e) { 
					        	sender.sendMessage(configuration.not_number());
					            return false;
					        }
					        
					        if(Integer.parseInt(arg3[0])-1 < 0) {
					        	sender.sendMessage(configuration.negative_number());
					        	return false;
					        }
					        
					        if(Integer.parseInt(arg3[0])-1 >= plugin.getRegisteredVideos().size()) {
					        	sender.sendMessage(configuration.video_invalid_index(String.valueOf(Integer.parseInt(arg3[0])-1)));
					        	return false;
					        }
					        video = plugin.getRegisteredVideos().get(Integer.parseInt(arg3[0])-1);
						}
						
						if(video != null) {
							if(arg3[2].equalsIgnoreCase("description") | arg3[2].equalsIgnoreCase("desc")) {
								
								String description = "";
								
					    		for(int i = 3; i < arg3.length; i++){
					                String arg = (arg3[i] + " ");
					                description = (description + arg);
					            }
					    		description = description.substring(0, description.length() - 1);
					    		
					    		try {
									video.setDescription(description);
									sender.sendMessage(configuration.video_description_updated(video.getName(), description));
									return true;
								}catch (IOException | InvalidConfigurationException e) {
									e.printStackTrace();
								}
							}
							
							if(arg3[2].equalsIgnoreCase("frame-rate")) {
						        try { 
						            Double.parseDouble(arg3[3]); 
						        }catch (NumberFormatException e) { 
						        	sender.sendMessage(configuration.not_number());
						            return false;
						        }
						        
						        if(Double.parseDouble(arg3[3]) < 0) {
						        	sender.sendMessage(configuration.negative_number());
						        	return false;
						        }
						        
						        double framerate = Double.parseDouble(arg3[3]);
						        
						        try {
									video.setFrameRate(framerate);
									sender.sendMessage(configuration.video_framerate_updated(video.getName(), String.valueOf(framerate)));
									return true;
								}catch (IOException | InvalidConfigurationException e) {
									e.printStackTrace();
								}
							}
							
							if(arg3[2].equalsIgnoreCase("speed")) {
						        try { 
						            Double.parseDouble(arg3[3]); 
						        }catch (NumberFormatException e) { 
						        	sender.sendMessage(configuration.not_number());
						            return false;
						        }
						        
						        if(Double.parseDouble(arg3[3]) < 0) {
						        	sender.sendMessage(configuration.negative_number());
						        	return false;
						        }
						        
						        double speed = Double.parseDouble(arg3[3]);
						        
						        try {
									video.setSpeed(speed);
									sender.sendMessage(configuration.video_speed_updated(video.getName(), String.valueOf(speed)));
									return true;
								}catch (IOException | InvalidConfigurationException e) {
									e.printStackTrace();
								}
							}
							
							if(arg3[2].equalsIgnoreCase("volume")) {
						        try { 
						            Double.parseDouble(arg3[3]); 
						        } catch (NumberFormatException e) { 
						        	sender.sendMessage(configuration.not_number());
						            return false;
						        }
						        
						        if(Double.parseDouble(arg3[3]) < 0) {
						        	sender.sendMessage(configuration.negative_number());
						        	return false;
						        }
						        
						        double volume = Double.parseDouble(arg3[3]);
						        
						        try {
									video.setVolume(volume);
									sender.sendMessage(configuration.video_volume_updated(video.getName(), String.valueOf(volume)));
									return true;
								}catch (IOException | InvalidConfigurationException e) {
									e.printStackTrace();
								}
							}
							
							if(arg3[2].equalsIgnoreCase("age-limit") | arg3[2].equalsIgnoreCase("al")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setRestricted(true);
										sender.sendMessage(configuration.video_age_limit_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setRestricted(false);
										sender.sendMessage(configuration.video_age_limit_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}	
							}
							
							if(arg3[2].equalsIgnoreCase("audio")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setEnableAudio(true);
										sender.sendMessage(configuration.video_audio_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setEnableAudio(false);
										sender.sendMessage(configuration.video_audio_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
							}
							
							if(arg3[2].equalsIgnoreCase("looping")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setLooping(true);
										sender.sendMessage(configuration.video_loop_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setLooping(false);
										sender.sendMessage(configuration.video_loop_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}	
							}
							
							if(arg3[2].equalsIgnoreCase("real-time-rendering") | arg3[2].equalsIgnoreCase("rtr")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setRealTimeRendering(true);
										sender.sendMessage(configuration.video_real_time_rendering_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setRealTimeRendering(false);
										sender.sendMessage(configuration.video_real_time_rendering_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}	
							}
							
							if(arg3[2].equalsIgnoreCase("skip-duplicated-frames") | arg3[2].equalsIgnoreCase("sdf")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setSkipDuplicatedFrames(true);
										sender.sendMessage(configuration.video_skip_duplicated_frames_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setSkipDuplicatedFrames(false);
										sender.sendMessage(configuration.video_skip_duplicated_frames_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}	
							}
							
							if(arg3[2].equalsIgnoreCase("show-informations") | arg3[2].equalsIgnoreCase("si")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setShowInformations(true);
										sender.sendMessage(configuration.video_show_informations_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setShowInformations(false);
										sender.sendMessage(configuration.video_show_informations_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}	
							}
							
							if(arg3[2].equalsIgnoreCase("show-fps")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setShowFPS(true);
										sender.sendMessage(configuration.video_show_fps_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setShowFPS(false);
										sender.sendMessage(configuration.video_show_fps_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}	
							}
							
							if(arg3[2].equalsIgnoreCase("run-on-startup") | arg3[2].equalsIgnoreCase("ros")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setRunOnStartup(true);
										sender.sendMessage(configuration.video_run_on_startup_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setRunOnStartup(false);
										sender.sendMessage(configuration.video_run_on_startup_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}	
							}
						}
						sender.sendMessage(configuration.video_invalid(arg3[0]));
						return false;
					}
				}
				sendHelp(sender, name);
				return false;
			}
			sender.sendMessage(configuration.insufficient_permissions());
			return false;
		}
		
		if(cmd.getName().equalsIgnoreCase("videos")) {
			if(sender.hasPermission("mediaplayer.command.videos")) {
				
				if(arg3.length == 1) {
					if(arg3[0].equalsIgnoreCase("reload")) {		
						new TaskAsyncLoadConfigurations().runTaskAsynchronously(plugin);
						sender.sendMessage(configuration.videos_reload_requested());
						return true;
					}
					
					if(arg3[0].equalsIgnoreCase("cancel-tasks") | arg3[0].equalsIgnoreCase("ct")) {
						for(int id : plugin.getTasks()) { Bukkit.getScheduler().cancelTask(id); }
						sender.sendMessage(configuration.videos_canceled_tasks(String.valueOf(plugin.getTasks().size())));
						plugin.getTasks().clear();
						return true;
					}
				}
				
				if(sender instanceof Player) {
					
					Player player = (Player) sender;
					
					player.openInventory(interfaces.getVideos(0));
					plugin.getPages().put(player.getUniqueId(), 0);
					return true;
				}
				sendHelp(sender, name);
				return false;
			}
			sender.sendMessage(configuration.insufficient_permissions());
			return false;
		}
		sendHelp(sender, name);
		return false;
	}
	
	/**
	* See Bukkit documentation : {@link TabCompleter}
	* 
	* <p>Called every time a player is try to auto-complete command.
	*/
	
	@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
				
        List<String> completions = new ArrayList<>();  
        
    	ArrayList<String> arguments = new ArrayList<>();
    	ArrayList<String> videos = new ArrayList<>();
    	
    	for(Video video : plugin.getRegisteredVideos()) {
    		videos.add(video.getName());
    		videos.add(String.valueOf(plugin.getRegisteredVideos().indexOf(video)));
    	}
    	
		arguments.addAll(Arrays.asList(commands));
		arguments.addAll(videos);
    	
        StringUtil.copyPartialMatches(args[0], arguments, completions);
                
        if(args.length > 1) {
        	
            String[] commands = { "play", "load", "unload", "info", "delete" };
        	
            if(Arrays.asList(commands).contains(args[0])) {
                StringUtil.copyPartialMatches(args[1], videos, completions);
            }
            
            String[] subcommands = { "set" };
        	
            StringUtil.copyPartialMatches(args[1], Arrays.asList(subcommands), completions);
            
            if(args.length > 2) {
            	
                String[] modifiers = { "description", "frame-rate", "speed", "volume", "age-limit", "audio",  "looping", "real-time-rendering",
                		"skip-duplicated-frames", "show-informations", "show-fps", "run-on-startup" };
                
                StringUtil.copyPartialMatches(args[2], Arrays.asList(modifiers), completions);
            }
        }
        
        Collections.sort(completions);
        
        return completions;
    }
	
	/**
	* Sends help messages to a {@link CommandSender} concerning video commands.
	* 
	* @param sender The command sender that has sent the command, can be {@link Bukkit#getConsoleSender()}.
	* @param cmd The command name that is sent by the command sender.
	*/
	
	public void sendHelp(CommandSender sender, String cmd) {
		sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Video's commands");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " play <video>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " load <video>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " unload <video>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " info <video>");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " delete <video>");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " start (selected-video)");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " stop (selected-video)");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " pause (selected-video)");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " resume (selected-video)");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " download <url>");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " <video> set description <description>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " <video> set frame-rate <integer>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " <video> set speed <double>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " <video> set volume <double>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " <video> set age-limit <true|false>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " <video> set audio <true|false>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " <video> set looping <true|false>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " <video> set real-time-rendering <true|false>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " <video> set skip-duplicated-frames <true|false>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " <video> set show-informations <true|false>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " <video> set show-fps <true|false>");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " <video> set run-on-startup <true|false>");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " list");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /videos");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /videos" + ChatColor.AQUA + " reload");
		sender.sendMessage(ChatColor.DARK_AQUA + "» /videos" + ChatColor.AQUA + " cancel-tasks");
	}
}
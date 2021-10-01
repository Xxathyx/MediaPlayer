package fr.xxathyx.mediaplayer.video.commands;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

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

public class VideoCommands implements CommandExecutor {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
	private final Interfaces interfaces = new Interfaces();
	
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
				}
				
				if(!(sender instanceof Player)) {
					sender.sendMessage(configuration.invalid_sender());
					return false;
				}
				
				Player player = (Player) sender;
				
				if(arg3.length == 1) {
					
					if(arg3[0].equalsIgnoreCase("help")) {
						sendHelp(sender, name);
						return true;
					}
					
					if(arg3[0].equalsIgnoreCase("play")) {
						sender.sendMessage(ChatColor.RED + "/video play <video>");
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
							player.sendMessage(configuration.videos_empty_registered());
							return false;
						}
						
						String videosList = ChatColor.DARK_GREEN + configuration.available_videos() + ChatColor.GRAY + " (" + plugin.getRegisteredVideos().size() + ")" + "\n " + ChatColor.RESET;
						
						for(int i = 0; i < plugin.getRegisteredVideos().size(); i++) {
							
							int index = i + 1;
							
							String video = ChatColor.GREEN + plugin.getRegisteredVideos().get(i).getName() + "\n";
							videosList = (videosList + "\n" + ChatColor.GREEN + index + ". " +  video);
						}
						player.sendMessage(videosList);
					    player.sendMessage(configuration.videos_notice());
						return true;
					}
					
					if(plugin.getVideoPlayers().containsKey(player.getUniqueId())) {
						
						VideoPlayer videoPlayer = plugin.getVideoPlayers().get(player.getUniqueId());
						
						if(arg3[0].equalsIgnoreCase("start")) {
							
							//if(!Bukkit.getScheduler().isCurrentlyRunning(videoPlayer.getScreen().task[0])) videoPlayer.getScreen().setSettings(new ScreenSettings(videoPlayer.getScreen().getVideo())); videoPlayer.getScreen().display();
							
							videoPlayer.getScreen().setRunning(true);
							player.sendMessage(configuration.video_instance_started(videoPlayer.getScreen().getVideoInstance().getVideo().getName(), String.valueOf(videoPlayer.getScreen().getId())));
							SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
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
				        	player.sendMessage(configuration.video_invalid_index(arg3[1]));
				        	return false;
				        }
				        video = plugin.getRegisteredVideos().get(Integer.parseInt(arg3[1])-1);
					}
			        					
					if(video != null) {
						if(arg3[0].equalsIgnoreCase("play")) {
							
				        	if(!video.isLoaded()) {
								player.sendMessage(configuration.video_not_loaded(video.getName()));
								SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
								return false;
							}
				        	
				        	if(plugin.getPlayingVideos().size() <= configuration.maximum_playing_videos()) {
				        		
				        		if(plugin.getSelectedVideos().containsKey(player.getUniqueId())) {
				        			player.sendMessage(configuration.video_already_selected(video.getName()));
									SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
				        			return false;
				        		}
				        		
				        		plugin.getPlayingVideos().add(video.getName());
								
				        		VideoInstance videoInstance = new VideoInstance(video);
				        		
				        		plugin.getSelectedVideos().put(player.getUniqueId(), videoInstance);
				        		
				        		player.sendMessage(configuration.video_selected(video.getName()));
				        		
								TextComponent dimension = new TextComponent(ChatColor.GRAY + "(Dimension: " + video.getVideoData().getMinecraftWidth() + "x" + video.getVideoData().getMinecraftHeight() + " -> " + ChatColor.BOLD + "/screen create " +
										video.getVideoData().getMinecraftWidth() + " " + video.getVideoData().getMinecraftHeight() + ChatColor.GRAY + ")");
								dimension.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "" + ChatColor.BOLD + "/" + ChatColor.GRAY +
										"screen create " + video.getVideoData().getMinecraftWidth() + " " + video.getVideoData().getMinecraftHeight()).create()));
								dimension.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/screen create " + video.getVideoData().getMinecraftWidth() + " " +
										video.getVideoData().getMinecraftHeight()));
								
								player.spigot().sendMessage(dimension);
				        		
								player.sendMessage(configuration.videos_notice());
								SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
								return true;
				        	}
				        	player.sendMessage(configuration.too_much_playing());
				        	return false;
						}
						
						if(arg3[0].equalsIgnoreCase("load")) {
							
							if(!video.isLoaded()) {
								if(plugin.getLoadingVideos().size() <= configuration.maximum_loading_videos()) {
									if(!plugin.getLoadingVideos().contains(video.getName())) {
										
							        	if(!video.hasEnoughtSpace()) {
							        		player.sendMessage(configuration.video_not_enought_space(video.getName()));
											SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
							        		return false;
							        	}
										
										video.load();
										
								    	player.sendMessage(configuration.video_load_requested());
								    	player.sendMessage(configuration.video_load_notice());
										player.sendMessage(configuration.videos_notice());
										SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
										return true;
									}
									player.sendMessage(configuration.video_already_loading(video.getName()));
									return false;
								}
								player.sendMessage(configuration.too_much_loading());
								return false;
							}
							player.sendMessage(configuration.video_already_loaded(video.getName()));
							SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
							return false;
						}
						
						if(arg3[0].equalsIgnoreCase("unload")) {
							
							if(!video.isLoaded()) {
								player.sendMessage(configuration.video_not_loaded(video.getName()));
								return false;
							}
							
							Video[] videoTask = {video};
							
							Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

								@Override
								public void run() {
									try {
										videoTask[0].unload();
										player.sendMessage(configuration.video_unloaded(videoTask[0].getName()));
										SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
									}catch (IOException e) {
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
											
						if(arg3[0].equalsIgnoreCase("delete")) {
							
							Video[] videoTask = {video};
							
							Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

								@Override
								public void run() {
									try {
										videoTask[0].delete();
									}catch (IOException e) {
										e.printStackTrace();
									}
									player.sendMessage(configuration.video_deleted(name));
									SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
									player.sendMessage(configuration.videos_notice());
								}
							});
							return true;
						}
					}
					player.sendMessage(configuration.video_invalid(arg3[1]));
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
					        	player.sendMessage(configuration.video_invalid_index(String.valueOf(Integer.parseInt(arg3[0])-1)));
					        	return false;
					        }
					        video = plugin.getRegisteredVideos().get(Integer.parseInt(arg3[0])-1);
						}
						
						if(video != null) {
							if(arg3[2].equalsIgnoreCase("description")) {
								
								String description = "";
								
					    		for(int i = 3; i < arg3.length; i++){
					                String arg = (arg3[i] + " ");
					                description = (description + arg);
					            }
					    		description = description.substring(0, description.length() - 1);
					    		
					    		try {
									video.setDescription(description);
									player.sendMessage(configuration.video_description_updated(video.getName(), description));
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
									player.sendMessage(configuration.video_speed_updated(video.getName(), String.valueOf(speed)));
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
									player.sendMessage(configuration.video_volume_updated(video.getName(), String.valueOf(volume)));
									return true;
								}catch (IOException | InvalidConfigurationException e) {
									e.printStackTrace();
								}
							}
							
							if(arg3[2].equalsIgnoreCase("age-limit")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setRestricted(true);
										player.sendMessage(configuration.video_age_limit_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setRestricted(false);
										player.sendMessage(configuration.video_age_limit_disabled(video.getName()));
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
										player.sendMessage(configuration.video_audio_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setEnableAudio(false);
										player.sendMessage(configuration.video_audio_disabled(video.getName()));
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
										player.sendMessage(configuration.video_loop_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setLooping(false);
										player.sendMessage(configuration.video_loop_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}	
							}
							
							if(arg3[2].equalsIgnoreCase("real-time-rendering")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setRealTimeRendering(true);
										player.sendMessage(configuration.video_skip_frames_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setRealTimeRendering(false);
										player.sendMessage(configuration.video_skip_frames_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}	
							}
							
							if(arg3[2].equalsIgnoreCase("skip-duplicated-frames")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setSkipDuplicatedFrames(true);
										player.sendMessage(configuration.video_skip_duplicated_frames_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setSkipDuplicatedFrames(false);
										player.sendMessage(configuration.video_skip_duplicated_frames_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}	
							}
							
							if(arg3[2].equalsIgnoreCase("show-informations")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setShowInformations(true);
										player.sendMessage(configuration.video_show_informations_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setShowInformations(false);
										player.sendMessage(configuration.video_show_informations_disabled(video.getName()));
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
										player.sendMessage(configuration.video_show_fps_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setShowFPS(false);
										player.sendMessage(configuration.video_show_fps_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}	
							}
							
							if(arg3[2].equalsIgnoreCase("run-on-startup")) {
								if(arg3[3].equalsIgnoreCase("true")) {
									try {
										video.setRunOnStartup(true);
										player.sendMessage(configuration.video_run_on_startup_enabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}
								
								if(arg3[3].equalsIgnoreCase("false")) {
									try {
										video.setRunOnStartup(false);
										player.sendMessage(configuration.video_run_on_startup_disabled(video.getName()));
										return true;
									}catch (IOException | InvalidConfigurationException e) {
										e.printStackTrace();
									}
								}	
							}
						}
						player.sendMessage(configuration.video_invalid(arg3[0]));
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
				}
				
				if(!(sender instanceof Player)) {
					sender.sendMessage(configuration.invalid_sender());
					return false;
				}
				
				Player player = (Player) sender;
				
				try {
					player.openInventory(interfaces.getVideos());
					return true;
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
			sender.sendMessage(configuration.insufficient_permissions());
			return false;
		}
		sendHelp(sender, name);
		return false;
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
		sender.sendMessage(ChatColor.DARK_AQUA + "» /" + cmd + ChatColor.AQUA + " link <video>");
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
	}
}
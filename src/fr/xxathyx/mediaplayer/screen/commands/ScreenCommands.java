package fr.xxathyx.mediaplayer.screen.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.bukkit.util.Vector;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.interfaces.Interfaces;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.sound.SoundPlayer;
import fr.xxathyx.mediaplayer.sound.SoundType;
import fr.xxathyx.mediaplayer.tasks.TaskSyncLoadScreens;
import fr.xxathyx.mediaplayer.util.FacingLocation;
import fr.xxathyx.mediaplayer.video.Video;

/** 
* The ScreenCommands class implements {@link CommandExecutor}, it grants an easy access
* to the plugin features concerning screens.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class ScreenCommands implements CommandExecutor, TabCompleter {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
	private final Interfaces interfaces = new Interfaces();
	
    private final String[] commands = { "create", "info", "teleport", "remove" };
	
	/**
	* See Bukkit documentation : {@link CommandExecutor#onCommand(CommandSender, Command, String, String[])}
	* 
	* <p>Called every time the screen command is sent.
	*/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] arg3) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(configuration.invalid_sender());
			return false;
		}
		
		if(cmd.getName().equalsIgnoreCase("screen")) {
			if(sender.hasPermission("mediaplayer.command.screen")) {
				
				Player player = (Player) sender;
				
				if(arg3.length == 0) {
					sendHelp(sender, msg);
					return true;
				}
				
				if(arg3.length == 1) {
					
					if(arg3[0].equalsIgnoreCase("help")) {
						sendHelp(sender, msg);
						return true;
					}
					
					if(arg3[0].equalsIgnoreCase("create")) {
						player.sendMessage(ChatColor.RED + "/screen create <video-index>");
						player.sendMessage(ChatColor.RED + "/screen create <width> <height>");
						return false;
					}
					
					if(arg3[0].equalsIgnoreCase("info")) {
						player.sendMessage(ChatColor.RED + "/screen info <screen-index>");
						return false;
					}
					
					if(arg3[0].equalsIgnoreCase("teleport")) {
						player.sendMessage(ChatColor.RED + "/screen teleport <screen-index>");
						return false;
					}
					
					if(arg3[0].equalsIgnoreCase("remove")) {
						player.sendMessage(ChatColor.RED + "/screen remove <screen-index>");
						return false;
					}
				}
				
				if(arg3.length > 2) {
					if(arg3[0].equalsIgnoreCase("create")) {
				        try { 
				            Integer.parseInt(arg3[1]); 
				        }catch (NumberFormatException e) { 
				        	sender.sendMessage(configuration.not_number());
				            return false;
				        }
				        
				        if(Integer.parseInt(arg3[1]) < 0) {
				        	sender.sendMessage(configuration.negative_number());
				        	return false;
				        }
						
				        try { 
				            Integer.parseInt(arg3[2]); 
				        }catch (NumberFormatException e) { 
				        	sender.sendMessage(configuration.not_number());
				            return false;
				        }
				        
				        if(Integer.parseInt(arg3[2]) < 0) {
				        	sender.sendMessage(configuration.negative_number());
				        	return false;
				        }
						
				        int width = Integer.parseInt(arg3[1]);
				        int height = Integer.parseInt(arg3[2]);
				        
				        createScreen(player, width, height);
				        player.sendMessage(configuration.screen_created(width + "x" + height));
						SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
				        return true;
					}
				}
				
				if(arg3.length > 1) {
					
			        try { 
			            Integer.parseInt(arg3[1]); 
			        }catch (NumberFormatException e) { 
			        	sender.sendMessage(configuration.not_number());
			            return false;
			        }
			        
			        if(Integer.parseInt(arg3[1]) < 0) {
			        	sender.sendMessage(configuration.negative_number());
			        	return false;
			        }
			        					
					if(arg3[0].equalsIgnoreCase("create")) {
						
				        if(Integer.parseInt(arg3[1]) >= plugin.getRegisteredVideos().size()) {
				        	player.sendMessage(configuration.video_invalid_index(arg3[1]));
				        	return false;
				        }
				        
				        Video video = plugin.getRegisteredVideos().get(Integer.parseInt(arg3[1]));
				        
				    	int width = (int) (Math.round(video.getWidth()/128)+1);
				    	int height = (int) (Math.round(video.getHeight()/128)+1);
				    	
				        createScreen(player, width, height);
				        player.sendMessage(configuration.screen_created(width + "x" + height));
						SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
				        return true;
					}
					
			        if(Integer.parseInt(arg3[1]) >= plugin.getRegisteredScreens().size()) {
			        	player.sendMessage(configuration.screen_invalid_index(arg3[1]));
			        	return false;
			        }
			        
			        Screen screen = plugin.getRegisteredScreens().get(Integer.parseInt(arg3[1]));
					
					if(arg3[0].equalsIgnoreCase("info")) {
						player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Informations: " + arg3[1]);
						player.sendMessage("");
						player.sendMessage(ChatColor.DARK_PURPLE + "index: " + ChatColor.LIGHT_PURPLE + arg3[1]);
						player.sendMessage(ChatColor.DARK_PURPLE + "video: " + ChatColor.WHITE + screen.getVideoInstance().getVideo().getName());
						player.sendMessage(ChatColor.DARK_PURPLE + "running: " + ChatColor.WHITE + screen.isRunning());
						player.sendMessage(ChatColor.DARK_PURPLE + "task-id: " + ChatColor.LIGHT_PURPLE + screen.tasks[0]);
						player.sendMessage(ChatColor.DARK_PURPLE + "width: " + ChatColor.LIGHT_PURPLE + screen.getWidth());
						player.sendMessage(ChatColor.DARK_PURPLE + "height: " + ChatColor.LIGHT_PURPLE + screen.getHeight());
						player.sendMessage(ChatColor.DARK_PURPLE + "lowest-frame: " + ChatColor.DARK_PURPLE + "x: " + ChatColor.LIGHT_PURPLE +
								screen.getLowestFrame().getLocation().getBlockX() + ChatColor.DARK_PURPLE + " y: " + ChatColor.LIGHT_PURPLE +
								screen.getLowestFrame().getLocation().getBlockY() + ChatColor.DARK_PURPLE + " z: " + ChatColor.LIGHT_PURPLE +
								screen.getLowestFrame().getLocation().getBlockZ());
						player.sendMessage(ChatColor.DARK_PURPLE + "highest-frame: " + ChatColor.DARK_PURPLE + "x: " + ChatColor.LIGHT_PURPLE +
								screen.getHighestFrame().getLocation().getBlockX() + ChatColor.DARK_PURPLE + " y: " + ChatColor.LIGHT_PURPLE +
								screen.getHighestFrame().getLocation().getBlockY() + ChatColor.DARK_PURPLE + " z: " + ChatColor.LIGHT_PURPLE +
								screen.getHighestFrame().getLocation().getBlockZ());
						player.sendMessage(ChatColor.DARK_PURPLE + "blocks: " + ChatColor.LIGHT_PURPLE + screen.getBlocks().size());
						player.sendMessage(ChatColor.DARK_PURPLE + "frames: " + ChatColor.LIGHT_PURPLE + screen.getFrames().size());
						return true;
					}
					
					if(arg3[0].equalsIgnoreCase("teleport") | arg3[0].equalsIgnoreCase("tp")) {
						player.teleport(screen.getFrames().get(0));
						player.sendMessage(configuration.screen_teleport(arg3[1]));
						return true;
					}
					
					if(arg3[0].equalsIgnoreCase("remove")) {
						screen.remove();
						player.sendMessage(configuration.screen_removed(arg3[1]));
						SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
						return true;
					}
					
					if(arg3[0].equalsIgnoreCase("delete")) {
						screen.delete();
						player.sendMessage(configuration.screen_deleted(arg3[1]));
						SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
						return true;
					}
				}
			}
			sender.sendMessage(configuration.insufficient_permissions());
			return false;
		}
		
		if(cmd.getName().equalsIgnoreCase("screens")) {
			if(sender.hasPermission("mediaplayer.command.screens")) {
								
				if(plugin.getRegisteredScreens().isEmpty()) {
					sender.sendMessage(configuration.no_screen_playing());
					if(sender instanceof Player) SoundPlayer.playSound((Player)sender, SoundType.NOTIFICATION_DOWN);
					return false;
				}
				
				if(arg3.length > 0) {
					if(arg3[0].equalsIgnoreCase("reload")) {
						new TaskSyncLoadScreens().runTask(plugin);
						sender.sendMessage(configuration.screens_reload_requested());
						return true;
					}
				}
				
				if(sender instanceof Player) {
					
					Player player = (Player) sender;
					
					player.openInventory(interfaces.getScreens(0));
					plugin.getScreensPages().put(player.getUniqueId(), 0);
					return true;
				}
			}
			sender.sendMessage(configuration.insufficient_permissions());
			return false;
		}
		sendHelp(sender, msg);
		return false;
	}
	
	/**
	* Creates an screen structure according to a {@link Player#getEyeLocation()}, and a structure width
	* and height, the {@link Player#getEyeLocation()} vector is multiplied three times.
	* 
	* <p>After construction the screen is registered.
	* 
	* @return An Arraylist of placed ItemFrames on the screen.
	*/
	
	@SuppressWarnings("deprecation")
	public static ArrayList<ItemFrame> createScreen(Player player, int width, int height) {
		
		ArrayList<ItemFrame> frames = new ArrayList<>();
		ArrayList<Block> blocks = new ArrayList<>();
		
	    Main plugin = Main.getPlugin(Main.class);
		Configuration configuration = new Configuration();
		
		Vector vector = player.getEyeLocation().getDirection();
		vector.multiply(3);
		
		Location location = player.getEyeLocation().add(vector);
		
		String entityName = "item_frame";
		if(configuration.glowing_screen_frames_support()) entityName = "glow_item_frame";
		
		try {
			for(int i = 0; i < width; i++) {
				for(int j = 0; j < height; j++) {
					
					ItemFrame itemFrame = null;			        
					Location locBlock = new Location(location.getWorld(), location.getX(), location.getY()+j, location.getZ());
					
					if(FacingLocation.getCardinalDirection(player).equals("N")) {
						locBlock.add(i, 0, 0);
						locBlock.getBlock().setType(Material.getMaterial(configuration.screen_block()));
						itemFrame = (ItemFrame) player.getWorld().spawnEntity(new Location(player.getWorld(), locBlock.getBlockX(), locBlock.getBlockY(), locBlock.getBlockZ()+1), EntityType.fromName(entityName));
					}
					if(FacingLocation.getCardinalDirection(player).equals("E")) {
						locBlock.subtract(0, 0, i);
						locBlock.getBlock().setType(Material.getMaterial(configuration.screen_block()));
						itemFrame = (ItemFrame) player.getWorld().spawnEntity(new Location(player.getWorld(), locBlock.getBlockX()-1, locBlock.getBlockY(), locBlock.getBlockZ()), EntityType.fromName(entityName));
					}
					if(FacingLocation.getCardinalDirection(player).equals("S")) {
						locBlock.subtract(i, 0, 0);
						locBlock.getBlock().setType(Material.getMaterial(configuration.screen_block()));
						itemFrame = (ItemFrame) player.getWorld().spawnEntity(new Location(player.getWorld(), locBlock.getBlockX(), locBlock.getBlockY(), locBlock.getBlockZ()-1), EntityType.fromName(entityName));
					}
					if(FacingLocation.getCardinalDirection(player).equals("W")) {
						locBlock.add(0, 0, i);
						locBlock.getBlock().setType(Material.getMaterial(configuration.screen_block()));
						itemFrame = (ItemFrame) player.getWorld().spawnEntity(new Location(player.getWorld(), locBlock.getBlockX()+1, locBlock.getBlockY(), locBlock.getBlockZ()), EntityType.fromName(entityName));
					}
									
					frames.add(itemFrame);
					blocks.add(locBlock.getBlock());
				}
			}
						
			Screen screen = new Screen(UUID.randomUUID(), width, height, frames, blocks);
			screen.createConfiguration(FacingLocation.getCardinalDirection(player), frames.get(0).getLocation());
						
			for(Block block : blocks) plugin.getScreensBlocks().put(block, screen);
			for(ItemFrame frame : frames) plugin.getScreensFrames().put(frame, screen);
			
			plugin.getRegisteredScreens().add(screen);
		}catch (IllegalArgumentException | NullPointerException e) {
			player.sendMessage(configuration.screen_cannot_create());
		}
		return frames;
	}
	
	/**
	* See Bukkit documentation : {@link TabCompleter}
	* 
	* <p>Called every time a player is try to auto-complete command.
	*/
	
	@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
        List<String> completions = new ArrayList<>();        
        
        StringUtil.copyPartialMatches(args[0], Arrays.asList(commands), completions);
        
        if(args.length > 1) {
        	
        	ArrayList<String> ids = new ArrayList<>();
        	
			for(Screen screen : plugin.getRegisteredScreens()) {
				ids.add(String.valueOf(screen.getId()));
			}
            StringUtil.copyPartialMatches(args[1], ids, completions);
        }
        
        Collections.sort(completions);
        
        return completions;
    }
	
	/**
	* Sends help messages to a {@link CommandSender} concerning screen commands.
	* 
	* @param sender The command sender that has sent the command, can be {@link Bukkit#getConsoleSender()}.
	* @param cmd The command name that is sent by the command sender.
	*/
	
	public void sendHelp(CommandSender sender, String cmd) {
		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Screen's commands");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.GOLD + "» /" + cmd + ChatColor.YELLOW + " create (selected-video)");
		sender.sendMessage(ChatColor.GOLD + "» /" + cmd + ChatColor.YELLOW + " create <video-index>");
		sender.sendMessage(ChatColor.GOLD + "» /" + cmd + ChatColor.YELLOW + " create <width> <height>");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.GOLD + "» /" + cmd + ChatColor.YELLOW + " info (selected-screen)");
		sender.sendMessage(ChatColor.GOLD + "» /" + cmd + ChatColor.YELLOW + " info <screen-index>");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.GOLD + "» /" + cmd + ChatColor.YELLOW + " teleport (selected-screen)");
		sender.sendMessage(ChatColor.GOLD + "» /" + cmd + ChatColor.YELLOW + " teleport <screen-index>");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.GOLD + "» /" + cmd + ChatColor.YELLOW + " remove (selected-screen)");
		sender.sendMessage(ChatColor.GOLD + "» /" + cmd + ChatColor.YELLOW + " remove <screen-index>");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.GOLD + "» /screens reload");
		sender.sendMessage(ChatColor.GOLD + "» /screens");
	}
}
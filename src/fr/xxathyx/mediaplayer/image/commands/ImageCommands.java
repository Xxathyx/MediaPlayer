package fr.xxathyx.mediaplayer.image.commands;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.image.Image;
import fr.xxathyx.mediaplayer.image.helpers.ImageHelper;
import fr.xxathyx.mediaplayer.sound.SoundPlayer;
import fr.xxathyx.mediaplayer.sound.SoundType;

/** 
* The ImageCommands class implements {@link CommandExecutor}, it grants an easy in game
* access to the plugin features concerning images.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class ImageCommands implements CommandExecutor, TabCompleter {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
    private final String[] commands = { "render", "delete", "give" };
    
	/**
	* See Bukkit documentation : {@link CommandExecutor#onCommand(CommandSender, Command, String, String[])}
	* 
	* <p>Called every time the image command is sent.
	*/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] arg3) {
		
		if(cmd.getName().equalsIgnoreCase("image")) {
			if(sender.hasPermission("mediaplayer.command.image")) {
				
				Player player = null;
				
				if(sender instanceof Player) player = (Player) sender;
				
				if(arg3.length == 1) {
					if(arg3[0].equalsIgnoreCase("help")) {
						sendHelp(sender, msg);
						return true;
					}
				}
				
				if(arg3.length >= 2) {
					
					if(arg3[0].equalsIgnoreCase("render")) {
						
						if(sender instanceof Player) {
							if(ImageHelper.isURL(arg3[1])) {
								
								Player[] players = {player};
								
								Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
									@Override
									public void run() {
										
										String url = arg3[1];
										
										try {
											
											if(ImageHelper.getImage(url) == null) {
												players[0].sendMessage(configuration.image_cannot_read());
												return;
											}
											
											String name = FilenameUtils.getName(new URL(url).getPath());
											
											File imageFile = new File(plugin.getDataFolder() + "/images/", name);
											
											ImageIO.write(ImageHelper.getImage(url), FilenameUtils.getExtension(name), imageFile);
											Image image = new Image(FilenameUtils.removeExtension(name), url);
											image.create(ImageIO.read(imageFile), players[0]);
											players[0].sendMessage(configuration.image_rendered(FilenameUtils.removeExtension(name)));
											SoundPlayer.playSound(players[0], SoundType.NOTIFICATION_UP);
											return;
										}catch (IOException e) {
											e.printStackTrace();
										}
									}
								});
								return true;
							}
							
							String name = arg3[1];
							
							File imageFile = new File(plugin.getDataFolder() + "/images/", name);
							
							if(imageFile.exists()) {
								try {
									Image image = new Image(FilenameUtils.removeExtension(name), imageFile.getPath());
									
									if(image.getFile().exists()) {
										player.sendMessage(configuration.image_already_rendered(FilenameUtils.removeExtension(name)));
										return false;
									}
									
									image.create(ImageIO.read(imageFile), player);
									player.sendMessage(configuration.image_rendered(FilenameUtils.removeExtension(name)));
									SoundPlayer.playSound(player, SoundType.NOTIFICATION_UP);
									return true;
								}catch (IOException e) {
									e.printStackTrace();
								}
							}
							player.sendMessage(configuration.image_invalid());
							SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
							return false;
						}
					}
					
					if(arg3[0].equalsIgnoreCase("delete")) {
						
						File imageFile = new File(plugin.getDataFolder() + "/images/maps/", arg3[1] + ".yml");
						
						if(imageFile.exists()) {
							
							Image image = new Image(arg3[1]);
					        String name = image.getName();
							
							new File(image.getPath()).delete();
							image.getFile().delete();
							sender.sendMessage(configuration.image_deleted(name));
							return true;
						}
						
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
				        
				        if(Integer.parseInt(arg3[1])-1 >= imageFile.getParentFile().listFiles().length) {
				        	sender.sendMessage(configuration.image_invalid());
				        	return false;
				        }
						
				        if(imageFile.getParentFile().listFiles()[Integer.parseInt(arg3[1])-1].exists()) {
				        	
					        Image image = new Image(FilenameUtils.removeExtension(imageFile.getParentFile().listFiles()[Integer.parseInt(arg3[1])-1].getName()));
					        String name = image.getName();
					        
							new File(image.getPath()).delete();
							image.getFile().delete();
							sender.sendMessage(configuration.image_deleted(name));
							return true;
				        }
				        sender.sendMessage(configuration.image_invalid());
						if(sender instanceof Player) SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
				        return false;
					}
					
					if(arg3.length == 2) {
						if(arg3[0].equalsIgnoreCase("give")) {
							if(sender instanceof Player) {
								
								File imageFile = new File(plugin.getDataFolder() + "/images/maps/", arg3[1] + ".yml");
								
								if(imageFile.exists()) {
									
									Image image = new Image(arg3[1]);
									image.give(player);
									player.sendMessage(configuration.image_received(image.getName()));
									return true;
								}
								
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
						        
						        if(Integer.parseInt(arg3[1])-1 >= imageFile.getParentFile().listFiles().length) {
						        	player.sendMessage(configuration.image_invalid());
						        	return false;
						        }
								
						        if(imageFile.getParentFile().listFiles()[Integer.parseInt(arg3[1])-1].exists()) {
							        Image image = new Image(FilenameUtils.removeExtension(imageFile.getParentFile().listFiles()[Integer.parseInt(arg3[1])-1].getName()));
							        image.give(player);
									player.sendMessage(configuration.image_received(image.getName()));
									return true;
						        }
						        player.sendMessage(configuration.image_invalid());
								SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
						        return false;
							}
						}
					}
					
					if(arg3.length >= 3) {
						
						if(arg3[0].equalsIgnoreCase("give")) {
							
							if(Bukkit.getPlayer(arg3[2]) != null) {
								
								File imageFile = new File(plugin.getDataFolder() + "/images/maps/", arg3[1] + ".yml");
								
								if(imageFile.exists()) {
									
									Image image = new Image(arg3[1]);
									image.give(Bukkit.getPlayer(arg3[2]));
									sender.sendMessage(configuration.image_gived(image.getName(), arg3[2]));
									Bukkit.getPlayer(arg3[2]).sendMessage(configuration.image_received(image.getName()));
									return true;
								}
								
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
						        
						        if(Integer.parseInt(arg3[1])-1 >= imageFile.getParentFile().listFiles().length) {
						        	sender.sendMessage(configuration.image_invalid());
						        	return false;
						        }
								
						        if(imageFile.getParentFile().listFiles()[Integer.parseInt(arg3[1])-1].exists()) {
							        Image image = new Image(FilenameUtils.removeExtension(imageFile.getParentFile().listFiles()[Integer.parseInt(arg3[1])-1].getName()));
									image.give(Bukkit.getPlayer(arg3[2]));
									sender.sendMessage(configuration.image_gived(image.getName(), arg3[2]));
									Bukkit.getPlayer(arg3[2]).sendMessage(configuration.image_received(image.getName()));
									return true;
						        }
						        sender.sendMessage(configuration.image_invalid());
								if(sender instanceof Player) SoundPlayer.playSound(player, SoundType.NOTIFICATION_DOWN);
						        return false;
								
							}
							sender.sendMessage(configuration.offline_player(arg3[2]));
							return false;
						}
					}
				}
			}
			sendHelp(sender, msg);
			return false;
		}
		
		if(cmd.getName().equalsIgnoreCase("images")) {
			if(sender.hasPermission("mediaplayer.command.images")) {
				
				String imagesList = ChatColor.DARK_GREEN + configuration.available_images() + ChatColor.GRAY + " (" + new File(plugin.getDataFolder() + "/images/maps/").listFiles().length + ")" + "\n " + ChatColor.RESET;
				
				for(int i = 0; i < new File(plugin.getDataFolder() + "/images/").listFiles().length; i++) {
					if(!new File(plugin.getDataFolder() + "/images/").listFiles()[i].isDirectory()) {
						
						int index = i + 1;
						
						String image = ChatColor.GREEN + "" + new File(plugin.getDataFolder() + "/images/").listFiles()[i].getName() + "\n";
						imagesList = (imagesList + "\n" + ChatColor.GREEN + index + ". " + image);
					}
				}
				sender.sendMessage(imagesList);
				return true;
			}
		}
		sendHelp(sender, msg);
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
        
        StringUtil.copyPartialMatches(args[0], Arrays.asList(commands), completions);
        
        if(args.length > 1) {
        	
        	ArrayList<String> images = new ArrayList<>();
        	
			for(int i = 0; i < new File(plugin.getDataFolder() + "/images/").listFiles().length; i++) {
				if(!new File(plugin.getDataFolder() + "/images/").listFiles()[i].isDirectory()) {
					images.add(new File(plugin.getDataFolder() + "/images/").listFiles()[i].getName());
				}
			}
            StringUtil.copyPartialMatches(args[1], images, completions);
        }
        
        Collections.sort(completions);
        
        return completions;
    }
	
	/**
	* Sends help messages to a {@link CommandSender} concerning image commands.
	* 
	* @param sender The command sender that has sent the command, can be {@link Bukkit#getConsoleSender()}.
	* @param cmd The command name that is sent by the command sender.
	*/
	
	public void sendHelp(CommandSender sender, String cmd) {
		sender.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Image's commands");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "/" + cmd + ChatColor.AQUA + " render <url|file>");
		sender.sendMessage(ChatColor.DARK_AQUA + "/" + cmd + ChatColor.AQUA + " delete <index|name>");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "/" + cmd + ChatColor.AQUA + " give <index|name> <player-name>");
		sender.sendMessage(ChatColor.DARK_AQUA + "/" + cmd + ChatColor.AQUA + " give <index|name>");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.DARK_AQUA + "/images");
	}
}
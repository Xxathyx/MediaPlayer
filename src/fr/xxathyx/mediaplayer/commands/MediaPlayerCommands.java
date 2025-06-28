package fr.xxathyx.mediaplayer.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;

/** 
* The MediaPlayerCommands class implements {@link CommandExecutor}, it grants informations
* about the running plugin.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class MediaPlayerCommands implements CommandExecutor {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	    
	/**
	* See Bukkit documentation : {@link CommandExecutor#onCommand(CommandSender, Command, String, String[])}
	* 
	* <p>Called every time the mediaplayer command is sent.
	*/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] arg3) {
		
		if(cmd.getName().equalsIgnoreCase("mediaplayer")) {
			if(sender.hasPermission("mediaplayer.command")) {
				
				if(arg3.length > 0) {
					if(arg3[0].equalsIgnoreCase("help")) {
						Bukkit.dispatchCommand(sender, "help MediaPlayer");
						return true;
					}
				}
				sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "MediaPlayer's informations");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.YELLOW + "Running version : " + plugin.getDescription().getVersion());
				return true;
			}
			sender.sendMessage(configuration.insufficient_permissions());
			return false;
		}
		return false;
	}
}
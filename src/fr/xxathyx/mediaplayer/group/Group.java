package fr.xxathyx.mediaplayer.group;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.Main;

/** 
* A Group consits on a list of players uuid, that group can be registered
* with the following add method of {@link Main#getGroups()}, groups aren't
* registered after been created, you need to do it manually. The class
* contains some utility static methods such as {@link #getGroup(Player)}
* that grant access to them to be called from everywhere.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class Group {
	
	private final static Main plugin = Main.getPlugin(Main.class);
	
	private ArrayList<UUID> players;
	
	/**
	* Constructor for Group class, creates a group of players based
	* on a common {@link String} permission.
	* 
	* <p>For example you can pass the permission mediaplayer.permission.admin and get
	* a group of all players that have that permission, administrators.
	* 
	* @param permission The common permission connecting the players.
	*/
	
	public Group(String permission) {
		
		ArrayList<UUID> players = new ArrayList<>();
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.hasPermission("mediaplayer.permission.admin")) {
				players.add(player.getUniqueId());
			}
		}
		this.players = players;
	}
	
	/**
	* Annother constructor for Group class, this one creates a group of players based
	* on an {@link ArrayList} of player's uuids to be parsed into a Group.
	* 
	* @param players The ArrayList containing player's uuids.
	*/
	
	public Group(ArrayList<UUID> players) {
		this.players = players;
	}
	
    /**
     * Gets an {@link ArrayList} of actually online {@link Player} contained in the Group.
     *
     *<p> <strong>Note: </strong>When a contained group player disconnect he will be no longer returned, until he
     * reconnect.
     *
     * @return Online players contained in the {@link Group}.
     */
	
	public ArrayList<Player> getOnlinePlayers() {
		
		ArrayList<Player> onlinePlayers = new ArrayList<>();
		
		for(UUID uuid : players) {
			if(Bukkit.getPlayer(uuid) != null) {
				onlinePlayers.add(Bukkit.getPlayer(uuid));
			}
		}
		return onlinePlayers;
	}
	
    /**
     * Gets an {@link ArrayList} of players {@link UUID} contained in the Group.
     *
     * @return Online players uuid contained in the Group.
     */
	
	public ArrayList<UUID> getPlayers() {
		return players;
	}
	
    /**
     * Gets a player {@link Group} according to the registered Groups {@link Main#getGroups()} and their
     * {@link UUID}, it loop in all registered group until finding a Group that contains the player uuid.
     * However if the player uuid contained in multiple registered Groups only the first registerer will be returned.
     *
     *<p> <strong>Note: </strong>Does return null when a Group isn't found.
     *
     * @param uuid The player uuid that should be contained in a reistered Group.
     * @return The player Group according to the registered Groups and their uuid.
     */
	
	public static Group getGroup(UUID uuid) {
		for(Group group : plugin.getGroups()) {
			if(group.getPlayers().contains(uuid)) {
				return group;
			}
		}
		return null;
	}
}
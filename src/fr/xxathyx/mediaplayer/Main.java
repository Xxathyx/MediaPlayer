package fr.xxathyx.mediaplayer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.xxathyx.mediaplayer.actionbar.ActionBarVersion;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.group.Group;
import fr.xxathyx.mediaplayer.image.commands.ImageCommands;
import fr.xxathyx.mediaplayer.image.listeners.PlayerInteractImage;
import fr.xxathyx.mediaplayer.interfaces.Interfaces;
import fr.xxathyx.mediaplayer.interfaces.listeners.InventoryClickPanel;
import fr.xxathyx.mediaplayer.interfaces.listeners.InventoryClickVideos;
import fr.xxathyx.mediaplayer.interfaces.listeners.InventoryClosePanel;
import fr.xxathyx.mediaplayer.map.util.MapUtilVersion;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.screen.commands.ScreenCommands;
import fr.xxathyx.mediaplayer.screen.listeners.PlayerBreakScreen;
import fr.xxathyx.mediaplayer.screen.listeners.PlayerDamageScreen;
import fr.xxathyx.mediaplayer.screen.listeners.PlayerInteractScreen;
import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadConfigurations;
import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadImages;
import fr.xxathyx.mediaplayer.translation.Translater;
import fr.xxathyx.mediaplayer.update.Updater;
import fr.xxathyx.mediaplayer.util.ActionBar;
import fr.xxathyx.mediaplayer.util.MapUtil;
import fr.xxathyx.mediaplayer.video.Video;
import fr.xxathyx.mediaplayer.video.commands.VideoCommands;
import fr.xxathyx.mediaplayer.video.instance.VideoInstance;
import fr.xxathyx.mediaplayer.video.listeners.PlayerInteractVideo;
import fr.xxathyx.mediaplayer.video.player.VideoPlayer;

/*
 * Copyright or © or Copr. xxathyxlive@gmail.com (2021)
 *
 * This software is a computer program add the possibility to use various
 * media on minecraft
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks
 * associated with loading,  using,  modifying and/or developing or
 * reproducing the software by the user in light of its specific status
 * of free software, that may mean  that it is complicated to manipulate,
 * and  that  also therefore means  that it is reserved for developers  and
 * experienced professionals having in-depth computer knowledge. Users are
 * therefore encouraged to load and test the software's suitability as regards
 * their requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have
 * had knowledge of the CeCILL license and that you accept its terms.
 */

/** 
* Represents the MediaPlayer main class of the MediaPlayer plugin,
* it mainly store informations used accross the plugin.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class Main extends JavaPlugin {
	
	private final ArrayList<Video> registeredVideos = new ArrayList<>();
	private final ArrayList<Screen> registeredScreens = new ArrayList<>();
	
	private final ArrayList<Block> screensBlocks = new ArrayList<>();
	private final ArrayList<ItemFrame> screensFrames = new ArrayList<>();
	
	private final Map<UUID, VideoPlayer> videoPlayers = new HashMap<>();
	
	private final ArrayList<Group> groups = new ArrayList<>();
	
	private final Map<UUID, Integer> pages = new HashMap<>();
	
	private final Map<UUID, Video> videoPanels = new HashMap<>();
	private final Map<UUID, VideoInstance> selectedVideos = new HashMap<>();
	
	private final ArrayList<String> loadingVideos = new ArrayList<>();
	private final ArrayList<String> playingVideos = new ArrayList<>();
	
	private Configuration configuration;
	private Translater translater;
	
	private Updater updater;
	
	private MapUtil mapUtil;
	private ActionBar actionBar;
	
	private boolean legacy = true;
	private boolean old = false;
	
	/**
	* See Bukkit documentation : {@link JavaPlugin#onEnable()}
	* 
	* <p>Load all necessary informations, creates configurations
	* and load videos and images.
	*/
	
	public void onEnable() {
		
		configuration = new Configuration();
		
		configuration.setup();
		
        translater = new Translater();

        try {
            translater.createTranslationFile("GB");
            //translater.createTranslationFile("AR");
            translater.createTranslationFile("DE");
            translater.createTranslationFile("ES");
            translater.createTranslationFile("FR");
            translater.createTranslationFile("IT");
            //translater.createTranslationFile("RU");
            //translater.createTranslationFile("TR");
        }catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
		
		updater = new Updater();
		updater.update();
		
		mapUtil = new MapUtilVersion().getMapUtil();
		actionBar = new ActionBarVersion().getActionBar();
		
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		
        if(serverVersion.equals("v1_18_R1") || serverVersion.equals("v1_17_R1") || serverVersion.equals("v1_16_R3") || serverVersion.equals("v1_16_R2") || serverVersion.equals("v1_16_R1") || serverVersion.equals("v1_15_R1")
        		|| serverVersion.equals("v1_14_R1") || serverVersion.equals("v1_13_R1") || serverVersion.equals("v1_13_R2")) {
        	legacy = false;
        }
        
        if(serverVersion.equals("v1_7_R4") || serverVersion.equals("v1_7_R3") || serverVersion.equals("v1_7_R2") || serverVersion.equals("v1_7_R1")) {
        	old = true;
	        Bukkit.getLogger().warning("[MediaPlayer]: The server running version is old and isn't well supported, you may encounter future issues while playing videos.");
        }
        
        getCommand("video").setExecutor(new VideoCommands());
        getCommand("video").setTabCompleter(new VideoCommands());
        getCommand("videos").setExecutor(new VideoCommands());   
        
        getCommand("screen").setExecutor(new ScreenCommands());
        getCommand("screen").setTabCompleter(new ScreenCommands());
        getCommand("screens").setExecutor(new ScreenCommands());
        
        getCommand("image").setExecutor(new ImageCommands());
        getCommand("image").setTabCompleter(new ImageCommands());
        getCommand("images").setExecutor(new ImageCommands());
        
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickVideos(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickPanel(), this);
		
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryClosePanel(), this);
		
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractImage(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractVideo(), this);
				
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerBreakScreen(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractScreen(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerDamageScreen(), this);
				
		new TaskAsyncLoadConfigurations().runTaskAsynchronously(this);
		if(legacy) new TaskAsyncLoadImages().runTaskAsynchronously(this);
		if(!legacy) new TaskAsyncLoadImages().runTask(this);
	}
	
	/**
	* See Bukkit documentation : {@link JavaPlugin#onDisable()}
	* 
	* <p>Turn off all registered screens, and remove them if
	* {@link Configuration#remove_screen_on_end()} is true.
	*/
	
	public void onDisable() {
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.closeInventory();
		}
		
		for(Screen screen : registeredScreens) {
			screen.end();
			if(configuration.remove_screen_on_restart()) {
				screen.remove();
			}
		}
	}
	
    /**
     * Gets the version that the server is running on.
     *
     * @return The version that the server is using.
     */
	
	public String getServerVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
	
    /**
     * Gets {@link MapUtil} variable initialized on load according to the server version.
     *
     * @return MapUtil of this server version.
     */
	
	public MapUtil getMapUtil() {
		return mapUtil;
	}
	
    /**
     * Gets {@link ActionBar} variable initialized on load according to the server version.
     *
     * @return ActionBar of this server version.
     */
	
	public ActionBar getActionBar() {
		return actionBar;
	}
	
    /**
     * Gets whether this server is running under a legacy version of Minecraft.
     *
     * @return Whether this server is running under a legacy version of Minecraft.
     */
	
	public boolean isLegacy() {
		return legacy;
	}
	
    /**
     * Gets whether this server is running under a old supported version of Minecraft.
     *
     * @return Whether this server is running under a old supported version of Minecraft.
     */
	
	public boolean isOld() {
		return old;
	}
	
    /**
     * Gets all detected and registered videos after running an {@link TaskAsyncLoadConfigurations}.
     *
     * @return Detected and registered videos from respective video folder.
     */
	
	public ArrayList<Video> getRegisteredVideos() {
		return registeredVideos;
	}
	
	public ArrayList<Screen> getRegisteredScreens() {
		return registeredScreens;
	}
	
	public ArrayList<Block> getScreensBlocks() {
		return screensBlocks;
	}
	
	public ArrayList<ItemFrame> getScreensFrames() {
		return screensFrames;
	}
	
    /**
     * Gets all players uuid that are actually playing videos and their video players.
     *
     * @return A HashMap of players that are playing videos and their video players.
     */
	
	public Map<UUID, VideoPlayer> getVideoPlayers() {
		return videoPlayers;
	}
	
    /**
     * Gets all registered groups of players.
     *
     * <p>Contains for example a {@link Group} of players watching a video.
     *
     * @return groups that are registered.
     */
	
	public ArrayList<Group> getGroups() {
		return groups;
	}
	
    /**
     * Gets all players uuid and their current page index of videos section.
     *
     * @return Players that are in videos section and their current page index.
     */
	
	public Map<UUID, Integer> getPages() {
		return pages;
	}
	
    /**
     * Gets all players uuid that are actually in an video panel inventory {@link Interfaces#getVideoPanel(Video)}.
     *
     * @return Players that are in a video panel inventory.
     */
	
	public Map<UUID, Video> getVideoPanels() {
		return videoPanels;
	}
	
    /**
     * Gets all players uuid that have an video instance.
     *
     * <p>Contains for example a player that haven't already selected a screen into assign a video.
     *
     * @return A HashMap of players that have an video instance and their video instance.
     */
	
	public Map<UUID, VideoInstance> getSelectedVideos() {
		return selectedVideos;
	}
	
    /**
     * Gets all loading videos names.
     *
     * @return A list of videos loading names.
     */
	
	public ArrayList<String> getLoadingVideos() {
		return loadingVideos;
	}
	
    /**
     * Gets all playing videos names.
     *
     * @return A list of videos playing names.
     */
	
	public ArrayList<String> getPlayingVideos() {
		return playingVideos;
	}
}
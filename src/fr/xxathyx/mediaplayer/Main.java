package fr.xxathyx.mediaplayer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.xxathyx.mediaplayer.actionbar.ActionBarVersion;
import fr.xxathyx.mediaplayer.audio.util.AudioUtilVersion;
import fr.xxathyx.mediaplayer.commands.MediaPlayerCommands;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.ffmpeg.Ffmpeg;
import fr.xxathyx.mediaplayer.ffmpeg.Ffprobe;
import fr.xxathyx.mediaplayer.group.Group;
import fr.xxathyx.mediaplayer.image.commands.ImageCommands;
import fr.xxathyx.mediaplayer.image.listeners.PlayerInteractImage;
import fr.xxathyx.mediaplayer.interfaces.Interfaces;
import fr.xxathyx.mediaplayer.interfaces.listeners.InventoryClickContents;
import fr.xxathyx.mediaplayer.interfaces.listeners.InventoryClickPanel;
import fr.xxathyx.mediaplayer.interfaces.listeners.InventoryClickScreens;
import fr.xxathyx.mediaplayer.interfaces.listeners.InventoryClickVideos;
import fr.xxathyx.mediaplayer.interfaces.listeners.InventoryClosePanel;
import fr.xxathyx.mediaplayer.map.colors.MCSDGenBukkit;
import fr.xxathyx.mediaplayer.map.colors.MapColorSpaceData;
import fr.xxathyx.mediaplayer.map.util.MapUtilVersion;
import fr.xxathyx.mediaplayer.resourcepack.listeners.ResourcePackStatus;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.screen.commands.ScreenCommands;
import fr.xxathyx.mediaplayer.screen.listeners.PlayerBreakScreen;
import fr.xxathyx.mediaplayer.screen.listeners.PlayerDamageScreen;
import fr.xxathyx.mediaplayer.screen.listeners.PlayerInteractScreen;
import fr.xxathyx.mediaplayer.server.Client;
import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadConfigurations;
import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadImages;
import fr.xxathyx.mediaplayer.tasks.TaskSyncLoadScreens;
import fr.xxathyx.mediaplayer.translation.Translater;
import fr.xxathyx.mediaplayer.update.Updater;
import fr.xxathyx.mediaplayer.util.ActionBar;
import fr.xxathyx.mediaplayer.util.AudioUtil;
import fr.xxathyx.mediaplayer.util.MapUtil;
import fr.xxathyx.mediaplayer.video.Video;
import fr.xxathyx.mediaplayer.video.commands.VideoCommands;
import fr.xxathyx.mediaplayer.video.instance.VideoInstance;
import fr.xxathyx.mediaplayer.video.listeners.PlayerInteractVideo;
import fr.xxathyx.mediaplayer.video.player.VideoPlayer;

/*
 * Copyright or © or Copr. xxathyxlive@gmail.com (2022)
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
	
	private final ArrayList<Integer> tasks = new ArrayList<>();
	private final ArrayList<Process> process = new ArrayList<>();
	
	private final ArrayList<Video> registeredVideos = new ArrayList<>();
	private final ArrayList<Screen> registeredScreens = new ArrayList<>();
	
	private final Map<UUID, URL> streamsURL = new HashMap<>();
	private final ArrayList<Video> playedStreams = new ArrayList<>();
	
	private final Map<Block, Screen> screensBlocks = new HashMap<>();
	private final Map<ItemFrame, Screen> screensFrames = new HashMap<>();
	
	private final Map<UUID, VideoPlayer> videoPlayers = new HashMap<>();
	private final Map<UUID, Screen> playersScreens = new HashMap<>();
	
	private final ArrayList<Group> groups = new ArrayList<>();
	
	private final Map<UUID, Integer> videosPages = new HashMap<>();
	private final Map<UUID, Integer> screensPages = new HashMap<>();
	
	private final Map<UUID, Integer> contentsPages = new HashMap<>();
	
	private final Map<UUID, Video> videoPanels = new HashMap<>();
	private final Map<UUID, Screen> screenPanels = new HashMap<>();
	
	private final Map<UUID, Screen> contentsPanels = new HashMap<>();
	
	private final Map<UUID, VideoInstance> selectedVideos = new HashMap<>();
	
	private final ArrayList<String> loadingVideos = new ArrayList<>();
	private final ArrayList<String> playingVideos = new ArrayList<>();
	
	private final MapColorSpaceData mapColorSpaceData = new MapColorSpaceData();
	
	private Configuration configuration;
	private Client client;
	
	private Ffmpeg ffmpeg;
	private Ffprobe ffprobe;
	
	private Translater translater;
	
	private Updater updater;
	
	private MapUtil mapUtil;
	private ActionBar actionBar;
	private AudioUtil audioUtil;
	
	private boolean legacy = true;
	private boolean old = false;
		
	/**
	* See Bukkit documentation : {@link JavaPlugin#onEnable()}
	* 
	* <p>Load all necessary informations, creates configurations
	* and load videos and images.
	*/
	
	public void onEnable() {
		
		client = new Client();
		
		configuration = new Configuration();
		configuration.setup();
				
		ffmpeg = new Ffmpeg();
		ffprobe = new Ffprobe();
		
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {				
				if(!ffmpeg.isInstalled()) ffmpeg.download();
				if(!ffprobe.isInstalled()) ffprobe.download();
			}
		});
		
        translater = new Translater();

        try {
            translater.createTranslationFile("GB");
            translater.createTranslationFile("FR");
        }catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
		
		updater = new Updater();
		updater.update();
		
		mapUtil = new MapUtilVersion().getMapUtil();
		actionBar = new ActionBarVersion().getActionBar();
		audioUtil = new AudioUtilVersion().getAudioUtil();
		
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
			      MCSDGenBukkit bukkitGen = new MCSDGenBukkit();
			      bukkitGen.generate();
			      mapColorSpaceData.readFrom((MapColorSpaceData)bukkitGen);
			}
		});
		
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		
        if(serverVersion.equals("v1_19_R2") || serverVersion.equals("v1_19_R1") || serverVersion.equals("v1_18_R2") || serverVersion.equals("v1_18_R1") || serverVersion.equals("v1_17_R1") || serverVersion.equals("v1_16_R3") ||
        		serverVersion.equals("v1_16_R2") || serverVersion.equals("v1_16_R1") || serverVersion.equals("v1_15_R1") || serverVersion.equals("v1_14_R1") || serverVersion.equals("v1_13_R1") || serverVersion.equals("v1_13_R2")) {
        	legacy = false;
        }
        
        if(serverVersion.equals("v1_7_R4") || serverVersion.equals("v1_7_R3") || serverVersion.equals("v1_7_R2") || serverVersion.equals("v1_7_R1")) {
        	old = true;
	        Bukkit.getLogger().warning("[MediaPlayer]: The server running version is old and isn't well supported, you may encounter future issues while playing videos.");
        }
        
        getCommand("mediaplayer").setExecutor(new MediaPlayerCommands());
        
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
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickScreens(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickContents(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickPanel(), this);
		
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryClosePanel(), this);
		
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractImage(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractVideo(), this);
				
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerBreakScreen(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractScreen(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerDamageScreen(), this);
		
		if(!old) Bukkit.getServer().getPluginManager().registerEvents(new ResourcePackStatus(), this);
				
		new TaskSyncLoadScreens().runTask(this);
		new TaskAsyncLoadConfigurations().runTaskAsynchronously(this);
		if(legacy) new TaskAsyncLoadImages().runTaskAsynchronously(this);
		if(!legacy) new TaskAsyncLoadImages().runTask(this);
		
		if(configuration.plugin_free_audio_server_handling() && client.getSocket() == null) {
			
			Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
				@Override
				public void run() {
					
					client.connect();
					
					client.write("mediaplayer.connect: ", configuration.free_audio_server_token());
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + ChatColor.GREEN + client.getResponse());
				}
			});
		}
	}
	
	/**
	* See Bukkit documentation : {@link JavaPlugin#onDisable()}
	* 
	* <p>Turn off all registered screens, and remove them if
	* {@link Configuration#remove_screen_on_end()} is true.
	* 
	* <p>Delete all temporary streamed videos, if
	* {@link Configuration#save_streams()} is true.
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
		
		for(Process process : process) {
			process.destroy();
		}
		
		for(Video video : playedStreams) {
			
			try {
				if(!configuration.save_streams() && video.isStreamed()) video.delete();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			client.write("mediaplayer.disconnect: ", configuration.free_audio_server_token());
			client.getSocket().close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Sets client if it can't be set, its only used on first time creation.
     *
     * @param client Client instance.
     */
	
	public void setClient(Client client) {
		this.client = client;
	}
	
    /**
     * Gets server-client that should be connected to the free audio server.
     *
     * @return the client instance.
     */
	
	public Client getClient() {
		return client;
	}
	
    /**
     * Gets access to the ffmpeg library.
     *
     * @return Ffmpeg library.
     */
	
	public Ffmpeg getFfmpeg() {
		return ffmpeg;
	}
	
    /**
     * Gets access to the ffprobe library.
     *
     * @return Ffprobe library.
     */
	
	public Ffprobe getFfprobe() {
		return ffprobe;
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
     * Gets {@link AudioUtil} variable initialized on load according to the server version.
     *
     * @return AudioUtil of this server version.
     */
	
	public AudioUtil getAudioUtil() {
		return audioUtil;
	}
	
    /**
     * Gets whether the plugin has been reloaded one a time.
     *
     * @return Whether the plugin has been reloaded one a time.
     */
	
	public boolean isReloaded() {
		
		int tickvalue = -1;
		
		try {
			Field tick = Bukkit.getScheduler().getClass().getDeclaredField("currentTick");
			tick.setAccessible(true);
			tickvalue = (Integer) tick.get(Bukkit.getScheduler());
		}catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
				
		if(tickvalue > 1) return true;	
		return false;
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
     * Gets the plugin running tasks, identified by their ids.
     *
     * @return Plugin running tasks ids.
     */
	
	public ArrayList<Integer> getTasks() {
		return tasks;
	}
	
    /**
     * Gets process such as ffmpeg and ffprobe tasks.
     *
     * @return The plugin libraries program process.
     */
	
	public ArrayList<Process> getProcess() {
		return process;
	}
	
    /**
     * Gets all detected and registered videos after running an {@link TaskAsyncLoadConfigurations}.
     *
     * @return Detected and registered videos from respective video folder.
     */
	
	public ArrayList<Video> getRegisteredVideos() {
		return registeredVideos;
	}
	
    /**
     * Gets all detected and registered screens after running an {@link TaskSyncLoadConfigurations},
     * or one time screen creation.
     *
     * @return Detected and registered videos from respective video folder.
     */
	
	public ArrayList<Screen> getRegisteredScreens() {
		return registeredScreens;
	}
	
    /**
     * Gets lives links of registered streamed video after being generated.
     *
     * @return Streamed videos originals links.
     */
	
	public Map<UUID, URL> getStreamsURL() {
		return streamsURL;
	}
	
    /**
     * Gets actually played livestreams videos.
     *
     * @return Streamed videos.
     */
	
	public ArrayList<Video> getPlayedStreams() {
		return playedStreams;
	}
	
    /**
     * Gets blocks belonging to screens structure.
     *
     * @return Blocks belonging to screens structure.
     */
	
	public Map<Block, Screen> getScreensBlocks() {
		return screensBlocks;
	}
	
    /**
     * Gets {@link ItemFrame} belonging to screens structure.
     *
     * @return {@link ItemFrame} belonging to screens structure.
     */
	
	public Map<ItemFrame, Screen> getScreensFrames() {
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
     * Gets players affiliated with screen instances, this is use for audio loading.
     *
     * @return A HashMap of players that are affiliated with screen instances.
     */
	
	public Map<UUID, Screen> getPlayersScreens() {
		return playersScreens;
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
	
	public Map<UUID, Integer> getVideosPages() {
		return videosPages;
	}
	
    /**
     * Gets all players uuid and their current page index of screens section.
     *
     * @return Players that are in screens section and their current page index.
     */
	
	public Map<UUID, Integer> getScreensPages() {
		return screensPages;
	}
	
    /**
     * Gets all players uuid and their current page index of contents section.
     *
     * @return Players that are in contents section and their current page index.
     */
	
	public Map<UUID, Integer> getContentsPages() {
		return contentsPages;
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
     * Gets all players uuid that are actually in an screen panel inventory {@link Interfaces#getScreenPanel(Screen)}.
     *
     * @return Players that are in a screen panel inventory.
     */
	
	public Map<UUID, Screen> getScreenPanels() {
		return screenPanels;
	}
	
    /**
     * Gets all players uuid that are actually in an screen contents panel inventory {@link Interfaces#getContents(Screen, Integer)}.
     *
     * @return Players that are in a screen contents panel inventory.
     */
	
	public Map<UUID, Screen> getContentsPanels() {
		return contentsPanels;
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
	
    /**
     * Gets {@link MapColorSpaceData} for {@link MapColorPalette}.
     *
     * @return {@link MapColorSpaceData} for {@link MapColorPalette}.
     */
	
	public MapColorSpaceData getMapColorSpaceData() {
		return mapColorSpaceData;
	}
}
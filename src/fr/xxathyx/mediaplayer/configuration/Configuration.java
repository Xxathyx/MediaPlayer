package fr.xxathyx.mediaplayer.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.server.Client;
import fr.xxathyx.mediaplayer.util.Host;
import fr.xxathyx.mediaplayer.util.RandomToken;

/** 
* The Configuration class allow a direct bridge between plugin configuration and
* the game itself, it mainly contains getter methods for messages, and some plugin
* parameters and other usefull tools between plugin folder and running application.
*
*<p>Configuration class can be define anyware as basic constructor, wich then grant acces
* to all the getter methods.
*
* @author  Xxathyx
* @version 1.0
* @since   2021-08-23 
*/

public class Configuration {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private final File configurationFile = new File(plugin.getDataFolder() + "/configuration/", "configuration.yml");
	private final File translationFile = new File(plugin.getDataFolder() + "/translations/", "GB.yml");	

	private final File videosFolder = new File(plugin.getDataFolder() + "/videos/");
	private final File screensFolder = new File(plugin.getDataFolder() + "/screens/");
	private final File mapsFolder = new File(plugin.getDataFolder() + "/images/maps/");
	
	private FileConfiguration fileconfiguration;
	
    /**
     * Creates the configuration file containing all plugin parametters and messages.
     *
     *<p>Setup should be called once on the server startup, and shall not be effective if
     * the configuration file exists, otherwise delete the configuration file to obtain
     * a new one.
     *
     * @throws IOException When failed or interrupted I/O operations occurs.
     * @throws InvalidConfigurationException When non-respect of YAML syntax.
     */
	
	public void setup() {
		
		if(!configurationFile.exists()) {
			
			String token = new RandomToken().random(10);
			
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
										
					Client client = new Client();
					client.connect();
					
					plugin.setClient(client);
					
					client.write("mediaplayer.register: ", token);
					
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + ChatColor.GREEN + client.getResponse());
				}
			});
			
			fileconfiguration = new YamlConfiguration();
			
			fileconfiguration.set("plugin.auto-update", false);
			fileconfiguration.set("plugin.force-permissions", false);
			fileconfiguration.set("plugin.free-audio-server-handling", true);
			fileconfiguration.set("plugin.free-audio-server-address", "37.187.196.226");
			fileconfiguration.set("plugin.free-audio-server-token", token);
			fileconfiguration.set("plugin.own-audio-server-handling-address", "localhost");
			fileconfiguration.set("plugin.own-audio-server-handling-port", "41");
			fileconfiguration.set("plugin.alternative-server", "http://37.187.196.226/");
			fileconfiguration.set("plugin.system", fr.xxathyx.mediaplayer.system.System.getSystemType().toString());
	    	fileconfiguration.set("plugin.langage", "GB");
	    	
		    Host host = new Host(Bukkit.getServer().getIp());
		    if(host.getOfficials().contains(host.getCountryCode())) fileconfiguration.set("plugin.langage", host.getCountryCode());
			
			fileconfiguration.set("plugin.ping-sound", true);
			fileconfiguration.set("plugin.verify-files-on-load", true);
			fileconfiguration.set("plugin.delete-frames-on-loaded", false);
			fileconfiguration.set("plugin.delete-video-on-loaded", false);
			fileconfiguration.set("plugin.save-streams", false);
			
			fileconfiguration.set("plugin.screen-block", "BARRIER");
			fileconfiguration.set("plugin.visible-screen-frames-support", false);
			fileconfiguration.set("plugin.glowing-screen-frames-support", false);
			
			fileconfiguration.set("plugin.maximum-distance-to-receive", 10);
			
			fileconfiguration.set("plugin.maximum-playing-videos", 5);
			fileconfiguration.set("plugin.maximum-loading-videos", 1);
			
			fileconfiguration.set("plugin.remove-screen-structure-on-restart", false);
			fileconfiguration.set("plugin.remove-screen-structure-on-end", false);
			
			fileconfiguration.set("plugin.detect-duplicated-frames", false);
			fileconfiguration.set("plugin.ressemblance-to-skip", 100);
			
			try {
				fileconfiguration.save(configurationFile);
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(!videosFolder.exists()) {
			videosFolder.mkdir();
		}
		if(!screensFolder.exists()) {
			screensFolder.mkdir();
		}
		if(!mapsFolder.exists()) {
			mapsFolder.mkdirs();
		}
	}
	
    /**
     * Gets an FileConfiguration instance of the configuration-file.
     *
     * <p>Called on every {@link Configuration} getter method, for
     * parameters.
     *
     * @return FileConfiguration instance of the configuration-file.
     */
	
	public FileConfiguration getConfigFile() {
		
		fileconfiguration = new YamlConfiguration();
		
		try {
			fileconfiguration.load(configurationFile);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return fileconfiguration;
    }
	
    /**
     * Gets an FileConfiguration instance of the messages-file.
     *
     * <p>Called on every {@link Configuration} getter method, for
     * messages.
     *
     * @return FileConfiguration instance of the messages-file.
     */
	
	public FileConfiguration getMessagesFile() {
		
		File translationFile = new File(plugin.getDataFolder() + "/translations/", plugin_langage() + ".yml");	
		if(!translationFile.exists()) translationFile = this.translationFile;
		
		fileconfiguration = new YamlConfiguration();
		
		try {
			fileconfiguration.load(translationFile);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return fileconfiguration;
    }
	
    /**
     * Gets the videos folder, which containing all relative informations
     * and data about all the videos.
     * 
     * @return Videos folder containing all videos and their own data.
     */
	
	public File getVideosFolder() {
		return videosFolder;
	}
	
    /**
     * Gets the screens folder, which containing all relative informations
     * and data about all the screens.
     * 
     * @return Screens folder containing all screens and their own data.
     */
	
	public File getScreensFolder() {
		return screensFolder;
	}
	
    /**
     * Translate alternate color codes such as Ampersand into Minecraft
     * colors.
     *
     * @param a The non Minecraft colored message.
     * @return The Minecraft colored message.
     */
	
	public String getMessage(String a) {
		return ChatColor.translateAlternateColorCodes('&', a);
	}
	
    /**
     * Translate alternate color codes such as Ampersand into Minecraft
     * colors, and replace requested holder by real value.
     *
     * @param a The non Minecraft colored message.
     * @param b The replacing value.
     * @return The Minecraft colored message within the replaced value.
     */
	
	public String getMessage(String a, String b) {
				
		if(a.contains("%video%")) {
			a = a.replaceAll("%video%", b);
		}
		
		if(a.contains("%screen%")) {
			a = a.replaceAll("%screen%", b);
		}
		
		if(a.contains("%index%")) {
			a = a.replaceAll("%index%", b);
		}
		
		if(a.contains("%dimension%")) {
			a = a.replaceAll("%dimension%", b);
		}
		
		if(a.contains("%time%")) {
			a = a.replaceAll("%time%", b);
		}
		
		if(a.contains("%image%")) {
			a = a.replaceAll("%image%", b);
		}
		
		if(a.contains("%url%")) {
			a = a.replaceAll("%url%", b);
		}
		
		if(a.contains("%player%")) {
			a = a.replaceAll("%player%", b);
		}
		
		if(a.contains("%tasks%")) {
			a = a.replaceAll("%tasks%", b);
		}
		return ChatColor.translateAlternateColorCodes('&', a);
	}
	
    /**
     * Translate alternate color codes such as Ampersand into Minecraft
     * colors, and replace all requested holders by real values.
     *
     * @param a The non Minecraft colored message.
     * @param b A replacing value.
     * @param c A replacing value.
     * @return The Minecraft colored message within the replaced values.
     */
	
	public String getMessage(String a, String b, String c) {
		
		if(a.contains("%video%")) {
			a = a.replaceAll("%video%", b);
		}
		
		if(a.contains("%image%")) {
			a = a.replaceAll("%image%", b);
		}
		
		if(a.contains("%description%")) {
			a = a.replaceAll("%description%", c);
		}
		
		if(a.contains("%framerate%")) {
			a = a.replaceAll("%framerate%", c);
		}
		
		if(a.contains("%speed%")) {
			a = a.replaceAll("%speed%", c);
		}
		
		if(a.contains("%volume%")) {
			a = a.replaceAll("%volume%", c);
		}
		
		if(a.contains("%time%")) {
			a = a.replaceAll("%time%", c);
		}
		
		if(a.contains("%id")) {
			a = a.replaceAll("%id%", c);
		}
		
		if(a.contains("%player")) {
			a = a.replaceAll("%player%", c);
		}
		return ChatColor.translateAlternateColorCodes('&', a);
	}
	
	//ALL GETTER METHODS THAT CAN BE CALLED FOR OBTAINING CONFIGURATION INFORMATIONS AND MESSAGES//
	
	public boolean plugin_auto_update() {
		return getConfigFile().getBoolean("plugin.auto-update");
	}
	
	public boolean plugin_force_permissions() {
		return getConfigFile().getBoolean("plugin.force-permissions");
	}
	
	public boolean plugin_free_audio_server_handling() {
		return getConfigFile().getBoolean("plugin.free-audio-server-handling");
	}
	
	public String plugin_free_audio_server_address() {
		return getConfigFile().getString("plugin.free-audio-server-address");
	}
	
	public String free_audio_server_token() {
		return getConfigFile().getString("plugin.free-audio-server-token");
	}
	
	public String own_audio_server_handling_address() {
		return getConfigFile().getString("plugin.own-audio-server-handling-address");
	}
	
	public int own_audio_server_handling_port() {
		return getConfigFile().getInt("plugin.own-audio-server-handling-port");
	}
	
	public String plugin_alternative_server() {
		return getConfigFile().getString("plugin.alternative-server");
	}
	
	public String plugin_langage() {
		return getConfigFile().getString("plugin.langage");
	}
	
	public boolean plugin_ping_sound() {
		return getConfigFile().getBoolean("plugin.ping-sound");
	}
	
	public boolean verify_files_on_load() {
		return getConfigFile().getBoolean("plugin.verify-files-on-load");
	}
	
	public boolean frames_delete_on_loaded() {
		return getConfigFile().getBoolean("plugin.delete-frames-on-loaded");
	}
	
	public boolean video_delete_on_loaded() {
		return getConfigFile().getBoolean("plugin.delete-video-on-loaded");
	}
	
	public boolean save_streams() {
		return getConfigFile().getBoolean("plugin.save-streams");
	}
	
	public String screen_block() {
		String material = getConfigFile().getString("plugin.screen-block");
		if(material.equalsIgnoreCase("BARRIER") && plugin.isOld()) material = "GLASS"; 
		return material;
	}
	
	public boolean visible_screen_frames_support() {
		return getConfigFile().getBoolean("plugin.visible-screen-frames-support");
	}
	
	public boolean glowing_screen_frames_support() {
		return getConfigFile().getBoolean("plugin.glowing-screen-frames-support");
	}
	
	public int maximum_distance_to_receive() {
		return getConfigFile().getInt("plugin.maximum-distance-to-receive");
	}
	
	public int maximum_playing_videos() {
		return getConfigFile().getInt("plugin.maximum-playing-videos");
	}
	
	public int maximum_loading_videos() {
		return getConfigFile().getInt("plugin.maximum-loading-videos");
	}
	
	public boolean remove_screen_on_end() {
		return getConfigFile().getBoolean("plugin.remove-screen-structure-on-end");
	}
	
	public boolean remove_screen_on_restart() {
		return getConfigFile().getBoolean("plugin.remove-screen-structure-on-restart");
	}
	
	public boolean detect_duplicated_frames() {
		return getConfigFile().getBoolean("plugin.detect-duplicated-frames");
	}
	
	public double ressemblance_to_skip() {
		return getConfigFile().getDouble("plugin.ressemblance-to-skip");
	}
	
	public String plugin_outdated() {
		return getMessage(getMessagesFile().getString("messages.plugin-outdated"));
	}
	
	public String video_load_requested() {
		return getMessage(getMessagesFile().getString("messages.video-load-requested"));
	}
		
	public String video_load_notice() {
		return getMessage(getMessagesFile().getString("messages.video-load-notice"));
	}
	
	public String video_unloaded(String video) {
		return getMessage(getMessagesFile().getString("messages.video-unloaded"), video);
	}
	
	public String video_downloaded(String video) {
		return getMessage(getMessagesFile().getString("messages.video-downloaded"), video);
	}
	
	public String video_deleted(String video) {
		return getMessage(getMessagesFile().getString("messages.video-deleted"), video);
	}
	
	public String video_already_loaded(String video) {
		return getMessage(getMessagesFile().getString("messages.video-already-loaded"), video);
	}
	
	public String video_already_loading(String video) {
		return getMessage(getMessagesFile().getString("messages.video-already-loading"), video);
	}
	
	public String video_not_loaded(String video) {
		return getMessage(getMessagesFile().getString("messages.video-not-loaded"), video);
	}
	
	public String video_selected(String video) {
		return getMessage(getMessagesFile().getString("messages.video-selected"), video);
	}
	
	public String video_assigned(String video) {
		return getMessage(getMessagesFile().getString("messages.video-assigned"), video);
	}
	
	public String video_not_enought_space(String video) {
		return getMessage(getMessagesFile().getString("messages.video-not-enought-space"), video);
	}
	
	public String video_description_updated(String video, String description) {
		return getMessage(getMessagesFile().getString("messages.video-description-updated"), video, description);
	}
	
	public String video_framerate_updated(String video, String framerate) {
		return getMessage(getMessagesFile().getString("messages.video-framerate-updated"), video, framerate);
	}
	
	public String video_speed_updated(String video, String speed) {
		return getMessage(getMessagesFile().getString("messages.video-speed-updated"), video, speed);
	}
	
	public String video_volume_updated(String video, String volume) {
		return getMessage(getMessagesFile().getString("messages.video-volume-updated"), video, volume);
	}
	
	public String video_age_limit_disabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-age-limit-disabled"), video);
	}
	
	public String video_age_limit_enabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-age-limit-enabled"), video);
	}
	
	public String video_audio_disabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-audio-disabled"), video);
	}
	
	public String video_audio_enabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-audio-enabled"), video);
	}
	
	public String video_loop_enabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-loop-enabled"), video);
	}
	
	public String video_loop_disabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-loop-disabled"), video);
	}
	
	public String video_real_time_rendering_enabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-real-time-rendering-enabled"), video);
	}
	
	public String video_real_time_rendering_disabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-real-time-rendering-disabled"), video);
	}
	
	public String video_skip_frames_enabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-skip-frames-enabled"), video);
	}
	
	public String video_skip_frames_disabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-skip-frames-disabled"), video);
	}
	
	public String video_skip_duplicated_frames_enabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-skip-duplicated-frames-enabled"), video);
	}
	
	public String video_skip_duplicated_frames_disabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-skip-duplicated-frames-disabled"), video);
	}
	
	public String video_show_informations_enabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-show-informations-enabled"), video);
	}
	
	public String video_show_informations_disabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-show-informations-disabled"), video);
	}
	
	public String video_show_fps_enabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-show-fps-enabled"), video);
	}
	
	public String video_show_fps_disabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-show-fps-disabled"), video);
	}
	
	public String video_run_on_startup_enabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-run-on-startup-enabled"), video);
	}
	
	public String video_run_on_startup_disabled(String video) {
		return getMessage(getMessagesFile().getString("messages.video-run-on-startup-disabled"), video);
	}
	
	public String video_processing_frames_starting(String video) {
		return getMessage(getMessagesFile().getString("messages.video-processing-frames-starting"), video);
	}
	
	public String video_processing_frames_finished(String video) {
		return getMessage(getMessagesFile().getString("messages.video-processing-frames-finished"), video);
	}
	
	public String video_processing_estimated_time(String time) {
		return getMessage(getMessagesFile().getString("messages.video-processing-estimated-time"), time);
	}
	
	public String video_processing_audio_starting(String video) {
		return getMessage(getMessagesFile().getString("messages.video-processing-audio-starting"), video);
	}
	
	public String video_processing_audio_finished(String video) {
		return getMessage(getMessagesFile().getString("messages.video-processing-audio-finished"), video);
	}
	
	public String video_processing_minecraft_starting(String video) {
		return getMessage(getMessagesFile().getString("messages.video-processing-minecraft-starting"), video);
	}
	
	public String video_processing_minecraft_finished(String video) {
		return getMessage(getMessagesFile().getString("messages.video-processing-minecraft-finished"), video);
	}
	
	public String video_processing_finished(String video, String time) {
		return getMessage(getMessagesFile().getString("messages.video-processing-finished"), video, time);
	}
	
	public String video_instance_started(String video, String id) {
		return getMessage(getMessagesFile().getString("messages.video-instance-started"), video, id);
	}
	
	public String video_instance_stopped(String video) {
		return getMessage(getMessagesFile().getString("messages.video-instance-stopped"), video);
	}
	
	public String video_instance_pause(String video) {
		return getMessage(getMessagesFile().getString("messages.video-instance-pause"), video);
	}
	
	public String video_instance_resume(String video) {
		return getMessage(getMessagesFile().getString("messages.video-instance-resume"), video);
	}
	
	public String video_instance_not_on_screen() {
		return getMessage(getMessagesFile().getString("messages.video-instance-not-on-screen"));
	}
	
	public String videos_empty_registered() {
		return getMessage(getMessagesFile().getString("messages.videos-empty-registered"));
	}
	
	public String videos_canceled_tasks(String tasks) {
		return getMessage(getMessagesFile().getString("messages.videos-canceled-tasks"), tasks);
	}
	
	public String videos_notice() {
		return getMessage(getMessagesFile().getString("messages.videos-notice"));
	}
	
	public String screen_created(String dimension) {
		return getMessage(getMessagesFile().getString("messages.screen-created"), dimension);
	}
	
	public String screen_removed(String index) {
		return getMessage(getMessagesFile().getString("messages.screen-removed"), index);
	}
	
	public String screen_deleted(String name) {
		return getMessage(getMessagesFile().getString("messages.screen-deleted"), name);
	}
	
	public String screen_teleport(String name) {
		return getMessage(getMessagesFile().getString("messages.screen-teleport"), name);
	}
	
	public String screen_invalid_index(String index) {
		return getMessage(getMessagesFile().getString("messages.screen-invalid-index"), index);
	}
	
	public String screen_cannot_create() {
		return getMessage(getMessagesFile().getString("messages.screen-cannot-create"));
	}
	
	public String screen_not_ready(String player) {
		return getMessage(getMessagesFile().getString("messages.screen-not-ready"), player);
	}
	
	public String screen_everyone_ready() {
		return getMessage(getMessagesFile().getString("messages.screen-everyone-ready"));
	}
	
	public String image_rendered(String image) {
		return getMessage(getMessagesFile().getString("messages.image-rendered"), image);
	}
	
	public String image_deleted(String image) {
		return getMessage(getMessagesFile().getString("messages.image-deleted"), image);
	}
	
	public String image_placed(String image) {
		return getMessage(getMessagesFile().getString("messages.image-placed"), image);
	}
	
	public String image_removed(String image) {
		return getMessage(getMessagesFile().getString("messages.image-removed"), image);
	}
	
	public String image_received(String image) {
		return getMessage(getMessagesFile().getString("messages.image-received"), image);
	}
	
	public String image_gived(String image, String player) {
		return getMessage(getMessagesFile().getString("messages.image-gived"), image, player);
	}
	
	public String image_not_found() {
		return getMessage(getMessagesFile().getString("messages.image-not-found"));
	}
	
	public String image_invalid() {
		return getMessage(getMessagesFile().getString("messages.image-invalid"));
	}
	
	public String image_cannot_read() {
		return getMessage(getMessagesFile().getString("messages.image-cannot-read"));
	}
	
	public String image_invalid_screen() {
		return getMessage(getMessagesFile().getString("messages.image-invalid-screen"));
	}
	
	public String image_already_rendered(String image) {
		return getMessage(getMessagesFile().getString("messages.image-already-rendered"), image);
	}
	
	public String item_previous_page_name() {
		return getMessage(getMessagesFile().getString("messages.item.previous-page.name"));
	}
	
	public String item_previous_page_lore() {
		return getMessage(getMessagesFile().getString("messages.item.previous-page.lore"));
	}
	
	public String item_refresh_page_name() {
		return getMessage(getMessagesFile().getString("messages.item.refresh-page.name"));
	}
	
	public String item_refresh_page_lore() {
		return getMessage(getMessagesFile().getString("messages.item.refresh-page.lore"));
	}
	
	public String item_next_page_name() {
		return getMessage(getMessagesFile().getString("messages.item.next-page.name"));
	}
	
	public String item_next_page_lore() {
		return getMessage(getMessagesFile().getString("messages.item.next-page.lore"));
	}
	
	public String item_play_name() {
		return getMessage(getMessagesFile().getString("messages.item.play.name"));
	}
	
	public String item_play_lore() {
		return getMessage(getMessagesFile().getString("messages.item.play.lore"));
	}
	
	public String item_switcher_name() {
		return getMessage(getMessagesFile().getString("messages.item.switcher.name"));
	}
	
	public String item_switcher_lore() {
		return getMessage(getMessagesFile().getString("messages.item.switcher.lore"));
	}
	
	public String item_remote_name() {
		return getMessage(getMessagesFile().getString("messages.item.remote.name"));
	}
	
	public String item_remote_lore() {
		return getMessage(getMessagesFile().getString("messages.item.remote.lore"));
	}
	
	public String item_load_name() {
		return getMessage(getMessagesFile().getString("messages.item.load.name"));
	}
	
	public String item_load_lore() {
		return getMessage(getMessagesFile().getString("messages.item.load.lore"));
	}
	
	public String item_teleport_name() {
		return getMessage(getMessagesFile().getString("messages.item.teleport.name"));
	}
	
	public String item_teleport_lore() {
		return getMessage(getMessagesFile().getString("messages.item.teleport.lore"));
	}
	
	public String item_delete_name() {
		return getMessage(getMessagesFile().getString("messages.item.delete.name"));
	}
	
	public String item_delete_lore_1() {
		return getMessage(getMessagesFile().getString("messages.item.delete.lore-1"));
	}
	
	public String item_delete_lore_2() {
		return getMessage(getMessagesFile().getString("messages.item.delete.lore-2"));
	}
	
	public String item_remove_name() {
		return getMessage(getMessagesFile().getString("messages.item.remove.name"));
	}
	
	public String item_remove_lore_1() {
		return getMessage(getMessagesFile().getString("messages.item.remove.lore-1"));
	}
	
	public String item_remove_lore_2() {
		return getMessage(getMessagesFile().getString("messages.item.remove.lore-2"));
	}
	
	public String item_poster_lore_1() {
		return getMessage(getMessagesFile().getString("messages.item.poster.lore-1"));
	}
	
	public String item_poster_lore_2() {
		return getMessage(getMessagesFile().getString("messages.item.poster.lore-2"));
	}
	
	public String item_poster_lore_3() {
		return getMessage(getMessagesFile().getString("messages.item.poster.lore-3"));
	}
	
	public String item_poster_lore_4() {
		return getMessage(getMessagesFile().getString("messages.item.poster.lore-4"));
	}
	
	public String libraries_not_installed() {
		return getMessage(getMessagesFile().getString("messages.libraries-not-installed"));
	}
	
	public String age_limit_warning() {
		return getMessage(getMessagesFile().getString("messages.age-limit-warning"));
	}
	
	public String incompatible() {
		return getMessage(getMessagesFile().getString("messages.incompatible"));
	}
	
	public String impossible_connection() {
		return getMessage(getMessagesFile().getString("messages.messages.impossible-connection"));
	}
	
	public String no_page_left() {
		return getMessage(getMessagesFile().getString("messages.no-page-left"));
	}
	
	public String too_much_loading() {
		return getMessage(getMessagesFile().getString("messages.too-much-loading"));
	}
	
	public String too_much_playing() {
		return getMessage(getMessagesFile().getString("messages.too-much-playing"));
	}
	
	public String no_screen_playing() {
		return getMessage(getMessagesFile().getString("messages.no-screen-playing"));
	}
	
	public String videos_reload_requested() {
		return getMessage(getMessagesFile().getString("messages.videos-reload-requested"));
	}
	
	public String screens_reload_requested() {
		return getMessage(getMessagesFile().getString("messages.screens-reload-requested"));
	}
	
	public String videos_reloaded() {
		return getMessage(getMessagesFile().getString("messages.videos-reloaded"));
	}
	
	public String screens_reloaded() {
		return getMessage(getMessagesFile().getString("messages.screens-reloaded"));
	}
	
	public String video_invalid(String video) {
		return getMessage(getMessagesFile().getString("messages.video-invalid"), video);
	}
	
	public String video_invalid_index(String index) {
		return getMessage(getMessagesFile().getString("messages.video-invalid-index"), index);
	}
	
	public String not_number() {
		return getMessage(getMessagesFile().getString("messages.not-number"));
	}
	
	public String available_images() {
		return getMessage(getMessagesFile().getString("messages.available-images"));
	}
	
	public String available_videos() {
		return getMessage(getMessagesFile().getString("messages.available-videos"));
	}
	
	public String loaded() {
		return getMessage(getMessagesFile().getString("messages.loaded"));
	}
	
	public String not_loaded() {
		return getMessage(getMessagesFile().getString("messages.not-loaded"));
	}
	
	public String negative_number() {
		return getMessage(getMessagesFile().getString("messages.negative-number"));
	}
	
	public String offline_player(String player) {
		return getMessage(getMessagesFile().getString("messages.offline-player"), player);
	}
	
	public String invalid_url(String url) {
		return getMessage(getMessagesFile().getString("messages.invalid-url"), url);
	}
	
	public String invalid_sender() {
		return getMessage(getMessagesFile().getString("messages.invalid-sender"));
	}
	
	public String insufficient_permissions() {
		return getMessage(getMessagesFile().getString("messages.insufficient-permissions"));
	}
}
package fr.xxathyx.mediaplayer.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;

/** 
* The Updater class, is uses to check for update and update the plugin in the main class,
* seen {@link Main#onEnable()}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class Updater {

	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
	/** 
	* Updates the plugin if it is outdated, the server need to be connected to internet and
	* https://api.spigotmc.org have to be responding to the server. The {@link Configuration#plugin_auto_update()}
	* have to return true in order to download the plugin jar.
	* 
	* Update isn't done asynchronously, and not effective after the plugin download in order to avoid reload issues.
	* After download the plugin will be stored in the plugin/update folder, and will replace the oldest jar on the
	* next server restart.
	* 
	* <p>This is the main updater method, because it uses all other ones, and see {@link #isOutdated()},
	* {@link #download()}.
	*/
	
	public void update() {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				if(isOutdated()) {
					Updater.createFolders();
					if(configuration.plugin_auto_update()) {
						download();
					}
				    Bukkit.getLogger().warning("[MediaPlayer]: Plugin version is out of date. Please enable auto-update in configuration, or update it manually from: " + plugin.getDescription().getWebsite());
				    return;
				}
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + ChatColor.GREEN + "You are using the last version of the plugin (" + plugin.getDescription().getVersion() + ")");
			}
		});
	}
	
	/** 
	* Gets whether the used plugin version isn't the same as the last version of the plugin on spigot.
	* 
	* <p>The server need to be connected to internet and <a href="https://api.spigotmc.org">https://api.spigotmc.org</a>
	* have to be responding to the server.
	* 
	* @return Whether the used plugin is outdated.
	*/
	
	public boolean isOutdated() {
	    try {
	        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=79033").openConnection();
	        String newVersion = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())).readLine();
	        httpURLConnection.disconnect();

	        if(!newVersion.equals(plugin.getDescription().getVersion())) {
	            return true;
	        }
	    }catch (Exception e) {
	        Bukkit.getLogger().warning("[MediaPlayer]: Couldn't verify plugin version.");
	    }
	    return false;
	}
	
	/** 
	* Downloads the latest version of the plugin.
	* 
	* <p> <strong>Note: </strong>For the moment the plugin link will change on every update, because it is stored
	* on Dropbox.
	* 
	* <p>The actual link is: <a href="https://www.dropbox.com/s/v1h95zrd5cnken2/MediaPlayer.jar?dl=1">https
	* :https://www.dropbox.com/s/v1h95zrd5cnken2/MediaPlayer.jar?dl=1</a>.
	* 
	* The server need to be connected to internet and <a href="https://www.dropbox.com">https://www.dropbox.com</a>
	* have to be responding to the server.
	* 
	* @throws IOException When failed or interrupted I/O operations occurs.
	* @throws URISyntaxException When the oldest jar locating is failed.
	*/
	
	public void download() {
						
		try {
			File jar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			File newJar = new File(plugin.getDataFolder().getParentFile() + "/update/" + jar.getName());
			
	    	URL onlineJar = new URL("https://www.dropbox.com/s/v1h95zrd5cnken2/MediaPlayer.jar?dl=1");
	    	
	    	FileUtils.copyURLToFile(onlineJar, newJar);
		}catch (URISyntaxException | IOException e) {
	        Bukkit.getLogger().warning("[MediaPlayer]: Couldn't download the new version of the plugin.");
			e.printStackTrace();
		}
	}
	
	/** 
	* Creates plugins update folder if doesn't doesn't exists yet.
	* 
	* <p>This method is called everytime the {@link #update()} detects an outdated plugin version, but if the folder
	* already exists nothing is done.
	*/
	
	public static void createFolders() {
		
		File receptionFolder = new File(Main.getPlugin(Main.class).getDataFolder().getParentFile() + "/update/");
		
		if(!(receptionFolder.exists())) {
			receptionFolder.mkdir();
		}
	}
}
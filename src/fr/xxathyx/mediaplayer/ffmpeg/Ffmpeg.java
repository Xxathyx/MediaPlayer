package fr.xxathyx.mediaplayer.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.system.SystemType;

/** 
* The Ffmpeg class, is used in order to use the ffmpeg library
* while the plugin is running, and this on all operating system,
* see {@link System} and {@link SystemType}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-03 
*/

public class Ffmpeg {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
    /**
     * Gets the ffmpeg library file according to used operating system.
     *
     * @return The ffmpeg library file.
     */
	
	public File getLibraryFile() {
		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.WINDOWS)) return new File(plugin.getDataFolder() + "/libraries/", "ffmpeg.exe");
		return new File(plugin.getDataFolder() + "/libraries/", "ffmpeg");
	}
	
    /**
     * Gets whether the ffmpeg library file is installed.
     *
     * @return Whether the ffmpeg library file is installed.
     */
	
	public boolean isInstalled() {
		
		if(getLibraryFile().exists()) {
			if(getLibraryFile().length() > getFileLength()) return true;
		}
		return false;
	}
	
    /**
     * Gets the ffmpeg library file lenght according to used operating system.
     *
     * @return The ffmpeg library file lenght.
     */
	
	public long getFileLength() {
		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.LINUX)) {return 76000000;}
		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.WINDOWS)) {return 112000000;}
		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.MAC)) {return 76000000;}
		return 76000000;
	}
	
    /**
     * Download from dropbox or fallback server the ffmpeg library according to used operating system.
     */
	
	public void download() {
    	try {
    		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.LINUX)) { FileUtils.copyURLToFile(new URL("https://www.dropbox.com/s/7j5i6n9d373t515/ffmpeg?dl=1"), getLibraryFile()); return;}
    		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.WINDOWS)) { FileUtils.copyURLToFile(new URL("https://www.dropbox.com/s/47mj4sc6p3c2s2m/ffmpeg.exe?dl=1"), getLibraryFile()); return;}
    		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.MAC)) { FileUtils.copyURLToFile(new URL("https://www.dropbox.com/s/aozdf7slrwcrv40/ffmpeg?dl=1"), getLibraryFile()); return;}
    	}catch (IOException e) {
    		try {
    			if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.LINUX)) { FileUtils.copyURLToFile(new URL(configuration.plugin_alternative_server() + "mediaplayer/download/libraries/linux/ffmpeg"), getLibraryFile()); return;}
        		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.WINDOWS)) { FileUtils.copyURLToFile(new URL(configuration.plugin_alternative_server() + "mediaplayer/download/libraries/windows/ffmpeg.exe"), getLibraryFile()); return;}
        		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.MAC)) { FileUtils.copyURLToFile(new URL(configuration.plugin_alternative_server() + "mediaplayer/download/libraries/mac/ffmpeg"), getLibraryFile()); return;}
    		}catch (IOException e1) {
		        Bukkit.getLogger().warning("[MediaPlayer]: Couldn't download plugin libraries, try again later or join our discord community, invitation visible on spigot ressource page.");
    			e1.printStackTrace();
    		}
		}
	}
}
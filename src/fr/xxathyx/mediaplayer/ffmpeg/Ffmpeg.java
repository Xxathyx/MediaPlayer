package fr.xxathyx.mediaplayer.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.system.SystemType;

public class Ffmpeg {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	public File getLibraryFile() {
		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.WINDOWS)) return new File(plugin.getDataFolder() + "/libraries/", "ffmpeg.exe");
		return new File(plugin.getDataFolder() + "/libraries/", "ffmpeg");
	}
	
	public boolean isInstalled() {
		return getLibraryFile().exists();
	}
	
	public void download() {
    	try {
    		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.LINUX)) { FileUtils.copyURLToFile(new URL("https://www.dropbox.com/s/7j5i6n9d373t515/ffmpeg?dl=1"), getLibraryFile()); return;}
    		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.WINDOWS)) { FileUtils.copyURLToFile(new URL("https://www.dropbox.com/s/47mj4sc6p3c2s2m/ffmpeg.exe?dl=1"), getLibraryFile()); return;}
    		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.MAC)) { FileUtils.copyURLToFile(new URL("https://www.dropbox.com/s/aozdf7slrwcrv40/ffmpeg?dl=1"), getLibraryFile()); return;}
    	}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
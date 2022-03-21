package fr.xxathyx.mediaplayer.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.system.SystemType;

public class Ffprobe {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	public File getLibraryFile() {
		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.WINDOWS)) return new File(plugin.getDataFolder() + "/libraries/", "ffprobe.exe");
		return new File(plugin.getDataFolder() + "/libraries/", "ffprobe");
	}
	
	public boolean isInstalled() {
		return getLibraryFile().exists();
	}
	
	public void download() {
    	try {
    		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.LINUX)) { FileUtils.copyURLToFile(new URL("https://www.dropbox.com/s/sw6sponbyea866u/ffprobe?dl=1"), getLibraryFile()); return;}
    		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.WINDOWS)) { FileUtils.copyURLToFile(new URL("https://www.dropbox.com/s/hjst1hw6haf98a2/ffprobe.exe?dl=1"), getLibraryFile()); return;}
    		if(fr.xxathyx.mediaplayer.system.System.getSystemType().equals(SystemType.MAC)) { FileUtils.copyURLToFile(new URL("https://www.dropbox.com/s/1yj1oo83qe90brx/ffprobe?dl=1"), getLibraryFile()); return;}    		
    	}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
package fr.xxathyx.mediaplayer.stream;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.video.Video;

public class Stream {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private Video video;
		
	public Stream(Video video) {
		this.video = video;
	}
	
	public void update() throws IOException {
				
		URL url = video.getStreamURL();
				
		String[] videoCommand = {FilenameUtils.separatorsToUnix(plugin.getFfmpeg().getLibraryFile().getAbsolutePath()),
				"-hide_banner",
				"-loglevel", "error",
				"-i", url.toString(),
				"-q:v", "0",
				"-start_number", "0",
				FilenameUtils.separatorsToUnix(new File(video.getFramesFolder().getPath(), "%d.jpg").getAbsolutePath())};
        
        ProcessBuilder videoProcessBuilder = new ProcessBuilder(videoCommand);
                 
        try {
			plugin.getProcess().add(videoProcessBuilder.inheritIO().start());
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
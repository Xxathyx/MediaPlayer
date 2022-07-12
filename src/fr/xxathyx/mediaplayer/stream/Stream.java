package fr.xxathyx.mediaplayer.stream;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.video.Video;

/** 
* The Stream class, is used to represent a streamed video,
* and update it, it only has one constructor and method,
* see {@link #update()}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-03 
*/

public class Stream {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private Video video;
	
	/**
	* Constructor for Stream class, creates an Stream variable according
	* to a temporary {@link Video} variable.
	* 
	* @param video The temporary video variable used for the stream.
	*/
	
	public Stream(Video video) {
		this.video = video;
	}
	
    /**
     * Add new frames from the streamed video, this method is called once
     * and only one time, it launch a ffmpeg process to catch frames.
     */
	
	public void update() {        
        try {
        	
    		URL url = video.getStreamURL();
			
    		String[] videoCommand = {FilenameUtils.separatorsToUnix(plugin.getFfmpeg().getLibraryFile().getAbsolutePath()),
    				"-hide_banner",
    				"-loglevel", "error",
    				"-i", url.toString(),
    				"-q:v", "0",
    				"-start_number", "0",
    				FilenameUtils.separatorsToUnix(new File(video.getFramesFolder().getPath(), "%d.jpg").getAbsolutePath())};
            
            ProcessBuilder videoProcessBuilder = new ProcessBuilder(videoCommand);
        	
			plugin.getProcess().add(videoProcessBuilder.inheritIO().start());
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
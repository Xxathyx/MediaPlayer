package fr.xxathyx.mediaplayer.video.instance;

import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.video.Video;

public class VideoInstance {
		
	private Video video;
	private Screen screen;
	
	private boolean linked = false;
		
	public VideoInstance(Video video) {
		this.video = video;
	}
	
	public Video getVideo() {
		return video;
	}
	
	public Screen getScreen() {
		return screen;
	}
	
	public boolean isLinked() {
		return linked;
	}
}
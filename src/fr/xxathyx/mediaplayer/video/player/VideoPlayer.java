package fr.xxathyx.mediaplayer.video.player;

import fr.xxathyx.mediaplayer.screen.Screen;

public class VideoPlayer {
	
	private Screen screen;
	
	public VideoPlayer(Screen screen) {
		this.screen = screen;
	}
	
	public Screen getScreen() {
		return screen;
	}
}
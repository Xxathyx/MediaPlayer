package fr.xxathyx.mediaplayer.api;

import fr.xxathyx.mediaplayer.Main;

public class MediaPlayerAPI {
	
	public static Main getPlugin() {
		return Main.getPlugin(Main.class);
	}
}
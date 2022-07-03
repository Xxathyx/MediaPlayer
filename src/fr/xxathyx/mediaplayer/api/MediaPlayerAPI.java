package fr.xxathyx.mediaplayer.api;

import fr.xxathyx.mediaplayer.Main;

/** 
* The MediaPlayerAPI class, is used as a pass-throught to use the
* plugin and its functionalities, as it is build in api style.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-03 
*/

public class MediaPlayerAPI {
	
    /**
     * Gets MediaPlayers main class, which grant access to all information about the plugin.
     *
     * @return MediaPlayer main class.
     */
	
	public static Main getPlugin() {
		return Main.getPlugin(Main.class);
	}
}
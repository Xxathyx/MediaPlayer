package fr.xxathyx.mediaplayer.audio.util;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.util.AudioUtil;

/** 
* The AudioUtilVersion is only called once, while the plugin
* is loading, see {@link Main#onEnable()}. It is based on a
* single method {@link #getAudioUtil()}, that return an adequate
* instance of {@link AudioUtil} according to the server running
* version, the variable is next used in the {@link Main} class.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-03 
*/

public class AudioUtilVersion {
	
    /**
     * Gets {@link AudioUtil} variable according to the running server version.
     * 
     * @return AudioUtil of the server version.
     */
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	public AudioUtil getAudioUtil() {
		
        final String serverVersion = plugin.getServerVersion();
        
        if(serverVersion.equals("v1_9_R2")) {
        	return new v1_9_R2();
        }
        if(serverVersion.equals("v1_9_R1")) {
        	return new v1_9_R1();
        }
        if(serverVersion.equals("v1_8_R3")) {
        	return new v1_8_R3();
        }
        if(serverVersion.equals("v1_8_R2")) {
        	return new v1_8_R2();
        }
        if(serverVersion.equals("v1_8_R1")) {
        	return new v1_8_R1();
        }
        if(serverVersion.equals("v1_7_R4")) {
        	return new v1_7_R4();
        }
        if(serverVersion.equals("v1_7_R3")) {
        	return new v1_7_R4();
        }
        if(serverVersion.equals("v1_7_R2")) {
        	return new v1_7_R4();
        }
        if(serverVersion.equals("v1_7_R1")) {
        	return new v1_7_R4();
        }
        return new Above();
	}
}
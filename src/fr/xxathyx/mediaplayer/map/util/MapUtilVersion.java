package fr.xxathyx.mediaplayer.map.util;

import org.bukkit.Bukkit;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.util.MapUtil;

/** 
* The MapUtilVersion is only called once, while the plugin
* is loading, see {@link Main#onEnable()}. It is based on a
* single method {@link #getMapUtil}, that return an adequate instance
* of {@link MapUtil} according to the server running version, the
* variable is next used in the {@link .Main} class.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class MapUtilVersion {
	
    /**
     * Gets {@link MapUtil} variable according to the running server version.
     * 
     * <p> <strong>Note: </strong> Does return null if the server running version is unreconized or isn't supported.
     * 
     * @return MapUtil of the server version.
     */
	
	public MapUtil getMapUtil() {
		
        final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        
        if(serverVersion.equals("v1_17_R1")) {
        	return new v1_17_R1();
        }
        if(serverVersion.equals("v1_16_R3")) {
        	return new v1_16_R3();
        }
        if(serverVersion.equals("v1_16_R2")) {
        	return new v1_16_R2();
        }
        if(serverVersion.equals("v1_16_R1")) {
        	return new v1_16_R1();
        }
        if(serverVersion.equals("v1_15_R1")) {
        	return new v1_15_R1();
        }
        if(serverVersion.equals("v1_14_R1")) {
        	return new v1_14_R1();
        }
        if(serverVersion.equals("v1_13_R2")) {
        	return new v1_13_R2();
        }
        if(serverVersion.equals("v1_13_R1")) {
        	return new v1_13_R1();
        }
        if(serverVersion.equals("v1_12_R1")) {
        	return new v1_12_R1();
        }
        if(serverVersion.equals("v1_11_R1")) {
        	return new v1_11_R1();
        }
        if(serverVersion.equals("v1_10_R1")) {
        	return new v1_10_R1();
        }
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
	    Bukkit.getLogger().warning("MediaPlayer do not support this server version! You may encounter issues.");
        return null;
	}
}
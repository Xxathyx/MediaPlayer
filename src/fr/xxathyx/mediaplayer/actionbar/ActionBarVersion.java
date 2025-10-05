package fr.xxathyx.mediaplayer.actionbar;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.util.ActionBar;

/** 
* The ActionBarVersion class is only called once, while the plugin
* is loading, see {@link Main#onEnable()}. It is based on a
* single method {@link #getActionBar}, that return an adequate
* instance of {@link ActionBar} according to the server running
* version, the variable is next used in the {@link Main} class.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class ActionBarVersion {
	
    /**
     * Gets {@link ActionBar} variable according to the running server version.
     * 
     * <p> <strong>Note: </strong> Does return null if the server running version is unreconized or isn't supported.
     * 
     * @return ActionBar of the server version.
     */
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	public ActionBar getActionBar() {
		
        final String serverVersion = plugin.getServerVersion();
        
        if(serverVersion.equals("v1_21_R6")) {
        	return new v1_21_R6();
        }
        if(serverVersion.equals("v1_21_R5")) {
        	return new v1_21_R5();
        }
        if(serverVersion.equals("v1_21_R4")) {
        	return new v1_21_R4();
        }
        if(serverVersion.equals("v1_21_R3")) {
        	return new v1_21_R3();
        }
        if(serverVersion.equals("v1_21_R2")) {
        	return new v1_21_R2();
        }
        if(serverVersion.equals("v1_21_R1")) {
        	return new v1_21_R1();
        }
        if(serverVersion.equals("v1_20_R4")) {
        	return new v1_20_R4();
        }
        if(serverVersion.equals("v1_20_R3")) {
        	return new v1_20_R3();
        }
        if(serverVersion.equals("v1_20_R2")) {
        	return new v1_20_R2();
        }
        if(serverVersion.equals("v1_20_R1")) {
        	return new v1_20_R1();
        }
        if(serverVersion.equals("v1_19_R3")) {
        	return new v1_19_R3();
        }
        if(serverVersion.equals("v1_19_R2")) {
        	return new v1_19_R2();
        }
        if(serverVersion.equals("v1_19_R1")) {
        	return new v1_19_R1();
        }
        if(serverVersion.equals("v1_18_R2")) {
        	return new v1_18_R2();
        }
        if(serverVersion.equals("v1_18_R1")) {
        	return new v1_18_R1();
        }
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
        if(serverVersion.equals("v1_7_R3")) {
        	return new v1_7_R4();
        }
        if(serverVersion.equals("v1_7_R2")) {
        	return new v1_7_R4();
        }
        if(serverVersion.equals("v1_7_R1")) {
        	return new v1_7_R4();
        }
	    return null;
	}
}
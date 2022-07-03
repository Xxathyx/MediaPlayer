package fr.xxathyx.mediaplayer.system;

import org.apache.commons.lang3.SystemUtils;

/** 
* The System class, is an utility class, as it only contains one
* method, see {@link #getSystemType()}
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-03 
*/

public class System {
	
    /**
     * Gets the operating system type, between the supported ones.
     *
     * @return The operating system type.
     */
	
	public static SystemType getSystemType() {
        if(SystemUtils.IS_OS_WINDOWS) return SystemType.WINDOWS;
        if(SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX) return SystemType.LINUX;
        if(SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) return SystemType.MAC;
		return SystemType.OTHER;
	}
}
package fr.xxathyx.mediaplayer.system;

import org.apache.commons.lang3.SystemUtils;

public class System {
	
	public static SystemType getSystemType() {
        if(SystemUtils.IS_OS_WINDOWS) return SystemType.WINDOWS;
        if(SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX) return SystemType.LINUX;
        if(SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) return SystemType.MAC;
		return SystemType.OTHER;
	}
}
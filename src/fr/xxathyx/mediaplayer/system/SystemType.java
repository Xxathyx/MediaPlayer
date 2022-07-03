package fr.xxathyx.mediaplayer.system;

/** 
* The NotificationType enum, is used in {@link System} system for {@link System#getSystemType()},
* has only one method, see {@link SystemType#toString()}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public enum SystemType {
	
	LINUX, WINDOWS, MAC, OTHER;
	
    /**
     * Gets enum operation system name as a string, this method is used during
     * first time configuration creation.
     *
     * @return A string of the enum operating system name.
     */
	
	public String toString() {
		if(this == LINUX) return "LINUX";
		if(this == WINDOWS) return "WINDOWS";
		if(this == MAC) return "MAC";
		return "OTHER";
	}
}
package fr.xxathyx.mediaplayer.system;

public enum SystemType {
	
	LINUX, WINDOWS, MAC, OTHER;
	
	public String toString() {
		if(this == LINUX) return "LINUX";
		if(this == WINDOWS) return "WINDOWS";
		if(this == MAC) return "MAC";
		return "OTHER";
	}
}
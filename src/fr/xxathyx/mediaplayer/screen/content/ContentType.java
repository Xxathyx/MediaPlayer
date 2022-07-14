package fr.xxathyx.mediaplayer.screen.content;

public enum ContentType {
	
	IMAGE, VIDEO, NONE;
	
	public String toString() {
		if(this == IMAGE) return "IMAGE";
		if(this == VIDEO) return "VIDEO";
		return "NONE";
	}
	
	public static ContentType fromString(String string) {
		if(string.equals("IMAGE")) return IMAGE;
		if(string.equals("VIDEO")) return VIDEO;
		return NONE;
	}
}
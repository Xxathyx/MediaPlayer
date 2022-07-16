package fr.xxathyx.mediaplayer.screen.content;

/** 
* The ContentType enum, manage the content type in a {@link Content} object.
* This used in the {@link Content} class.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public enum ContentType {
	
	IMAGE, VIDEO, NONE;
	
	/** 
	* Gets the corresponding {@link String} to a given {@link ContentType}.
	* 
	* <p>Does return a 'NONE' string if the content-type isn't reconized.
	*
	* @return The corresponding {@link String} according to as {@link ContentType}. 
	*/
	
	public String toString() {
		if(this == IMAGE) return "IMAGE";
		if(this == VIDEO) return "VIDEO";
		return "NONE";
	}
	
	/** 
	* Gets the corresponding {@link ContentType} to a given {@link String}.
	* 
	* <p>Does return a none-value if the string isn't reconized as a content-type,
	* its reverse {@link #toString()}.
	*
	* @return The corresponding {@link ContentType} according to as {@link String}. 
	*/
	
	public static ContentType fromString(String string) {
		if(string.equals("IMAGE")) return IMAGE;
		if(string.equals("VIDEO")) return VIDEO;
		return NONE;
	}
}
package fr.xxathyx.mediaplayer.util;

import java.util.Arrays;
import java.util.List;

/** 
* The Format class serves as an utility class. As named, it contains compatible videos and streams format
* list, see {@link #getCompatibleFormats()} and {@link #getCompatibleStreamsFormats()}.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class Format {
	
	/** 
	* The Format class can serves as an utility class. As named, it contains compatible videos format list,
	* see {@link #getCompatibleFormats()}, more formats will be supported further.
	* 
	* <p>Compatible video formats: mp4, mov, m4v, avi, gif, webm, mkv, wmv, ts, m3u8.
	* 
	* @return A String list containing all compatible video formats.
	*/
	
	public static List<String> getCompatibleFormats() {
		return Arrays.asList(new String[] { "mp4", "mov", "m4v", "avi", "webm", "mkv", "gif", "m3u8", "ts", "wmv" });
	}
	
	/** 
	* The Format class can serves as an utility class. As named, it contains compatible streams format list,
	* see {@link #getCompatibleStreamsFormats()}, more formats will be supported further.
	* 
	* <p>Compatible streams formats: m3u8.
	* 
	* @return A String list containing all compatible video formats.
	*/
	
	public static List<String> getCompatibleStreamsFormats() {
		return Arrays.asList(new String[] { "m3u8" });
	}
}
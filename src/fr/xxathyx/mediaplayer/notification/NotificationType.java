package fr.xxathyx.mediaplayer.notification;

import fr.xxathyx.mediaplayer.configuration.Configuration;

/** 
* The NotificationType enum, manage what messages can be send according to field.
* This used in the {@link Notification} system to redirect on a real {@link String}
* message through the {@link Configuration}, see {@link #toString(String[])}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public enum NotificationType {
	
	PLUGIN_OUTDATED,
	
	VIDEO_PROCESSING_ESTIMATED_TIME,
	VIDEO_PROCESSING_FRAMES_STARTING,
	VIDEO_PROCESSING_FRAMES_FINISHED,
	VIDEO_PROCESSING_AUDIO_STARTING,
	VIDEO_PROCESSING_AUDIO_FINISHED,
	VIDEO_PROCESSING_MINECRAFT_STARTING,
	VIDEO_PROCESSING_MINECRAFT_FINISHED,
	VIDEO_PROCESSING_FINISHED,
	
	VIDEOS_RELOADED;
	
	/** 
	* Gets the corresponding {@link String} to a given {@link NotificationType},
	* replace all blank values such as %video% for example, to a given String array
	* element(s), if it contains values to be replaced.
	* 
	* <p>Does return a null String if the NotificationType isn't registered or found.
	*
	* @param The arguments to be replaced instead of blank values in the message.
	* @return The corresponding values replaced message. 
	*/
	
	public String toString(String[] args) {
		
		final Configuration configuration = new Configuration();
		
		if(this == PLUGIN_OUTDATED) {
			return configuration.video_processing_estimated_time(args[0]);
		}
		
		if(this == VIDEO_PROCESSING_ESTIMATED_TIME) {
			return configuration.video_processing_estimated_time(args[0]);
		}
		
		if(this == VIDEO_PROCESSING_FRAMES_STARTING) {
			return configuration.video_processing_frames_starting(args[0]);
		}
		
		if(this == VIDEO_PROCESSING_FRAMES_STARTING) {
			return configuration.video_processing_frames_starting(args[0]);
		}
		
		if(this == VIDEO_PROCESSING_FRAMES_FINISHED) {
			return configuration.video_processing_frames_finished(args[0]);
		}
		
		if(this == VIDEO_PROCESSING_AUDIO_STARTING) {
			return configuration.video_processing_audio_starting(args[0]);
		}
		
		if(this == VIDEO_PROCESSING_AUDIO_FINISHED) {
			return configuration.video_processing_audio_finished(args[0]);
		}
		
		if(this == VIDEO_PROCESSING_MINECRAFT_STARTING) {
			return configuration.video_processing_minecraft_starting(args[0]);
		}
		
		if(this == VIDEO_PROCESSING_MINECRAFT_FINISHED) {
			return configuration.video_processing_minecraft_finished(args[0]);
		}
		
		if(this == VIDEO_PROCESSING_FINISHED) {
			return configuration.video_processing_finished(args[0], args[1]);
		}
				
		if(this == VIDEOS_RELOADED) {
			return configuration.videos_reloaded();
		}
		return "null";
	}
}
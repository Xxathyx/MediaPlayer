package fr.xxathyx.mediaplayer.screen.settings;

import fr.xxathyx.mediaplayer.video.Video;

/** 
* The ScreenSettings class is used in {@link Screen} in order to propose adequate settings
* to a screen accord to a {@link Video}. This class only has one constructor.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class ScreenSettings {
	
	private Video video;
	
	public String name;
	public String description;
	public String framesExtension;
	
	public int total;
	public int framerate;
	
	public int differencial;
	
	public double speed;
	
	public boolean realtimeRendering;
	
	public boolean skipDuplicatedFrames;
	
	public boolean showInformations;
	public boolean showFPS;
	
	public int count = 1;
	public int missed = 0;
	
	public int max = 0;
	
	public long time = System.currentTimeMillis();
	public int fps = 0;
	
	/**
	* Constructor for ScreenSettings class, creates an ScreenSettings variable according
	* to a {@link Video}.
	* 
	* @param video The video that the settings are about.
	*/
	
	public ScreenSettings(Video video) {
		
		this.video = video;
		
		name = video.getName();
		description = video.getDescription();
		framesExtension = video.getFramesExtension();
		
		total = video.getTotalFrames()-1;
		framerate = video.getFrameRate();
		
		differencial = (int) Math.round(((double) 20/framerate)*(framerate-20));
		
		speed = video.getSpeed();
		
		realtimeRendering = video.getVideoData().getRealTimeRendering();
		
		skipDuplicatedFrames = video.getVideoData().getSkipDuplicatedFrames();
		
		showInformations = video.getVideoData().getShowInformations();
		showFPS = video.getVideoData().getShowFPS();
		
		count = 0;
		missed = 0;
		
		max = 0;
		
		time = System.currentTimeMillis();
		fps = 0;
	}
	
	/**
	* Gets the the video that the settings are about.
	* 
	* @param video The video that the settings are about.
	*/
	
	public Video getVideo() {
		return video;
	}
}
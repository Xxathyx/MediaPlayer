package fr.xxathyx.mediaplayer.screen.settings;

import fr.xxathyx.mediaplayer.video.Video;

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
	
	public ScreenSettings(String name, String description, String framesExtension, int total, double framerate, double speed, boolean realtimeRendering,
			boolean skipDuplicatedFrames, boolean showFPS, boolean showInformations) {
				
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
	
	public Video getVideo() {
		return video;
	}
}
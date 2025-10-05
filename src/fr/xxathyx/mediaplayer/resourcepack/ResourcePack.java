package fr.xxathyx.mediaplayer.resourcepack;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.simple.JSONObject;

import com.google.gson.Gson;

import dev.jeka.core.api.file.JkPathTree;
import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.video.Video;
import fr.xxathyx.mediaplayer.video.data.VideoData;

/** 
* The ResourcePack class is only called once, while a video
* is loading, and within the audio is enabled, see {@link Video#hasAudio()}
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class ResourcePack {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	/**
	* Creates a resource-pack file base on a video, its used if the video is short enought during video loading.
	* 
	* @param The video containing audio track.
	*/
	
	@SuppressWarnings("unchecked")
	public void create(Video video) {
		
		if(!video.hasAudio()) return;
		
		VideoData videoData = video.getVideoData();
		
		File resourcePackFolder = new File(videoData.getResourcePacksFolder(), video.getName());
		File zipFile = new File(videoData.getResourcePacksFolder(), video.getName() + ".zip");
		
		resourcePackFolder.mkdir();
		
		File metaFile = new File(resourcePackFolder, "pack.mcmeta");
		
        JSONObject metaJSON = new JSONObject();
        metaJSON.put("pack_format", getResourcePackFormat());
        metaJSON.put("description", "Audio addon for " + video.getName());
         
        JSONObject packObject = new JSONObject(); 
        packObject.put("pack", metaJSON);
        
        try (FileWriter file = new FileWriter(metaFile)) {
            file.write(packObject.toJSONString()); 
            file.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }
        
		try {
			Image pack = ImageIO.read(Main.class.getResource("resources/audio.png"));
			BufferedImage bufferedPack = (BufferedImage) pack;
			
			ImageIO.write(bufferedPack, "png", new File(resourcePackFolder, "pack.png"));
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		File assets = new File(resourcePackFolder + "/assets/minecraft/sounds/mediaplayer/");
		assets.mkdirs();
		
		File soundsFile = new File(resourcePackFolder + "/assets/minecraft/", "sounds.json");
		
		try {
			
		    Map<String, Map<String, Object>> soundsMap = new HashMap<>();
		    Map<String, Object> submab = new HashMap<>();
		    
			for(int i = 0; i < video.getAudioChannels(); i++) {
				
				List<Map<String, Object>> param = new ArrayList<Map<String, Object>>();

			    Map<String, Object> inner = new HashMap<>();
			    
			    inner.put("name", "mediaplayer/" + i);
			    inner.put("preload", true);
				
			    param.add(inner);
			    
			    submab.put("sounds", param);
			    soundsMap.put("mediaplayer." + i, submab);
			    
			    com.google.common.io.Files.copy(new File(video.getAudioFolder(), i + ".ogg"), new File(assets, i + ".ogg"));
			}

		    Writer writer = new FileWriter(soundsFile);

		    new Gson().toJson(soundsMap, writer);

		    writer.close();
		}catch (Exception ex) {
		    ex.printStackTrace();
		}
		JkPathTree.of(resourcePackFolder.toPath()).zipTo(zipFile.toPath());
	}
	
	/**
	* Gets the resource pack-format according to the server running
	* version.
	* 
	* <p> <strong>Note: </strong>
	* 
	* 1 is for versions 1.6.1 - 1.8.9,
	* 2 is for versions 1.9 - 1.10.2,
	* 3 is for versions 1.11 - 1.12.2,
	* 4 is for versions 1.13 - 1.14.4,
	* 5 is for versions 1.15 - 1.16.1,
	* 6 is for versions 1.16.2 - 1.16.5,
    * 7 is for versions 1.17.x,
	* 8 is for versions 1.18.x,
	* 9 is for versions 1.19.1,
	* 12 is for versions 1.19.2-3,
	* 13 is for versions 1.19.4,
	* 15 is for versions 1.20.1.
	* 18 is for versions 1.20.2.
	* 23 is for versions 1.20.3.
	* 31 is for versions 1.20.4.
	* 34 is for versions 1.21.1.
	* 42 is for versions 1.21.3.
	* 46 is for versions 1.21.4.
	* 55 is for versions 1.21.5.
	* 60 is for versions 1.21.6.
	* 64 is for versions 1.21.7
	* 64 is for versions 1.21.8
	* 69 is for versions 1.21.9
	* 
	* @return The resource pack-format.
	*/
	
	public int getResourcePackFormat() {
		if(plugin.getServerVersion().equals("v1_21_R6")) return 69;
        if(plugin.getServerVersion().equals("v1_21_R5")) return 64;
        if(plugin.getServerVersion().equals("v1_21_R4")) return 55;
        if(plugin.getServerVersion().equals("v1_21_R3")) return 46;
        if(plugin.getServerVersion().equals("v1_21_R2")) return 42;
        if(plugin.getServerVersion().equals("v1_21_R1")) return 34;
        if(plugin.getServerVersion().equals("v1_20_R4")) return 31;
        if(plugin.getServerVersion().equals("v1_20_R3")) return 23;
        if(plugin.getServerVersion().equals("v1_20_R2")) return 18;
        if(plugin.getServerVersion().equals("v1_20_R1")) return 15;
        if(plugin.getServerVersion().equals("v1_19_R3")) return 13;
        if(plugin.getServerVersion().equals("v1_19_R2")) return 12;
        if(plugin.getServerVersion().equals("v1_19_R1")) return 9;
        if(plugin.getServerVersion().equals("v1_18_R1") || plugin.getServerVersion().equals("v1_18_R2")) return 8;
        if(plugin.getServerVersion().equals("v1_17_R1")) return 7;
        if(plugin.getServerVersion().equals("v1_16_R2") || plugin.getServerVersion().equals("v1_16_R3")) return 6;
        if(plugin.getServerVersion().equals("v1_15_R1") || plugin.getServerVersion().equals("v1_16_R1")) return 5;
        if(plugin.getServerVersion().equals("v1_13_R1") || plugin.getServerVersion().equals("v1_13_R2") || plugin.getServerVersion().equals("v1_14_R1")) return 4;
        if(plugin.getServerVersion().equals("v1_11_R1") || plugin.getServerVersion().equals("v1_12_R1")) return 3;
        if(plugin.getServerVersion().equals("v1_9_R1") || plugin.getServerVersion().equals("v1_9_R2") || plugin.getServerVersion().equals("v1_10_R1")) return 2;
        if(plugin.getServerVersion().equals("v1_8_R1") || plugin.getServerVersion().equals("v1_8_R2") || plugin.getServerVersion().equals("v1_7_R4") || plugin.getServerVersion().equals("v1_7_R3") || plugin.getServerVersion().equals("v1_7_R2") || plugin.getServerVersion().equals("v1_7_R1")) return 1;
		return 8;
	}
}
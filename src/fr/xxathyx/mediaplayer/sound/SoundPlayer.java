package fr.xxathyx.mediaplayer.sound;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.util.AudioUtil;
import fr.xxathyx.mediaplayer.video.audio.Audio;

/** 
* The SoundPlayer class can be described as an utility class, it provide throught
* static method {@link #playSound(Player, SoundType)} a way to direct play sound
* effect to players, and stops an actual played sound {@link #stopSound(Player)}.
*
* <p> <strong>Note: </strong>The class methods aren't used to play video audios,
* see {@link Audio}.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class SoundPlayer {
		
	/** 
	* Plays a sound effect to a player according to a {@link SoundType}.
	* 
	* @param player The player that will be hearing the sound effect.
	* @param soundType The sound effect type to be played.
	*/
	
	public static void playSound(Player player, SoundType soundType) {
		
		if(new Configuration().plugin_ping_sound()) {
			
			final Main plugin = Main.getPlugin(Main.class);
			
			if(soundType.equals(SoundType.NOTIFICATION_UP)) {
				if(plugin.getServerVersion().equals("v1_8_R1") || plugin.getServerVersion().equals("v1_8_R2") || plugin.getServerVersion().equals("v1_8_R3") ||
						plugin.getServerVersion().equals("v1_7_R1") || plugin.getServerVersion().equals("v1_7_R2") || plugin.getServerVersion().equals("v1_7_R3") || plugin.getServerVersion().equals("v1_7_R4")) {
					player.playSound(player.getEyeLocation(), Sound.valueOf("NOTE_PLING"), 1f, 1f);
					return;
				}
				
				if(plugin.getServerVersion().equals("v1_9_R1") || plugin.getServerVersion().equals("v1_9_R2") || plugin.getServerVersion().equals("v1_10_R1") ||
						plugin.getServerVersion().equals("v1_11_R1") || plugin.getServerVersion().equals("v1_12_R1")) {
					player.playSound(player.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 1f, 1f);
					return;
				}
				player.playSound(player.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_BLOCK_PLING"), 1f, 1f);
				return;
			}
			
			if(soundType.equals(SoundType.NOTIFICATION_DOWN)) {
				if(plugin.getServerVersion().equals("v1_8_R1") || plugin.getServerVersion().equals("v1_8_R2") || plugin.getServerVersion().equals("v1_8_R3") ||
						plugin.getServerVersion().equals("v1_7_R1") || plugin.getServerVersion().equals("v1_7_R2") || plugin.getServerVersion().equals("v1_7_R3") || plugin.getServerVersion().equals("v1_7_R4")) {
					player.playSound(player.getEyeLocation(), Sound.valueOf("NOTE_BASS"), 1f, 1f);
					return;
				}
				
				if(plugin.getServerVersion().equals("v1_9_R1") || plugin.getServerVersion().equals("v1_9_R2") || plugin.getServerVersion().equals("v1_10_R1") ||
						plugin.getServerVersion().equals("v1_11_R1") || plugin.getServerVersion().equals("v1_12_R1")) {
					player.playSound(player.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_BASS"), 1f, 1f);
					return;
				}
				player.playSound(player.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_BLOCK_BASS"), 1f, 1f);
				return;
			}
		}
	}
	
	/** 
	* Stops a sound effect that is actually played to a player.
	* 
	* <p>Duplicated from: {@link AudioUtil#stopAudio(Player)}.
	* 
	* @param player The player hearing a sound.
	*/
	
	public static void stopSound(Player player, String sound) {
		Main.getPlugin(Main.class).getAudioUtil().stopAudio(player, sound);
	}
}
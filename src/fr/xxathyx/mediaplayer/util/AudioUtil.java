package fr.xxathyx.mediaplayer.util;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.sound.SoundPlayer;

/** 
* The AudioUtil class serves as an utility class in order to perform audio actions to {@link Player}.
* For the moment it only contains a static method, see {@link #stopAudio(Player)}, more methods will be added
* further.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2022-06-30
*/

public interface AudioUtil {
		
	/** 
	* Stops a sound effect that is actually played to a player, packets will be used for the following versions:
	* v1_8_R1, v1_8_R2, v1_8_R3, v1_9_R1, v1_9_R2, otherwise for further versions the Minecraft stopsound command
	* will be called from Console.
	* 
	* <p>This is the same as calling {@link SoundPlayer#stopSound(Player)}.
	* 
	* @param player The player hearing a sound.
	*/
	
    public abstract void stopAudio(Player player, String sound);
}
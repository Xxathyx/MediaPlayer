package fr.xxathyx.mediaplayer.audio.util;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.util.AudioUtil;

public class Above implements AudioUtil {

	@Override
	public void stopAudio(Player player, String sound) {
		if(player != null) player.stopSound(sound);
	}
}
package fr.xxathyx.mediaplayer.screen.listeners;

import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.sound.SoundPlayer;
import fr.xxathyx.mediaplayer.sound.SoundType;
import fr.xxathyx.mediaplayer.video.data.VideoData;

public class PlayerInteractScreen implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if(event.getRightClicked() instanceof ItemFrame) {
        	
        	Material material = Material.MAP;
        	if(!plugin.isLegacy()) material = Material.FILLED_MAP;
        	        	
			if(((ItemFrame)event.getRightClicked()).getItem().getType() == material) {
				for(Screen screen : plugin.getRegisteredScreens()) {
					if(screen.getFrames().contains((ItemFrame) event.getRightClicked())) {
						
						VideoData videoData = screen.getVideoInstance().getVideo().getVideoData();
						
						if(screen.getFrames().indexOf((ItemFrame) event.getRightClicked()) == (videoData.getMinecraftWidth()*videoData.getMinecraftHeight())/2) {
							if(!screen.isRunning()) {
								screen.setRunning(true);
								event.getPlayer().sendMessage(configuration.video_instance_started(screen.getVideoInstance().getVideo().getName(), String.valueOf(screen.getId())));
								SoundPlayer.playSound(event.getPlayer(), SoundType.NOTIFICATION_UP);
							}
						}
						event.setCancelled(true);
					}
				}
			}
        }
    }
}
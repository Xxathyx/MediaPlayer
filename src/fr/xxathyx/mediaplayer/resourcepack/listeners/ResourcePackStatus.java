package fr.xxathyx.mediaplayer.resourcepack.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import fr.xxathyx.mediaplayer.Main;

public class ResourcePackStatus implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
    @EventHandler
    public void onResourcepackStatusEvent(PlayerResourcePackStatusEvent event) {
    	if(event.getStatus().equals(Status.SUCCESSFULLY_LOADED)) plugin.getPlayersScreens().put(event.getPlayer().getUniqueId(), null);
    }
}
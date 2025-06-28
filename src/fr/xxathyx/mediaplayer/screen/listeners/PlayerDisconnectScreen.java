package fr.xxathyx.mediaplayer.screen.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.xxathyx.mediaplayer.Main;

public class PlayerDisconnectScreen implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	@EventHandler
	public void quitScreen(PlayerInteractEvent event) {
		if(plugin.getPlayersScreens().containsKey(event.getPlayer().getUniqueId())) {
			for(int i = 0; i < plugin.getPlayersScreens().size(); i++) {
				if(plugin.getPlayersScreens().get(event.getPlayer().getUniqueId()) != null) {
					plugin.getPlayersScreens().get(event.getPlayer().getUniqueId()).listeners.remove(event.getPlayer().getUniqueId());
					plugin.getPlayersScreens().remove(event.getPlayer().getUniqueId());
				}
			}
		}
	}
}
package fr.xxathyx.mediaplayer.screen.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import fr.xxathyx.mediaplayer.Main;

public class PlayerBreakScreen implements Listener {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(plugin.getScreensBlocks().contains(event.getBlock())) {
			event.setCancelled(true);
		}
	}
}
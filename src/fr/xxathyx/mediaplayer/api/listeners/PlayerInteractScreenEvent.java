package fr.xxathyx.mediaplayer.api.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.xxathyx.mediaplayer.screen.Screen;

/** 
* Represents an event that is called when a player interacts with an screen, this event
* isn't cancellable.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-03 
*/

public class PlayerInteractScreenEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private Player player;
	private Screen screen;
	
	private int x;
	private int y;
	
	/**
	* Constructor for PlayerInteractScreenEvent class, creates an PlayerInteractScreenEvent variable according
	* to an interact screen event with : {@link Player}, {@link Screen}, and the cursor coordination on the 2-D screen.
	* 
	* @param player The player that touched the screen.
	* @param screen The screen that has been touched.
	* @param x The cursor X according to the screen and content inside it size.
	* @param y The cursor Y according to the screen and content inside it size.
	*/
	
	public PlayerInteractScreenEvent(Player player, Screen screen, int x, int y) {
		this.player = player;
		this.screen = screen;
		this.x = x;
		this.y = y;
	}
	
    /**
     * Gets the {@link Player} that touched the screen.
     *
     * @return The player that touched the screen.
     */
	
	public Player getPlayer() {
		return player;
	}
	
    /**
     * Gets the {@link Screen} that has been touched.
     *
     * @return The screen that has been touched.
     */
	
	public Screen getScreen() {
		return screen;
	}
	
    /**
     * Gets the cursor X according to the screen and content inside it size.
     *
     * @return The cursor X.
     */
	
	public int getCursorX() {
		return x;
	}
	
    /**
     * Gets the cursor Y according to the screen and content inside it size.
     *
     * @return The cursor Y.
     */
	
	public int getCursorY() {
		return y;
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
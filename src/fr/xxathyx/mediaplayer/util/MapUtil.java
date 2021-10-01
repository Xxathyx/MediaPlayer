package fr.xxathyx.mediaplayer.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import fr.xxathyx.mediaplayer.map.util.MapUtilVersion;

/**
 * Interface to a MapUtil, see {@link MapUtilVersion} and {@link fr.xxathyx.icarya.player.maputil}.
 * Contains only one method {@link #refresh(Player, short)}.
 */

public interface MapUtil {
		
    /**
     * Performs a map-id update for a certain player.
     * 
     * @param player The player who will receive the update.
     * @param id The map-id to be updated.
     * @param buffer The new map-pixels.
     */
	
	public abstract void update(Player player, int id, byte[] buffer);
	
    /**
     * Gets a {@link MapView} according to the server running version.
     * 
     * <p> <strong>Note: </strong> The API isn't used directly, its because
     * {@link Bukkit#getMap(int)} in later Minecraft versions (1.13+) only accepts
     * {@link Integer} parameter and not {@link Short}, while compiling with latest
     * version of the API may results in a {@link NoSuchMethodError} for
     * org.bukkit.Bukkit.getMap(I)Lorg/bukkit/map/MapView} in older versions, thats
     * why this method is used instead to support older ones.
     * 
     * @param id The targeted map-id to obtain an {@link MapView}.
     * @return The corresponding MapView to the given id.
     */
	
	public abstract MapView getMapView(int id);
	
    /**
     * Gets an map-id according to a {@link MapView}.
     * 
     * <p>This getter do the opposite of {@link #getMapView(int)}.
     * 
     * <p> <strong>Note: </strong> The API isn't used directly, its because
     * {@link Bukkit#getMap(int)} in later Minecraft versions (1.13+) only accepts
     * {@link Integer} parameter and not {@link Short}, while compiling with latest
     * version of the API may results in a {@link NoSuchMethodError} for
     * org.bukkit.Bukkit.getMap(I)Lorg/bukkit/map/MapView} in older versions, thats
     * why this method is used instead to support older ones.
     * 
     * @param mapView The targeted MapView to obtain an map-id.
     * @return The corresponding id to the given {@link MapView}.
     */
	
	public abstract int getMapId(MapView mapView);
} 
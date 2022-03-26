package fr.xxathyx.mediaplayer.map.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import fr.xxathyx.mediaplayer.util.MapUtil;

/**
 * The v1_18_R2 class implements {@link MapUtil}, it can only be defined once if
 * the server is running under this version, see {@link MapUtilVersion}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2021-12-03 
 */

public class v1_18_R2 implements MapUtil {

	@Override
	public void update(Player player, int id, byte[] buffer) {	    	
    	((org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer)player).getHandle().b.a(new net.minecraft.network.protocol.game.PacketPlayOutMap(id, (byte) 4, false,
    			new ArrayList<>(), new net.minecraft.world.level.saveddata.maps.WorldMap.b(0, 0, 128, 128, buffer)));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public MapView getMapView(int id) {
		return Bukkit.getMap(id);
	}
	
	@Override
	public int getMapId(MapView mapView) {
		return mapView.getId();
	}
}
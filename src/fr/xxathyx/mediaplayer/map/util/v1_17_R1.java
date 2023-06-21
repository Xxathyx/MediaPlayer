package fr.xxathyx.mediaplayer.map.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import fr.xxathyx.mediaplayer.util.MapUtil;

/**
 * The v1_17_R1 class implements {@link MapUtil}, it can only be defined once if
 * the server is running under this version, see {@link MapUtilVersion}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2021-08-23 
 */

public class v1_17_R1 implements MapUtil {
	
	private final Method method = net.minecraft.server.network.PlayerConnection.class.getMethods()[60];
	
	public v1_17_R1() {
		method.setAccessible(true);
	}
	
	@Override
	public void update(Player player, int id, byte[] buffer) {
		try {
			method.invoke(((org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer)player).getHandle().b,
					new net.minecraft.network.protocol.game.PacketPlayOutMap(id, (byte) 4, false, new ArrayList<>(), new net.minecraft.world.level.saveddata.maps.WorldMap.b(0, 0, 128, 128, buffer)));
		}catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
		}
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
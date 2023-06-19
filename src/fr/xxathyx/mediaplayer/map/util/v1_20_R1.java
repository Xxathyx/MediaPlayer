package fr.xxathyx.mediaplayer.map.util;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import fr.xxathyx.mediaplayer.util.MapUtil;
import net.minecraft.server.network.PlayerConnection;

/**
 * The v1_20_R1 class implements {@link MapUtil}, it can only be defined once if
 * the server is running under this version, see {@link MapUtilVersion}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2023-06-15 
 */

public class v1_20_R1 implements MapUtil {
	
	@Override
	public void update(Player player, int id, byte[] buffer) {
				
		Field[] fields = ((org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer)player).getHandle().getClass().getFields();
		net.minecraft.server.network.PlayerConnection playerConnection;
		
		try {
			playerConnection = (PlayerConnection) fields[0].get(((org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer)player).getHandle());
	    	playerConnection.a(new net.minecraft.network.protocol.game.PacketPlayOutMap(id, (byte) 4, false,
	    			new ArrayList<>(), new net.minecraft.world.level.saveddata.maps.WorldMap.b(0, 0, 128, 128, buffer)));
		}catch (IllegalArgumentException | IllegalAccessException e) {
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
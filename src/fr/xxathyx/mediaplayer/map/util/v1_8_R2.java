package fr.xxathyx.mediaplayer.map.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import fr.xxathyx.mediaplayer.util.MapUtil;

/**
 * The v1_8_R2 class implements {@link MapUtil}, it can only be defined once if
 * the server is running under this version, see {@link MapUtilVersion}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2021-08-23 
 */

public class v1_8_R2 implements MapUtil {

	@Override
	public void update(Player player, int id, byte[] buffer) {
		((org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer)player).getHandle().playerConnection.sendPacket(new net.minecraft.server.v1_8_R2.PacketPlayOutMap(id, (byte) 4,
				new ArrayList<>(), buffer, 0, 0, 128, 128));
	}
	
	@Override
	public MapView getMapView(int id) {
		return ((net.minecraft.server.v1_8_R2.WorldMap) ((org.bukkit.craftbukkit.v1_8_R2.CraftWorld) Bukkit.getWorlds().get(0)).getHandle().worldMaps.get(
				net.minecraft.server.v1_8_R2.WorldMap.class, "map_" + id)).mapView;
	}
	
	@Override
	public int getMapId(MapView mapView) {
		return (int) ((org.bukkit.craftbukkit.v1_8_R2.map.CraftMapView) mapView).getId();
	}
}
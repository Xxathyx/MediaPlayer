package fr.xxathyx.mediaplayer.map.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import fr.xxathyx.mediaplayer.util.MapUtil;

/**
 * The v1_7_R3 class implements {@link MapUtil}, it can only be defined once if
 * the server is running under this version, see {@link MapUtilVersion}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2021-08-23 
 */

public class v1_7_R3 implements MapUtil {
	
	@Override
	public void update(Player player, int id, byte[] buffer) {
        for(int x = 0; x < 128; ++x) {
            byte[] bytes = new byte[131];
            bytes[1] = (byte)x;
            for(int y = 0; y < 128; ++y) {
                bytes[y + 3] = buffer[y * 128 + x];
            }
			((org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer)player).getHandle().playerConnection.sendPacket(new net.minecraft.server.v1_7_R3.PacketPlayOutMap(id, bytes));
        }
	}
	
	@Override
	public MapView getMapView(int id) {
		return ((net.minecraft.server.v1_7_R3.WorldMap) ((org.bukkit.craftbukkit.v1_7_R3.CraftWorld) Bukkit.getWorlds().get(0)).getHandle().worldMaps.get(
				net.minecraft.server.v1_7_R3.WorldMap.class, "map_" + id)).mapView;
	}
	
	@Override
	public int getMapId(MapView mapView) {
		return (int) ((org.bukkit.craftbukkit.v1_7_R3.map.CraftMapView) mapView).getId();
	}
}
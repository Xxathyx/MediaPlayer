package fr.xxathyx.mediaplayer.map.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import fr.xxathyx.mediaplayer.util.MapUtil;
import net.minecraft.server.level.EntityPlayer;

/**
 * The v1_20_R3 class implements {@link MapUtil}, it can only be defined once if
 * the server is running under this version, see {@link MapUtilVersion}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2023-12-28 
 */

public class v1_20_R3 implements MapUtil {
	
	private boolean isPaper = false;
	
	public v1_20_R3() {
        try {Class.forName("com.destroystokyo.paper.ParticleBuilder"); isPaper = true;}catch (ClassNotFoundException ignored) {}
	}
	
	@Override
	public void update(Player player, int id, byte[] buffer) {
		
		try {
			
			net.minecraft.network.protocol.game.PacketPlayOutMap packet = new net.minecraft.network.protocol.game.PacketPlayOutMap(id,
					(byte) 4, false, new ArrayList<>(), new net.minecraft.world.level.saveddata.maps.WorldMap.b(0, 0, 128, 128, buffer));
			
			EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer)player).getHandle();
			
			net.minecraft.server.network.PlayerConnection connection =
					((net.minecraft.server.network.PlayerConnection) entityPlayer.getClass().getFields()[isPaper ? 1 : 0].get(entityPlayer));
			connection.getClass().getMethod("b", net.minecraft.network.protocol.Packet.class).invoke(connection, packet);
			
		}catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | SecurityException | NoSuchMethodException e) {
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
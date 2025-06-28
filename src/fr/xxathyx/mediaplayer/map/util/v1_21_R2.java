package fr.xxathyx.mediaplayer.map.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import fr.xxathyx.mediaplayer.util.MapUtil;
import net.minecraft.server.level.EntityPlayer;

/**
 * The v1_21_R2 class implements {@link MapUtil}, it can only be defined once if
 * the server is running under this version, see {@link MapUtilVersion}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2024-11-16
 */

public class v1_21_R2 implements MapUtil {
		
	private Class<?> serverGamePacketListenerImpl;
	private Method sendPacket;
	
	private Constructor<?> packetPlayOutMapConstructor = net.minecraft.network.protocol.game.PacketPlayOutMap.class.getConstructors()[0];
	private Constructor<?> mapIdConstructor;
	private Constructor<?> initWorldMap;
	
	private boolean isPaper = false;
	
	public v1_21_R2() {
		try {
			
			mapIdConstructor = Class.forName("net.minecraft.world.level.saveddata.maps.MapId").getConstructors()[0];
			initWorldMap = net.minecraft.world.level.saveddata.maps.WorldMap.class.getDeclaredClasses()[2].getConstructors()[0];
			initWorldMap.setAccessible(true);
	        try {Class.forName("com.destroystokyo.paper.ParticleBuilder"); isPaper = true;}catch (ClassNotFoundException ignored) {}
	        if(isPaper) {
				serverGamePacketListenerImpl = Class.forName("net.minecraft.server.network.ServerGamePacketListenerImpl");
				sendPacket = serverGamePacketListenerImpl.getMethod("b", net.minecraft.network.protocol.Packet.class);
	        }
		}catch (SecurityException | ClassNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(Player player, int id, byte[] buffer) {
		
		try {
			
			net.minecraft.network.protocol.game.PacketPlayOutMap packet =
					(net.minecraft.network.protocol.game.PacketPlayOutMap) packetPlayOutMapConstructor.newInstance(mapIdConstructor.newInstance(id),
							(byte) 4, false, new ArrayList<>(), initWorldMap.newInstance(0, 0, 128, 128, buffer));
			
			EntityPlayer entityPlayer = ((org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer)player).getHandle();
			
			if(isPaper) {
				sendPacket.invoke(entityPlayer.getClass().getFields()[6].get(entityPlayer), packet);
			}else {
				net.minecraft.server.network.PlayerConnection connection = ((net.minecraft.server.network.PlayerConnection) entityPlayer.getClass().getFields()[5].get(
						((org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer)player).getHandle()));
				connection.getClass().getMethod("b", net.minecraft.network.protocol.Packet.class).invoke(connection, packet);
			}
			
		}catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | SecurityException | NoSuchMethodException e) {
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
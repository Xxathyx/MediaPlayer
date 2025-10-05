package fr.xxathyx.mediaplayer.map.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import fr.xxathyx.mediaplayer.util.MapUtil;
import net.minecraft.network.protocol.Packet;

/**
 * The v1_21_R6 class implements {@link MapUtil}, it can only be defined once if
 * the server is running under this version, see {@link MapUtilVersion}.
 *  
 * @author  Xxathyx
 * @version 1.0.0
 * @since   2025-10-03
 */

public class v1_21_R6 implements MapUtil {
		
	private Class<?> serverGamePacketListenerImpl;
	private Method sendPacket;
	
	private Constructor<?> packetPlayOutMapConstructor;
	private Constructor<?> mapIdConstructor;
	private Constructor<?> initWorldMap;
	
	private boolean isPaper = false;
	
	public v1_21_R6() {
		
		try {
			
			mapIdConstructor = Class.forName("net.minecraft.world.level.saveddata.maps.MapId").getConstructors()[0];
	        try {Class.forName("com.destroystokyo.paper.ParticleBuilder"); isPaper = true;}catch (ClassNotFoundException ignored) {}
	        
			initWorldMap = isPaper ? Class.forName("net.minecraft.world.level.saveddata.maps.MapItemSavedData$MapPatch").getConstructors()[0] : net.minecraft.world.level.saveddata.maps.WorldMap.class.getDeclaredClasses()[2].getConstructors()[0];
			initWorldMap.setAccessible(true);
			
        	packetPlayOutMapConstructor = isPaper ? Class.forName("net.minecraft.network.protocol.game.ClientboundMapItemDataPacket").getConstructors()[0] : net.minecraft.network.protocol.game.PacketPlayOutMap.class.getConstructors()[0];
			
	        if(isPaper) {	
				serverGamePacketListenerImpl = Class.forName("net.minecraft.server.network.ServerGamePacketListenerImpl");
				sendPacket = serverGamePacketListenerImpl.getMethod("send", net.minecraft.network.protocol.Packet.class);
	        }
		}catch (SecurityException | ClassNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(Player player, int id, byte[] buffer) {
		
		try {
						
			Packet<?> packet = (Packet<?>) packetPlayOutMapConstructor.newInstance(
					mapIdConstructor.newInstance(id),
					(byte) 4,
					false,
					new ArrayList<>(),
					initWorldMap.newInstance(0, 0, 128, 128, buffer)
					);
						
			if(isPaper) {
				
				Class<?> serverPlayerClass = Class.forName("net.minecraft.server.level.ServerPlayer");
		        Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer");
		        
		        Method getHandle = craftPlayerClass.getMethod("getHandle");		
		        
		        Object nmsPlayer = getHandle.invoke(player);
		        Object serverPlayer = serverPlayerClass.cast(nmsPlayer);

		        Object playerConnection = serverPlayerClass.getFields()[7].get(serverPlayer);

		        sendPacket.invoke(playerConnection, packet);
			}else {
				net.minecraft.server.network.PlayerConnection connection = ((net.minecraft.server.network.PlayerConnection) net.minecraft.server.level.EntityPlayer.class.getFields()[6].get(
						((org.bukkit.craftbukkit.v1_21_R6.entity.CraftPlayer)player).getHandle()));
				connection.getClass().getMethod("b", net.minecraft.network.protocol.Packet.class).invoke(connection, packet);
			}
			
		}catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | SecurityException | NoSuchMethodException | ClassNotFoundException e) {
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
package fr.xxathyx.mediaplayer.util;

import org.bukkit.entity.LivingEntity;

/** 
* The FacingLocation class serves as an utility class in order to perform actions related to
* an {@link LivingEntity#getEyeLocation()}. For the moment it only contains a static method,
* see {@link #getCardinalDirection(LivingEntity)}, more methods will be added further.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class FacingLocation {
	
	/** 
	* Gets an {@link LivingEntity} facing direction according to his {@link LivingEntity#getLocation()}
	* Yaw value. Catchs : North, Northeast, East, South-East, South, South-West, West, North-West, or null
	* if there isn't corresponding value to the given Yaw.
	* 
	* @param The targeted living-entity.
	* @return The entity facing cardinal direction.
	*/
	
	public static String getCardinalDirection(LivingEntity livingEntity) {
		
        double rotation = (livingEntity.getLocation().getYaw() - 180) % 360;
        
        if(rotation < 0) {
            rotation += 360.0;
        }
        
        if(0 <= rotation && rotation < 22.5) {
            return "N";
        }
        
        if(22.5 <= rotation && rotation < 67.5) {
            return "NE";
        }
        
        if(67.5 <= rotation && rotation < 112.5) {
            return "E";
        }
        
        if(112.5 <= rotation && rotation < 157.5) {
            return "SE";
        }
        
        if(157.5 <= rotation && rotation < 202.5) {
            return "S";
        }
        
        if(202.5 <= rotation && rotation < 247.5) {
            return "SW";
        }
        
        if(247.5 <= rotation && rotation < 292.5) {
            return "W";
        }
        
        if(292.5 <= rotation && rotation < 337.5) {
            return "NW";
        }
        
        if(337.5 <= rotation && rotation < 360.0) {
            return "N";
        }
        return null;
    }
}
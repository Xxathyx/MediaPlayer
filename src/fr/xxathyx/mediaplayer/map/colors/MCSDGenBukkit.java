package fr.xxathyx.mediaplayer.map.colors;

import org.bukkit.map.MapPalette;

public class MCSDGenBukkit extends MapColorSpaceData {
	
    @SuppressWarnings("deprecation")
    public void generate() {
    	
        clear();
        
        for(int i = 0; i < 256; i++) {
            try {
                setColor((byte) i, MapPalette.getColor((byte) i));
            }catch(Throwable throwable) {}
        }
        
        for(int r = 0; r < 256; r++) {
            for(int g = 0; g < 256; g++) {
                for(int b = 0; b < 256; b++) {
                    set(r, g, b, MapPalette.matchColor(r, g, b));
                }
            }
        }
    }
}
package fr.xxathyx.mediaplayer.map.colors;

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.util.Arrays;

/*
 * https://github.com/bergerhealer/BKCommonLib
 */

public class MapColorSpaceData implements Cloneable {
	
    private final Color[] colors = new Color[256];
    private final byte[] data = new byte[16777216];

    public MapColorSpaceData() {
        Arrays.fill((Object[]) colors, new Color(0, 0, 0, 0));
    }

    public final int getColorCount() {
        int count = 0;
        boolean found_all_transparent = false;
        for(Color color: colors) {
            if(color.getAlpha() >= 128) {
                found_all_transparent = true;
                count++;
            }else if(!found_all_transparent) {
                count++;
            }
        }
        return count;
    }

    public final void clearRGBData() {
        Arrays.fill(data, (byte) 0);
    }

    public final void clear() {
        Arrays.fill((Object[]) colors, new Color(0, 0, 0, 0));
        Arrays.fill(data, (byte) 0);
    }

    public void readFrom(MapColorSpaceData data) {
        System.arraycopy(data.data, 0, this.data, 0, this.data.length);
        System.arraycopy(data.colors, 0, colors, 0, colors.length);
    }

    public final void setColor(byte code, Color color) {
        colors[code & 0xFF] = color;
    }

    public final Color getColor(int index) {
        return colors[index & 0xFF];
    }

    public final Color getColor(byte code) {
        return colors[code & 0xFF];
    }

    public final void set(int r, int g, int b, byte code) {
        data[getDataIndex(r, g, b)] = code;
    }

    public final byte get(byte r, byte g, byte b) {
        return data[getDataIndex(r, g, b)];
    }

    public final byte get(int r, int g, int b) {
        return data[getDataIndex(r, g, b)];
    }

    public final void set(int index, byte code) {
        data[index] = code;
    }

    public final byte get(int index) {
        return data[index];
    }

    public final byte[] getRG(int b) {
        byte[] result = new byte[65536];
        getRG(b, result);
        return result;
    }

    public final void getRG(int b, byte[] data) {
        System.arraycopy(data, b << 16, data, 0, 65536);
    }

    public final void setRG(int b, byte[] data) {
        System.arraycopy(data, 0, data, b << 16, 65536);
    }

    public IndexColorModel toIndexColorModel() {
        int num_colors = 0;
        byte[] r = new byte[256];
        byte[] g = new byte[256];
        byte[] b = new byte[256];
        byte[] a = new byte[256];
        for (int i = 0; i < 256; i++) {
            Color c = colors[i];
            if (c.getAlpha() >= 128) {
                num_colors = i + 1;
                r[i] = (byte) c.getRed();
                g[i] = (byte) c.getGreen();
                b[i] = (byte) c.getBlue();
                a[i] = (byte) c.getAlpha();
            }
        }
        return new IndexColorModel(8, num_colors, r, g, b, a);
    }

    public MapColorSpaceData clone() {    	
        MapColorSpaceData clone = new MapColorSpaceData();  
        System.arraycopy(colors, 0, clone.colors, 0, colors.length);
        System.arraycopy(data, 0, clone.data, 0, data.length);   
        return clone;
    }

    private static final int getDataIndex(byte r, byte g, byte b) {
        return (r & 0xFF) + ((g & 0xFF) << 8) + ((b & 0xFF) << 16);
    }

    private static final int getDataIndex(int r, int g, int b) {
        return (r & 0xFF) + ((g & 0xFF) << 8) + ((b & 0xFF) << 16);
    }
}
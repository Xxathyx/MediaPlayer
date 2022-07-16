package fr.xxathyx.mediaplayer.map.colors;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

import fr.xxathyx.mediaplayer.Main;

public class MapColorPalette {

    private static final MapColorSpaceData COLOR_MAP_DATA = Main.getPlugin(Main.class).getMapColorSpaceData();

    public static byte[] convertImage(Image image) {

        BufferedImage bufferedImage;
        
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        
        if(image instanceof BufferedImage) {
        	bufferedImage = (BufferedImage) image;
        }else {
        	bufferedImage = new BufferedImage(width, height, 2);
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.drawImage(image, 0, 0, (ImageObserver) null);
            graphics.dispose();
        }
        
        int[] intPixels = null;
        byte[] bytePixels = null;
        
        DataBuffer dataBuffer = bufferedImage.getRaster().getDataBuffer();
        
        if(dataBuffer instanceof DataBufferInt) {
            intPixels = ((DataBufferInt) dataBuffer).getData();
        }else if(dataBuffer instanceof DataBufferByte) {
            bytePixels = ((DataBufferByte) dataBuffer).getData();
        }
        
        int type = bufferedImage.getType();
        int byteStep = 4;
        
        ColorConverterType converterType = null;
        
        if(type == 1) {
            converterType = ColorConverterType.RGB;
        }else if(type == 2) {
            converterType = ColorConverterType.ARGB;
        }else if(type == 4) {
            converterType = ColorConverterType.BGR;
        }else if(type == 5) {
            converterType = ColorConverterType.BGR;
            byteStep = 3;
        }else if(type == 6) {
            converterType = ColorConverterType.ABGR;
        }
        
        if((intPixels == null && bytePixels == null) || converterType == null) {
            intPixels = new int[width * height];
            bytePixels = null;
            bufferedImage.getRGB(0, 0, width, height, intPixels, 0, width);
            converterType = ColorConverterType.ARGB;
        }
        
        byte[] result = new byte[width * height];
        
        if(bytePixels != null) {
        	
            int index = 0;
            
            for(int i = 0; i < result.length; i++) {
                result[i] = converterType.convertBytes(bytePixels, index);
                index += byteStep;
            }
            
        }else {
            for(int i = 0; i < result.length; i++) result[i] = converterType.convert(intPixels[i]);
        }
        return result;
    }

    private static abstract class ColorConverterType {
    	
        private ColorConverterType() {}

        public abstract byte convert(int param1Int);
        public abstract byte convertBytes(byte[] param1ArrayOfbyte, int param1Int);

        public static final ColorConverterType RGB = new ColorConverterType() {
        	
            public byte convert(int color) {
                return MapColorPalette.COLOR_MAP_DATA.get(color >> 16, color >> 8, color);
            }

            public byte convertBytes(byte[] buffer, int index) {
                return MapColorPalette.COLOR_MAP_DATA.get(buffer[index], buffer[index + 1], buffer[index + 2]);
            }
        };

        public static final ColorConverterType ARGB = new ColorConverterType() {
        	
            public byte convert(int color) {
                if((color & Integer.MIN_VALUE) == 0) return 0;
                return MapColorPalette.COLOR_MAP_DATA.get(color >> 16, color >> 8, color);
            }

            public byte convertBytes(byte[] buffer, int index) {
                if((buffer[index] & 0x80) == 0) return 0;
                return MapColorPalette.COLOR_MAP_DATA.get(buffer[index + 1], buffer[index + 2], buffer[index + 3]);
            }
        };

        public static final ColorConverterType BGR = new ColorConverterType() {
        	
            public byte convert(int color) {
                return MapColorPalette.COLOR_MAP_DATA.get(color, color >> 8, color >> 16);
            }

            public byte convertBytes(byte[] buffer, int index) {
                return MapColorPalette.COLOR_MAP_DATA.get(buffer[index + 2], buffer[index + 1], buffer[index]);
            }
        };

        public static final ColorConverterType ABGR = new ColorConverterType() {
        	
            public byte convert(int color) {
                if((color & Integer.MIN_VALUE) == 0) return 0;
                return MapColorPalette.COLOR_MAP_DATA.get(color, color >> 8, color >> 16);
            }

            public byte convertBytes(byte[] buffer, int index) {
                if((buffer[index] & 0x80) == 0) return 0;
                return MapColorPalette.COLOR_MAP_DATA.get(buffer[index + 3], buffer[index + 2], buffer[index + 1]);
            }
        };
    }
}
package fr.xxathyx.mediaplayer.util;

import java.awt.Color;
import java.awt.image.BufferedImage;

import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadVideo;
import fr.xxathyx.mediaplayer.video.data.VideoData;

/** 
* The ImageUtil class serves as an utility class in order to perform actions into images.
* For the moment it only contains two static method, see {@link #blur(BufferedImage)},
* and {@link #getResemblance(BufferedImage, BufferedImage)} more methods will be added further.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class ImageUtil {
	
	/** 
	* Blurs an given imagen this method is used for restricted content for thumbnail, see
	* {@link VideoData#createThumbnail()}.
	* 
	* @param bufferedImage The image to be blurred.
	*/
	
	public static void blur(BufferedImage bufferedImage) {
		
        int i = 0;
        int max = 400, rad = 10;
        int a1 = 0, r1 = 0, g1 = 0, b1 = 0;
        
        Color[] color = new Color[max];
        
        int x = 1, y = 1, x1, y1, d = 0;
        
        for(x = rad; x < bufferedImage.getHeight() - rad; x++) {
            for(y = rad; y < bufferedImage.getWidth() - rad; y++) {
                for(x1 = x - rad; x1 < x + rad; x1++) {
                    for(y1 = y - rad; y1 < y + rad; y1++) {
                        color[i++] = new Color(bufferedImage.getRGB(y1, x1));
                    }
                }
                i = 0;
                for(d = 0; d < max; d++) {
                    a1 = a1 + color[d].getAlpha();
                }
                a1 = a1 / (max);
                for(d = 0; d < max; d++) {
                    r1 = r1 + color[d].getRed();
                }
                r1 = r1 / (max);
                for(d = 0; d < max; d++) {
                    g1 = g1 + color[d].getGreen();
                }
                g1 = g1 / (max);
                for(d = 0; d < max; d++) {
                    b1 = b1 + color[d].getBlue();
                }
                b1 = b1 / (max);
                int sum1 = (a1 << 24) + (r1 << 16) + (g1 << 8) + b1;
                bufferedImage.setRGB(y, x, (int) (sum1));
            }
        }
	}
	
	/** 
	* Gets for two image their ressemblance in percentage.
	* 
	* <p>This method is used to detect duplicated frames while video loading, see {@link TaskAsyncLoadVideo}.
	* 
	* @param first The original image to be compared.
	* @param second The witness image to be compared.
	*/
	
	public static double getResemblance(BufferedImage first, BufferedImage second) {
		
		int total = first.getWidth()*first.getHeight();
		double ressemblance = 0;
		
		for(int i = 0; i < first.getHeight(); i++) {
			for(int j = 0; j < first.getWidth(); j++) {
				
		        int firstClr = first.getRGB(j, i);
		        
		        int firstRed =   (firstClr & 0x00ff0000) >> 16;
		        int firstGreen = (firstClr & 0x0000ff00) >> 8;
		        int firstBlue =   firstClr & 0x000000ff;
				
		        int secondClr = second.getRGB(j, i);
		        
		        int secondRed =   (secondClr & 0x00ff0000) >> 16;
		        int secondGreen = (secondClr & 0x0000ff00) >> 8;
		        int secondBlue =   secondClr & 0x000000ff;
		        
		        if(firstRed == secondRed && firstGreen == secondGreen && firstBlue == secondBlue) {
		        	ressemblance++;
		        }
			}
		}
		return (ressemblance/total)*100;
	}
}
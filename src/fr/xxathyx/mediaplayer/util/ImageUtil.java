package fr.xxathyx.mediaplayer.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

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
	
    private static int maxGradient = -1;
    	
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
	
	public static BufferedImage convolution(BufferedImage bufferedImage, double[] convolution) {
		
		BufferedImage finalImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		for(int i = 0; i < bufferedImage.getWidth(); i++) {
			for(int j = 0; j < bufferedImage.getHeight(); j++) {				
				if(i < bufferedImage.getWidth()-1 && i > 1 && j < bufferedImage.getHeight()-1 && j > 1) {
					
					int redSum = 0;
					int greenSum = 0;
					int blueSum = 0;
										
					List<Color> pixelColor = Arrays.asList(new Color[] { new Color(bufferedImage.getRGB(i-1, j+1), true), new Color(bufferedImage.getRGB(i, j+1), true),
							new Color(bufferedImage.getRGB(i+1, j+1), true), new Color(bufferedImage.getRGB(i-1, j), true), new Color(bufferedImage.getRGB(i+1, j), true),
							new Color(bufferedImage.getRGB(i-1, j-1), true), new Color(bufferedImage.getRGB(i, j-1), true), new Color(bufferedImage.getRGB(i+1, j-1), true)});
			        
					for(Color color : pixelColor) {
						
						int index = pixelColor.indexOf(color);
						
						redSum = redSum + (int) Math.round(color.getRed()*convolution[index]);
						greenSum = greenSum + (int) Math.round(color.getGreen()*convolution[index]);
						blueSum = blueSum + (int) Math.round(color.getBlue()*convolution[index]);
					}					

					if(redSum > 255) redSum = 255; if(greenSum > 255) greenSum = 255; if(blueSum > 255) blueSum = 255;
					if(redSum  < 0) redSum = 0; if(greenSum < 0) greenSum = 0; if(blueSum < 0) blueSum = 255;
					
					finalImage.setRGB(i, j, new Color(redSum, greenSum, blueSum).getRGB());
				}
			}
		}
		return finalImage;
	}
	
	public static BufferedImage sobel(BufferedImage bufferedImage) {
				
		BufferedImage finalImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
	    
		bufferedImage = toBinary(bufferedImage, 100);
		
		int[] gx = new int[] {-1, 0, 1,
                              -2, 0, 2,
                              -1, 0, 1};
		
		int[] gy = new int[] {-1, -2, -1,
                               0, 0, 0,
                               1, 2, 1};
		
	    int[][] gradients = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
		
        for(int i = 1; i < bufferedImage.getWidth() - 1; i++) {
            for(int j = 1; j < bufferedImage.getHeight() - 1; j++) {
            	
            	List<Integer> pixelColor = Arrays.asList(new Integer[] { bufferedImage.getRGB(i-1, j+1), bufferedImage.getRGB(i, j+1),
            			bufferedImage.getRGB(i+1, j+1), bufferedImage.getRGB(i-1, j), bufferedImage.getRGB(i, j),
            			bufferedImage.getRGB(i+1, j), bufferedImage.getRGB(i-1, j-1), bufferedImage.getRGB(i, j-1),
            			bufferedImage.getRGB(i+1, j-1)});
            	
            	int gradientX = 0;
            	int gradientY = 0;
            	
            	for(int color : pixelColor) {
            		
            		int index = pixelColor.indexOf(color);
            		
            		gradientX = gradientX + index*gx[index];
            		gradientY = gradientY + index*gy[index];
            	}
            	
            	int g = (int) Math.round(Math.sqrt((gradientX*gradientX) + (gradientY*gradientY)));
            	            	
                if(maxGradient < g) {
                    maxGradient = g;
                }
                gradients[i][j] = g;
            }
        }
        
        double scale = 255.0 / maxGradient;
        
        for(int i = 1; i < bufferedImage.getWidth() - 1; i++) {
            for(int j = 1; j < bufferedImage.getHeight() - 1; j++) {
            	
                int edgeColor = gradients[i][j];
                
                edgeColor = (int)(edgeColor * scale);
                edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;

                finalImage.setRGB(i, j, edgeColor);
            }
        }
        return finalImage;
	}
	
    private static BufferedImage toBinary(BufferedImage bufferedImage, int threshold) {
        
        BufferedImage finalImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

        int blackRGB = Color.BLACK.getRGB();
        int whiteRGB = Color.WHITE.getRGB();

        for(int i = 0; i < bufferedImage.getWidth(); i++) {
            for(int j = 0; j < bufferedImage.getHeight(); j++) {

                int rgb = bufferedImage.getRGB(i, j);
                
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = (rgb) & 0xFF;
                
                int gray = (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);
                
                if(gray >= threshold) {
                	finalImage.setRGB(i, j, whiteRGB);
                }else{
                	finalImage.setRGB(i, j, blackRGB);
                }
            }
        }
        return finalImage;
    } 
	
    public static int getGrayScale(int rgb) {
    	
        int red = (rgb >> 16) & 0xff;
        int green = (rgb >> 8) & 0xff;
        int blue = (rgb) & 0xff;
        
        int gray = (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);

        return gray;
    }
}
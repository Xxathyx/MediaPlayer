package fr.xxathyx.mediaplayer.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadVideo;

/** 
* The GIFUtil class serves as an utility class in order to perform video processing on
* them, see {@link TaskAsyncLoadVideo}. For the moment it only contains two static methods,
* see {@link #split(File, File)} and {@link #makeImageForIndex(ImageReader, int, BufferedImage)}.
* more methods will be added further.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class GIFUtil {
	
	/** 
	* Splits an given GIF file into several Portable Network Graphics (png) files, this method
	* is acutally used in video loading, see {@link TaskAsyncLoadVideo}.
	* 
	* @throws IOException When failed or interrupted I/O operations occurs.
	* @param file The GIF file to be splitted.
	* @param destination The folder that will be containing the splitted images.
	*/
	
	public static void split(File file, File destination) throws IOException {
		
	    ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
	    reader.setInput(ImageIO.createImageInputStream(new FileInputStream(file)), false);
	    
	    BufferedImage lastImage = reader.read(0);
	    
	    ImageIO.write(lastImage, "png", new File(destination, destination.listFiles().length + ".png"));

	    for(int i = destination.listFiles().length; i < reader.getNumImages(true); i++) {
	        BufferedImage image = makeImageForIndex(reader, i, lastImage);
	        ImageIO.write(image, "png", new File(destination, destination.listFiles().length + ".png"));
	    }
	}
		
	/** 
	* Fills the transparent pixels of the current frame with the last non-transparent pixel of
	* one of the previous frames.
	* 
	* @throws IOException When failed or interrupted I/O operations occurs.
	* @param reader The GIF image reader, see {@link ImageIO#getImageReadersByFormatName(String)}
	* @param index The current frame to be filled.
	* @param lastImage The last non-transparent pixel of the previous frame.
	*/
	
	public static BufferedImage makeImageForIndex(ImageReader reader, int index, BufferedImage lastImage) throws IOException {
		
	    BufferedImage image = reader.read(index);
	    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

	    if(lastImage != null) newImage.getGraphics().drawImage(lastImage, 0, 0, null);
	    newImage.getGraphics().drawImage(image, 0, 0, null);

	    return newImage;
	}
}
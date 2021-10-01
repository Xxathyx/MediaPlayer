package fr.xxathyx.mediaplayer.image.helpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.image.commands.ImageCommands;

/** 
* The ImageHelper class is an utility class for both {@link Image} and
* {@link ImageCommands} and other classes related with {@link URL} usage,
* it contains a few static methods that can be called from everywhere as
* utilities.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class ImageHelper {
	
    /**
     * Gets whether an given {@link String} is valid as a {@link URL} path.
     *
     * @param path The path that is meant to be an valid URL.
     * @return Whether the given path is a valid URL.
     */
	
	public static boolean isURL(String path) {
		return path.startsWith("http://") || path.startsWith("https://");
	}
	
    /**
     * Gets an image of a given {@link String} path leading to image file.
     * 
     * @throws IOException When the image isn't found.
     * @param path The path to the image file.
     * @return The image as {@link BufferedImage}.
     */
	
	public static BufferedImage getImage(String path) throws IOException {
		
		if(isURL(path)) {
			return ImageIO.read(new URL(path));
		}
		
		if(new File(path).exists()) {
			return ImageIO.read(new File(path));
		}
		throw new IOException(new Configuration().image_not_found());
	}
}
package fr.xxathyx.mediaplayer.video.data.cache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import org.bukkit.configuration.InvalidConfigurationException;

import fr.xxathyx.mediaplayer.map.colors.MapColorPalette;
import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadVideo;

/** 
* The Cache class has a single constructor. As named, the cache serves for storing video
* data, more precisely Minecraft map buffer, a cache array have 16384 elements (128x128),
* each pixel is a color, and the cache store all of them in order to not render the map
* again, the Cache system is created and used in non-realtime-rendering, see
* {@link TaskAsyncLoadVideo}.
* 
* <p>A cache file will always lenght 16384 bytes.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class Cache {
		
	private File file;
	
	/**
	* Constructor for Cache class, creates an Cache variable according to a {@link File}.
	* 
	* <p>Cache files must have an .cache extension to avoid computers transfer problems,
	* the parameter isn't necessary a created cache-file, it can be one about to be created.
	* 
	* @param file The cache-file itself.
	*/
	
	public Cache(File file) {
		this.file = file;
	}
	
	/**
	* Creates a cache-file according to a {@link BufferedImage} and a cache {@link File}
	* passed earlier in the constructor.
	* 
	* @param bufferedImage The image to be stored in a cache system.
	* 
	* @throws FileNotFoundException When the parameter {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
    */
	
	public void createCache(BufferedImage bufferedImage) throws FileNotFoundException, IOException, InvalidConfigurationException {
		writeBytesToFile(file.getPath(), MapColorPalette.convertImage(bufferedImage));
	}
	
	/**
	* Saves a byte array into a {@link File}.
	* 
	* <p>File output will be automatically created if doesn't exists. 
	* 
	* @param fileOutput The file where the byte array will be stored. 
	* @param bytes The byte array to be stored in the file.
	* 
	* @throws IOException When failed or interrupted I/O operations occurs.
    */
	
	public void writeBytesToFile(String fileOutput, byte[] bytes) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(fileOutput);
		fileOutputStream.write(bytes);
		fileOutputStream.close();
    }
	
	/**
	* Reads a cached byte array according to cache {@link File} passed earlier
	* in the constructor.
	* 
	* @return The byte array corresponding containing map colors.
	* 
	* @throws IOException When failed or interrupted I/O operations occurs.
    */
	
	public byte[] getCache() throws IOException {
	    return FileUtils.readFileToByteArray(file);
	}
	
	/**
	* Gets the cache-file according to cache {@link File} passed earlier in the
	* constructor.
	* 
	* @return The cache-file itself.
    */
	
	public File getFile() {
		return file;
	}
}
package fr.xxathyx.mediaplayer.stream.m3u8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import fr.xxathyx.mediaplayer.image.helpers.ImageHelper;
import fr.xxathyx.mediaplayer.ffmpeg.Ffmpeg;

/** 
* The Reader class is used in {@link Video} in order to read and get video sequences
* from online-stream, this method is only used one, then {@link Ffmpeg} take over the
* situtation, its only compatible with Twitch livestreams currently. This class only
* has one constructor.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class Reader {
	
	private File file;
	private ArrayList<URL> sequences = new ArrayList<>();
	
	/**
	* Constructor for Reader class, creates an Reader variable according to a m3u8
	* livestream {@link File}.
	* 
	* @param file The stream m3u8 file to be read.
	*/
	
	public Reader(File file) {
		this.file = file;
	}
	
	/**
	* Reads the m3u8 livestream file in order to get links to the video sequences.
	*/
	
	@SuppressWarnings("deprecation")
	public void read() {
		
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getPath()))) {

			String line;
			
			while((line = bufferedReader.readLine()) != null) {
				
				if(line.startsWith("#EXT-X-TWITCH-PREFETCH:")) {
					line = line.replace("#EXT-X-TWITCH-PREFETCH:", "");
				}
				
				if(!line.startsWith("#")) {		
					if(ImageHelper.isURL(line)) {
						sequences.add(new URL(line));
					}
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Gets the video sequences links to file.
	* 
	* @eturn The video sequences links to file.
	*/
	
	public ArrayList<URL> getSequences() {
		return sequences;
	}
}
package fr.xxathyx.mediaplayer.stream.m3u8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import fr.xxathyx.mediaplayer.image.helpers.ImageHelper;

public class Reader {
	
	private File file;
	private ArrayList<URL> sequences = new ArrayList<>();
	
	public Reader(File file) {
		this.file = file;
	}
	
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
	
	public ArrayList<URL> getSequences() {
		return sequences;
	}
}
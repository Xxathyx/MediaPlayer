package fr.xxathyx.mediaplayer.util;

import java.util.Random;

/** 
* The RandomToken class is used in {@link Configuration} in order to obtain a random
* key for the free authentification server, its an utility class, see {@link #random(int)}.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class RandomToken {
	
	String ch = "azertyuiopqsdfghjklmwxcvbn1234567890";
	
	/** 
	* Generates a random token according to a certain lenght.
	* 
	* @param lenght The caracter lenght of the random-token.
	* @return The random-token as a {@link String}
	*/
	
	public String random(int lenght) {
		
		String key = "";
		
		for(int i = 0; i < lenght; i++) {
		    Random random = new Random();
		    int index = random.nextInt(ch.length());
		    key = key + ch.charAt(index);
		}
		return key;
	}
}
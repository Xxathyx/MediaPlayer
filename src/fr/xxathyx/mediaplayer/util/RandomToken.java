package fr.xxathyx.mediaplayer.util;

import java.util.Random;

public class RandomToken {
	
	String c = "azertyuiopqsdfghjklmwxcvbn1234567890";
	
	public String random(int lenght) {
		
		String key = "";
		
		for(int i = 0; i < lenght; i++) {
			
		    Random random = new Random();
		    
		    int index = random.nextInt(c.length());
		    
		    key = key + c.charAt(index);
		}
		return key;
	}
}
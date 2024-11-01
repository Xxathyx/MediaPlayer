package fr.xxathyx.mediaplayer.util;

import org.bukkit.Bukkit;

public class ServerVersion {
		
	public static String getServerVersion() {
		
		String version = "";
		
        boolean isPaper = false;
        
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            isPaper = true;
        }catch (ClassNotFoundException ignored) {}
                
        if(isPaper) {
        	
        	String brut = Bukkit.getBukkitVersion();
        	
        	if(brut.equals("1.21-R0.1-SNAPSHOT") | brut.equals("1.21.1-R0.1-SNAPSHOT")) version = "v1_21_R1";
        	if(brut.equals("1.20.6-R0.1-SNAPSHOT")) version = "v1_20_R4";
        	if(brut.equals("1.20.4-R0.1-SNAPSHOT")) version = "v1_20_R3";
        	if(brut.equals("1.20.2-R0.1-SNAPSHOT")) version = "v1_20_R2";
        	if(brut.equals("1.20.1-R0.1-SNAPSHOT") | brut.equals("1.20-R0.1-SNAPSHOT")) version = "v1_20_R1";
        	if(brut.equals("1.19.4-R0.1-SNAPSHOT")) version = "v1_19_R3";
        	if(brut.equals("1.19.3-R0.1-SNAPSHOT")) version = "v1_19_R2";
        	if(brut.equals("1.19.2-R0.1-SNAPSHOT")) version = "v1_19_R1";
        	if(brut.equals("1.19.1-R0.1-SNAPSHOT") | brut.equals("1.19-R0.1-SNAPSHOT")) version = "v1_19_R1";
        	if(brut.equals("1.18.2-R0.1-SNAPSHOT")) version = "v1_18_R2";
        	if(brut.equals("1.18.1-R0.1-SNAPSHOT") | brut.equals("1.18-R0.1-SNAPSHOT")) version = "v1_18_R1";
        	if(brut.equals("1.17.1-R0.1-SNAPSHOT") | brut.equals("1.17-R0.1-SNAPSHOT")) version = "v1_17_R1";
        	if(brut.equals("1.16.5-R0.1-SNAPSHOT")) version = "v1_16_R3";
        	if(brut.equals("1.16.4-R0.1-SNAPSHOT")) version = "v1_16_R3";
        	if(brut.equals("1.16.3-R0.1-SNAPSHOT")) version = "v1_16_R2";
        	if(brut.equals("1.16.2-R0.1-SNAPSHOT")) version = "v1_16_R2";
        	if(brut.equals("1.16.1-R0.1-SNAPSHOT")) version = "v1_16_R1";
        	if(brut.equals("1.15.2-R0.1-SNAPSHOT")) version = "v1_15_R1";
        	if(brut.equals("1.15.1-R0.1-SNAPSHOT") | brut.equals("1.15.1-R0.1-SNAPSHOT")) version = "v1_15_R1";
        	if(brut.equals("1.14.4-R0.1-SNAPSHOT")) version = "v1_14_R1";
        	if(brut.equals("1.14.3-R0.1-SNAPSHOT")) version = "v1_14_R1";
        	if(brut.equals("1.14.2-R0.1-SNAPSHOT")) version = "v1_14_R1";
        	if(brut.equals("1.14.1-R0.1-SNAPSHOT") | brut.equals("1.14-R0.1-SNAPSHOT")) version = "v1_14_R1";
        	if(brut.equals("1.13.2-R0.1-SNAPSHOT")) version = "v1_13_R2";
        	if(brut.equals("1.13.1-R0.1-SNAPSHOT")) version = "v1_13_R2";
        	if(brut.equals("1.13-R0.1-SNAPSHOT")) version = "v1_13_R1";
        	if(brut.equals("1.12.2-R0.1-SNAPSHOT")) version = "v1_12_R1";
        	if(brut.equals("1.12.1-R0.1-SNAPSHOT") | brut.equals("1.12-R0.1-SNAPSHOT")) version = "v1_12_R1";
        	if(brut.equals("1.11.2-R0.1-SNAPSHOT")) version = "v1_11_R1";
        	if(brut.equals("1.11.1-R0.1-SNAPSHOT") | brut.equals("1.11-R0.1-SNAPSHOT")) version = "v1_11_R1";
        	if(brut.equals("1.10.2-R0.1-SNAPSHOT")) version = "v1_10_R1";
        	if(brut.equals("1.10-R0.1-SNAPSHOT")) version = "v1_10_R1";
        	if(brut.equals("1.9.4-R0.1-SNAPSHOT")) version = "v1_9_R2";
        	if(brut.equals("1.9.2-R0.1-SNAPSHOT")) version = "v1_9_R1";
        	if(brut.equals("1.8.8-R0.1-SNAPSHOT")) version = "v1_8_R3";

        }else version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        
        return version;
	}
}
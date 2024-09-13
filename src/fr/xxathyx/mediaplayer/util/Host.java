package fr.xxathyx.mediaplayer.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** 
* The Host class is used in {@link Configuration} in order to know the server country
* related to the server-ip throught a online-api, in order to get the adequate plugin
* translation. This class only has one constructor.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class Host {

	private String server = "";
	private JsonObject object;
	
	private List<String> officials = Arrays.asList(new String[] { "FR", "GB" });
	
	/**
	* Constructor for Host class, creates an Host variable according to the running
	* server-ip address {@link String}.
	* 
	* @param ip The server running ip-address.
	*/
	
	public Host(String ip) {
		server = "http://ip-api.com/json/" + ip + "?fields=status,message,countryCode";
		object = new JsonParser().parse(websiteData(server)).getAsJsonObject();
	}
	
	/**
	* Gets the server country-code.
	* 
	* @return The server country-code.
	*/
	
	public String getCountryCode() {
		return getObjectString("countryCode");
	}
	
	/**
	* Gets the plugin supported translations.
	* 
	* @return The plugin supported translations.
	*/
	
	public List<String> getOfficials() {
		return officials;
	}
	
	public String getObjectString(String obj) {
		try {
			return object.get(obj).getAsString();
		}catch (Exception e) {
			return "unknown";
		}
	}
	
    public String websiteData(String website) {
        try {
            StringBuilder stringBuilder = new StringBuilder("");
			URL url = new URL(website);
            
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            
            String line;
            
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            
            bufferedReader.close();
            
            return stringBuilder.toString();
        }catch (Exception e) {
            return null;
        }
    }
}
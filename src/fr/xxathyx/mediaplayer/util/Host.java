package fr.xxathyx.mediaplayer.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Host {

	private String server = "";
	private JsonObject object;
	
	private List<String> officials = Arrays.asList(new String[] { "AR", "DE", "ES", "FR", "GB", "IT", "RU", "TR" });
	
	public Host(String ip) {
		server = "http://ip-api.com/json/" + ip + "?fields=status,message,countryCode";
		object = new JsonParser().parse(websiteData(server)).getAsJsonObject();
	}
	
	public String getCountryCode() {
		
		String countryCode = getObjectString("countryCode");
		
		if(!officials.contains(countryCode)) {
			//return new ServerConfiguration().getServerLangage();
		}
		return getObjectString("countryCode");
	}
	
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
            
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            
            bufferedReader.close();
            
            return stringBuilder.toString();
        }catch (Exception e) {
            return null;
        }
    }
}
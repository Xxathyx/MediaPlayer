package fr.xxathyx.mediaplayer.configuration.updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.xxathyx.mediaplayer.util.YamlConfigurationManager;

/** 
* The ConfigurationUpdater is a tool-class allowing to check for existing YAML
* section {@link #isOutdated(String)}, then mainly rests on the {@link YamlConfigurationManager}
* in order to add/remove configuration content.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class ConfigurationUpdater {
		
	private File file;
	private File update;
	
	private String section;
	
	private FileConfiguration fileconfiguration;
	
	/**
	* Constructor for ConfigurationUpdater class
	* 
	* @param file The YAML configuration-file to be consider by the update.
	* 
	* @throws FileNotFoundException When {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public ConfigurationUpdater(File file, File update, String section)  {
		fileconfiguration = new YamlConfiguration();
		try {
			fileconfiguration.load(file);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		this.file = file;
		this.update = update;
		this.section = section;
	}
	
	/**
	* Checks whether a configuration is outdated or not using her sections.
	* 
    * @return True if the configuration is outdated.
	* @throws URISyntaxException 
	* @throws IOException 
	* @throws InvalidConfigurationException 
	*/
	
	public boolean update() throws URISyntaxException, IOException, InvalidConfigurationException {
		
		FileConfiguration update = new YamlConfiguration();
		update.load(this.update);
		
		String d = section.equals("messages") ? "\"" : "";
		String spaces = "  ";
		
		for(int i=0; i<section.codePoints().filter(c -> c == '.').count(); i++) spaces=spaces+"  ";
		
		Set<String> keys = update.getConfigurationSection(section).getKeys(true);
		keys.removeAll(fileconfiguration.getConfigurationSection(section).getKeys(true));
				
		if(!keys.isEmpty()) {
		    FileWriter writer = new FileWriter(file, true);
			for(String key : keys) {
			    writer.write(spaces+key+": " +d+update.get(section+"."+key)+d+"\n");
			}
			writer.close();
		}
		
		this.update.delete();
		
		return keys.isEmpty();
	}
	
	/**
	* Checks whether a configuration contains a specific section.
	* 
	* @param newSection The configuration section.
	* 
    * @return True if the configuration contains the section or false if it doesn't.
	*/
	
	public boolean contains(String section) {
		return !fileconfiguration.isConfigurationSection(section);
	}
	
	/**
	* Update the configuration by adding a new section to it, it mainly rests on
	* the {@link YamlConfigurationManager#add(File, String, Object)} method.
	* 
	* @param section The configuration section concerned by the update.
	* @param object The object that have to be introduced to the section.
	* 
	* @throws FileNotFoundException When the configuration {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public void update(String section, Object object) {
		try {
			YamlConfigurationManager.add(file, section, object);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Gets the configuration file passed by the constructor {@link #ConfigurationUpdater(File)}.
	* 
    * @return The configuration file.
	*/
	
	public File getFile() {
		return file;
	}
}
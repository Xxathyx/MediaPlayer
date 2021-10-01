package fr.xxathyx.mediaplayer.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.xxathyx.mediaplayer.configuration.updater.ConfigurationUpdater;;

/** 
* The YamlConfigurationManager class serves as an utility class in order to perform actions on
* YAML configurations, see for example {@link ConfigurationUpdater#update(String, Object)}.
* For the moment it only contains three static methods, see {@link #add(File, String, Object)},
* {@link #edit(File, String, Object)} and {@link #remove(File, String)}, more methods will be added
* further.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class YamlConfigurationManager {
	
	private static FileConfiguration fileconfiguration;
	
	/** 
	* Adds a new configuration section with attached object to a given YAML configuration-file.
	* 
	* @param file The YAML configuration-file.
	* @param section The configuration section to be added.
	* @param object The object that have to be introduced to the section.
	* 
	* @throws FileNotFoundException When the parameter {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public static void add(File file, String section, Object object) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set(section, object);
		fileconfiguration.save(file);
	}
	
	/** 
	* Edits an existing configuration section changing the current attached object for a given YAML configuration-file.
	* 
	* <p>Nothing happens if the configuration section isn't found.
	* 
	* @param file The YAML configuration-file.
	* @param section The configuration section concerned by the change.
	* @param object The object that have to be introduced to the section.
	* 
	* @throws FileNotFoundException When the parameter {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public static void edit(File file, String section, Object object) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		
		if(fileconfiguration.isConfigurationSection(section)) {
			fileconfiguration.set(section, object);
			fileconfiguration.save(file);
		}
	}
	
	/** 
	* Removes an existing configuration section for a given YAML configuration-file.
	* 
	* @param file The YAML configuration-file.
	* @param section The configuration section to be removed.
	* 
	* @throws FileNotFoundException When the parameter {@link File#exists()} return false.
	* @throws IOException When failed or interrupted I/O operations occurs.
    * @throws InvalidConfigurationException When non-respect of YAML syntax.
	*/
	
	public static void remove(File file, String section) throws FileNotFoundException, IOException, InvalidConfigurationException {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.load(file);
		fileconfiguration.set(section, null);
		fileconfiguration.save(file);
	}
}
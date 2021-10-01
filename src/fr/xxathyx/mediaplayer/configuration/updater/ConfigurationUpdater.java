package fr.xxathyx.mediaplayer.configuration.updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
	
	public ConfigurationUpdater(File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
		fileconfiguration = new YamlConfiguration();
		fileconfiguration.load(file);
		this.file = file;
	}
	
	/**
	* Checks whether a configuration is outdated or not using her sections.
	* 
	* @param newSection The new introduced configuration section by an updated.
	* 
    * @return True if the configuration contains the section or false if it doesn't.
	*/
	
	public boolean isOutdated(String newSection) {
		return !fileconfiguration.isConfigurationSection(newSection);
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
	
	public void update(String section, Object object) throws FileNotFoundException, IOException, InvalidConfigurationException {
		YamlConfigurationManager.add(file, section, object);
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
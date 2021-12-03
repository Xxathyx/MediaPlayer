package fr.xxathyx.mediaplayer.translation;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import fr.xxathyx.mediaplayer.Main;

public class Translater {
	
	private File file;
	
	public Translater(File file) {
		this.file = file;
	}
	
	public Translater() {
		
	}

	private Main plugin = Main.getPlugin(Main.class);
		
    public void createTranslationFile(String langage) throws URISyntaxException, IOException {
		
		file = new File(plugin.getDataFolder() + "/translations/", langage + ".yml");
	    
		if(!file.exists()) {
			file.getParentFile().mkdir();
		}
		
		if(!file.exists()) {
			
			URI uri = Main.class.getResource("translations/" + langage + ".yml").toURI();

			if("jar".equals(uri.getScheme())) {
			    for(FileSystemProvider provider: FileSystemProvider.installedProviders()) {
			        if(provider.getScheme().equalsIgnoreCase("jar")) {
			            try {
			                provider.getFileSystem(uri);
			            }catch (FileSystemNotFoundException e) {
			                provider.newFileSystem(uri, Collections.emptyMap());
			            }
			        }
			    }
			}
			
			Path source = Paths.get(uri);
			
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + ChatColor.GRAY + "Installing langage: " + langage + ".");
			
			Files.copy(source, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
    }	
}
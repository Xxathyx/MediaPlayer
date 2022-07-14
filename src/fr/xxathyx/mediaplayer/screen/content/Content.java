package fr.xxathyx.mediaplayer.screen.content;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.xxathyx.mediaplayer.image.Image;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.video.Video;

public class Content {
		
	private FileConfiguration fileconfiguration;
	private File file;
	
	public Content(File file) {
		this.file = file;
	}
	
	public void createConfiguration(Screen screen, UUID uuid, File source, ContentType contentType, int keyframe) {
		
		fileconfiguration = new YamlConfiguration();
		
		fileconfiguration.set("screen.uuid", screen.getUUID().toString());
		
		fileconfiguration.set("content.uuid", uuid.toString());
		fileconfiguration.set("content.type", contentType.toString());
		fileconfiguration.set("content.source", source.getAbsolutePath());
		fileconfiguration.set("content.key-frame", keyframe);
		
		try {
			fileconfiguration.save(file);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getConfigFile() {
		
		fileconfiguration = new YamlConfiguration();
		
		try {
			fileconfiguration.load(file);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return fileconfiguration;
    }
	
	public File getFile() {
		return file;
	}
	
	public Screen getScreen() {
		return new Screen(UUID.fromString(getConfigFile().getString("screen.uuid")));
	}
	
	public UUID getUUID() {
		return UUID.fromString(getConfigFile().getString("content.uuid"));
	}
	
	public ContentType getType() {
		return ContentType.fromString(getConfigFile().getString("content.type"));
	}
	
	public File getSource() {
		return new File(getConfigFile().getString("content.source"));
	}
	
	public Video getVideo() {
		if(getType() == ContentType.VIDEO) return new Video(getSource());
		return null;
	}
	
	public Image getImage() {
		if(getType() == ContentType.IMAGE) return new Image(getSource());
		return null;
	}
	
	public int getKeyFrame() {
		return getConfigFile().getInt("content.key-frame");
	}
	
	public ItemStack getIcon() {
		
		String material = "ITEM_FRAME";
		if(getType() == ContentType.IMAGE) material = "PAINTING";
		
		ItemStack icon = new ItemStack(Material.getMaterial(material), 1);
	    ItemMeta icon_meta = icon.getItemMeta();
	    
	    String name = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + FilenameUtils.removeExtension(getSource().getName());
	    if(getType() == ContentType.IMAGE) name = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + FilenameUtils.removeExtension(getSource().getName());
	    
	    icon_meta.setDisplayName(name);
	    icon_meta.setLore(Arrays.asList(new String[] { ChatColor.LIGHT_PURPLE + "screen-uuid: " + getScreen().getUUID().toString(), "", ChatColor.LIGHT_PURPLE +
	    		"content-uuid: " + getUUID().toString(), ChatColor.LIGHT_PURPLE + "content-type: " + getType().toString() }));
	    icon.setItemMeta(icon_meta);
		
	    return icon;
	}
}
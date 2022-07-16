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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.xxathyx.mediaplayer.image.Image;
import fr.xxathyx.mediaplayer.screen.Screen;
import fr.xxathyx.mediaplayer.video.Video;

/** 
* The Content class is used in {@link Screen}, represents a content that can
* be etheir image or video. The class have only one constructor.
* 
* @author  Xxathyx
* @version 1.0.0
* @since   2022-07-16 
*/

public class Content {
		
	private FileConfiguration fileconfiguration;
	private File file;
	
	/**
	* Constructor for Content class, creates an Content variable according
	* to a content configuration-file.
	* 
	* @param file The content configuration-file.
	*/
	
	public Content(File file) {
		this.file = file;
	}
	
	/**
	* Creates a content configuration-file according to a {@link Screen}, a source {@link File}, a {@link ContentType},
	* and a key-frame between {@link Screen#getFrames()}.
	* 
	* @param screen The screen holding the registered content.
	* @param uuid The content unique-id.
	* @param source The content original source, configuration-file of an image or a video.
	* @param keyframe The frame from where the content will spread to be displayed.
	*/
	
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
	
    /**
     * Gets an FileConfiguration instance of the content configuration-file,
     * wich grant access to the configuration data.
     * 
     * <p> This method isn't usable directly, its used on class getters method
     * such as {@link #getName()} to access data.
     *
     * @return FileConfiguration instance of the content configuration-file.
     */
	
	public FileConfiguration getConfigFile() {
		
		fileconfiguration = new YamlConfiguration();
		
		try {
			fileconfiguration.load(file);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return fileconfiguration;
    }
	
	/**
	* Gets the content configuration-file accoding to the values passed earlier in the constructors.
	* 
	* <p>The configuration file does not need to exist, specially if the {@link #createConfiguration(File)}
	* will be called next.
	* 
	* @return The content configuration-file.
	*/
	
	public File getFile() {
		return file;
	}
	
	/**
	* Gets the {@link Screen} where the content is being registered on.
	* 
	* @return The {@link Screen} where the content is being registered on.
	*/
	
	public Screen getScreen() {
		return new Screen(UUID.fromString(getConfigFile().getString("screen.uuid")));
	}
	
	/**
	* Gets the content unique-id.
	* 
	* @return The content unique-id.
	*/
	
	public UUID getUUID() {
		return UUID.fromString(getConfigFile().getString("content.uuid"));
	}
	
	/**
	* Gets the {@link ContentType}, whether its a video, or an image.
	* 
	* <p>{@link Stream}, and custom sources are considered to be videos.
	* 
	* @return The content type.
	*/
	
	public ContentType getType() {
		return ContentType.fromString(getConfigFile().getString("content.type"));
	}
	
	/**
	* Gets the source {@link File} of the content.
	* 
	* @return The source {@link File} of the content.
	*/
	
	public File getSource() {
		return new File(getConfigFile().getString("content.source"));
	}
	
	/**
	* Gets the {@link Video} related to the content instance.
	* 
	* <p>Could return null, if the video isn't found, or the content is an image.
	* 
	* @return The {@link Video} related to the content instance.
	*/
	
	public Video getVideo() {
		if(getType() == ContentType.VIDEO) return new Video(getSource());
		return null;
	}
	
	/**
	* Gets the {@link Image} related to the content instance.
	* 
	* <p>Could return null, if the video isn't found, or the content is an video.
	* 
	* @return The {@link Image} related to the content instance.
	*/
	
	public Image getImage() {
		if(getType() == ContentType.IMAGE) return new Image(getSource());
		return null;
	}
	
	/**
	* Gets the {@link ItemFrame} index of the key-frame, consider it has the screen origin
	* for displaying.
	* 
	* @return The {@link ItemFrame} index of the key-frame.
	*/
	
	public int getKeyFrame() {
		return getConfigFile().getInt("content.key-frame");
	}
	
	/** 
	* Gets an {@link ItemStack} corresponding to a content-icon in a interface {@link Inventory},
	* return an {@link Material#PAINTING} if the content is an image, or a {@link Material#ITEM_FRAME} if the content
	* is a video.
	*
	* @return A content icon.
	*/
	
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
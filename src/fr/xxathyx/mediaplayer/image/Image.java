package fr.xxathyx.mediaplayer.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.image.commands.ImageCommands;
import fr.xxathyx.mediaplayer.image.renderer.ImageRenderer;
import fr.xxathyx.mediaplayer.items.ItemStacks;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/** 
* The Image class is mainly used in the Image part of the ressource, but has
* few usefull usages when crossing to the Video part. The class has three constructors
* and some other methods that bridge between an Image and the {@link ImageCommands}.
* 
* <p>In most cases the class is used to be a reliable between a YAML configuration of
* registered image and their Minecraft features.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class Image {
	
	private final Main plugin = Main.getPlugin(Main.class);
		
	private FileConfiguration fileconfiguration;
	private File file;
	
	private String name;
	private String path;
	
	/**
	* Constructor for Image class, creates an Image variable according
	* to a {@link String} name, it search for an YAML image configuration-file,
	* with the parameter name.
	* 
	* @param name The name of the image without extension.
	*/
	
	public Image(String name) {
		this.name = name;
		this.file = new File(plugin.getDataFolder() + "/images/maps/", name + ".yml");
	}
	
	/**
	* Annother constructor for Image class, this one creates an Image variable according
	* to a {@link File}, the file variable is assigned to the given parameter file.
	* 
	* @param file The image configuration-file.
	*/
	
	public Image(File file) {
		this.file = file;
	}
	
	/**
	* Annother constructor for Image class, this one creates an Image variable according
	* to a {@link String} name and a {@link String} path to image YAML configuration-file.
	* 
	* <p>This constructor should be called when the YAML configuration-file ins't created,
	* then a {@link #create(BufferedImage, Player)} can be called.
	* 
	* @param file The image configuration-file.
	*/
	
	public Image(String name, String path) {
		this.name = name;
		this.path = path;
		this.file = new File(plugin.getDataFolder() + "/images/maps/", name + ".yml");
	}
	
	/**
	* Creates a Minecraft rendered image and gives an image-poster to a {@link Player}, see
	* {@link #give(Player)}.
	* 
    * <p> <strong>Note: </strong>This method should be called once and then wait it finish
    * to be called again for annother image, to avoid a {@link ConcurrentModificationException}
    * since the render is done in asynchronously from the main thread.
	* 
	* @param bufferedImage The image that will be rendered in Minecraft.
	* @param player The player that will receive the image-poster.
	*/
	
	public void create(BufferedImage bufferedImage, Player player) {
    	
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
		    	
		    	ImageRenderer imageRenderer = new ImageRenderer(bufferedImage);
				
				fileconfiguration = new YamlConfiguration();
				
				fileconfiguration.set("image.name", name);
				fileconfiguration.set("image.path", path);
				
		    	imageRenderer.createMaps(Bukkit.getWorlds().get(0));
		    	
				fileconfiguration.set("image.width", imageRenderer.columns);
				fileconfiguration.set("image.height", imageRenderer.lines);
				fileconfiguration.set("image.world", Bukkit.getWorlds().get(0).getName());
				fileconfiguration.set("image.ids", imageRenderer.getIds());
				
				try {
					fileconfiguration.save(file);
				}catch (IOException e) {
					e.printStackTrace();
				}
				
				give(player);
				
				TextComponent dimension = new TextComponent(ChatColor.GRAY + "(Dimension: " + imageRenderer.columns + "x" + imageRenderer.lines + " -> " + ChatColor.BOLD + "/screen create " +
						imageRenderer.columns + " " + imageRenderer.lines + ChatColor.GRAY + ")");
				dimension.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "" + ChatColor.BOLD + "/" + ChatColor.GRAY + "screen create " +
						imageRenderer.columns + " " + imageRenderer.lines).create()));
				dimension.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/screen create " + imageRenderer.columns + " " + imageRenderer.lines));
				player.spigot().sendMessage(dimension);
			}
		});
	}
	
	/**
	* Gives an image-poster {@link ItemStack} to a {@link Player}.
	* 
	* <p>If player inventory is full no items will be dropped.
	* 
	* @param bufferedImage The image that will be rendered in Minecraft.
	* @param player The player that will receive the image-poster.
	*/
	
	public void give(Player player) {
		player.getInventory().addItem(new ItemStacks().poster(this));
	}
	
	/**
	* Gets the image name without extension from the image configuration.
	* 
	* @return The image name.
	*/
	
	public String getName() {
		return getConfigFile().getString("image.name");
	}
	
	/**
	* Gets the original image file path, it can be an online redirection.
	* 
	* @return The original image file path.
	*/
	
	public String getPath() {
		return getConfigFile().getString("image.path");
	}
	
	/**
	* Gets the Minecraft image width.
	* 
	* <p>Corresponding to the number of horizontal ItemFrames necessary
	* to display the image in a single horizontal line.
	* 
	* @return The Minecraft image width count.
	*/
	
	public int getWidth() {
		return getConfigFile().getInt("image.width");
	}
	
	/**
	* Gets the Minecraft image height.
	* 
	* <p>Corresponding to the number of vertical ItemFrames necessary
	* to display the image in a single vertical line.
	* 
	* @return The Minecraft image height count.
	*/
	
	public int getHeight() {
		return getConfigFile().getInt("image.height");
	}
	
	/**
	* Gets the {@link World} that an image is rendered in.
	* 
	* <p> <strong>Note: </strong> The World parameter point the World in wich will
	* be rendered the Map, map_ids.dat will always be stored in the main minecraft
	* World data-folder, even if the maps are being rendered in other world, but more
	* strangely this don't really work has it supposed to, if map are being rendered
	* somewhere else than the main World they will not be showed, because what is always
	* showed and can't be change correspond to main World and no other, even if you render
	* a map on a external world, the map_id of main world will always be displayed has an
	* main world-map, expect this method to always return the main Minecraft World.
	* 
	* @return The World where the image was rendered in.
	*/
	
	public World getWorld() {
		return Bukkit.getWorld(getConfigFile().getString("image.world"));
	}
	
	/**
	* Gets an list of {@link Integer} corresponding to the ids wich were used to render
	* the image in Minecraft.
	* 
	* @return A list of the ids that were used to render the image.
	*/
	
	public List<Integer> getIds() {
		return getConfigFile().getIntegerList("image.ids");
	}
	
	/**
	* Gets the image configuration-file passed earlier by the constructors.
	* 
	* @return The image configuration-file.
	*/
	
	public File getFile() {
		return file;
	}
	
    /**
     * Gets an FileConfiguration instance of the image configuration-file,
     * wich grant access to the configuration data.
     * 
     * <p>This method isn't usable directly, its used on class getters method
     * such as {@link #getName()} to access data.
     *
     * @return FileConfiguration instance of the image configuration-file.
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
}
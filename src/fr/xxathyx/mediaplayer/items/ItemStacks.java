package fr.xxathyx.mediaplayer.items;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;
import fr.xxathyx.mediaplayer.image.Image;
import fr.xxathyx.mediaplayer.image.listeners.PlayerInteractImage;
import fr.xxathyx.mediaplayer.interfaces.Interfaces;

/** 
* The ItemStacks class is a contener, same as {@link Interfaces} but for {@link ItemStack}.
* Storing thtought methods the differents {@link ItemStack} tools for {@link Interfaces}
* inventories.
*  
* <p>There is no special constructor, ItemStacks class can be instantiated from everywhere
* granting an access to the ItemStacks tools through getters.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class ItemStacks {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
	@SuppressWarnings("deprecation")
	public ItemStack glass() {
		
		String material = "BLACK_STAINED_GLASS_PANE";
		if(plugin.isLegacy()) material = "STAINED_GLASS_PANE";
		
  	    ItemStack glass = new ItemStack(Material.getMaterial(material), 1, (byte) 15);;
	    ItemMeta glass_meta = glass.getItemMeta();
	    
	    glass_meta.setDisplayName(ChatColor.BLACK + ".");
	    glass.setItemMeta(glass_meta);
		
	    return glass;
	}
	
	public ItemStack previous() {
		
	    ItemStack previous = new ItemStack(Material.PAPER, 1);
	    ItemMeta previous_meta = previous.getItemMeta();
	    
	    previous_meta.setDisplayName(configuration.item_previous_page_name());
	    previous_meta.setLore(Arrays.asList(new String[] { configuration.item_previous_page_lore() }));
	    previous.setItemMeta(previous_meta);
	    
	    return previous;
	}
	
	public ItemStack refresh() {
		
		String material = "SUNFLOWER";
		if(plugin.isLegacy()) material = "DOUBLE_PLANT";
		
	    ItemStack refresh = new ItemStack(Material.getMaterial(material), 1);
	    ItemMeta refresh_meta = refresh.getItemMeta();
	    
	    refresh_meta.setDisplayName(configuration.item_refresh_page_name());
	    refresh_meta.setLore(Arrays.asList(new String[] { configuration.item_refresh_page_lore() }));
	    refresh.setItemMeta(refresh_meta);
		
	    return refresh;
	}
	
	public ItemStack next() {
		
	    ItemStack next = new ItemStack(Material.PAPER, 1);
	    ItemMeta next_meta = next.getItemMeta();
	    
	    next_meta.setDisplayName(configuration.item_next_page_name());
	    next_meta.setLore(Arrays.asList(new String[] { configuration.item_next_page_lore() }));
	    next.setItemMeta(next_meta);
		
	    return next;
	}
	
	/** 
	* Gets an {@link ItemStack} corresponding to a play icon in a interface {@link Inventory}.
	*
	* @return A play icon.
	*/
	
	public ItemStack play() {
		
		ItemStack play = new ItemStack(Material.ITEM_FRAME, 1);
	    ItemMeta play_meta = play.getItemMeta();
	    
	    play_meta.setDisplayName(configuration.item_play_name());
	    play_meta.setLore(Arrays.asList(new String[] { configuration.item_play_lore() }));
	    play.setItemMeta(play_meta);
		
	    return play;
	}
	
	/** 
	* Gets an {@link ItemStack} corresponding to a load icon in a interface {@link Inventory}.
	* 
	* <p> <strong>Note: </strong> The returned ItemStack Material name is different if the server
	* is running under a legacy version of Minecraft (< 1.13), see {@link Main#isLegacy()}.
	*
	* @return A load icon.
	*/
	
	public ItemStack load() {
		
        String material = "REPEATER";
        if(plugin.isLegacy()) material = "DIODE";
                
		ItemStack load = new ItemStack(Material.getMaterial(material), 1);
	    ItemMeta load_meta = load.getItemMeta();
	    
	    load_meta.setDisplayName(configuration.item_load_name());
	    load_meta.setLore(Arrays.asList(new String[] { configuration.item_load_lore() }));
	    load.setItemMeta(load_meta);
		
	    return load;
	}
	
	/** 
	* Gets an {@link ItemStack} corresponding to a delete icon in a interface {@link Inventory}.
	*
	* @return A delete icon.
	*/
	
	public ItemStack delete() {
		
		ItemStack delete = new ItemStack(Material.FLINT_AND_STEEL, 1);
	    ItemMeta delete_meta = delete.getItemMeta();
	    
	    delete_meta.setDisplayName(configuration.item_delete_name());
	    delete_meta.setLore(Arrays.asList(new String[] { configuration.item_delete_lore_1(), configuration.item_delete_lore_2() }));
	    delete.setItemMeta(delete_meta);
		
	    return delete;
	}
	
	/** 
	* Gets an {@link ItemStack} corresponding to an image-poster.
	*
	* <p>Image posters can be used in the following event : {@link PlayerInteractImage#interactImage(org.bukkit.event.player.PlayerInteractEntityEvent)}.
	*
	* @param image The image that the poster will be displaying.
	* @return A poster-image.
	*/
	
	public ItemStack poster(Image image) {
		
		ItemStack poster = getMap(image.getIds().get(0));
	    ItemMeta poster_meta = poster.getItemMeta();
	    
	    poster_meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + image.getName());
	    poster_meta.setLore(Arrays.asList(new String[] { configuration.item_poster_lore_1(), configuration.item_poster_lore_2(), "", configuration.item_poster_lore_3(),
	    		configuration.item_poster_lore_4(), "", ChatColor.GRAY + "Dimension: " + image.getWidth() + " x " + image.getHeight() }));
	    
	    if(!plugin.isOld()) poster_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
	    if(!plugin.isOld()) poster_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

		poster.setItemMeta(poster_meta);
		
		poster.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.LUCK, 1);
	    
	    return poster;
	}
	
	/** 
	* Gets a Minecraft map {@link ItemStack} according to an {@link Integer} id and
	* the server running version.
	*
	* @param id The map-id.
	* @return A Minecraft map item.
	*/
	
	@SuppressWarnings("deprecation")
	public ItemStack getMap(int id) {
		
		if(!plugin.isLegacy()) {
			
			ItemStack map = new ItemStack(Material.FILLED_MAP);
			
			MapMeta mapMeta = (MapMeta) map.getItemMeta();
			MapView mapView = Bukkit.getMap(id);
			
			mapView.setScale(MapView.Scale.FARTHEST);
	        mapMeta.setMapView(mapView);
	        
			map.setItemMeta(mapMeta);
			
			return map;
		}
		return new ItemStack(Material.MAP, 1, (short) id);
	}
}
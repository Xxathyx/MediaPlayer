package fr.xxathyx.mediaplayer.translation.flags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class Flags {
		
	@SuppressWarnings("deprecation")
	public ItemStack french(Player player) {
		
		ItemStack banner = new ItemStack(Material.LEGACY_BANNER, 1);
	    BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();

	    bannerMeta.setBaseColor(DyeColor.WHITE);

	    List<Pattern> patterns = new ArrayList<Pattern>();

	    patterns.add(new Pattern(DyeColor.BLUE, PatternType.STRIPE_LEFT));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_RIGHT));
	    
	    bannerMeta.setPatterns(patterns);
	    bannerMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Français");
	    bannerMeta.setLore(Arrays.asList(new String[] { ChatColor.AQUA + "Cliquez ici pour changer la langue." }));
	    bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
	    
	    banner.setItemMeta(bannerMeta);
	    
		return banner;
	}
	
    @SuppressWarnings("deprecation")
	public ItemStack deutsch(Player player) {
		
		ItemStack banner = new ItemStack(Material.LEGACY_BANNER, 1);
	    BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();

	    bannerMeta.setBaseColor(DyeColor.WHITE);

	    List<Pattern> patterns = new ArrayList<Pattern>();

	    patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT));
	    patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT));
	    patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT));
	    patterns.add(new Pattern(DyeColor.YELLOW, PatternType.STRIPE_RIGHT));
	    patterns.add(new Pattern(DyeColor.YELLOW, PatternType.STRIPE_RIGHT));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_CENTER));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_CENTER));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_CENTER));
	    
	    bannerMeta.setPatterns(patterns);
	    bannerMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Deutsch");
	    bannerMeta.setLore(Arrays.asList(new String[] { ChatColor.AQUA + "Klicken Sie hier, um die Sprache zu ändern" }));
	    bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
	    
	    banner.setItemMeta(bannerMeta);
	    
		return banner;
	}
    
    @SuppressWarnings("deprecation")
	public ItemStack arabian(Player player) {
		
		ItemStack banner = new ItemStack(Material.LEGACY_BANNER, 1);
	    BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();

	    bannerMeta.setBaseColor(DyeColor.WHITE);

	    List<Pattern> patterns = new ArrayList<Pattern>();

	    patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT));
	    patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT));
	    patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_RIGHT));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_RIGHT));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_RIGHT));
	    
	    bannerMeta.setPatterns(patterns);
	    bannerMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "عربي");
	    bannerMeta.setLore(Arrays.asList(new String[] { ChatColor.AQUA + "انقر هنا لتغيير اللغة" }));
	    bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
	    
	    banner.setItemMeta(bannerMeta);
	    
		return banner;
	}
    
    @SuppressWarnings("deprecation")
	public ItemStack russian(Player player) {
		
		ItemStack banner = new ItemStack(Material.LEGACY_BANNER, 1);
	    BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();

	    bannerMeta.setBaseColor(DyeColor.WHITE);

	    List<Pattern> patterns = new ArrayList<Pattern>();

	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_LEFT));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_LEFT));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_LEFT));
	    patterns.add(new Pattern(DyeColor.BLUE, PatternType.STRIPE_CENTER));
	    patterns.add(new Pattern(DyeColor.BLUE, PatternType.STRIPE_CENTER));
	    patterns.add(new Pattern(DyeColor.BLUE, PatternType.STRIPE_CENTER));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));

	    
	    bannerMeta.setPatterns(patterns);
	    bannerMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Pусский");
	    bannerMeta.setLore(Arrays.asList(new String[] { ChatColor.AQUA + "Нажмите здесь, чтобы изменить язык" }));
	    bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
	    
	    banner.setItemMeta(bannerMeta);
	    
		return banner;
	}
    
    @SuppressWarnings("deprecation")
	public ItemStack spanish(Player player) {
		
		ItemStack banner = new ItemStack(Material.LEGACY_BANNER, 1);
	    BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();

	    bannerMeta.setBaseColor(DyeColor.ORANGE);

	    List<Pattern> patterns = new ArrayList<Pattern>();

	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_LEFT));
	    patterns.add(new Pattern(DyeColor.BROWN, PatternType.MOJANG));
	    patterns.add(new Pattern(DyeColor.BROWN, PatternType.FLOWER));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_LEFT));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_RIGHT));
	    
	    bannerMeta.setPatterns(patterns);
	    bannerMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Espanol");
	    bannerMeta.setLore(Arrays.asList(new String[] { ChatColor.AQUA + "Haga clic aquí para cambiar el idioma." }));
	    bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
	    
	    banner.setItemMeta(bannerMeta);
	    
		return banner;
	}
    
    @SuppressWarnings("deprecation")
	public ItemStack italian(Player player) {
		
		ItemStack banner = new ItemStack(Material.LEGACY_BANNER, 1);
	    BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();

	    bannerMeta.setBaseColor(DyeColor.WHITE);

	    List<Pattern> patterns = new ArrayList<Pattern>();

	    patterns.add(new Pattern(DyeColor.GREEN, PatternType.STRIPE_TOP));
	    patterns.add(new Pattern(DyeColor.GREEN, PatternType.STRIPE_TOP));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_BOTTOM));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_BOTTOM));
	    
	    bannerMeta.setPatterns(patterns);
	    bannerMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Italiano");
	    bannerMeta.setLore(Arrays.asList(new String[] { ChatColor.AQUA + "Clicca qui per cambiare la lingua" }));
	    bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
	    
	    banner.setItemMeta(bannerMeta);
	    
		return banner;
	}
    
    @SuppressWarnings("deprecation")
	public ItemStack english(Player player) {
		
		ItemStack banner = new ItemStack(Material.LEGACY_BANNER, 1);
	    BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();

	    bannerMeta.setBaseColor(DyeColor.BLUE);

	    List<Pattern> patterns = new ArrayList<Pattern>();
	    
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_CENTER));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNRIGHT));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNRIGHT));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_CENTER));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_CENTER));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_MIDDLE));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRAIGHT_CROSS));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.CROSS));
	    
	    bannerMeta.setPatterns(patterns);
	    bannerMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "English");
	    bannerMeta.setLore(Arrays.asList(new String[] { ChatColor.AQUA + "Click here to change the language" }));
	    bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
	    
	    banner.setItemMeta(bannerMeta);
	    
		return banner;
	}
    
    @SuppressWarnings("deprecation")
	public ItemStack turkish(Player player) {
		
		ItemStack banner = new ItemStack(Material.LEGACY_BANNER, 1);
	    BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();

	    bannerMeta.setBaseColor(DyeColor.RED);

	    List<Pattern> patterns = new ArrayList<Pattern>();
	    
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.HALF_VERTICAL));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.CURLY_BORDER));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_TOP));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_BOTTOM));
	    patterns.add(new Pattern(DyeColor.RED, PatternType.CIRCLE_MIDDLE));
	    
	    bannerMeta.setPatterns(patterns);
	    bannerMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Türk");
	    bannerMeta.setLore(Arrays.asList(new String[] { ChatColor.AQUA + "Dili degistirmek icin burayi tiklayin" }));
	    bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
	    
	    banner.setItemMeta(bannerMeta);
	    
		return banner;
	}
    
    /*
    @SuppressWarnings("deprecation")
	public ItemStack Unknown(Player player) {
		
		File profilfile = new File(plugin.getDataFolder() + "/profils/", player.getUniqueId() + ".yml");
		
		Profil profil = new Profil(profilfile);
    	
		File translationFile = new File(plugin.getDataFolder() + "/translations/", profil.getLangage() + ".yml");
		CustomTranslation ct = new CustomTranslation(translationFile);
		
		ItemStack banner = new ItemStack(Material.LEGACY_BANNER, 1);
	    BannerMeta bannerMeta = (BannerMeta)banner.getItemMeta();

	    bannerMeta.setBaseColor(DyeColor.BLACK);

	    List<Pattern> patterns = new ArrayList<Pattern>();
	    
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
	    patterns.add(new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT));
	    patterns.add(new Pattern(DyeColor.BLACK, PatternType.HALF_HORIZONTAL_MIRROR));
	    patterns.add(new Pattern(DyeColor.WHITE, PatternType.TRIANGLE_BOTTOM));
	    patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
	    patterns.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM));
	    patterns.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
	    
	    bannerMeta.setPatterns(patterns);
	    bannerMeta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + ct.getName());
	    bannerMeta.setLore(Arrays.asList(new String[] { ChatColor.AQUA + ct.getDescription() }));
	    bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
	    
	    banner.setItemMeta(bannerMeta);
	    
		return banner;
	}*/
}
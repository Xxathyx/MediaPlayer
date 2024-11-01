package fr.xxathyx.mediaplayer.util;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.group.Group;

import net.md_5.bungee.api.ChatColor;

public class ProgressBar {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private int count = 0;
	private int total;
	
	private String title;
	private char base;
	
	private ChatColor left;
	private ChatColor done;
	
	public ProgressBar(int count, int total, String title, char base, ChatColor left, ChatColor done) {
		this.count = count;
		this.total = total;
		this.base = base;
		this.title = title;
		this.left = left;
		this.done = done;
	}
	
	public void setProgress(int count) {
		this.count = count;
	}
	
	public void send(Group group, String output, String additional) {
		for(Player player : group.getOnlinePlayers()) {
			plugin.getActionBar().send(player, output + " " + additional);
		}
	}
	
	public String build() {
		
		String output = done + title + ": ";
		double decimalPercentage = ((float)count/(float)total)*100;
		int percentage = Integer.parseInt(new DecimalFormat("#").format(decimalPercentage));
				
		int left = (int) (10-percentage/10)+1;
		int done = 10-left;
		
		for(int i=0; i<done; i++) output=output+this.done+base;
		for(int i=0; i<left; i++) output=output+this.left+base;

		return output;
	}
}
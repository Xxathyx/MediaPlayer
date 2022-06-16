package fr.xxathyx.mediaplayer.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.server.Client;
import fr.xxathyx.mediaplayer.util.Host;
import fr.xxathyx.mediaplayer.util.RandomToken;

/** 
* The Configuration class allow a direct bridge between plugin configuration and
* the game itself, it mainly contains getter methods for messages, and some plugin
* parameters and other usefull tools between plugin folder and running application.
*
*<p>Configuration class can be define anyware as basic constructor, wich then grant acces
* to all the getter methods.
*
* @author  Xxathyx
* @version 1.0
* @since   2021-08-23 
*/

public class Configuration {
	
	private final Main plugin = Main.getPlugin(Main.class);
	
	private final File configurationFile = new File(plugin.getDataFolder() + "/configuration/", "configuration.yml");
		
	private final File videosFolder = new File(plugin.getDataFolder() + "/videos/");
	private final File mapsFolder = new File(plugin.getDataFolder() + "/images/maps/");
	
	private FileConfiguration fileconfiguration;
	
    /**
     * Creates the configuration file containing all plugin parametters and messages.
     *
     *<p>Setup should be called once on the server startup, and shall not be effective if
     * the configuration file exists, otherwise delete the configuration file to obtain
     * a new one.
     *
     * @throws IOException When failed or interrupted I/O operations occurs.
     * @throws InvalidConfigurationException When non-respect of YAML syntax.
     */
	
	public void setup() {
		
		if(!configurationFile.exists()) {
			
			String token = new RandomToken().random(10);
			
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
					
					Client client = new Client();
					client.connect();
					
					plugin.setClient(client);
					
					client.write("mediaplayer.register: ", token);
					
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + ChatColor.GREEN + client.getResponse());
				}
			});
			
			fileconfiguration = new YamlConfiguration();
			
			fileconfiguration.set("plugin.auto-update", false);
			fileconfiguration.set("plugin.force-permissions", false);
			fileconfiguration.set("plugin.free-audio-server-handling", true);
			fileconfiguration.set("plugin.free-audio-server-token", token);
			fileconfiguration.set("plugin.own-audio-server-handling-ip", "localhost");
			fileconfiguration.set("plugin.own-audio-server-handling-port", "41");
			fileconfiguration.set("plugin.system", fr.xxathyx.mediaplayer.system.System.getSystemType().toString());
	    	fileconfiguration.set("plugin.langage", "GB");
	    	
		    Host host = new Host(Bukkit.getServer().getIp());
		    if(host.getOfficials().contains(host.getCountryCode())) fileconfiguration.set("plugin.langage", host.getCountryCode());
			
			fileconfiguration.set("plugin.ping-sound", true);
			fileconfiguration.set("plugin.delete-frames-on-loaded", false);
			fileconfiguration.set("plugin.delete-video-on-loaded", false);
			fileconfiguration.set("plugin.save-streams", false);
			
			fileconfiguration.set("plugin.screen-block", "BARRIER");
			fileconfiguration.set("plugin.visible-screen-frames-support", false);
			
			fileconfiguration.set("plugin.maximum-distance-to-receive", 10);
			
			fileconfiguration.set("plugin.maximum-playing-videos", 10);
			fileconfiguration.set("plugin.maximum-loading-videos", 3);
			
			fileconfiguration.set("plugin.remove-screen-structure-on-restart", false);
			fileconfiguration.set("plugin.remove-screen-structure-on-end", false);
			
			fileconfiguration.set("plugin.detect-duplicated-frames", false);
			fileconfiguration.set("plugin.ressemblance-to-skip", 100);
						
			fileconfiguration.set("messages.plugin-outdated", "&cLa version utilisée du plugin n'est plus à jour, activez l'option auto-update depuis la configuration,"
					+ " ou faîtes le manuellement depuis: " + plugin.getDescription().getWebsite());
			
			fileconfiguration.set("messages.video-load-requested", "&aRequête de chargement envoyé, vous serez informé sur la progression du chargement.");
			fileconfiguration.set("messages.video-load-notice", "&7(Plus une vidéo est longue plus le chargement en est de même)");
			fileconfiguration.set("messages.video-unloaded", "&aLa vidéo &l%video% &avient d'être déchargée.");
			fileconfiguration.set("messages.video-downloaded", "&aVous venez de télécharger la video &l%video%&a.");
			fileconfiguration.set("messages.video-deleted", "&aLa vidéo &l%video% &aa été supprimée avec succès.");
			fileconfiguration.set("messages.video-already-loaded", "&cLa vidéo &l%video% &cest déjà chargée.");
			fileconfiguration.set("messages.video-already-loading", "&cLa vidéo &l%video% &cest déjà en train d'être chargée.");
			fileconfiguration.set("messages.video-not-loaded", "&cLa vidéo &l%video% &cn'est pas chargée, &l/video load %video%.");
			fileconfiguration.set("messages.video-invalid", "&cLa vidéo &l%video% &cest introuvable.");
			fileconfiguration.set("messages.video-invalid-index", "&cLa vidéo d'index &l%index% &cest introuvable.");
			fileconfiguration.set("messages.video-selected", "&aVous venez de sélectionner la video &l%video%&a, affectez la en cliquant sur le coin supérieur gauche d'un écran.");
			fileconfiguration.set("messages.video-assigned", "&aVous venez d'assigner la video &l%video% &aà cet écran, &l/video start &apour la jouer.");
			fileconfiguration.set("messages.video-not-enought-space", "&cVous n'avez pas assez d'espace pour charger &l%video%&c.");
			
			fileconfiguration.set("messages.video-description-updated", "&aVous venez de mettre à jour la description de &l%video% &aen %description%&a.");
			fileconfiguration.set("messages.video-framerate-updated", "&aVous venez de mettre à jour le temps de raffraichissement de &l%video% &aà &l%framerate%&a.");
			fileconfiguration.set("messages.video-speed-updated", "&aVous venez de mettre à jour la vitesse de lecture de &l%video% &aen &l%speed%x&a.");
			fileconfiguration.set("messages.video-volume-updated", "&aVous venez de mettre à jour le volume de &l%video% &aà &l%volume%x&a.");
			fileconfiguration.set("messages.video-age-limit-disabled", "&aLa vidéo &l%video% &an'est plus réservée à un certain publique.");
			fileconfiguration.set("messages.video-age-limit-enabled", "&aLa vidéo &l%video% &aest maintenant réservée à un certain publique.");
			fileconfiguration.set("messages.video-audio-disabled", "&aVous venez de désactiver la piste audio de &l%video%&a.");
			fileconfiguration.set("messages.video-audio-enabled", "&aVous venez d'activer la piste audio de &l%video%&a.");
			fileconfiguration.set("messages.video-loop-enabled", "&aVous venez d'activer la répétition de &l%video%&a.");
			fileconfiguration.set("messages.video-loop-disabled", "&aVous venez de désactiver la répétition de &l%video%&a.");
			fileconfiguration.set("messages.video-real-time-rendering-enabled", "&aVous venez d'activer le rendu en temps réel de &l%video%&a.");
			fileconfiguration.set("messages.video-real-time-rendering-disabled", "&aVous venez de désactiver le rendu en temps réel de &l%video%&a.");
			fileconfiguration.set("messages.video-skip-duplicated-frames-enabled", "&aVous venez d'activer le saut d'image dupliquées de &l%video%&a.");
			fileconfiguration.set("messages.video-skip-duplicated-frames-disabled", "&aVous venez de désactiver le saut d'image dupliquées de &l%video%&a.");
			fileconfiguration.set("messages.video-show-informations-enabled", "&aVous venez d'activer l'affichage d'informations de &l%video%&a.");
			fileconfiguration.set("messages.video-show-informations-disabled", "&aVous venez de désactiver l'affichage d'informations de &l%video%&a.");
			fileconfiguration.set("messages.video-show-fps-enabled", "&aVous venez d'activer l'affichage du nombre d'image par seconde de &l%video%&a.");
			fileconfiguration.set("messages.video-show-fps-disabled", "&aVous venez de désactiver l'affichage du nombre d'image par seconde de &l%video%&a.");
			fileconfiguration.set("messages.video-run-on-startup-enabled", "&aVous venez d'activer le lancement au démarrage de &l%video%&a.");
			fileconfiguration.set("messages.video-run-on-startup-disabled", "&aVous venez de désactiver le lancement au démarrage de &l%video%&a.");
			
			fileconfiguration.set("messages.video-processing-frames-starting", "&7L'extraction des images de &l%video% &7vient de commencer. (1/3)");
			fileconfiguration.set("messages.video-processing-frames-finished", "&7L'extraction des images de &l%video% &7s'est terminée.");
			fileconfiguration.set("messages.video-processing-estimated-time", "&7(Durée totale du chargement estimée à : &l%time%±1 &7minutes)");
			
			fileconfiguration.set("messages.video-processing-audio-starting", "&7L'extraction du fichier audio de &l%video% &7vient de commencer. (2/3)");
			fileconfiguration.set("messages.video-processing-audio-finished", "&7L'extraction du fichier audio de &l%video% &7s'est terminée.");
			
			fileconfiguration.set("messages.video-processing-minecraft-starting", "&7La conversion vers Minecraft de &l%video% &7vient de commencer. (3/3)");
			fileconfiguration.set("messages.video-processing-minecraft-finished", "&7La conversion vers Minecraft de &l%video% &7s'est terminée. (3/3)");
			
			fileconfiguration.set("messages.video-processing-finished", "&7Chargement de la vidéo &l%video% &7effectué en &l%time% &7minute(s).");
			
			fileconfiguration.set("messages.video-instance-started", "&aVous venez de lancer la vidéo &l%video% &asur l'écran d'ID: &l%id%&a.");
			fileconfiguration.set("messages.video-instance-stopped", "&aVous venez d'arrêter la vidéo &l%video%&a.");
			fileconfiguration.set("messages.video-instance-not-on-screen", "&cCette vidéo n'est affectée à aucun écran, affectez-la avec le /screen create.");
			
			fileconfiguration.set("messages.videos-reload-requested", "&aRequête de rechargement des vidéos reçue.");
			fileconfiguration.set("messages.videos-reloaded", "&aLa liste des vidéos enregistrées vient d'être rafraichie.");
			fileconfiguration.set("messages.videos-empty-registered", "&cAucune vidéo n'a été détectée et enregistrée.");
			fileconfiguration.set("messages.videos-canceled-tasks", "&aToutes les tâches en cours d'exécution ont été annulées. &8(%tasks%)");
			fileconfiguration.set("messages.videos-notice", "&7(Vous pouvez effectuer la même action, simplement à travers une interface graphique : /videos)");
			
			fileconfiguration.set("messages.screen-created", "&aVous venez de créer un écran de dimension : &l%dimension%&a.");
			fileconfiguration.set("messages.screen-removed", "&aVous venez de retirer l'écran d'index: &l%index%&a.");
			fileconfiguration.set("messages.screen-teleport", "&aVous venez de vous téléporter sur l'écran d'index: &l%index%&a.");
			fileconfiguration.set("messages.screen-invalid-index", "&cL'écran d'index &l%index% &cest introuvable.");
			fileconfiguration.set("messages.screen-cannot-create", "&cImpossible de créer un écran à cet emplacement, des blocs gênent sans doute la place.");
			
			fileconfiguration.set("messages.image-rendered", "&aLe rendu de l'image &l%image% &aest terminé.");
			fileconfiguration.set("messages.image-deleted", "&aVous venez de supprimer l'image &l%image%&a.");
			fileconfiguration.set("messages.image-received", "&aVous venez de recevoir le poster de l'image : &l%image%&a.");
			fileconfiguration.set("messages.image-gived", "&aVous d'envoyez un poster de l'image &l%image% &aà &l%player%&a.");
			fileconfiguration.set("messages.image-placed", "&aVous venez de placer l'image : &l%image%&a.");
			fileconfiguration.set("messages.image-removed", "&aVous venez de retirer l'image : &l%image%&a.");
			fileconfiguration.set("messages.image-not-found", "&cL'image spécifiée est introuvable.");
			fileconfiguration.set("messages.image-invalid", "&cL'adresse, l'index ou le nom de fichier renseigné est invalide.");
			fileconfiguration.set("messages.image-cannot-read", "&cImpossible de récupérer l'image saisit, vérifiez la redirection de l'url.");
			fileconfiguration.set("messages.image-invalid-screen", "&cL'écran sélectionné ne contient pas un nombre suffisant de cadre.");
			fileconfiguration.set("messages.image-already-rendered", "&cLe rendue de l'image &l%image% &ca déjà été fait, faîtes /image give &l%image%&c.");
			
			fileconfiguration.set("messages.item.previous-page.name", "&4&lPage précédente");
			fileconfiguration.set("messages.item.previous-page.lore", "&cCliquez-ici pour acceder à la page précédente.");
			
			fileconfiguration.set("messages.item.refresh-page.name", "&6&lActualiser");
			fileconfiguration.set("messages.item.refresh-page.lore", "&eCliquez-ici pour actualiser la page.");
			
			fileconfiguration.set("messages.item.next-page.name", "&2&lPage suivante");
			fileconfiguration.set("messages.item.next-page.lore", "&aCliquez-ici pour acceder à la page suivante.");
			
			fileconfiguration.set("messages.item.play.name", "&2&lJouer");
			fileconfiguration.set("messages.item.play.lore", "&aCliquez-ici pour jouer la vidéo.");
			
			fileconfiguration.set("messages.item.load.name", "&6&lCharger");
			fileconfiguration.set("messages.item.load.lore", "&eCliquez-ici pour charger la video.");
			
			fileconfiguration.set("messages.item.delete.name", "&4&lSupprimer");
			fileconfiguration.set("messages.item.delete.lore-1", "&cCliquez-ici pour supprimer la vidéo.");
			fileconfiguration.set("messages.item.delete.lore-2", "&c&l[!] Aucun retour en arrière une fois supprimée.");
			
			fileconfiguration.set("messages.item.poster.lore-1", "&eCliquez-droit avec la carte sur le cadre du coin");
			fileconfiguration.set("messages.item.poster.lore-2", "&esupérieur gauche de l'écran pour étaler l'image.");
			fileconfiguration.set("messages.item.poster.lore-3", "&ePour enlever l'image, s'accroupir puis, cliquez-");
			fileconfiguration.set("messages.item.poster.lore-4", "&edroit sur le coin supérieur gauche de l'écran.");
			fileconfiguration.set("messages.age-limit-warning", "&c[!] Contient du contenu explicite.");
			fileconfiguration.set("messages.incompatible", "&4[!] Cette vidéo n'est pas compatible.");
			fileconfiguration.set("messages.no-page-left", "&cVous ne pouvez pas tourner la page car cette page semble inexistante.");
			fileconfiguration.set("messages.too-much-loading", "&cChargement impossible, trop de vidéos sont en cours de chargements.");
			fileconfiguration.set("messages.too-much-playing", "&cLancement impossible, trop de vidéos sont jouées.");
			fileconfiguration.set("messages.no-screen-playing", "&cAucun écran n'est en train de jouer une vidéo.");
			fileconfiguration.set("messages.not-number", "&cLe nombre choisi est invalide.");
			fileconfiguration.set("messages.available-images", "&2Liste des images disponnibles:");
			fileconfiguration.set("messages.available-videos", "&2Liste des vidéos disponnibles:");
			fileconfiguration.set("messages.loaded", "&aChargée.");
			fileconfiguration.set("messages.not-loaded", "&cNon-chargée.");
			fileconfiguration.set("messages.negative-number", "&cLe nombre choisi ne peut pas etre negatif.");
			fileconfiguration.set("messages.offline-player", "&cLe joueur %player% est hors ligne.");
			fileconfiguration.set("messages.invalid-url", "&cL'url saisit : &l%url% &cest invalide.");
			fileconfiguration.set("messages.invalid-sender", "&cCette commande n'est disponible qu'en jeu.");
			fileconfiguration.set("messages.insufficient-permissions", "&cVous n'avez pas la permission d'executer cette commande.");
			
			try {
				fileconfiguration.save(configurationFile);
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(!videosFolder.exists()) {
			videosFolder.mkdir();
		}
		if(!mapsFolder.exists()) {
			mapsFolder.mkdirs();
		}
	}
	
    /**
     * Gets an FileConfiguration instance of the configuration-file.
     *
     * <p>Called on every {@link Configuration} getter method, for both
     * parameters and messages.
     *
     * @return FileConfiguration instance of the configuration-file.
     */
	
	public FileConfiguration getConfigFile() {
		
		fileconfiguration = new YamlConfiguration();
		
		try {
			fileconfiguration.load(configurationFile);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return fileconfiguration;
    }
	
    /**
     * Gets the videos folder, wich containing all relative informations
     * and data about all the videos.
     * 
     * @return Videos folder containing all videos and their own data.
     */
	
	public File getVideosFolder() {
		return videosFolder;
	}
	
    /**
     * Translate alternate color codes such as Ampersand into Minecraft
     * colors.
     *
     * @param a The non Minecraft colored message.
     * @return The Minecraft colored message.
     */
	
	public String getMessage(String a) {
		return ChatColor.translateAlternateColorCodes('&', a);
	}
	
    /**
     * Translate alternate color codes such as Ampersand into Minecraft
     * colors, and replace requested holder by real value.
     *
     * @param a The non Minecraft colored message.
     * @param b The replacing value.
     * @return The Minecraft colored message within the replaced value.
     */
	
	public String getMessage(String a, String b) {
				
		if(a.contains("%video%")) {
			a = a.replaceAll("%video%", b);
		}
		
		if(a.contains("%index%")) {
			a = a.replaceAll("%index%", b);
		}
		
		if(a.contains("%dimension%")) {
			a = a.replaceAll("%dimension%", b);
		}
		
		if(a.contains("%time%")) {
			a = a.replaceAll("%time%", b);
		}
		
		if(a.contains("%image%")) {
			a = a.replaceAll("%image%", b);
		}
		
		if(a.contains("%url%")) {
			a = a.replaceAll("%url%", b);
		}
		
		if(a.contains("%player%")) {
			a = a.replaceAll("%player%", b);
		}
		
		if(a.contains("%tasks%")) {
			a = a.replaceAll("%tasks%", b);
		}
		return ChatColor.translateAlternateColorCodes('&', a);
	}
	
    /**
     * Translate alternate color codes such as Ampersand into Minecraft
     * colors, and replace all requested holders by real values.
     *
     * @param a The non Minecraft colored message.
     * @param b A replacing value.
     * @param c A replacing value.
     * @return The Minecraft colored message within the replaced values.
     */
	
	public String getMessage(String a, String b, String c) {
		
		if(a.contains("%video%")) {
			a = a.replaceAll("%video%", b);
		}
		
		if(a.contains("%image%")) {
			a = a.replaceAll("%image%", b);
		}
		
		if(a.contains("%description%")) {
			a = a.replaceAll("%description%", c);
		}
		
		if(a.contains("%framerate%")) {
			a = a.replaceAll("%framerate%", c);
		}
		
		if(a.contains("%speed%")) {
			a = a.replaceAll("%speed%", c);
		}
		
		if(a.contains("%volume%")) {
			a = a.replaceAll("%volume%", c);
		}
		
		if(a.contains("%time%")) {
			a = a.replaceAll("%time%", c);
		}
		
		if(a.contains("%id")) {
			a = a.replaceAll("%id%", c);
		}
		
		if(a.contains("%player")) {
			a = a.replaceAll("%player%", c);
		}
		return ChatColor.translateAlternateColorCodes('&', a);
	}
	
	//ALL GETTER METHODS THAT CAN BE CALLED FOR OBTAINING CONFIGURATION INFORMATIONS//
	
	public boolean plugin_auto_update() {
		return getConfigFile().getBoolean("plugin.auto-update");
	}
	
	public boolean plugin_force_permissions() {
		return getConfigFile().getBoolean("plugin.force-permissions");
	}
	
	public boolean plugin_free_audio_server_handling() {
		return getConfigFile().getBoolean("plugin.free-audio-server-handling");
	}
	
	public String free_audio_server_token() {
		return getConfigFile().getString("plugin.free-audio-server-token");
	}
	
	public String own_audio_server_handling_ip() {
		return getConfigFile().getString("plugin.own-audio-server-handling-ip");
	}
	
	public int own_audio_server_handling_port() {
		return getConfigFile().getInt("plugin.own-audio-server-handling-port");
	}
	
	public String plugin_langage() {
		return getConfigFile().getString("plugin.langage");
	}
	
	public boolean plugin_ping_sound() {
		return getConfigFile().getBoolean("plugin.ping-sound");
	}
	
	public boolean frames_delete_on_loaded() {
		return getConfigFile().getBoolean("plugin.delete-frames-on-loaded");
	}
	
	public boolean video_delete_on_loaded() {
		return getConfigFile().getBoolean("plugin.delete-video-on-loaded");
	}
	
	public boolean save_streams() {
		return getConfigFile().getBoolean("plugin.save-streams");
	}
	
	public String screen_block() {
		String material = getConfigFile().getString("plugin.screen-block");
		if(material.equalsIgnoreCase("BARRIER") && plugin.isOld()) material = "GLASS"; 
		return material;
	}
	
	public boolean visible_screen_frames_support() {
		return getConfigFile().getBoolean("plugin.visible-screen-frames-support");
	}
	
	public int maximum_distance_to_receive() {
		return getConfigFile().getInt("plugin.maximum-distance-to-receive");
	}
	
	public int maximum_playing_videos() {
		return getConfigFile().getInt("plugin.maximum-playing-videos");
	}
	
	public int maximum_loading_videos() {
		return getConfigFile().getInt("plugin.maximum-loading-videos");
	}
	
	public boolean remove_screen_on_end() {
		return getConfigFile().getBoolean("plugin.remove-screen-structure-on-end");
	}
	
	public boolean remove_screen_on_restart() {
		return getConfigFile().getBoolean("plugin.remove-screen-structure-on-restart");
	}
	
	public boolean detect_duplicated_frames() {
		return getConfigFile().getBoolean("plugin.detect-duplicated-frames");
	}
	
	public double ressemblance_to_skip() {
		return getConfigFile().getDouble("plugin.ressemblance-to-skip");
	}
	
	public String plugin_outdated() {
		return getMessage(getConfigFile().getString("messages.plugin-outdated"));
	}
	
	public String video_load_requested() {
		return getMessage(getConfigFile().getString("messages.video-load-requested"));
	}
		
	public String video_load_notice() {
		return getMessage(getConfigFile().getString("messages.video-load-notice"));
	}
	
	public String video_unloaded(String video) {
		return getMessage(getConfigFile().getString("messages.video-unloaded"), video);
	}
	
	public String video_downloaded(String video) {
		return getMessage(getConfigFile().getString("messages.video-downloaded"), video);
	}
	
	public String video_deleted(String video) {
		return getMessage(getConfigFile().getString("messages.video-deleted"), video);
	}
	
	public String video_already_loaded(String video) {
		return getMessage(getConfigFile().getString("messages.video-already-loaded"), video);
	}
	
	public String video_already_loading(String video) {
		return getMessage(getConfigFile().getString("messages.video-already-loading"), video);
	}
	
	public String video_not_loaded(String video) {
		return getMessage(getConfigFile().getString("messages.video-not-loaded"), video);
	}
	
	public String video_selected(String video) {
		return getMessage(getConfigFile().getString("messages.video-selected"), video);
	}
	
	public String video_assigned(String video) {
		return getMessage(getConfigFile().getString("messages.video-assigned"), video);
	}
	
	public String video_not_enought_space(String video) {
		return getMessage(getConfigFile().getString("messages.video-not-enought-space"), video);
	}
	
	public String video_description_updated(String video, String description) {
		return getMessage(getConfigFile().getString("messages.video-description-updated"), video, description);
	}
	
	public String video_framerate_updated(String video, String framerate) {
		return getMessage(getConfigFile().getString("messages.video-framerate-updated"), video, framerate);
	}
	
	public String video_speed_updated(String video, String speed) {
		return getMessage(getConfigFile().getString("messages.video-speed-updated"), video, speed);
	}
	
	public String video_volume_updated(String video, String volume) {
		return getMessage(getConfigFile().getString("messages.video-volume-updated"), video, volume);
	}
	
	public String video_age_limit_disabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-age-limit-disabled"), video);
	}
	
	public String video_age_limit_enabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-age-limit-enabled"), video);
	}
	
	public String video_audio_disabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-audio-disabled"), video);
	}
	
	public String video_audio_enabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-audio-enabled"), video);
	}
	
	public String video_loop_enabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-loop-enabled"), video);
	}
	
	public String video_loop_disabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-loop-disabled"), video);
	}
	
	public String video_real_time_rendering_enabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-real-time-rendering-enabled"), video);
	}
	
	public String video_real_time_rendering_disabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-real-time-rendering-disabled"), video);
	}
	
	public String video_skip_frames_enabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-skip-frames-enabled"), video);
	}
	
	public String video_skip_frames_disabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-skip-frames-disabled"), video);
	}
	
	public String video_skip_duplicated_frames_enabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-skip-duplicated-frames-enabled"), video);
	}
	
	public String video_skip_duplicated_frames_disabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-skip-duplicated-frames-disabled"), video);
	}
	
	public String video_show_informations_enabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-show-informations-enabled"), video);
	}
	
	public String video_show_informations_disabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-show-informations-disabled"), video);
	}
	
	public String video_show_fps_enabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-show-fps-enabled"), video);
	}
	
	public String video_show_fps_disabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-show-fps-disabled"), video);
	}
	
	public String video_run_on_startup_enabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-run-on-startup-enabled"), video);
	}
	
	public String video_run_on_startup_disabled(String video) {
		return getMessage(getConfigFile().getString("messages.video-run-on-startup-disabled"), video);
	}
	
	public String video_processing_frames_starting(String video) {
		return getMessage(getConfigFile().getString("messages.video-processing-frames-starting"), video);
	}
	
	public String video_processing_frames_finished(String video) {
		return getMessage(getConfigFile().getString("messages.video-processing-frames-finished"), video);
	}
	
	public String video_processing_estimated_time(String time) {
		return getMessage(getConfigFile().getString("messages.video-processing-estimated-time"), time);
	}
	
	public String video_processing_audio_starting(String video) {
		return getMessage(getConfigFile().getString("messages.video-processing-audio-starting"), video);
	}
	
	public String video_processing_audio_finished(String video) {
		return getMessage(getConfigFile().getString("messages.video-processing-audio-finished"), video);
	}
	
	public String video_processing_minecraft_starting(String video) {
		return getMessage(getConfigFile().getString("messages.video-processing-minecraft-starting"), video);
	}
	
	public String video_processing_minecraft_finished(String video) {
		return getMessage(getConfigFile().getString("messages.video-processing-minecraft-finished"), video);
	}
	
	public String video_processing_finished(String video, String time) {
		return getMessage(getConfigFile().getString("messages.video-processing-finished"), video, time);
	}
	
	public String video_instance_started(String video, String id) {
		return getMessage(getConfigFile().getString("messages.video-instance-started"), video, id);
	}
	
	public String video_instance_stopped(String video) {
		return getMessage(getConfigFile().getString("messages.video-instance-stopped"), video);
	}
	
	public String video_instance_not_on_screen() {
		return getMessage(getConfigFile().getString("messages.video-instance-not-on-screen"));
	}
	
	public String videos_empty_registered() {
		return getMessage(getConfigFile().getString("messages.videos-empty-registered"));
	}
	
	public String videos_canceled_tasks(String tasks) {
		return getMessage(getConfigFile().getString("messages.videos-canceled-tasks"), tasks);
	}
	
	public String videos_notice() {
		return getMessage(getConfigFile().getString("messages.videos-notice"));
	}
	
	public String screen_created(String dimension) {
		return getMessage(getConfigFile().getString("messages.screen-created"), dimension);
	}
	
	public String screen_removed(String index) {
		return getMessage(getConfigFile().getString("messages.screen-removed"), index);
	}
	
	public String screen_teleport(String index) {
		return getMessage(getConfigFile().getString("messages.screen-teleport"), index);
	}
	
	public String screen_invalid_index(String index) {
		return getMessage(getConfigFile().getString("messages.screen-invalid-index"), index);
	}
	
	public String screen_cannot_create() {
		return getMessage(getConfigFile().getString("messages.screen-cannot-create"));
	}
	
	public String image_rendered(String image) {
		return getMessage(getConfigFile().getString("messages.image-rendered"), image);
	}
	
	public String image_deleted(String image) {
		return getMessage(getConfigFile().getString("messages.image-deleted"), image);
	}
	
	public String image_placed(String image) {
		return getMessage(getConfigFile().getString("messages.image-placed"), image);
	}
	
	public String image_removed(String image) {
		return getMessage(getConfigFile().getString("messages.image-removed"), image);
	}
	
	public String image_received(String image) {
		return getMessage(getConfigFile().getString("messages.image-received"), image);
	}
	
	public String image_gived(String image, String player) {
		return getMessage(getConfigFile().getString("messages.image-gived"), image, player);
	}
	
	public String image_not_found() {
		return getMessage(getConfigFile().getString("messages.image-not-found"));
	}
	
	public String image_invalid() {
		return getMessage(getConfigFile().getString("messages.image-invalid"));
	}
	
	public String image_cannot_read() {
		return getMessage(getConfigFile().getString("messages.image-cannot-read"));
	}
	
	public String image_invalid_screen() {
		return getMessage(getConfigFile().getString("messages.image-invalid-screen"));
	}
	
	public String image_already_rendered(String image) {
		return getMessage(getConfigFile().getString("messages.image-already-rendered"), image);
	}
	
	public String item_previous_page_name() {
		return getMessage(getConfigFile().getString("messages.item.previous-page.name"));
	}
	
	public String item_previous_page_lore() {
		return getMessage(getConfigFile().getString("messages.item.previous-page.lore"));
	}
	
	public String item_refresh_page_name() {
		return getMessage(getConfigFile().getString("messages.item.refresh-page.name"));
	}
	
	public String item_refresh_page_lore() {
		return getMessage(getConfigFile().getString("messages.item.refresh-page.lore"));
	}
	
	public String item_next_page_name() {
		return getMessage(getConfigFile().getString("messages.item.next-page.name"));
	}
	
	public String item_next_page_lore() {
		return getMessage(getConfigFile().getString("messages.item.next-page.lore"));
	}
	
	public String item_play_name() {
		return getMessage(getConfigFile().getString("messages.item.play.name"));
	}
	
	public String item_play_lore() {
		return getMessage(getConfigFile().getString("messages.item.play.lore"));
	}
	
	public String item_load_name() {
		return getMessage(getConfigFile().getString("messages.item.load.name"));
	}
	
	public String item_load_lore() {
		return getMessage(getConfigFile().getString("messages.item.load.lore"));
	}
	
	public String item_delete_name() {
		return getMessage(getConfigFile().getString("messages.item.delete.name"));
	}
	
	public String item_delete_lore_1() {
		return getMessage(getConfigFile().getString("messages.item.delete.lore-1"));
	}
	
	public String item_delete_lore_2() {
		return getMessage(getConfigFile().getString("messages.item.delete.lore-2"));
	}
	
	public String item_poster_lore_1() {
		return getMessage(getConfigFile().getString("messages.item.poster.lore-1"));
	}
	
	public String item_poster_lore_2() {
		return getMessage(getConfigFile().getString("messages.item.poster.lore-2"));
	}
	
	public String item_poster_lore_3() {
		return getMessage(getConfigFile().getString("messages.item.poster.lore-3"));
	}
	
	public String item_poster_lore_4() {
		return getMessage(getConfigFile().getString("messages.item.poster.lore-4"));
	}
	
	public String age_limit_warning() {
		return getMessage(getConfigFile().getString("messages.age-limit-warning"));
	}
	
	public String incompatible() {
		return getMessage(getConfigFile().getString("messages.incompatible"));
	}
	
	public String no_page_left() {
		return getMessage(getConfigFile().getString("messages.no-page-left"));
	}
	
	public String too_much_loading() {
		return getMessage(getConfigFile().getString("messages.too-much-loading"));
	}
	
	public String too_much_playing() {
		return getMessage(getConfigFile().getString("messages.too-much-playing"));
	}
	
	public String no_screen_playing() {
		return getMessage(getConfigFile().getString("messages.no-screen-playing"));
	}
	
	public String videos_reload_requested() {
		return getMessage(getConfigFile().getString("messages.videos-reload-requested"));
	}
	
	public String videos_reloaded() {
		return getMessage(getConfigFile().getString("messages.videos-reloaded"));
	}
	
	public String video_invalid(String video) {
		return getMessage(getConfigFile().getString("messages.video-invalid"), video);
	}
	
	public String video_invalid_index(String index) {
		return getMessage(getConfigFile().getString("messages.video-invalid-index"), index);
	}
	
	public String not_number() {
		return getMessage(getConfigFile().getString("messages.not-number"));
	}
	
	public String available_images() {
		return getMessage(getConfigFile().getString("messages.available-images"));
	}
	
	public String available_videos() {
		return getMessage(getConfigFile().getString("messages.available-videos"));
	}
	
	public String loaded() {
		return getMessage(getConfigFile().getString("messages.loaded"));
	}
	
	public String not_loaded() {
		return getMessage(getConfigFile().getString("messages.not-loaded"));
	}
	
	public String negative_number() {
		return getMessage(getConfigFile().getString("messages.negative-number"));
	}
	
	public String offline_player(String player) {
		return getMessage(getConfigFile().getString("messages.offline-player"), player);
	}
	
	public String invalid_url(String url) {
		return getMessage(getConfigFile().getString("messages.invalid-url"), url);
	}
	
	public String invalid_sender() {
		return getMessage(getConfigFile().getString("messages.invalid-sender"));
	}
	
	public String insufficient_permissions() {
		return getMessage(getConfigFile().getString("messages.insufficient-permissions"));
	}
}
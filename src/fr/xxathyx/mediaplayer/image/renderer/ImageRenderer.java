package fr.xxathyx.mediaplayer.image.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.tasks.TaskAsyncLoadVideo;

/** 
* The ImageRenderer class extends {@link MapRenderer}, it isn't only used in the image part,
* its a key in video video loading {@link TaskAsyncLoadVideo} and real time rendering,
* it bridge the gap between images and Minecraft. It contains a lot of usefull methods to
* render images into Minecraft.
* 
* <p>The class can be described has an utility one, it have only one constructor.
*
* @author  Xxathyx
* @version 1.0.0
* @since   2021-08-23 
*/

public class ImageRenderer extends MapRenderer {
	
	private final Main plugin = Main.getPlugin(Main.class);
	private final ArrayList<Object> ids = new ArrayList<>();
	
	private boolean shouldRender;
	
	private BufferedImage bufferedImage;
    private BufferedImage[] cutImages = {};
    
    public int lines;
    public int columns;
    
    private int cutImagesCount;
    private int remainderX;
    private int remainderY;
	
	/**
	* Constructor for ImageRenderer class, creates an ImageRenderer variable according
	* to a {@link BufferedImage}, after passing the original image to be rendered you
	* will still need to {@link #calculateDimensions()} and {@link #splitImages()} before
	* calling a {@link #render(MapView, MapCanvas, Player)} in order to have correct results.
	* 
	* @param bufferedImage The original image to be rendered in Minecraft.
	*/
    
	public ImageRenderer(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
		this.shouldRender = true;
	}
	
	/**
	* Creates Minecraft maps based on the image passed earlier in the constructor, here
	* no need to call {@link #calculateDimensions()} or {@link #splitImages()} it is
	* directly inclued in the method itself.
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
	* @param world The World where the image will be rendered in.
	*/
	
	public void createMaps(World world) {
		
		calculateDimensions();
		splitImages();
		
		MapView map;
		
    	for(BufferedImage bufferedImage : cutImages) {
    		
			map = Bukkit.getServer().createMap(world);
			map = resetRenderers(map);
			
			map.setScale(MapView.Scale.FARTHEST);
			if(!plugin.isLegacy()) map.setUnlimitedTracking(false);
			map.addRenderer(new ImageRenderer(bufferedImage));
			
			ids.add(plugin.getMapUtil().getMapId(map));
    	}
	}
	
	/**
	* See Bukkit documentation : {@link MapRenderer#render(MapView, MapCanvas, Player)}
	* 
	* <p>Render an image into Minecraft using {@link MapCanvas}.
	*/
	
	@Override
	public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
		if(shouldRender) {
			mapCanvas.drawImage(0, 0, bufferedImage);
			shouldRender = false;
		}
	}
	
	/**
	* Resets previous applied render of a {@link MapView}, see {@link MapView#getRenderers()}.
	* 
	* @param mapView The MapView where the renderers will be cleared.
	*/
	
	public MapView resetRenderers(MapView mapView) {
		
		Iterator<MapRenderer> iterator = mapView.getRenderers().iterator();
		
		while(iterator.hasNext()) {
			mapView.removeRenderer(iterator.next());
		}
		return mapView;
	}
	
	/**
	* Calculates the number of columns and lines required to display the image in Minecraft
	* according to the original {@link BufferedImage} and a fourth level scaled map who does
	* 128x128, this method must be called before calling other methods.
	* 
	* <p>Sometimes the original image can't be cut into a natural number of square, so we need
	* one more frame for the remaining pixels.
	*/
	
    public void calculateDimensions() {

        int originalWidth = bufferedImage.getWidth();
        int originalHeight = bufferedImage.getHeight();

        columns = ((int) Math.ceil(originalWidth / 128));
        lines = ((int) Math.ceil(originalHeight / 128));

        remainderX = (originalWidth % 128);
        remainderY = (originalHeight % 128);
        
        if(remainderX > 0) {
            columns += 1;
        }
        if(remainderY > 0) {
            lines += 1;
        }
        cutImagesCount = (columns*lines);
    }
	
	/**
	* Splits the original image according to the number of columns and lines calculated before with
	* {@link #calculateDimensions()}, after being called, the {@link #getBufferedImage()} can be called,
	* in order to get the splitted images.
	* 
	* <p> <strong>Note: </strong> The {@link #calculateDimensions()} must be called before calling this one.
	*/
    
    public void splitImages() {

        cutImages = new BufferedImage[cutImagesCount];

        int imageY = remainderY == 0 ? 0 : (remainderY - 128) / 2;
        
        for(int i = 0; i < lines; i++) {
            int imageX = remainderX == 0 ? 0 : (remainderX - 128) / 2;
            for(int j = 0; j < columns; j++) {
                cutImages[(i * columns + j)] = makeSubImage(imageX, imageY);
                imageX += 128;
            }
            imageY += 128;
        }
        bufferedImage = null;
    }

	/**
	* Creates an new squared image, based on a 128x128 partition of the orginal image. This method is called
	* multiple times from the {@link #splitImages()} method to make small pieces of the original image.
	* 
	* @param x Starting square x coordinates in the original image.
	* @param y Starting square y coordinates in the original image.
	* @return The new squared subimage of the orginal image.
	*/
    
    public BufferedImage makeSubImage(int x, int y) {
    	
        BufferedImage newImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = newImage.getGraphics();
        
        graphics.drawImage(bufferedImage, -x, -y, null);
        graphics.dispose();
        
        return newImage;
    }
    
	/**
	* Gets the original image passed earlier in the constructor.
	* 
	* @return The original image.
	*/
    
	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}
	
	/**
	* Gets all the splitted images of the original one.
	* 
	* <p> <strong>Note: </strong> This method can only be called after {@link #splitImages()},
	* otherwise it will return an empty array.
	* 
	* @return The splitted images.
	*/
	
	public BufferedImage[] getBufferedImages() {
		return cutImages;
	}
	
	/**
	* Gets an {@link ArrayList} of {@link Integer} corresponding to the ids wich were used to render
	* the image in Minecraft.
	* 
	* <p> <strong>Note: </strong> This method can only be called after {@link #createMaps(World)},
	* otherwise it will return an empty ArrayList.
	* 
	* @return An ArrayList of the ids that were used to render the image.
	*/
	
	public ArrayList<Object> getIds() {
		return ids;
	}
}
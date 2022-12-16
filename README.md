# MediaPlayer

[![icon.png](https://i.postimg.cc/gj8Pj7mb/icon.png)](https://postimg.cc/tnqcm2sB)

Allows you to play and use various medias such as videos on Minecraft.

Videos : https://youtu.be/LYVOkX7uQ5M

Livestreams : https://youtu.be/swcMQTto5rI


Download compiled plugin : https://www.dropbox.com/s/xchs5b54d71k0oh/MediaPlayer.jar?dl=1


### How to use MediaPlayer API

First of all, you need to add MediaPlayer.jar plugin as a library in your Java project, and add it as a plugin
into you server plugin folder.

### Brows into plugin informations with MediaPlayerAPI class :

First step : ```bash import fr.xxathyx.mediaplayer.api.MediaPlayerAPI;```

Then call ```bash getPlugin``` method in order to gain access to live running plugin informations.

### A player interacts with a screen, use the PlayerInteractScreenEvent to detect it :

You will need to : ```bash import fr.xxathyx.mediaplayer.api.listeners.PlayerInteractScreenEvent```

Then you can access informations such as the screen that as been touched with ```bash getScreen```,
the player itself : ```bash getPlayer``` and the click location with ```bash getCursorX```, ```bash getCursorY```.

Notice that those integers represent the real location of the click according to the size of the content displayed insed it.

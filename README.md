# MediaPlayer

<p align="center"> <img width="160" height="160" src="https://i.postimg.cc/gj8Pj7mb/icon.png"> </p>

Currently in development, plugin that allows you to play and use various medias such as images, videos (with audio, yes), and livestreams on Minecraft.

<p align="center"> <img width="640" height="360" src="https://i.ibb.co/nDBXRgg/th.jpg"> </p>

Videos : https://youtu.be/2uF8IeQ0Dto

Livestreams : https://youtu.be/swcMQTto5rI


Download compiled plugin (working on 1.7-1.20) : https://www.dropbox.com/s/xchs5b54d71k0oh/MediaPlayer.jar?dl=1


### How to use MediaPlayer API

First of all, you need to add MediaPlayer.jar plugin as a library in your Java project, and add it as a plugin
into you server plugin folder.

### Brows into plugin informations with MediaPlayerAPI class :

First step : ```import fr.xxathyx.mediaplayer.api.MediaPlayerAPI;```

Then call ```getPlugin``` method in order to gain access to live running plugin informations.

### A player interacts with a screen, use the PlayerInteractScreenEvent to detect it :

You will need to : ```import fr.xxathyx.mediaplayer.api.listeners.PlayerInteractScreenEvent```

Then you can access informations such as the screen that as been touched with ```getScreen```,
the player itself : ```getPlayer``` and the click location with ```getCursorX```, ```getCursorY```.

Notice that those integers represent the real location of the click according to the size of the content displayed insed it.


### How to setup your own audio handling server :

In order to setup you own audio handling server you will need to have a web-server that supports HTTP protocol and Java installed on it,
then download and place https://github.com/Xxathyx/MediaPlayer-server jar archive, somewhere in your server, most likely in a folder
in which the program could write and read informations, so make sure to put in a folder only with write-execute permissions.

Then make it run everytime on web-server startup by creating a service file such as

```
[Unit]
Description=Runs the server.jar

[Service]
ExecStart=/usr/bin/java -jar /var/www/html/server.jar

[Install]
WantedBy=multi-user.target
```

You can now eventually restart you web-server and then specify your web-server adress in the plugin configuration file field : ```own-audio-server-handling-address```
and the port in ```own-audio-server-handling-port```, and restart your server.

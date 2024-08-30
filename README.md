# MediaPlayer

<h1 align="center">
  <img src="https://i.postimg.cc/gj8Pj7mb/icon.png" alt="MediaPlayer">
</h1>

Allows you to play and use various medias such as videos with audio on Minecraft (1.7-1.21).
For better performance, set network-compression-threshold to -1 in server.properties

Videos : https://youtu.be/LYVOkX7uQ5M

Livestreams : https://youtu.be/swcMQTto5rI


Download compiled plugin : https://www.dropbox.com/s/xchs5b54d71k0oh/MediaPlayer.jar?dl=1

### How to use Mediaplyer

1. Make sure the video format is compatible, here are the compatible formats : mp4, mov, m4v, avi, webm, mkv, gif, m3u8, ts, wmv
2. Drag your video into the videos folder, in the plugin folder.
3. Type the command /videos reload.
4. Then type /video load video-name-without-extension (the video should not include space, otherwise you have to get the video index) this usually take time
5. Type /video play video-name-without-extension
6. Then click on the chat-message that says create a screen.
7. Right click on the top right corner of the screen, the screen display should change into the video thumbnail.
8. Then type /video start
9. To stop it : /video stop

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

Then make it run everytime on web-server startup by creating a service file (mediaplater.service) such as

```
[Unit]
Description=Runs the server.jar

[Service]
ExecStart=/usr/bin/java -jar /var/www/html/server.jar

[Install]
WantedBy=multi-user.target
```
Notice that server.jar is located in ```/var/www/html/``` and mediaplater.service file is located in ```/etc/systemd/system/```. You can now
register mediaplayer service by executing the following instructions : ```sudo systemctl daemon-reload``` and ```sudo systemctl start mediaplayer.service```,
then enable it on boot : ``` sudo systemctl enable mediaplayer```.

You can now eventually restart you web-server (then to confirm that mediaplayer is running : ```sudo systemctl status mediaplayer```), specify your web-server
adress in the plugin configuration file field : ```own-audio-server-handling-address``` and the port in ```own-audio-server-handling-port```, and restart your server.

### Notice that if you already have a token, you would have to delete the plugin folder in order to generate a new one for the newly created audio-server.

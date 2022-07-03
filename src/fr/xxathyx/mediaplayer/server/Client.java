package fr.xxathyx.mediaplayer.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import fr.xxathyx.mediaplayer.configuration.Configuration;

public class Client {

	private final Configuration configuration = new Configuration();
	
    private Socket socket;
    
    public void connect() {
		try {
			Socket socket = new Socket(configuration.plugin_free_audio_server_address(), 8888);
			this.socket = socket;
		}catch (IOException e) {
			e.printStackTrace();
			//Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + configuration.impossible_connection());
		}
    }
    
    public void disconnect() {
    	try {
			socket.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void write(String header, String content) {
		try {
			
			refresh();
			
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataOutputStream.writeUTF(header + content);	
		}catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void write(byte[] buffer) {    	
		try {
			
			refresh();
			
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataOutputStream.write(buffer);	
		}catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void refresh() {
    	if(!socket.isConnected() || socket.isClosed())
			try {
				socket.connect(new InetSocketAddress(configuration.plugin_free_audio_server_address(), 8888));
			}catch (IOException e) {
				e.printStackTrace();
				//Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "[MediaPlayer]: " + configuration.impossible_connection());
			}
    }
    
    public Socket getSocket() {
    	return socket;
    }
    
    public String getResponse() {
		try {
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			return dataInputStream.readUTF();

		}catch (IOException e) {
			e.printStackTrace();
		}
		return "null";
    }
}
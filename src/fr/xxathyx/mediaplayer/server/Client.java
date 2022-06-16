package fr.xxathyx.mediaplayer.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    private Socket socket;
    
    public void connect() {
		try {
			Socket socket = new Socket("37.187.196.226", 8888);
			this.socket = socket;
		}catch (IOException e) {
			e.printStackTrace();
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
    	if(!socket.isConnected() || socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown())
			try {
				socket = new Socket("37.187.196.226", 8888);
			}catch (IOException e) {
				e.printStackTrace();
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
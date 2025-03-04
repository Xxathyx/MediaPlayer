package fr.xxathyx.mediaplayer.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import fr.xxathyx.mediaplayer.Main;
import fr.xxathyx.mediaplayer.configuration.Configuration;

public class Server {
		
	private final Main plugin = Main.getPlugin(Main.class);
	private final Configuration configuration = new Configuration();
	
	public static File file;
	
    private String ip = "localhost";
    private int port = getRandomNumber(1000, 8000);
    	
    public Server(File file) {
    	Server.file=file;
    }
    
    public boolean start() {
    	
    	ip = plugin.getServer().getIp(); if (ip == null || ip.equals("")) ip = "localhost";
    	if(!configuration.plugin_alternative_server().equals("http://54.38.185.225/")
    			&& !configuration.plugin_alternative_server().equals("none")) ip = configuration.plugin_alternative_server();
    	
        try {
        	HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new FileHandler());
            server.setExecutor(null);
            server.start();
                        
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    static class FileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
        	
            if(!file.exists()) {
                String response = "404 (File Not Found)\n";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            byte[] fileBytes = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(fileBytes, 0, fileBytes.length);
            
            exchange.sendResponseHeaders(200, file.length());
            OutputStream os = exchange.getResponseBody();
            os.write(fileBytes, 0, fileBytes.length);
            os.close(); bis.close();
        }
    }
    
    public String url() {
    	return "http://"+ip+":"+port;
    }
    
	public static int getRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
	}
}
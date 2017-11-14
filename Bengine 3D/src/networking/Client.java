package networking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.lwjgl.util.vector.Vector3f;

import entities.Bullet;
import entities.Player;
import toolBox.Assets;
import world.World;

public class Client implements Runnable{

    BufferedReader in;
    PrintWriter out;
    Socket socket;
    World world;
    
    boolean active = true;
    boolean multiplayer = false;
    
    public String name;
    
    public Client(boolean multiplayer) throws IOException{
    	this.multiplayer = multiplayer;
    	if(multiplayer){
    		setup();
    	}else{
    		in = null;
    		out = null;
    		name = "P0";
    	}
    }
    	
    public void setup() throws IOException{
    	// Make connection and initialize streams
        //String serverAddress = "10.0.1.10";
    	String serverAddress = "localhost";
        socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        //setup connection with the server
        String line = "";
        while(!line.startsWith("CONNECTED")){
			try {
				line = in.readLine();
				if (line.startsWith("SETID")) {
					name = "P" + (int) (Math.random() * 99);
		            out.println(name);
		        }
			}catch(IOException e){
				e.printStackTrace();
			}
        }

        System.out.println("client setup done");
        
        while(!line.startsWith("START")){
        	line = in.readLine();
        }
    }
    
    public void start(World world){
    	if(!multiplayer) return;
    	
    	//connect to the world
    	this.world = world;
    	
    	//start up the reader
        new Thread(this).start();
    }
    
    public void updatePosition(String key, Vector3f position){
		sendData("p," + key + "," + position.x + "," + position.y + "," + position.z);
    }
    
    public void updateRotation(String key, Vector3f rotation){
		sendData("r," + key + "," + rotation.x + "," + rotation.y + "," + rotation.z);
    }
    
    public void updateVelocity(String key, Vector3f velocity){
		sendData("v," + key + "," + velocity.x + "," + velocity.y + "," + velocity.z);
    }
    
    public void addPlayer(String key, Vector3f position){
		sendData("cp," + key + "," + position.x + "," + position.y + "," + position.z);
    }
    
    public void addBullet(String key, Vector3f position){
    	sendData("cb," + key + "," + position.x + "," + position.y + "," + position.z);
    }
    
    public void updateHealth(String key, float health){
    	sendData("h," + key + "," + health);
    }
    
    public void deleteEntity(String key){
    	sendData("d," + key);
    }
    
    public void sendData(String data){
    	if(!multiplayer) return;
    	out.println(data);
    }

	public void run() {
		System.out.println("Started a thread");
		while(active){
			//receive input
			try {
				String[] input = in.readLine().split(",");
				if(input[0].equalsIgnoreCase("cp")){
					world.addDynEntity(input[1], new Player(new Vector3f(
							Float.parseFloat(input[2]),
							Float.parseFloat(input[3]),
							Float.parseFloat(input[4]))));
				}else if(input[0].equalsIgnoreCase("cb")){
					world.addDynEntity(input[1], new Bullet(new Vector3f(
							Float.parseFloat(input[2]),
							Float.parseFloat(input[3]),
							Float.parseFloat(input[4])), 0, 0));
				}else if(input[0].equalsIgnoreCase("p")){
					String key = input[1];
					world.dynEntities.get(key).position.x = Float.parseFloat(input[2]);
					world.dynEntities.get(key).position.y = Float.parseFloat(input[3]);
					world.dynEntities.get(key).position.z = Float.parseFloat(input[4]);
				}else if(input[0].equalsIgnoreCase("r")){
					String key = input[1];
					world.dynEntities.get(key).rotation.x = Float.parseFloat(input[2]);
					world.dynEntities.get(key).rotation.y = Float.parseFloat(input[3]);
					world.dynEntities.get(key).rotation.z = Float.parseFloat(input[4]);
				}else if(input[0].equalsIgnoreCase("v")){
					String key = input[1];
					world.dynEntities.get(key).velocity.x = Float.parseFloat(input[2]);
					world.dynEntities.get(key).velocity.y = Float.parseFloat(input[3]);
					world.dynEntities.get(key).velocity.z = Float.parseFloat(input[4]);
				}else if(input[0].equalsIgnoreCase("h")){
					world.dynEntities.get(input[1]).health = Float.parseFloat(input[2]);
				}else if(input[0].equalsIgnoreCase("d")){
					world.deleteDynEntity(input[1]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

package networking;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server implements Runnable{
	
	public static boolean waiting = true;

    private static final int PORT = 9001;
    private ServerSocket listener;

    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
    
    static JFrame frame = new JFrame("Server");
    static JTextField textField = new JTextField(80);
    static JButton startButton = new JButton("Start Game");
    static JTextArea messageArea = new JTextArea(30, 40);

    //creates the server
    public static void main(String[] args) throws Exception {
    	Server server = new Server();
    }
    
    public Server() throws IOException{
    	// Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(startButton, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        startButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		System.out.println("Starting Game");
        		waiting = false;
        		}
        });
        
    	//create the server
        System.out.println("Started new server on port: " + PORT + "on server " + InetAddress.getLocalHost());
        listener = new ServerSocket(PORT);
        frame.setTitle("Server: " + InetAddress.getLocalHost());
        Thread addNewClients = new Thread(this);
        addNewClients.start();
        while(waiting){
        	System.out.print("");
        }
        //start all players
        for(PrintWriter out: writers){
        	out.println("START");
        }
        while(true);
    }
    
    public void run(){
    	try {
            while (waiting) {
            	//create a new handler for each new client
                new Handler(listener.accept()).start();
                System.out.println("New handler created for client");
            }
        } catch (IOException e) {
			e.printStackTrace();
		} finally {
            try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    //thread for handling one client
    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        //create handler with socket from the new client
        public Handler(Socket socket) {
            this.socket = socket;
        }
        
        public void updateDisplay(){
        	messageArea.setText("");
        	for(String name: names){
        		messageArea.append(name + "\n");
        	}
        }
        
        //start the loop for the client
        public void run() {
        	//check to make sure new clients are being accepted
        	if(waiting){
	            try {
	
	                // Create character streams for the socket.
	                in = new BufferedReader(new InputStreamReader(
	                    socket.getInputStream()));
	                out = new PrintWriter(socket.getOutputStream(), true);
	                
	                //repeat until the client creates an unused name
	                while (true) {
	                    out.println("SETID");
	                    name = in.readLine();
	                    if (name == null) {
	                        return;
	                    }
	                    synchronized (names) {
	                        if (!names.contains(name)) {
	                            names.add(name);
	                            updateDisplay();
	                            break;
	                        }
	                    }
	                }
	                
	                //alert the client of the connection and add to list of writers
	                System.out.println("Connected Player: " + name);
	                out.println("CONNECTED");
	                writers.add(out);
	
	                //broadcast messages from the client
	                while (true) {
	                    String input = in.readLine();
	                    if (input == null) {
	                        return;
	                    }
	                    for (PrintWriter writer : writers) {
	                    	if(writer == out) continue;
	                        writer.println(input);
	                    }
	                }
	            } catch (IOException e) {
	                System.out.println(e);
	            } finally {
	            	//if the client connection has failed, remove the client close
	            	System.out.println("Removing player: " + name);
	                if (name != null) {
	                    names.remove(name);
	                    updateDisplay();
	                }
	                if (out != null) {
	                    writers.remove(out);
	                }
	                try {
	                    socket.close();
	                } catch (IOException e) {
	                }
	            }
	        }
	    }
    }
}
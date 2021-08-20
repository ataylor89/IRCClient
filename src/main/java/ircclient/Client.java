package ircclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrewtaylor
 */
public class Client {
    
    private Socket socket;
    private String hostname;
    int port;
    private BufferedReader networkIn;
    private PrintWriter networkOut;
    private CommandLineUI commandLineUI;
    private InstantMessengerGUI gui;
    private String username;
        
    public Client() {}
    
    public void connect(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        connect();
    }
    
    public void connect() {
        try {
            socket = new Socket(hostname, port);
            networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            networkOut = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        Thread listener = new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    String line = networkIn.readLine();
                    if (line != null) {
                        System.out.println(line);
                        gui.display(line + "\n");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        listener.start();
        
        Thread ping = new Thread(() -> {
           while (socket.isConnected()) {
               try {
                   networkOut.println("PING " + hostname);
                   Thread.sleep(60000);
               } catch (InterruptedException ex) {
                   Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
        });
        ping.start();
    }
    
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void exit() {
        disconnect();
        System.exit(0);       
    }
   
    public void startKeyboardAdapter() {
        commandLineUI = new CommandLineUI(this);
        commandLineUI.start();
    }
    
    public void openGUI() {
        gui = new InstantMessengerGUI(this);
        gui.setVisible(true);
    }
       
    public void send(String message) {
        if (socket.isConnected())
            networkOut.println(message);
        
        if (message.startsWith("USER")) {
            this.username = message.split(" ")[1];
        }
        
        if (message.startsWith("NICK")) {
            this.username = message.split(" ")[1];
        }
        
        if (message.startsWith("/register")) {
            this.username = message.split(" ")[1];
        }
    }
    
    public String getUserName() {
        return username;
    }
    
    public static void main(String[] args) {
        Client client = new Client();
        client.startKeyboardAdapter();
        client.openGUI();    
    }
}

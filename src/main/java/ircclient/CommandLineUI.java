package ircclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrewtaylor
 */
public class CommandLineUI extends Thread {
    private Client client;
    
    public CommandLineUI(Client client) {
        this.client = client;
    }
    
    @Override
    public void run() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String line = in.readLine();
                
                if (line != null) {
                    client.send(line);
                }
            } catch (IOException ex) {
                Logger.getLogger(CommandLineUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

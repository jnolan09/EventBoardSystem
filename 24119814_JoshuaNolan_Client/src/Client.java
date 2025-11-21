
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author joshu
 */

/**
 * TCP Client - connects to Event Board server
 * Handles user input and displays server responses
 */

public class Client {
    private static InetAddress host;
    private static final int PORT = 8080;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("Host ID not found");
            System.exit(1);
        }
        
        run();
    }
    
    private static void run() {
        Socket link = null;
        try {
            //Create Socket and connect to server
            link = new Socket(host, PORT);
            System.out.println("Connected to server at " + host + ":" + PORT + "\n");
            
            //Setup input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);
            
            //Setup keyboard input
            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));
            
            String message;
            String response;
            
            
            // HTTP Import on startup
            System.out.println("Checking for import URL");
            System.out.println("Enter import URL (or press ENTER to skip): ");
            String importURL = userEntry.readLine();
            if (importURL == null) importURL = "";
            importURL = importURL.trim();
            
            if (!importURL.isEmpty()) {
                importEvents(importURL, out, in);
            }
            
            //Communication loop
            while (true) {
                System.out.println("Enter message (or STOP to quit): ");
                message = userEntry.readLine();
                
                //Send message to server
                out.println(message);
                
                //Recieve response from server
                response = in.readLine();
                System.out.println("SERVER> " + response);
                
                //Check if we should stop
                if (message.trim().equalsIgnoreCase("STOP")) {
                    if (response != null && response.equals("TERMINATE")) {
                        System.out.println("\nConnection terminated by server.");
                    }
                    break;
                }
                
                System.out.println("");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            //Close connection
            try {
                if (link != null) {
                    System.out.println("\nClosing connection... ");
                    link.close();
                }
            } catch (IOException e) {
                System.out.println("Unable to disconnect");
                System.exit(1);
            }
        }
    }
    
    // Method to import events from URL
    private static void importEvents(String urlString, PrintWriter out, BufferedReader in) {
        System.out.println("Importing events from: " + urlString);
        
        int imported = 0;
        int skipped = 0;
        
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            BufferedReader urlReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            String line;
            while ((line = urlReader.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }
                
                // Validate format: date; time; description
                String[] parts = line.split(";");
                if (parts.length != 3) {
                    System.out.println("Skipped (invalid format): " + line);
                    skipped++;
                    continue;
                }
                
                // Add command
                String date = parts[0].trim();
                String time = parts[1].trim();
                String desc = parts[2].trim();
                
                String command = "add; " + date + "; " + time + "; " + desc;
                
                // Send to server
                out.println(command);
                String response = in.readLine();
                
                System.out.println("Imported: " + line);
                System.out.println("Server: " + response);
                imported++;
            }
            
            urlReader.close();
            System.out.println("\nImport complete. Imported: " + imported + "; Skipped: " + skipped);
        } catch (Exception e) {
            System.out.println("Error importing from URL: " + e.getMessage());
        }
    }
}

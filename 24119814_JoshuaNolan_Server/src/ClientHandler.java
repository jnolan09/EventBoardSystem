
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author joshu
 */
public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private String clientID;
    private static ArrayList<Event> events; // Event list
    
    public ClientHandler(Socket socket, String id, ArrayList<Event> eventList) {
        this.clientSocket = socket;
        this.clientID = id;
        this.events = eventList;
    }
    
    @Override
    public void run() {
        try {
            // Setup input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            System.out.println(clientID + " connected from: " + clientSocket.getInetAddress());
            
            // Communication loop
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message from " + clientID + ": " + message);
                
                // Check for STOP command
                if (message.trim().equalsIgnoreCase("STOP")) {
                    System.out.println(clientID + " requested termination");
                    out.println("TERMINATE");
                    break;
                }
                
                // Parse the message 
                String[] parts = message.split(";");
                
                // Trim whitespace
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                }
                
                // Extract action
                String action = parts[0].toLowerCase();
                String response;
                
                // Handle different actions
                if (action.equals("add") ) {
                    String date = parts[1];
                    String time = parts[2];
                    String desc = parts[3];
                    
                    Event newEvent = new Event(date, time, desc);
                    events.add(newEvent);
                    response = "Event added";
                    
                } else if (action.equals("remove")) {
                    String date = parts[1];
                    String time = parts[2];
                    String desc = parts[3];
                    
                    Event toRemove = new Event(date, time, desc);
                    events.remove(toRemove);
                    response = "Event removed";
                    
                } else if (action.equals("list")) {
                    response = "List events";
                } else {
                    response = "Error";
                }
                
                out.println(response);
                System.out.println("Sent to " + clientID + ": " + response);
            }
        
        } catch (IOException e) {
            System.out.println(clientID + " Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                System.out.println(clientID + " disconnecting...");
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing connection for " + clientID);
            }
        }
    }
}

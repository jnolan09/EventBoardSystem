package server;

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
                
                String response;
                
                try {
                    // Validate number of fields (must be 4)
                    if (parts.length != 4) {
                    throw new InvalidCommandException("Invalid format: expected 4 fields (action; date; time; description)");
                }
                    
                // Extract and validate action
                String action = parts[0].toLowerCase();
                
                if(!action.equals("add") && !action.equals("remove") && !action.equals("list")) {
                    throw new InvalidCommandException("Unknown action '" + action + "': must be add, remove or list");
                }
                
                // Handle different actions
                if (action.equals("add") ) {
                    String date = parts[1];
                    String time = parts[2];
                    String desc = parts[3];
                    
                    Event newEvent = new Event(date, time, desc);
                    synchronized(events) {
                        events.add(newEvent);
                        response = getEventsForDate(date);
                    }
                    
                } else if (action.equals("remove")) {
                    String date = parts[1];
                    String time = parts[2];
                    String desc = parts[3];
                    
                    Event toRemove = new Event(date, time, desc);
                    synchronized(events) {
                        events.remove(toRemove);
                        response = getEventsForDate(date);
                    }
                    
                } else { // List
                    String date = parts[1];
                    synchronized(events) {
                        response = getEventsForDate(date);
                    }
                }
                
            } catch (InvalidCommandException e) {
                response = "InvalidCommandException: " + e.getMessage();
                    System.out.println("Validation error for " + clientID + ": " + e.getMessage());
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
    
    // Helper method to get all events for a specific date sorted by time
    private static String getEventsForDate(String date) {
        ArrayList<Event> dateEvents = new ArrayList<>();
        
        for (Event event : events) {
            if (event.getDate().equals(date)) {
                dateEvents.add(event);
            }
        }
        
        if (dateEvents.isEmpty()) {
            return date + "; No events scheduled";
        }
        
        // Sort events by time
        dateEvents.sort((e1, e2) -> {
            int time1 = timeToMinutes(e1.getTime());
            int time2 = timeToMinutes(e2.getTime());
            return Integer.compare(time1, time2);
        });
        
        // Build response string
        StringBuilder result = new StringBuilder(date);
        for (Event event : dateEvents) {
            result.append("; ").append(event.toString());
        }
        return result.toString();
    }
    
    // Helper method to convert time string to 24 hour format for sorting events
    private static int timeToMinutes(String time) {
        time = time.toLowerCase().trim();
        
        String[] parts = time.replace(" pm", "").replace( " am", "").split("\\.");
        int hour = Integer.parseInt(parts[0]);
        int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        
        // Convert to 24 hour format
        if (time.contains("pm") && hour != 12) {
            hour += 12;
        } else if (time.contains("am") && hour == 12) {
            hour = 0;
        }
        return hour * 60 + minutes;
    }
}

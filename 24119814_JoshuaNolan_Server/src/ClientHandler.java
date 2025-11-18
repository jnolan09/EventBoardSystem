
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
    private final Socket clientSocket;
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
            PrintWriter out = new PrintWriter(clientSocket.getOuputStream(), true);
            
            System.out.println(clientID + " connected from: " + clientSocket.getInetAddress());
        
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

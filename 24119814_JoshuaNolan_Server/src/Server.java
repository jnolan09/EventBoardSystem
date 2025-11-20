
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author joshu
 */
public class Server {
    private static final int PORT = 8080;
    //ArrayList to store all events
    private static ArrayList<Event> events = new ArrayList<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ServerSocket servSock = null;
        
        System.out.println("Opening Port " + PORT + "\n");
        
        try {
            //Create ServerSocket
            servSock = new ServerSocket(PORT);
            System.out.println("Server is running and waiting for client\n");
            
            int clientCount = 0;
            
            // Infinite loop to accept multiple clients
            while (true) {
                Socket clientSocket = servSock.accept();
                clientCount++;
                String clientID = "Client: " + clientCount;
                
                System.out.println(clientID + " connection recieved, Creating thread");
                
                // Create and start new thread for this client
                ClientHandler handler = new ClientHandler(clientSocket, clientID, events);
                Thread thread = new Thread(handler);
                thread.start();
                
                System.out.println(clientID + " thread started.");
            }
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (servSock != null) {
                    servSock.close();
                }
            } catch (IOException e) {
                System.out.println("Unable to disconnect");
            }
        }
        
    }
}


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
            
            //Accept Connection
            Socket link = servSock.accept();
            System.out.println("Client Connected: " + link.getInetAddress());
            
            //Setup input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);
            
            //Communication loop to read and respond to multiple messages
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message recieved from client: " + message);
                
                //Check for stop command
                if (message.trim().equalsIgnoreCase("STOP")) {
                    System.out.println("Client requested termination");
                    out.println("TERMINATE");
                    break;
                }
                
                //Parse the message: action, date, time and description
                String[] parts = message.split(";");
                
                //Trim whitespace from each part
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                }
                
                //Extract action
                String action = parts[0].toLowerCase();
                String response;
                
                //Handle different actions
                if (action.equals("add")) {
                    response = "ADD action recieved";
                } else if (action.equals("remove")) {
                    response = "REMOVE action recieved";
                } else if (action.equals("list")) {
                    response = "LIST action recieved";
                } else {
                    response = "Error: Unknown action";
                }
                out.println(response);
                System.out.println("Sent to client: " + response);
            
            }
            
            //Close connection
            System.out.println("\n Closing connection... ");
            link.close();
            servSock.close();
            
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

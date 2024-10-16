import java.io.*;
import java.util.*;
import java.net.*;

public class player {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private int port_number;
    private InetAddress address;
    private ServerSocket player1_socket;
    private Socket player2_socket1;
    private Scanner scanner = new Scanner(System.in);
    //
    public static String start(String[] args){
        String broadcast_address = args[0];
        String broadcast_port = args[1];
        return broadcast_address + ":" + broadcast_port;
    }
    public void connection(String connection_details){
        String[] connectionData = connection_details.split(":");
        try {
            address = InetAddress.getByName(connectionData[0]); // Get the IP address of the server
            port_number = Integer.parseInt(connectionData[1]); // Get the port number of the server
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        try{
            socket = new DatagramSocket(port_number);
            socket.setSoTimeout(30000); // Wait for 30 seconds before timing out
            System.out.println("Server is running on port " + port_number);
            running = true;
            while(running){
                DatagramPacket packet = new DatagramPacket(buf, buf.length); // Create a packet to receive data
                try{
                    socket.receive(packet); // Receive data from the player
                    String received = new String(packet.getData(), 0, packet.getLength()); // Convert the data to a string
                    String[] receivedData = received.split(":");
                    System.out.println("Received from client: " + received);
                    if(received.startsWith("NEW GAME:")){
                        int client_port = Integer.parseInt(receivedData[1]); // Get the port number of the player
                        address = packet.getAddress(); // Get the IP address of the player
                        playAsPlayer2(client_port, address); // Play as player 2
                    }
                    else{
                        continue;
                    }
                } catch (SocketTimeoutException e) {
                    playAsPlayer1(); // Play as player 1 if no player is found
                }   
            }
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public int getPort(){
        Random random = new Random();
        int port = random.nextInt(9000,9101); // Generate a random port number between 9000 and 9100
        return port;
    }

    //checks if the message is a new game message
    public int newGameMessage(String message){
        int port = 0;
        if(message.startsWith("NEW GAME:")){
            String[] messageData = message.split(":");
            port = Integer.parseInt(messageData[1]); // Get the port number from the message
        }
        return port;
    }
    public void playAsPlayer2(int port, InetAddress address){
        try{
            Socket player2_socket = new Socket(address, port); // Create a new socket to connect to the player
            PrintWriter out = new PrintWriter(player2_socket.getOutputStream(), true); // Create a new output stream
            BufferedReader in = new BufferedReader(new InputStreamReader(player2_socket.getInputStream())); // Create a new input stream
            player2_socket.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    public void playAsPlayer1(){
        int port = getPort(); // Get a random port number
        try{
            DatagramSocket socket = new DatagramSocket(); // Create a new socket
            String message = "NEW GAME:" + port; // Create a new game message
            byte[] buf = message.getBytes(); // Convert the message to a byte array
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port_number); // Create a new packet to send the message
            socket.send(packet); // Send the message to the server
            // Wait for player 2 to connect to the player 1
            player1_socket = new ServerSocket(port); // Create a new server socket for the player
            player2_socket1 = player1_socket.accept(); // Accept the connection from the other player
            System.out.println("Which column would you like to drop your piece in?(1-7): ");
            int column = scanner.nextInt(); // Get the column number from the player
            PrintWriter out = new PrintWriter(player2_socket1.getOutputStream(), true); // Create a new output stream
            String insertmessage = "INSERT:" + column; // Create a new insert message
            out.println(insertmessage); // Send the insert message to the other player
            socket.close(); // Close the socket
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    }
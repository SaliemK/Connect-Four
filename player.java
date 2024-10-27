/**
 * NAME : SALIEM ABRAHALEY KIDANE
 * STUDENT ID: C3395781
 * COURSE : SENG4500 
 * ASSIGNMENT : ASSIGNMENT 2
 * PROGRAM : Player file, runs as both Player 1 and Player 2
 */

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

public class Player {
    
    private InetAddress broadcastAddress; // Broadcast address
    private int portUDP; // UDP port number, broadcast port
    private int portTCP; // TCP port number
    private DatagramSocket socket; // Socket for UDP connection
    private byte[] buf = new byte[256]; // Buffer for data
    private ServerSocket player1_socket = null; // Server socket for Player 1
    private Socket player2_socket; // Socket for Player 2
    private Scanner scanner = new Scanner(System.in); // Scanner for user input
    game game = new game(); // Create a new game object

    public static void main(String[] args) {
        Player player = new Player();
        player.run(args);
    }
    
    // Method to run the Player program
    public void run(String[] args) {
        Player player = new Player();
        player.portTCP = player.getPort();  // Get random port number for Player 1's server socket
        player.connection(start(args));
        player.receiveBroadcast();
    }
    
    // Method to start the game
    public static String start(String[] args) {
        if(args.length != 2) {
            System.out.println("Usage: java Player <broadcast_address> <broadcast_port>");
            System.exit(1);
        }
        String broadcast_address = args[0];
        String broadcast_port = args[1];
        return broadcast_address + ":" + broadcast_port;  // Return connection details
    }
    
    // Method to get the connection details, IP address and port number
    public void connection(String connection_details) {
        String[] connectionData = connection_details.split(":");
        try {
            broadcastAddress = InetAddress.getByName(connectionData[0]);  // Get the IP address of the server
            portUDP = Integer.parseInt(connectionData[1]);  // Get the port number
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    // Method to receive a broadcast message
    public void receiveBroadcast() {
        try {
            socket = new DatagramSocket(portUDP);  // Create a new socket
            socket.setSoTimeout(10000);  // timeout for 30 seconds
            System.out.println("Server is running on port " + portUDP);
            DatagramPacket packet = new DatagramPacket(buf, buf.length);  // Create packet to receive data
            try {
                socket.receive(packet);  // Receive data from the network
                String received = new String(packet.getData(), 0, packet.getLength());  // Convert to string
                System.out.println("Received: " + received);

                if (received.startsWith("NEW GAME:")) {
                    int client_port = Integer.parseInt(received.split(":")[1]);  // Get client port
                    InetAddress player1Address = packet.getAddress();  // Get client IP address
                    System.out.println("Player found. Starting new game as Player 2.");
                    playAsPlayer2(client_port, player1Address);  // Initiate as Player 2
                }
                else {
                    receiveBroadcast(); } // If invalid message, keep listening
                } catch (SocketTimeoutException e) {
                    System.out.println("No player found, Sending broadcast to start new game.");
                    sendBroadcast();  // If no player is found, start as Player 1
                }
            socket.close();  // Close the socket when done
        } catch (BindException e) {
            System.out.println("Port is already in use. Sending broadcast to start new game.");
            sendBroadcast();  // If port is already in use, start as Player 1
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generate a random port number between 9000 and 9100, to be used as a TCP port
    public int getPort() {
        Random random = new Random();
        return random.nextInt(9000, 9101);  // Generate a random port number between 9000 and 9100
    }

    // Method to send a broadcast to start a new game
    public void sendBroadcast(){
        try {
            DatagramSocket socket1 = new DatagramSocket();  // Create a new socket
            String message = "NEW GAME:" + portTCP;  // Broadcast the new game message
            System.out.println(message);
            byte[] buf = message.getBytes();  // Convert message to byte array
            DatagramPacket packet = new DatagramPacket(buf, buf.length, broadcastAddress, portUDP);  // Packet to send "NEW GAME" message
            player1_socket = new ServerSocket(portTCP);  // Create a new server socket for Player 1
            socket1.send(packet);  
            player1_socket.setSoTimeout(30000);  // 30 second wait time for Player 2 to join
            player2_socket = player1_socket.accept();  // Accept connection from Player 2
            System.out.println("Player 2 joined. Starting new game as Player 1.");
            playAsPlayer1(portTCP); // Start as Player 1
            socket1.close();  // Close the socket when done
            player1_socket.close();  // Close the server socket when done
        } catch (SocketTimeoutException e) {
            System.out.println("No player found. Starting new game as Player 2.");
            try{
                player1_socket.close(); // Close the server socket if no player is found
            }catch (IOException e1) {
                e1.printStackTrace();
            }
            receiveBroadcast();  // If no player is found, start listening for broadcasts.
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
  
    // Method to play as Player 1
    public void playAsPlayer1(int port) {
        try {
            PrintWriter out = new PrintWriter(player2_socket.getOutputStream(), true);  // Output stream to Player 2
            BufferedReader in = new BufferedReader(new InputStreamReader(player2_socket.getInputStream()));  // Input stream from Player 2
            System.out.println("Which column would you like to drop your piece in? (1-7): "); // Ask Player 1 for move
            int column = scanner.nextInt();  // Get the move from Player 1
            boolean continuePlaying = false;
            if(sendMessage(out, column,"X")) // Send the move to Player 2
            {
                continuePlaying = receiveMessage(in,"O"); // Receive the move from Player 2
            }
            while(continuePlaying) {
                System.out.println("Which column would you like to drop your piece in? (1-7): ");
                column = scanner.nextInt();  // Get the move from Player 1
                if(sendMessage(out, column,"X")){ // Send the move to Player 2
                    continuePlaying = receiveMessage(in,"O"); // Receive the move from Player 2
                }
                else{
                    continuePlaying = false;
                }
                // Check if Player 2 has won
                if(game.checkWin("O",game.getLastColumn(),game.getLastRow())) {
                    out.println("YOU WIN");
                    continuePlaying = false;
                    break;
                }
            }
            // Close connection when done
            if(!continuePlaying) {
                player1_socket.close();
            }
        }catch (SocketException e) {
            System.out.println("Player 2 has disconnected.");
            try{
                player1_socket.close(); // Close the socket if Player 2 has disconnected
        }catch (Exception e1) {
            e1.printStackTrace();
        }
    }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to play as Player 2
    public void playAsPlayer2(int port, InetAddress address) {
        try {
            Socket player2_socket = new Socket(address, port);  // Connect to Player 1's server socket
            System.out.println("Connected to Player 1 on port " + port);
            PrintWriter out = new PrintWriter(player2_socket.getOutputStream(), true);  // Create output stream
            BufferedReader in = new BufferedReader(new InputStreamReader(player2_socket.getInputStream()));  // Input stream
            boolean continuePlaying = receiveMessage(in,"X");
            while(continuePlaying) {
                System.out.println("Which column would you like to drop your piece in? (1-7): ");
                int column = scanner.nextInt();  // Get the move from Player 2
                if(sendMessage(out, column,"O")){
                    continuePlaying = receiveMessage(in,"X");
                }
                else{
                    continuePlaying = false;
                }
                // Check if Player 2 has won
                if(game.checkWin("X",game.getLastColumn(),game.getLastRow())) {
                    out.println("YOU WIN");
                    continuePlaying = false;
                    break;
                }
            }// Close connection when done
            if(!continuePlaying) {
                player2_socket.close();
            }
        }catch (SocketException e) {
            System.out.println("Player 1 has disconnected.");
            try{
                player2_socket.close(); // Close the socket if Player 1 has disconnected
            }catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Method to send a message to other player and insert the piece into the board
    public boolean sendMessage(PrintWriter out, int column,String piece) {
        String insertMessage = "INSERT:" + column; 
        boolean valid = game.insertPiece(column, piece);  // Insert the piece into the board 
        if(valid) {
            out.println(insertMessage);  // Send the move to the other player
            game.printBoard();
        }
        else {
            out.println("ERROR");
        }
        return valid;  // Return whether the move is valid or not
    }
    // Method to receive a message from other player
    public boolean receiveMessage(BufferedReader in,String piece) {
        boolean continuePlaying = false;
        try {
            String receivedMessage = in.readLine();  // Read data from Player 1
            System.out.println(receivedMessage);
            // Check if the message is an "INSERT" message
            if(receivedMessage.startsWith("INSERT:")) {
                int column = Integer.parseInt(receivedMessage.split(":")[1]);  // Get the move from Player 1
                continuePlaying = game.insertPiece(column, piece); // Insert the piece into the board
                if(continuePlaying) {
                    game.printBoard();
                }
            }
            // Check if the message is a "YOU WIN" message
            else if(receivedMessage.startsWith("YOU WIN")) {
                continuePlaying = false; // Stop playing if Player has won.
            }
            // Check if the message is an "ERROR" message
            else if(receivedMessage.startsWith("ERROR")) {
                continuePlaying = false; // Keep playing if invalid move
            }
            else {
                receiveMessage(in,piece); // If invalid message, keep waiting for a valid message
            }
        } catch (Exception e) {
            e.printStackTrace();
  }
return continuePlaying; // Return whether to continue playing or not
    }
}

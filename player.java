import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

public class Player {
    
    private InetAddress address;
    private int port_number;
    private DatagramSocket socket;
    private boolean running = false;
    private byte[] buf = new byte[256];
    private ServerSocket player1_socket;
    private Socket player2_socket1;
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Player player = new Player();
        player.connection(start(args));  // Setup connection using args
        player.run();  // Start running the game logic
    }

    public static String start(String[] args) {
        String broadcast_address = args[0];
        String broadcast_port = args[1];
        return broadcast_address + ":" + broadcast_port;  // Return connection details
    }

    public void connection(String connection_details) {
        String[] connectionData = connection_details.split(":");
        try {
            address = InetAddress.getByName(connectionData[0]);  // Get the IP address of the server
            port_number = Integer.parseInt(connectionData[1]);  // Get the port number
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            socket = new DatagramSocket(port_number);  // Create a new socket
            socket.setSoTimeout(10000);  // Set timeout for receiving data
            System.out.println("Server is running on port " + port_number);
            running = true;
            while (running) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);  // Create packet to receive data
                try {
                    socket.receive(packet);  // Receive data from the network
                    String received = new String(packet.getData(), 0, packet.getLength());  // Convert to string
                    System.out.println("Received from client: " + received);

                    if (received.startsWith("NEW GAME:")) {
                        int client_port = Integer.parseInt(received.split(":")[1]);  // Get client port
                        address = packet.getAddress();  // Get client IP address
                        playAsPlayer2(client_port, address);  // Initiate as Player 2
                    } else {
                        continue;
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("No player found. Starting new game as Player 1.");
                    playAsPlayer1();  // If no player is found, start as Player 1
                }
            }
            socket.close();  // Close the socket when done
        } catch (BindException e) {
           playAsPlayer1();  // If port is already in use, start as Player 1
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        Random random = new Random();
        return random.nextInt(9000, 9101);  // Generate a random port number between 9000 and 9100
    }

    public void playAsPlayer2(int port, InetAddress address) {
        try {
            Socket player2_socket = new Socket(address, port);  // Connect to Player 1's server socket
            System.out.println("Connected to Player 1 on port " + port);
            PrintWriter out = new PrintWriter(player2_socket.getOutputStream(), true);  // Create output stream
            BufferedReader in = new BufferedReader(new InputStreamReader(player2_socket.getInputStream()));  // Input stream

            // Here you could add the game logic for Player 2's interaction (reading moves, sending responses, etc.)
            // Example:
            String receivedMessage = in.readLine();  // Read data from Player 1
            System.out.println("Player 1 move: " + receivedMessage);
            
            // Close connection when done
            player2_socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playAsPlayer1() {
        int port = getPort();  // Get random port number for Player 1's server socket
        try {
            DatagramSocket socket = new DatagramSocket();  // Create a new socket
            String message = "NEW GAME:" + port;  // Broadcast the new game message
            byte[] buf = message.getBytes();  // Convert message to byte array
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port_number);  // Packet to send
            socket.send(packet);  // Send the packet to the network
            
            // Now, wait for Player 2 to connect
            player1_socket = new ServerSocket(port);  // Server socket to accept incoming connections
            System.out.println("Waiting for Player 2 to join...");
            player2_socket1 = player1_socket.accept();  // Accept connection from Player 2
            
            // Interact with Player 2 - for example, sending the first move
            System.out.println("Which column would you like to drop your piece in? (1-7): ");
            int column = scanner.nextInt();  // Get the column number from Player 1
            PrintWriter out = new PrintWriter(player2_socket1.getOutputStream(), true);  // Output stream to Player 2
            String insertMessage = "INSERT:" + column;  // Send the move
            out.println(insertMessage);
            
            // After sending the move, close the sockets
            player2_socket1.close();  // Close connection with Player 2
            player1_socket.close();  // Close server socket
            socket.close();  // Close the DatagramSocket
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

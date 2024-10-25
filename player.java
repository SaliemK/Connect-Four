import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

public class Player {
    
    private InetAddress address;
    private int portUDP;
    private int portTCP;
    private DatagramSocket socket;
    private byte[] buf = new byte[256];
    private ServerSocket player1_socket = null;
    private Socket player2_socket;
    private Scanner scanner = new Scanner(System.in);
    game game = new game();


    public static void main(String[] args) {
        Player player = new Player();
        player.run(args);
    }
    public void run(String[] args) {
        Player player = new Player();
        player.portTCP = player.getPort();  // Get random port number for Player 1's server socket
        //player.portTCP= 9002;
        player.connection(start(args));
        player.receiveBroadcast();
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
            portUDP = Integer.parseInt(connectionData[1]);  // Get the port number
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void receiveBroadcast() {
        try {
            socket = new DatagramSocket(portUDP);  // Create a new socket
            socket.setSoTimeout(10000);  // timeout for 30 seconds
            System.out.println("Server is running on port " + portUDP);
            DatagramPacket packet = new DatagramPacket(buf, buf.length);  // Create packet to receive data
            try {
                socket.receive(packet);  // Receive data from the network
                String received = new String(packet.getData(), 0, packet.getLength());  // Convert to string
                System.out.println("Received from client: " + received);

                if (received.startsWith("NEW GAME:")) {
                    int client_port = Integer.parseInt(received.split(":")[1]);  // Get client port
                    address = packet.getAddress();  // Get client IP address
                    playAsPlayer2(client_port, address);  // Initiate as Player 2
                }
                } catch (SocketTimeoutException e) {
                    System.out.println("No player found, Sending broadcast to start new game.");
                    sendBroadcast();  // If no player is found, start as Player 1
                }
            socket.close();  // Close the socket when done
        } catch (BindException e) {
            System.out.println("Port is already in use. Sending broadcast to start new game.");
            //socket.close();
           sendBroadcast();  // If port is already in use, start as Player 1
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
            boolean continuePlaying = receiveMessage(in,"X");
            while(continuePlaying) {
                System.out.println("Which column would you like to drop your piece in? (1-7): ");
                int column = scanner.nextInt();  // Get the move from Player 2
                sendMessage(out, column,"O");
                continuePlaying = receiveMessage(in,"X");
                if(game.checkWin("X",game.getLastColumn(),game.getLastRow())) {
                    out.println("YOU WIN");
                    continuePlaying = false;
                    break;
                }
            }
            if(!continuePlaying) {
                // Close connection when done
                player2_socket.close();
                //player1_socket.close();
            }
        }catch (IOException e) {
            System.out.println("Player 1 has disconnected.");
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendBroadcast(){
        //int port = getPort();  // Get random port number for Player 1's server socket
        //ServerSocket player1_socket = null;  // Server socket to accept incoming connections
        try {
            DatagramSocket socket1 = new DatagramSocket();  // Create a new socket
            //socket.setBroadcast(true);  // Enable broadcast
            String message = "NEW GAME:" + portTCP;  // Broadcast the new game message
            System.out.println(message);
            byte[] buf = message.getBytes();  // Convert message to byte array
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portUDP);  // Packet to send
            player1_socket = new ServerSocket(portTCP); 
            socket1.send(packet);  // Send the packet to the network
            player1_socket.setSoTimeout(10000);  // Set timeout for accepting connections
            System.out.println("Waiting for Player 2 to join...");
            System.out.println("Player1 is running on port " + portTCP);
            player2_socket = player1_socket.accept();  // Accept connection from Player 2
            playAsPlayer1(portTCP);
            socket1.close();  // Close the socket
            player1_socket.close();  // Close the server socket
        } catch (SocketTimeoutException e) {
            System.out.println("No player found. Starting new game as Player 2.");
            socket.close();
            try{
                player1_socket.close();
            }catch (IOException e1) {
                e1.printStackTrace();
            }
            receiveBroadcast();  // If no player is found, start as Player 1
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally{
           
        }
        
    }
    public void sendMessage(PrintWriter out, int column,String piece) {
        String insertMessage = "INSERT:" + column;  // Send the move
        game.insertPiece(column, piece);
        game.printBoard();
        out.println(insertMessage);
    }

    public boolean receiveMessage(BufferedReader in,String piece) {
        boolean continuePlaying = false;
        try {
            String receivedMessage = in.readLine();  // Read data from Player 1
            System.out.println(receivedMessage);
            if(receivedMessage.startsWith("INSERT:")) {
                int column = Integer.parseInt(receivedMessage.split(":")[1]);  // Get the move from Player 1
                game.insertPiece(column, piece);
                continuePlaying = true;
                game.printBoard();
            }
            else if(receivedMessage.startsWith("YOU WIN")) {
                continuePlaying = false;
            }
            else{
                receiveMessage(in,piece); // 
            }
        } catch (Exception e) {
            e.printStackTrace();
  }
return continuePlaying;
    }
    public void playAsPlayer1(int port) {
        try {
            // Now, wait for Player 2 to connect
            //player1_socket = new ServerSocket(port);  // Server socket to accept incoming connections
            System.out.println("Player 2 has joined the game.");
            PrintWriter out = new PrintWriter(player2_socket.getOutputStream(), true);  // Output stream to Player 2
            BufferedReader in = new BufferedReader(new InputStreamReader(player2_socket.getInputStream()));  // Input stream from Player 2
            System.out.println("Which column would you like to drop your piece in? (1-7): ");
            int column = scanner.nextInt();  // Get the move from Player 1
            sendMessage(out, column,"X");
            boolean continuePlaying = receiveMessage(in,"O");
            while(continuePlaying) {
                System.out.println("Which column would you like to drop your piece in? (1-7): ");
                column = scanner.nextInt();  // Get the move from Player 1
                sendMessage(out, column,"X");
                continuePlaying = receiveMessage(in,"O");
                if(game.checkWin("O",game.getLastColumn(),game.getLastRow())) {
                    out.println("YOU WIN");
                    continuePlaying = false;
                }
            }
            if(!continuePlaying) {
                // Close connection when done
                player1_socket.close();
                player2_socket.close();
            }
        }catch (IOException e) {
            System.out.println("Player 2 has disconnected.");
            e.printStackTrace(); 
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

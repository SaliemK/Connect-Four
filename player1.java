import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;

public class player1 {
    private int port_number;
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public void run(int port_number){
        try{
            socket = new DatagramSocket(port_number);
            System.out.println("Server is running on port " + port_number);
            running = true;
            InetAddress client_IP = null;
            while(running){
                DatagramPacket packet = new DatagramPacket(buf, buf.length); // Create a packet to receive data
                socket.receive(packet); // Receive data from the client
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received from client: " + received);
                if(received.equals("PING")){
                    //store the IP address and port number of the client
                    client_IP = packet.getAddress();
                    byte[] data = "PONG".getBytes();
                    DatagramPacket response = new DatagramPacket(data, data.length, client_IP, client_port);
                    socket.send(response);
                    System.out.println("Sending PONG to client on port " + client_port);
                    running = false;
                    break;
                }
                else{
                continue;
                }
            }
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    }
}

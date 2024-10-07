import java.util.Random;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
public class player2 {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    public void run(int port_number){
        try{
            socket = new DatagramSocket(port_number);
            socket.setSoTimeout(1000); // Wait for 30 seconds before timing out
            System.out.println("Server is running on port " + port_number);
            running = true;
            InetAddress client_IP = null;
            while(running){
                DatagramPacket packet = new DatagramPacket(buf, buf.length); // Create a packet to receive data
                try{
                    socket.receive(packet); // Receive data from the client
                    String received = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Received from client: " + received);
                    if(received.startsWith("NEW GAME:")){
                    //store the IP address and port number of the client
                    client_IP = packet.getAddress();
                }
                else{
                continue;
                }
                } catch (SocketTimeoutException e) {
                    // Timeout occurred, move to the next port
                    //System.out.println("Timeout on port " + i + ", moving to next port.");
    
                }
                
            }
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
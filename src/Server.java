/*
https://www.baeldung.com/udp-in-java
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    String deathmessage = "death";


    public Server() throws SocketException {
        socket = new DatagramSocket(4445);
        System.out.println(socket.getLocalAddress());
    }

    public static void main(String[] args) throws SocketException {
        Server server = new Server();
        server.run();
    }

    public void run() {
        System.out.println("Running server on port " + this.socket.getLocalPort());
        running = true;

        while (running) {

            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received
                    = new String(packet.getData(), 0, packet.getLength());
            try {
                received = received.trim();
                buf = new byte[256];
                int num = 0;
                String contents = null;
                try {
                    num = Integer.parseInt(received);
                    contents = num * num + "";
                } catch (NumberFormatException e) {
                    if (received.equals("end")) {
                        running = false;
                        continue;
                    }
                    if (received.equals("death")) {
                        DatagramPacket dp = new DatagramPacket(deathmessage.getBytes(), deathmessage.length(), address, port);
                        while (running) {
                            socket.send(dp);
                            if (received.equals("help")) break;
                        }
                    }
                    System.out.println("I would prefer an integer but you will get your string after 5 dots");
                    for (int i=0; i<=5; i++) {
                        sleep(1000);
                        System.out.print(". ");
                    }
                    contents = received;
                }
                DatagramPacket p = new DatagramPacket(contents.getBytes(), contents.length(), address, port);
                socket.send(p);
                System.out.println(contents);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}
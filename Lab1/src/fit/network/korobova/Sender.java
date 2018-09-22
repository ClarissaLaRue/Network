package fit.network.korobova;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.TimerTask;

public class Sender extends TimerTask {
    private int port;
    private DatagramSocket socket;
    private InetAddress group;
    byte [] data;
    DatagramPacket datagramPacket;

    Sender(int port, InetAddress group, DatagramSocket socket){
        this.group = group;
        this.port = port;
        this.socket = socket;
        data = "Something".getBytes();
        datagramPacket = new DatagramPacket(data, data.length, group, port);
    }

    @Override
    public void run() {
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package fit.network.korobova;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Timer;

public class Reciever implements Runnable {
    byte[] buf = new byte[256];
    private MulticastSocket socket;
    DatagramPacket input = new DatagramPacket(buf, buf.length);
    private HashMap<String, Long> connectors;
    long timer;
    private int countConnectors = 0;

    Reciever(MulticastSocket socket, HashMap<String, Long> connectors ){
        this.socket = socket;
        this.connectors = connectors;
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(3000);
            while (true) {
                socket.receive(input);
                String key = input.getAddress().toString() + " " + input.getPort();
                if (!connectors.containsKey(key)) {
                    timer = System.currentTimeMillis();
                    connectors.put(key, timer);
                }
                checkConnectors();
                printer();
            }
        } catch (SocketException e) {
            e.printStackTrace();
            checkConnectors();
            printer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkConnectors(){
        for (HashMap.Entry<String, Long> elem:connectors.entrySet()) {
            if (timer - (elem.getValue()) > 3000){
                connectors.remove(elem.getKey());
            }
        }
    }

    public int getCountConnectors(){
        return connectors.size();
    }

    public HashMap<String, Long> getConnectors(){
        return connectors;
    }

    public void printer() {
        int newCount = getCountConnectors();
        if (countConnectors != newCount) {
            System.out.println(getConnectors().keySet());
            countConnectors = newCount;
        }
    }

}

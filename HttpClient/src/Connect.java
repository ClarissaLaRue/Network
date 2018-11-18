import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Connect {
    private InetSocketAddress serverAddr;
    private InetSocketAddress address;
    private Boolean isStopped;
    private ConcurrentLinkedQueue<String> messages;
    private String token;

    public Connect(InetSocketAddress address, InetSocketAddress serverAddr, ConcurrentLinkedQueue<String> messages) {
        this.serverAddr = serverAddr;
        this.address = address;
        isStopped = false;
        this.messages = messages;
    }

    public void stop() {
        isStopped = true;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void run(){
        Socket socket;
        try {
            socket = new Socket(address.getHostName(), address.getPort());
        } catch (IOException e) {
            System.out.println("Unknown socket address");
            stop();
            return;
        }
        try {
            sendLoginMessage();
        }
        while (!isStopped){
            try {
                String msg = messages.poll();
                OutputStream out = socket.getOutputStream();
                sendMessage(msg, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String msg, OutputStream out) {
    }

    private void sendLoginMessage() {
    }
}

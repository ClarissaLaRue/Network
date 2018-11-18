import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
    private InetSocketAddress serverAddr;
    private ConcurrentLinkedQueue<String> messages;
    private InetSocketAddress address;

    Client (String hostname, int port, String serverHost, int serverPort){
        address = new InetSocketAddress(hostname, port);
        serverAddr = new InetSocketAddress(serverHost, serverPort);
        messages = new ConcurrentLinkedQueue<>();
    }

    void start(){
        Connect connect = new Connect(address, serverAddr, messages);
        Scanner scanner = new Scanner(System.in);
        String msg;
        while (!connect.isStopped()){
            if ((msg = scanner.nextLine()).equals("exit")) break;
            messages.add(msg);
        }
        connect.stop();
    }

}

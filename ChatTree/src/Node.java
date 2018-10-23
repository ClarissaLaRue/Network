import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Node {
    static final Object o = new Object();
    static boolean isRoot;
    static int loss;
    static int port;
    static InetSocketAddress perent;
    static ArrayList<InetSocketAddress> connectors;
    Sender sender;

    public Node(boolean isRoot, int loss, int port ,int perentPort, InetAddress perentAdress) throws SocketException {
        this.isRoot = isRoot;
        this.loss = loss;
        this.port = port;
        if (!isRoot){
            perent = new InetSocketAddress( perentAdress, perentPort);
            connectors.add(perent);
        }
        sender = new Sender(isRoot);
        sender.start();//start send messages
    }

    //read message and add to message list
    public void start(){
        Scanner scanner = new Scanner(System.in);
        while (true){
            sender.addMessage(scanner.nextLine(), Message.USUAL);
        }
    }
}

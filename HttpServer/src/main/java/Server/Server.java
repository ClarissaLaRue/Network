package Server;

import Server.Matchers.*;
import Server.Handlers.*;
import Server.Handlers.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
    private ArrayList<User> users;
    private ArrayList<Message> messages;
    private ServerSocket socket;
    private HandlersTree handlersTree;

    public Server(String host, int port) throws IOException {
        messages = new ArrayList<>();
        users = new ArrayList<>();
        socket = new ServerSocket();
        socket.bind(new InetSocketAddress(host, port));
    }


    public void start() {
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(15);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 5, 0, TimeUnit.SECONDS, queue);
        while (true){
            try {
                Socket s = socket.accept();
                threadPool.execute(new Sender(users, messages, s));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

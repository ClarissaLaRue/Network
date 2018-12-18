package Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Server server = new Server("localhost", 8080);
        server.start();
    }
}

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

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    public static final String USERS = "users";
    public static final String MESSAGE = "message";
    public static final String COUNT = "count=";
    public static final String OFFSET = "offset=";

    public Server(String host, int port) throws IOException {
        messages = new ArrayList<>();
        users = new ArrayList<>();
        socket = new ServerSocket();
        socket.bind(new InetSocketAddress(host, port));
        handlersTree = new HandlersTree();
        fillHandlersTree();
    }


    public void start() {
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(15);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 5, 0, TimeUnit.SECONDS, queue);
        while (true){
            try {
                Socket s = socket.accept();
                Request request = new Request();
                Handler handler;
                try {
                    request = makeRequest(s.getInputStream());
                    handler = chooseHendler(request);
                }catch (ServerException e) {
                    System.out.println(e.getInfo());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Server.Request makeRequest(InputStream in) throws ServerException, IOException {
        int c = 0;
        int prev = c;
        Request request = new Request();
        StringBuilder requestStr = new StringBuilder();
        try{
            while (((c = in.read()) != -1 ) && ((((char)c != '\n'))||((char)prev != '\n'))){
                if (c == '\r') {
                    continue;
                }
                requestStr.append((char)c);
                prev = c;
            }
        } catch (IOException e) {
            throw new ServerException(500, "InternalServerError");
        }
        if (requestStr.length() != 0){
            request = parse(requestStr);
        }
        request.setBody(in);
        return request;
    }

    private Request parse(StringBuilder requestStr) throws ServerException {
        Request request = new Request();
        //Получаем глагол
        String verb = requestStr.substring(0, requestStr.indexOf(" "));
        request.setMethod(verb);
        requestStr = requestStr.delete(0, requestStr.indexOf(" ")+1);
        //Получаем путь
        String path = requestStr.substring(0, requestStr.indexOf(" "));
        request.setPath(path);
        requestStr = requestStr.delete(0, requestStr.indexOf(" ")+1);
        //Получаем протокол
        String protocol = requestStr.substring(0, requestStr.indexOf("\n"));
        request.setProtocol(protocol);
        requestStr = requestStr.delete(0, requestStr.indexOf("\n")+1);
        //Получаем список Headers
        request.setHeaders(new String(requestStr));
        return request;
    }

    private void fillHandlersTree() {
        ArrayList<Matcher> login = new ArrayList<>();
        login.add(new StringMatcher(POST));
        login.add(new StringMatcher(LOGIN));
        handlersTree.addBranch(login, new Login());

        ArrayList<Matcher> logout = new ArrayList<>();
        logout.add(new StringMatcher(POST));
        logout.add(new StringMatcher(LOGOUT));
        handlersTree.addBranch(logout, new Logout());

        ArrayList<Matcher> messageGet = new ArrayList<>();
        messageGet.add(new StringMatcher(GET));
        messageGet.add(new StringMatcher(MESSAGE));
        handlersTree.addBranch(messageGet, new MessageGetter());

        ArrayList<Matcher> offsetCountMesGet = new ArrayList<>();
        offsetCountMesGet.add(new StringMatcher(GET));
        offsetCountMesGet.add(new StringMatcher(MESSAGE));
        offsetCountMesGet.add(new StringMatcher(OFFSET));
        offsetCountMesGet.add(new StringMatcher(COUNT));
        handlersTree.addBranch(offsetCountMesGet, new OffsetCountMessageGetter());

        ArrayList<Matcher> userGet = new ArrayList<>();
        userGet.add(new StringMatcher(GET));
        userGet.add(new StringMatcher(USERS));
        handlersTree.addBranch(userGet, new UsersGetter());

        ArrayList<Matcher> userIdGet = new ArrayList<>();
        userIdGet.add(new StringMatcher(GET));
        userIdGet.add(new StringMatcher(USERS));
        userIdGet.add(new IntMatcher());
        handlersTree.addBranch(userIdGet, new UserIdGetter());
    }

    private Handler chooseHendler(Request request) {
        return handlersTree.getHandler(request.getMethod(), request.getPath());
    }



}

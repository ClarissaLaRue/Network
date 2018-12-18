package Server;

import Server.Handlers.*;
import Server.Matchers.IntMatcher;
import Server.Matchers.Matcher;
import Server.Matchers.StringMatcher;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Sender implements Runnable{
    private BufferedInputStream in;
    private BufferedWriter outputStream;
    private final ArrayList<User> users;
    private ArrayList<Message> messages;
    private HandlersTree handlersTree;
    private Request request;

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    public static final String USERS = "users";
    public static final String MESSAGE = "message";
    public static final String COUNT = "count=";
    public static final String OFFSET = "offset=";

    public Sender(ArrayList<User> users, ArrayList<Message> messages, Socket socket) throws IOException {
        this.users = users;
        this.messages = messages;
        handlersTree = new HandlersTree();
        fillHandlersTree();
        in = new BufferedInputStream(socket.getInputStream());
        outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        handlersTree = new HandlersTree();
        fillHandlersTree();
    }

    @Override
    public void run() {
        request = new Request();
        Handler handler;
        try {
            request = makeRequest(in);
            handler = chooseHendler(request);
            sendResponse(handler.getResponse(request));
        } catch (ServerException e) {
            System.out.println("SERVER ERR");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendResponse(Respones response) throws ServerException {
        try {
            outputStream.write(response.getTitle() + "\r\n");
            outputStream.write(response.getHeadersAsString());
            outputStream.write(response.getBody());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new ServerException(500, "Writing response error");
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
        handlersTree.addBranch(login, new Login(users, messages));

        ArrayList<Matcher> logout = new ArrayList<>();
        logout.add(new StringMatcher(POST));
        logout.add(new StringMatcher(LOGOUT));
        handlersTree.addBranch(logout, new Logout(users, messages));

        ArrayList<Matcher> messageGet = new ArrayList<>();
        messageGet.add(new StringMatcher(GET));
        messageGet.add(new StringMatcher(MESSAGE));
        handlersTree.addBranch(messageGet, new MessageGetter(messages));

        ArrayList<Matcher> offsetCountMesGet = new ArrayList<>();
        offsetCountMesGet.add(new StringMatcher(GET));
        offsetCountMesGet.add(new StringMatcher(MESSAGE));
        offsetCountMesGet.add(new StringMatcher(OFFSET));
        offsetCountMesGet.add(new StringMatcher(COUNT));
        handlersTree.addBranch(offsetCountMesGet, new OffsetCountMessageGetter(users, messages));

        ArrayList<Matcher> userGet = new ArrayList<>();
        userGet.add(new StringMatcher(GET));
        userGet.add(new StringMatcher(USERS));
        handlersTree.addBranch(userGet, new UsersGetter(users));

        ArrayList<Matcher> userIdGet = new ArrayList<>();
        userIdGet.add(new StringMatcher(GET));
        userIdGet.add(new StringMatcher(USERS));
        userIdGet.add(new IntMatcher());
        handlersTree.addBranch(userIdGet, new UserIdGetter(users));
    }

    private Handler chooseHendler(Request request) {
        return handlersTree.getHandler(request.getMethod(), request.getPath());
    }
}

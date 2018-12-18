package Server.Handlers;

import Server.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class Login implements Handler {
    private final ArrayList<User> users;
    private final ArrayList<Message> messages;

    public Login (ArrayList<User> users, ArrayList<Message> messages) {
        this.users = users;
        this.messages = messages;
    }

    @Override
    public Respones getResponse(Request request) throws ServerException {
        JSONParser jsonParser = new JSONParser();
        try {
            Respones response = new Respones();
            JSONObject reqBody = (JSONObject) jsonParser.parse(request.getBody());
            String username = (String) reqBody.get("username");
            User equal;
            if ((equal = findUser(username)) != null){
                if (equal.isOnline()) {
                    response.setTitle("HTTP/1.1 401 Unauthorized");
                    response.setHeader("WWW-Authenticate", "Token realm='Username is already in use'");
                    return response;
                } else {
                    equal.setOnline();
                    synchronized (messages) {
                        messages.add(new Message("User " + username + " online again", equal.getId()));
                    }
                    response.setOKTitle();
                    JSONObject responseBody = new JSONObject();
                    responseBody.put("id", equal.getId());
                    responseBody.put("username", equal.getName());
                    responseBody.put("online", equal.isOnline());
                    responseBody.put("token", equal.getToken());
                    String bodyStr = responseBody.toJSONString();
                    response.setHeader("Content-Type", "application/json");
                    response.setBody(bodyStr);
                    return response;
                }
            }
            User user = new User(username, true);
            synchronized (users) {
                users.add(user);
            }
            synchronized (messages) {
                messages.add(new Message("New user : " + username, user.getId()));
            }
            response.setOKTitle();
            JSONObject responseBody = new JSONObject();
            responseBody.put("id", user.getId());
            responseBody.put("username", user.getName());
            responseBody.put("online", user.isOnline());
            responseBody.put("token", user.getToken());
            String bodyStr = responseBody.toJSONString();
            response.setHeader("Content-Type", "application/json");
            response.setBody(bodyStr);
            return response;
        } catch (ParseException e) {
            throw new ServerException(400, "Can't parse body(json)");
        }
    }

    private User findUser(String username) {
        synchronized (users) {
            for (User u : users) {
                if (u.getName().equals(username)) {
                    return u;
                }
            }
        }
        return null;
    }
}

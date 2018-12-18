package Server.Handlers;

import Server.*;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Logout implements Handler{
    private final ArrayList<User> users;
    private final ArrayList<Message> messages;

    public Logout(ArrayList<User> users, ArrayList<Message> messages) {
        this.users = users;
        this.messages = messages;
    }

    @Override
    public Respones getResponse(Request request) throws ServerException {
        String token = request.getToken();
        User deleted = null;
        synchronized (users) {
            for (User u : users) {
                if (u.getToken().equals(token)){
                    u.setOffline();
                    deleted = u;
                    break;
                }
            }
        }
        if (deleted != null) {
            synchronized (messages) {
                messages.add(new Message("Exit user : " + deleted.getName(), deleted.getId()));
            }
        }
        Respones response = new Respones();
        response.setOKTitle();
        JSONObject responseBody = new JSONObject();
        responseBody.put("message", "Goodbye!");
        String bodyStr = responseBody.toJSONString();
        response.setHeader("Content-Type", "application/json");
        response.setBody(bodyStr);
        return response;
    }
}

package Server.Handlers;

import Server.Request;
import Server.Respones;
import Server.ServerException;
import Server.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class UsersGetter implements Handler{
    private final ArrayList<User> users;

    public UsersGetter(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public Respones getResponse(Request request) throws ServerException {
        JSONObject userList = new JSONObject();
        JSONArray usersArr = new JSONArray();
        synchronized (users) {
            users.forEach(u -> {
                JSONObject user = new JSONObject();
                user.put("id", u.getId());
                user.put("username", u.getName());
                user.put("online", u.isOnline());
                usersArr.add(user);
            });
        }
        userList.put("users", usersArr);
        String jsonContent = userList.toJSONString();
        Respones response = new Respones();
        response.setHeader("Content-Type", "application/json");
        response.setOKTitle();
        response.setBody(jsonContent);
        return response;
    }
}

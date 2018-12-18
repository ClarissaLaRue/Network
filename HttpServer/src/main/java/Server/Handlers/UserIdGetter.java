package Server.Handlers;

import Server.Request;
import Server.Respones;
import Server.ServerException;
import Server.User;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class UserIdGetter implements Handler {
    private final ArrayList<User> users;

    public UserIdGetter(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public Respones getResponse(Request request) throws ServerException {
        JSONObject userInfo = new JSONObject();
        Integer id = Integer.parseInt(request.getPath().split("/")[1]);
        User user = getUser(id);
        if (user == null) {
            throw new ServerException(404, "Not found");
        }
        userInfo.put("id", id);
        userInfo.put("username", user.getName());
        userInfo.put("online", user.isOnline());
        String strBody = userInfo.toJSONString();
        Respones response = new Respones();
        response.setHeader("Content-Type", "application/json");
        response.setOKTitle();
        response.setBody(strBody);
        return response;
    }

    private User getUser(Integer id) {
        synchronized (users) {
            for (User u : users) {
                if (u.getId() == id) {
                    return u;
                }
            }
        }
        return null;
    }
}

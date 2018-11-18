package Server.Handlers;

import Server.Request;
import Server.Respones;
import Server.User;

import java.util.ArrayList;

public class Login implements Handler {
    private final ArrayList<User> users;

    public Login (ArrayList<User> users) {
        this.users = users;
    }

    public Login(){
        users = null;
    }

    @Override
    public Respones getResponse(Request message) {
        return null;
    }
}

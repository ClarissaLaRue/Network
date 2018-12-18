package Server.Handlers;

import Server.Matchers.Matcher;
import Server.Message;
import Server.Request;
import Server.Respones;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class MessageGetter implements Handler{
    private final ArrayList<Message> messages;

    public MessageGetter(ArrayList<Message> messages) {
        this.messages = messages;
    }
    @Override
    public Respones getResponse(Request request) {
        JSONObject respBody = new JSONObject();
        JSONArray msgArr = new JSONArray();
        synchronized (messages) {
            messages.forEach(message-> createMsgObj(msgArr, message));
        }
        respBody.put("messages", msgArr);
        Respones response = new Respones();
        response.setOKTitle();
        response.setHeader("Content-Type", "application/json");
        response.setBody(respBody.toJSONString());
        return response;
    }

    static void createMsgObj(JSONArray msgArr, Message msg) {
        JSONObject msgObj = new JSONObject();
        msgObj.put("id", msg.getId());
        msgObj.put("message", msg.getData());
        msgObj.put(("author"), msg.getSenderId());
        msgArr.add(msgObj);
    }
}

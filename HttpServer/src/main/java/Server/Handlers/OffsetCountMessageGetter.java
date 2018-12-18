package Server.Handlers;

import Server.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class OffsetCountMessageGetter implements Handler{
    private final ArrayList<User> users;
    private final ArrayList<Message> messages;

    public OffsetCountMessageGetter(ArrayList<User> users, ArrayList<Message> messages) {
        this.users = users;
        this.messages = messages;
    }

    @Override
    public Respones getResponse(Request request) throws ServerException {
        String[] pathArr = request.getPath().split("[?&=]");
        if (pathArr.length < 5) {
            throw new ServerException(405, "");
        }
        Integer offset = Integer.valueOf(pathArr[2]);
        int count = Integer.parseInt(pathArr[4]);
        if (count > 100 || count < 0) {
            throw new ServerException(400, "Bad count of messages");
        }
        JSONObject respBody = new JSONObject();
        JSONArray msgArr = new JSONArray();
        if (count == 1) {
            while (true) {
                int size;
                synchronized (messages) {
                    size = messages.size();
                }
                if (offset < size) {
                    ArrayList<Message> messageArrayList;
                    synchronized (messages) {
                        messageArrayList = new ArrayList<>(messages);
                    }
                    for (int i = offset; i < messageArrayList.size(); i++) {
                        MessageGetter.createMsgObj(msgArr, messageArrayList.get(i));
                    }
                    break;
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new ServerException(500, "Server interrupted");
                }
            }
        } else {
            synchronized (messages) {
                if (offset >= messages.size()) {
                    return makeRespones(respBody, msgArr);
                }
            }
            ArrayList<Message> messageArrayList;
            synchronized (messages) {
                messageArrayList = new ArrayList<>(messages);
            }
            for (int i = offset; i < messageArrayList.size() && i < offset + count; i++) {
                MessageGetter.createMsgObj(msgArr, messageArrayList.get(i));
            }
            messageArrayList.clear();
        }
        return makeRespones(respBody, msgArr);
    }

    private Respones makeRespones(JSONObject respBody, JSONArray msgArr) {
        respBody.put("messages", msgArr);
        Respones response = new Respones();
        response.setOKTitle();
        response.setHeader("Content-Type", "application/json");
        response.setBody(respBody.toJSONString());
        return response;
    }
}

package com.client;

import com.client.json.*;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class Poller extends TimerTask {
    private Gson gson;
    private List<Message> messageList;
    private int token;
    private OkHttpClient client;
    private List<UserInfo> users;
    private Client c;
    Poller(Gson gson, int token, OkHttpClient client, Client c){
        this.gson = gson;
        this.token = token;
        this.client = client;
        this.c = c;
        messageList = new ArrayList<>();
    }
    @Override
    public void run() {
        if(!c.isLogin()){
            return;
        }
        Request requestMessages = new Request.Builder()
                .method("GET", null)
                .addHeader("Authorization", "Token " + token)
                .url("http://localhost:8081/messages?offset=" + messageList.size() + "&count=" + 10)
                .build();
        Request requestUsers = new Request.Builder()
                .method("GET", null)
                .addHeader("Authorization", "Token " + token)
                .url("http://localhost:8081/users")
                .build();
        client.newCall(requestMessages).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {                                                             //ВСЕ ПРОВЕРКИ НА КОДЫ!!!
                    MessageList messages = gson.fromJson(response.body().string(), MessageList.class);
                    if(messages != null) {
                        printNewMessages(messages);
                    }
                } else if(response.code() == 403){
                    c.setLogin(false);
                }
            }
        });
        client.newCall(requestUsers).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {                                                             //ВСЕ ПРОВЕРКИ НА КОДЫ!!!
                    UsersList usersList = gson.fromJson(response.body().string(), UsersList.class);
                    addUsers(usersList);
                } else if(response.code() == 403){
                    c.setLogin(false);
                }
            }
        });

    }

    private void printNewMessages(MessageList newMessages){
        List<Message> arrayList = newMessages.getMessages();
        for(Message message: arrayList){
            System.out.println(message.getSender() + ": " + message.getMessage() + '\n');
            messageList.add(message);
        }
    }

    private void addUsers(UsersList usersList){
        users = usersList.getUsers();
    }

    public List<UserInfo> getUsers(){
        return users;
    }
}

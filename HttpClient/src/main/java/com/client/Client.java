package com.client;

import com.google.gson.Gson;
import com.client.json.*;
import okhttp3.*;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;

public class Client {
    private OkHttpClient client;
    private int token;
    private Scanner scanner;
    private Gson gson;
    private boolean messageSent;
    private boolean logout;
    private boolean login;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Client(){
        client = new OkHttpClient();
        scanner = new Scanner(System.in);
        gson = new Gson();
        messageSent = false;
        logout = false;
        login = false;
    }
    void start() throws IOException {
        login();
        Timer timer = new Timer(true);
        Poller poller = new Poller(gson, token, client, this);
        timer.schedule(poller, 3000, 3000);
        String message = scanner.nextLine();
        while(!message.equals("/logout")){
            if(!login){
                login();
            }
            if(message.equals("/list")){
                printUsersList(poller.getUsers());
            } else {
                sendMessage(message);
            }
            message = scanner.nextLine();
        }
        poller.cancel();
        logout();

    }

    private void login() throws IOException {
        Response response;
        UserInfo userInfo;
        while (true) {      //нужно ли проверить хэдер?
            System.out.println("Input your username\n");
            String username = scanner.nextLine();
            Login login = new Login(username);
            String json = gson.toJson(login);
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .url("http://localhost:8081/login")
                    .post(body)
                    .build();
            response = client.newCall(request).execute();
            if (response.code() == 200){
                userInfo = gson.fromJson(response.body().string(), UserInfo.class);
                this.login = true;
                break;
            }
        }
        token = userInfo.getToken();
    }

    private void sendMessage(String message){
        if(!login){
            System.out.println("Connection lost\n");
            return;
        }
        Message messageSend = new Message(message);
        String json = gson.toJson(messageSend);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Token " + token)
                .url("http://localhost:8081/messages")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code() == 200){            //А ЧТО ЕСЛИ НЕ 200????
                    setMessageSent(true);
                } else if(response.code() == 403){
                    login = false;
                }
            }
        });
    }

    private void logout() throws IOException {
        RequestBody body = RequestBody.create(JSON, "");
        Request request = new Request.Builder()
                .method("POST", body)
                .addHeader("Authorization", "Token " + token)
                .url("http://localhost:8081/logout")
                .build();
        Response response = client.newCall(request).execute();
        if(response.code() == 200)
            return;
    }

    private void printUsersList(List<UserInfo> users){
        if(!login){
            System.out.println("Connection lost\n");
            return;
        }
        for(UserInfo userInfo: users){
            System.out.println(userInfo.getUserName() + '\n');
        }
    }

    void setMessageSent(boolean val){
        messageSent = val;
    }

    void setLogout(boolean val){
        logout = val;
    }

    public boolean isLogin(){
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }
}
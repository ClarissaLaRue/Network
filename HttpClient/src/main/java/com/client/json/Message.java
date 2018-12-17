package com.client.json;

public class Message {
    private String message;
    private int id;
    private String sender;

    public Message(String message){
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }
}

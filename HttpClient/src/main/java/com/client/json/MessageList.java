package com.client.json;

import java.util.List;

public class MessageList {
    private List<Message> messages;
    private int offset;
    private int num;

    public MessageList(){
        offset = 0;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public int getOffset() {
        return offset;
    }

    public int getNum() {
        return num;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setNum(int num) {
        this.num = num;
    }
}

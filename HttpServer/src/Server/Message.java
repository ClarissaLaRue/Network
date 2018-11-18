package Server;

public class Message {
    private static long lastID = 0;
    private long id;
    private String data;
    private long senderID;

    public Message(String data, long senderID){
        lastID++;
        id = lastID;
        this.data = data;
        this.senderID = senderID;
    }

    public long getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public long getSenderID() {
        return senderID;
    }
}

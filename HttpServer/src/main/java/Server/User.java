package Server;

import java.net.InetSocketAddress;
import java.security.SecureRandom;

public class User {
    private static long lastID = 0;
    private long id;
    private String name;
    private String token;
    private boolean isOnline;
    private InetSocketAddress address;

    public User(String name, boolean isOnline, InetSocketAddress address){
        lastID++;
        this.id = lastID;
        this.name = name;
        this.isOnline = isOnline;
        this.address = address;
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        token = bytes.toString();
    }

    public User(String name, boolean isOnline){
        lastID++;
        this.id = lastID;
        this.name = name;
        this.isOnline = isOnline;
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        token = bytes.toString();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setOnline() { isOnline = true; }

    public void setOffline() { isOnline = false; }
}


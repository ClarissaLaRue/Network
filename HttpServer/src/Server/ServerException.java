package Server;

import java.util.HashMap;

public class ServerException extends Exception {
    private Integer code;
    private String info;
    private HashMap<Integer, String> codes = new HashMap<>() {{
        put(400, "Bad Request");
        put(401, "Unauthorized");
        put(403, "Forbidden");
        put(404, "Not Found");
        put(405, "Method Not Allowed");
        put(500, "Internal Server Error");
    }};

    public ServerException(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getTitle() {
        return code + " " + codes.get(code);
    }

    public String getInfo() {
        return "Info : \" " + info;
    }
}
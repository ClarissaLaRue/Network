package Server;

import java.util.HashMap;

public class Respones {
    private String title;
    HashMap<String, String> headers;
    String body;

    public Respones(){ }

    public void setTitle(String title){
        this.title = title;
    }

    public void setOKTitle(){
        title = "HTTP 200 OK";
    }

    public void setHeader(String str1, String str2) {
        headers.put(str1, str2);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getHeadersAsString() {
        StringBuilder res = new StringBuilder();
        headers.forEach((k, v) -> res.append(k).append(": ").append(v).append("\r\n"));
        res.append("\r\n");
        return res.toString();
    }

    public String getBody() {
        return body;
    }
}

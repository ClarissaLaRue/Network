package Server;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Request {
    private String method;
    private String path;
    private String protocol;
    private HashMap<String, String> headers;
    private byte[] body;

    public Request(){
        method = null;
        path = null;
        protocol = null;
        headers = new HashMap<>();
        body = null;
    }

    public Request(String method, String path, String protocol, HashMap<String, String> headers, byte[] body){
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getBody() { return String.valueOf(body); }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setHeaders(String headers) {
        String[] block = headers.split("\n");
        String head;
        String body;
        for (int i = 0; i < block.length; i++){
            head = block[i].substring(0, block[i].indexOf(":"));
            body = block[i].substring(block[i].indexOf(":")+1 , block[i].length());
            this.headers.put(head, body);
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setBody(InputStream in) throws IOException {
        body = in.readAllBytes();
    }

    public String getToken(){
        String auth = headers.get("Authorization");
        if (auth != null){
            return auth.replaceFirst("Token ", "");
        } else {
            return null;
        }
    }
}

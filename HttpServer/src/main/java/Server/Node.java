package Server;

import Server.Matchers.Matcher;
import Server.Handlers.Handler;

import java.util.ArrayList;

public class Node {
    private ArrayList<Node> children;
    private Matcher matcher;
    private Handler handler;
    private boolean isLeaf;

    public Node(){
        children = new ArrayList<>();
        this.matcher = null;
        this.handler = null;
        isLeaf = true;
    }

    public Node(Matcher matcher) {
        this.matcher = matcher;
        isLeaf = false;
        children = new ArrayList<>();
    }

    public Node(Matcher matcher, Handler handler){
        children = new ArrayList<>();
        this.matcher = matcher;
        this.handler = handler;
        isLeaf = true;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public Handler getHandler(){
        return handler;
    }

    public boolean haveChild(Node node) {
        for (int i = 0; i < children.size(); i++) {
            if (node.equals(children.get(i))) {
                return true;
            }
        }
        return false;
    }

    public int numberOfChild() {
        return children.size();
    }

    public Node getChild(int number) {
        if (number < numberOfChild()) {
            return children.get(number);
        }
        return null;
    }
}

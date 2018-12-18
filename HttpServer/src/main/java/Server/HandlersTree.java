package Server;

import Server.Matchers.Matcher;
import Server.Handlers.Handler;

import javax.xml.ws.spi.http.HttpHandler;
import java.util.ArrayList;

public class HandlersTree {
    private Node root;

    public HandlersTree(){
        root = new Node();
    }

    public void addBranch(ArrayList<Matcher> matchers, Handler handler){
        Node parent = root;
        Node child;
        for(int i = 0; i < matchers.size()-1; i++){
            child = new Node(matchers.get(i));
            if (!parent.haveChild(child)){
                parent.addChild(child);
            }
            parent = child;
        }
        child = new Node(matchers.get(matchers.size()-1), handler);
        parent.addChild(child);
    }

    public Handler getHandler(String method, String path){
        String[] segments =  path.split("/");
        boolean flag = true;
        Node current = root;
        Node child;
        int counterForNode=1;
        int counterForChild=0;
        while(true){
            flag = current.getMatcher().match(segments[counterForNode]);
            if (!flag){
                return null;
            }
            if (current.isLeaf()) {//Дошли листа
                if (counterForNode == (segments.length-1)){//Проверили весь путь
                    return current.getHandler();
                }else {//Дошли до листа, но путь еще не закончился
                    return null;
                }
            }
            counterForNode++;
            if (counterForNode >= segments.length){
                return null;
            }
            flag = false;
            while( (counterForChild<current.numberOfChild()) && (!flag) ){ //Проверяем детей, пока не закончатся или не найдем нужного
                flag = current.getChild(counterForChild).getMatcher().match(segments[counterForNode]);
                counterForChild++;
            }
            if ((counterForChild == current.numberOfChild()) && (!flag)){//Среди детей нет нужного
                return null;
            }
            counterForChild--;
            current = current.getChild(counterForChild);
        }
    }

    
}


import java.io.IOException;
import java.net.*;
import java.util.*;

public class Sender extends Thread {
    private static LinkedList<Message> messageSend = new LinkedList<>();
    Reciever reciever;

    public Sender (boolean isRoot) throws SocketException {
        if (!isRoot){
            //first message to perent
            addMessage("NEW CHILD", Message.NEWCHILD, Node.perent);
        }
        reciever = new Reciever(new InetSocketAddress(Node.port), Node.loss);
        reciever.start();
    }

    //add new message
    public void addMessage(String data, int code){
        synchronized (Node.o){
            messageSend.add(new Message(data, code, 0,
                    new InetSocketAddress(Node.port), UUID.randomUUID()));
        }
    }

    public void addMessage(String data, int code, InetSocketAddress reciver){
        synchronized (Node.o){
            messageSend.add(new Message(data, code, 0,
                    new InetSocketAddress(Node.port), reciver, UUID.randomUUID()));
        }
    }

    //Add answer to message
    public void makeAnswer(Message message){
        synchronized (Node.o){
            messageSend.add(new Message(message.getData(), Message.ANSWER, 0, message.getReceiver(), message.getSender(), message.getGuid()));
        }
    }

    public void checkRecvMessage(){
        Iterator<Message> iterator = messageSend.iterator();
        while (iterator.hasNext()){//delet message if have answer, make answer message
            Message mes = iterator.next();
            if (reciever.isAnswer(mes)){
                synchronized (Node.o){
                    iterator.remove();
                }
            }
        }
        LinkedList<Message> mes = reciever.getUsualMessage(); //make answer for usual of newchild message
        if (mes != null){
            for (Message message: mes) {
                makeAnswer(message);
            }
        }
    }

    private void sendMessage(Message mes) throws SocketException {
        DatagramSocket socket = new DatagramSocket();
        String packetMessage = mes.getCode() + ":" + mes.getGuid() + ":" + mes.getSender().getPort() + ":" + mes.getData();
        byte[] buf = packetMessage.getBytes();
        DatagramPacket pack = null;
        if (mes.getReceiver() == null){//if dont have receviver send to all connectors
            for (InetSocketAddress addresses: Node.connectors) {
                pack = new DatagramPacket(buf, 0, buf.length, addresses);
            }
        }else {
            if (Node.connectors.contains(mes.getReceiver())){
                pack = new DatagramPacket(buf, 0, buf.length, mes.getReceiver());
            }else {
                return;
            }
        }
        try {
            socket.send(pack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteConnector(InetSocketAddress address){
        if (Node.connectors.contains(address)){
            if (address == Node.perent){ //if it perent we became root
                Node.isRoot = true;
            }
            Node.connectors.remove(address);
        }
    }

    @Override
    public void run() {
        while (true) {
            Iterator<Message> iterator = messageSend.iterator();
            while (iterator.hasNext()){
                Message mes = iterator.next();
                if (mes.getCountOfSend() == Message.STOPSEND){//if we dont have answer 3 times delet mes and conector
                    synchronized (Node.o){
                        deleteConnector(mes.getSender());
                        iterator.remove();
                    }
                    continue;
                }
                if ((mes.getCode() == Message.USUAL) || (mes.getCode() == Message.NEWCHILD)){ //Send message
                    try {
                        sendMessage(mes);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                    mes.addSend();
                }
                if (mes.getCode() == Message.ANSWER){ //message is answer
                    try {
                        sendMessage(mes);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                    iterator.remove();
                }
            }
            checkRecvMessage();
        }
    }
}

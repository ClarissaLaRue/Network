import java.io.IOException;
import java.net.*;
import java.util.*;

public class Sender extends Thread {
    private static List<Message> messageSend = Collections.synchronizedList(new LinkedList<Message>());
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
                    new InetSocketAddress(Node.name, Node.port), UUID.randomUUID()));

        }
    }

    public void addMessage(String data, int code, InetSocketAddress reciver){
        synchronized (Node.o){
            messageSend.add(new Message(data, code, 0,
                    new InetSocketAddress(Node. name, Node.port), reciver, UUID.randomUUID()));
        }
    }

    //Add answer to message
    public void makeAnswer(Message message){
        synchronized (Node.o){
            messageSend.add(new Message(message.getData(), Message.ANSWER, 0, message.getReceiver(), message.getSender(), message.getGuid()));
        }
    }

    private void sendMessage(Message mes) throws SocketException {
        if (Node.connectors.isEmpty()){
            System.out.println("-no connectors-");
            return;
        }
        DatagramSocket socket = new DatagramSocket();
        String packetMessage = mes.getCode() + ":" + mes.getGuid() + ":" + mes.getSender().getPort() + ":" + mes.getData();
        byte[] buf = packetMessage.getBytes();
        DatagramPacket pack = null;
        if (mes.getReceiver() == null){//if dont have receviver send to all connectors
            for (InetSocketAddress addresses: Node.connectors) {
                pack = new DatagramPacket(buf, 0, buf.length, addresses);
                try {
                    socket.send(pack);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            if (Node.connectors.contains(mes.getReceiver())){
                pack = new DatagramPacket(buf, 0, buf.length, mes.getReceiver());
                try {
                    socket.send(pack);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                return;
            }
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
        Iterator<Message> iterator = null;
        while (true) {
            synchronized (messageSend) {
                iterator = messageSend.iterator();
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
                            System.out.println("Cant send message, please try again");
                        }
                        mes.addSend(); //не хорошо отравлять в синхронайз, мб копировать в другой список и из него уже отправлять
                    }
                    if (mes.getCode() == Message.ANSWER){ //message is answer
                        try {
                            sendMessage(mes);
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                        synchronized (Node.o){
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }
}

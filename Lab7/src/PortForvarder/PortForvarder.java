package PortForvarder;

import org.xbill.DNS.ARecord;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.ResolverConfig;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Map;

public class PortForvarder {
    private Map<SelectionKey, Connection> connectionClientMap = new HashMap<>();
    private Map<SelectionKey, Connection> connectionServerMap = new HashMap<>();
    private Map<Integer, Connection> connectionDnsMap = new HashMap<>();
    private DatagramChannel dnsSocketChannel;
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private int l_port;
    private SelectionKey dnsSocketKey;

    public PortForvarder(String args[]) throws IOException {

        l_port = Integer.valueOf(args[0]);

        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(l_port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        String dnsServers[] = ResolverConfig.getCurrentConfig().servers();
        dnsSocketChannel = DatagramChannel.open();
        dnsSocketChannel.configureBlocking(false);
        dnsSocketChannel.connect(new InetSocketAddress("8.8.8.8",53)); //////////////////
        dnsSocketKey = dnsSocketChannel.register(selector, SelectionKey.OP_READ);

    }

    public void start() throws IOException {

        while (true) {
            selector.select();
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isValid()) {
                    if(dnsSocketKey == key){
                        if(key.isReadable()){
                            setResolvedDns();
                        }
                    } else if (key.isAcceptable()) {
                        accept();
                    } else if (key.isConnectable()) {
                        connect(key);
                    } else if (key.isReadable()) {
                        read(key);
                    } else if (key.isWritable()) {
                        write(key);
                    }
                }
            }
        }
    }

    private void setResolvedDns() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int len = dnsSocketChannel.read(buffer);
        if (len <= 0) return;
        Message msg = new Message(buffer.array());
        Record[] recs = msg.getSectionArray(1);
        for (Record rec : recs) {
            if (rec instanceof ARecord) {
                ARecord arec = (ARecord)rec;
                String str = arec.getName().toString();
                int id = msg.getHeader().getID();
                Connection mate = connectionDnsMap.get(id);
                if (mate == null) continue;
                InetAddress adr = arec.getAddress();
                mate.onDnsResolved(adr);
                //dns_resolve.remove(id);
            }
        }
    }

    private void accept() throws IOException {
        SocketChannel clientChannel;
        if((clientChannel = serverChannel.accept()) != null) {
            clientChannel.configureBlocking(false);
            SelectionKey key = clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

            connectionClientMap.put(key, new Connection(selector, clientChannel, connectionServerMap, l_port, dnsSocketChannel, connectionDnsMap));
        }
    }

    private void connect(SelectionKey key) throws IOException {
        try {
            ((SocketChannel) key.channel()).finishConnect();
        } catch (ConnectException ex){
            connectionServerMap.get(key).sendConnectionRefused();
            return;
        }
        connectionServerMap.get(key).sendConnectionConfirmation();
    }

    private void read(SelectionKey key) throws IOException {
        int ret;
        if (connectionClientMap.containsKey(key)) {
            ret = connectionClientMap.get(key).readFromClient();
        } else if (connectionServerMap.containsKey(key)) {
            ret = connectionServerMap.get(key).readFromServer();
        } else {
            ret = 0;
        }
        if (ret != 0) {
            connectionServerMap.remove(key);
            connectionClientMap.remove(key);
        }
    }

    private void write(SelectionKey key) throws IOException {
        int ret;
        if (connectionClientMap.containsKey(key)) {
            ret = connectionClientMap.get(key).writeToClient();
        } else if (connectionServerMap.containsKey(key)) {
            ret = connectionServerMap.get(key).writeToServer();
        } else {
            ret = 0;
        }
        if (ret != 0) {
            connectionServerMap.remove(key);
            connectionClientMap.remove(key);
            key.cancel();
        }
    }
}

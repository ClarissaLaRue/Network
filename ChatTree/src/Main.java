import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) throws UnknownHostException {
        if((args.length != 3) && ((args.length != 5))){
            System.out.println("Write name, loss persent, port and optional perent port and perent IP");
            return;
        }

        try{ //parse args
            String name = args[0];
            int loss = Integer.parseInt(args[1]);
            int port = Integer.parseInt(args[2]);
            boolean isRoot = true;
            int perentPort = -1;
            InetAddress perentAdress = null;
            if (args.length > 3){
                perentPort = Integer.parseInt(args[3]);
                perentAdress= InetAddress.getByName(args[4]);
                isRoot = false;
            }
            Node MyNode = new Node(isRoot, loss, port, perentPort, perentAdress);
        }catch (NumberFormatException | UnknownHostException e){
            System.out.println("Incorrect arguments");
            return;
        } catch (SocketException e) {
            e.printStackTrace();
        }


    }
}

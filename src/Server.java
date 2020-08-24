import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.net.*;

public class Server {
    static ArrayList<ClientHandler> ClientVec = new ArrayList<ClientHandler>();
    static int nClients = 0;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9999);
        System.out.println("Waiting bitch");
        while(true){
            Socket socketServer = server.accept();
            System.out.println("Start Communication");
            System.out.println("The client port number is : " + socketServer.getPort());
            System.out.println("Waiting for the ID of the Client");
            DataInputStream ServerInput = new DataInputStream(socketServer.getInputStream());
            DataOutputStream ServerOutput = new DataOutputStream(socketServer.getOutputStream());
            String id = ServerInput.readUTF();
            System.out.println("Client ID : " + id);
            ClientHandler ClientsHand = new ClientHandler(socketServer, id, ServerInput, ServerOutput);
            Thread thread = new Thread(ClientsHand);
            ClientVec.add(ClientsHand);
            thread.start();
            nClients++;
        }
    }
}
class ClientHandler implements Runnable{
    static ArrayList<ClientHandler> newClientVec = new ArrayList<>();
    final DataOutputStream out;
    final DataInputStream in;
    public String ID;
    Socket s;
    public ClientHandler(Socket s, String ID, DataInputStream input, DataOutputStream output) {
        this.ID = ID;
        this.s = s;
        this.in = input;
        this.out = output;
    }

    public void run(){
        try
        {
            while (true) {
                System.out.println("The Client Sent this Text to share..");

                // receive "share"
                String received = in.readUTF();

                if (received.equals("Share")) {
                    // receive the IDs
                    received = in.readUTF();
                    newClientVec.clear();

                    String[] IDs = received.split("\\-");
                    for (String s : IDs) {
                        for (ClientHandler mc : Server.ClientVec) {
                            if (s.equals(mc.ID) && !mc.ID.equals(this.ID)) {
                                newClientVec.add(mc);
                                break;
                            }
                        }
                        // when the the other clients want to edit show this at the parent
                        newClientVec.add(this);
                    }
                }



                received = in.readUTF();
                for (ClientHandler mc : newClientVec){
                    if (!ID.equals(mc.ID)) {
                        mc.out.writeUTF(received);
                        mc.out.flush();
                    }
                }
            }


        }
        catch (IOException e)
        {
            e.printStackTrace();
            e.getMessage();
        }

    }
}
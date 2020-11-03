import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private final Vector<ClientHandler> clients;

    public Server() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try {

            AuthService.connect();
            server = new ServerSocket(8189);
            System.out.println("Server started");

            while (true) {
                socket = server.accept();
                System.out.println("Client accepted");
                subscribe(new ClientHandler(this, socket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    public boolean checkNick(String nick) {
        if (clients.isEmpty()) {
            for (ClientHandler client: clients) {
                if (client.getNick().equals(nick)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void broadcastMsg(String msg) {
        for (ClientHandler client: clients) {
            client.sendMsg(msg);
        }
    }

    public void privateMsg(String nick, String msg){
        for (ClientHandler client: clients) {
            if (client.getNick().equals(nick)) {
                client.sendMsg(msg);
                break;
            }
        }
    }
}

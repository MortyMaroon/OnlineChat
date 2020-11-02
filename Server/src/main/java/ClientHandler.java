import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    public String getNick() {
        return nick;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    authorization();
                    readMsg();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authorization() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/auth")) {
                String[] parts = str.split(" ");
                String newNick = AuthService.getNickByLoginAndPass(parts[1], parts[2]);
                if (newNick != null) {
                    if (server.checkNick(newNick)){
                        sendMsg("/authok");
                        nick = newNick;
                        server.subscribe(this);
                        return;
                    } else {
                        sendMsg("/busy");
                    }
                } else {
                    sendMsg("Неверный логин/пароль");
                }
            }
        }
    }

    public void closeConnection() {
        server.unsubscribe(this);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMsg() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/")) {
                if (str.equals("/end")) {
                    out.writeUTF("/serverClosed");
                    break;
                }
                if (str.startsWith("/w")) {
                    String[] parts = str.split(" ");
                    if (!server.checkNick(parts[1])) {
                        server.privateMsg(parts[1], "From " + nick + ": " + parts[2]);
                    } else {
                        sendMsg("Такого пользователя не существует");
                    }
                }
            } else {
                server.broadcastMsg(nick + ": " + str);
            }
        }
    }

    public void sendMsg(String str) {
        try {
            out.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

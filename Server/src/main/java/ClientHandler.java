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
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    signIn();
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

    public void signIn() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/auth"))
                if (authorization(str)) return;
            if (str.startsWith("/reg"))
                if (registration(str)) return;
        }
    }

    private boolean checkLogin(String str) {
        return AuthService.checkLogin(str) != null;
    }

    private boolean checkNick(String str) {
        return AuthService.checkNickName(str) != null;
    }

    private boolean authorization(String str) {
        String[] parts = str.split(" ");
        String Nick = AuthService.getNickByLoginAndPass(parts[1], parts[2]);
        if (Nick != null) {
            if (server.checkNick(Nick)){
                sendMsg("/authOk");
                this.nick = Nick;
                server.subscribe(this);
                return true;
            } else {
                sendMsg("/busy");
            }
        } else {
            sendMsg("/noSuch");
        }
        return false;
    }

    private boolean registration(String str) {
        String[] parts = str.split(" ");
        if (!checkLogin(parts[5])) {
            sendMsg("/loginNO");
            return false;
        } else if (!checkNick(parts[3])) {
            sendMsg("/nickNO");
            return false;
        } else {
            String Nick = AuthService.tryRegister(parts[1], parts[2], parts[3], parts[4], parts[5]);
            sendMsg("/authOk");
            this.nick = Nick;
            server.subscribe(this);
            return true;
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
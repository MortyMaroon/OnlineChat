import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ClientController {
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    final String IP = "localhost";
    final int PORT = 8189;

    @FXML
    private TextField lblLogin;

    @FXML
    private PasswordField lblPass;

    @FXML
    private Label loginMessageLable;

    private void connect() {
        try {
            socket = new Socket(IP, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    authorization();
                    read();
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

    private void authorization() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/authok")) {
                System.exit(0);
                break;
            } else if (str.startsWith("/busy")) {
                loginMessageLable.setText("This user is online.");
            } else {
                loginMessageLable.setText("Invalid Login. Please try again.");
            }
        }
    }

    private void read() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.equals("/serverClosed")) break;
        }
    }

    @FXML
    public void signIN() {
        if (!lblLogin.getText().isEmpty() && !lblPass.getText().isEmpty()) {
            if (socket == null || socket.isClosed()) {
                connect();
            }
            try {
                out.writeUTF("/auth" + lblLogin.getText() + " " + lblPass.getText());
                lblLogin.clear();
                lblPass.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loginMessageLable.setText("Please enter login and password.");
        }

    }

    public void closeConnection() {
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

    @FXML
    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }
}

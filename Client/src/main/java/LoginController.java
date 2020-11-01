import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField lblLogin;
    @FXML
    private TextField lblPass;
    @FXML
    private Button btnSignUp;
    @FXML
    private Button btnSignIn;
    @FXML
    private Button btnExit;


    @FXML
    private void handleMouseClick(MouseEvent event) {

    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {

    }

}

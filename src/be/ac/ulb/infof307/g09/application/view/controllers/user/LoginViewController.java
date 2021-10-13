package be.ac.ulb.infof307.g09.application.view.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Controller of login view.
 */
public class LoginViewController {

  private Listener listener;
  private static final int MIN_STAGE_WIDTH = 600;
  private static final int MIN_STAGE_HEIGHT = 450;
  @FXML
  private TextField passwordField;
  @FXML
  private TextField usernameField;
  @FXML
  private Label errorMsg;

  /**
   * Called once all the fxml files are loaded. The user can connect itself by pressing ENTER.
   */
  @FXML
  private void initialize() {
    passwordField.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ENTER) {
        buttonActionConnexion();
      }
    });
  }

  /**
   * Sets the min size of the stage.
   *
   * @param stage the stage in which the min size is changed
   */
  public void setMinStageSize(Stage stage) {
    stage.setMinWidth(MIN_STAGE_WIDTH);
    stage.setMinHeight(MIN_STAGE_HEIGHT);
  }


  /**
   * The register button event, shows the register scene once the register button is pressed.
   */
  @FXML
  public void buttonActionRegister() {
    listener.showRegister();
  }

  /**
   * The connection button action, once it is pressed it connects the user.
   */
  @FXML
  public void buttonActionConnexion() {
    listener.connectUser(usernameField.getText(), passwordField.getText());
  }

  /**
   * Sets the listener.
   *
   * @param listener the listener to set
   */
  public void setListener(Listener listener) {
    this.listener = listener;
  }

  /**
   * Show error messages.
   *
   * @param error the error description
   */
  public void setErrorLabelText(String error) {
    errorMsg.setText(error);
    errorMsg.setTextFill(Color.web("#ff0000", 0.8));
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * Change to register Scene.
     */
    void showRegister();

    /**
     * Connect the user and change to home Scene.
     *
     * @param username the username
     * @param password the password
     */
    void connectUser(String username, String password);
  }
}

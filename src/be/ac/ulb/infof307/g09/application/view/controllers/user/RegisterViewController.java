package be.ac.ulb.infof307.g09.application.view.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * The Class that controls the Registering panel where the user can create a new account.
 */
public class RegisterViewController {

  private Listener listener;

  @FXML
  private TextField lastNameField;
  @FXML
  private TextField firstNameField;
  @FXML
  private TextField usernameField;
  @FXML
  private TextField mailField;
  @FXML
  private TextField passwordField;
  @FXML
  private TextField passwordConfirmField;
  @FXML
  private Label errorLabelRegister;
  @FXML
  private CheckBox conditionsAccepted;
  @FXML
  private Button confirmButton;


  /**
   * Takes the user back to the previous scene.
   */
  @FXML
  public void buttonActionCancel() {
    listener.goBack();
  }


  /**
   * Verifies the following conditions before registering
   * the user: no empty fields, correct format of email,
   * username and email are new.
   */
  @FXML
  public void buttonActionConfirm() {
    String username = usernameField.getText();
    String firstName = firstNameField.getText();
    String lastName = lastNameField.getText();
    String email = mailField.getText();
    String password = passwordField.getText();
    String passwordConfirm = passwordConfirmField.getText();

    listener.registerUser(username, firstName, lastName, email, password, passwordConfirm);
  }

  /**
   * Displays the Terms of use upon clicking the hyperlink.
   *
   * @author Alexios Konstantopoulos
   */
  @FXML
  public void showTermsOfUse() {
    listener.showTermsOfUse();
  }

  /**
   * Ensures that the confirm button stays disabled
   * as long as the user doesn't agree with terms of use.
   *
   * @author Ali Manzer
   * @author Alexios Konstantopoulos
   */
  @FXML
  public void checkConditionsAccepted() {
    confirmButton.setDisable(!conditionsAccepted.isSelected());
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
    errorLabelRegister.setText(error);
    errorLabelRegister.setTextFill(Color.web("#ff0000", 0.8));
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * Go back to the old Scene.
     */
    void goBack();

    /**
     * Tries to register a new user.
     *
     * @param username        the username
     * @param firstName       the first name
     * @param lastName        the last name
     * @param email           the email
     * @param password        the password
     * @param passwordConfirm the password confirmation
     */
    void registerUser(String username, String firstName, String lastName,
                      String email, String password, String passwordConfirm);

    /**
     * Show the terms of use in a new popup window.
     */
    void showTermsOfUse();
  }

}
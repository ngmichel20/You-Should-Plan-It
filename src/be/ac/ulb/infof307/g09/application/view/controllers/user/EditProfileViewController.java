package be.ac.ulb.infof307.g09.application.view.controllers.user;

import be.ac.ulb.infof307.g09.application.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * EditProfileViewController class.
 *
 * @author Nguyen Khanh-Michel
 * @author Haal Benoît.
 */
public class EditProfileViewController {
  private static final int INVALID_USER_ID = 0;

  private Listener listener;
  @FXML
  private TextField lastNameTextField;
  @FXML
  private TextField firstNameTextField;
  @FXML
  private TextField usernameTextField;
  @FXML
  private TextField emailTextField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private PasswordField passwordFieldConfirm;
  @FXML
  private Label errorLabel;


  /**
   * This setter method allows to set the textFields by the profile's data.
   *
   * @param lastName  the last name of the user.
   * @param firstName the first name of the user.
   * @param userName  the userName of the user.
   * @param email     the email address of the user.
   * @param password  the password of the user.
   */
  @FXML
  public void setTextFields(String userName, String email, String lastName, String firstName,
                            String password) {
    this.usernameTextField.setText(userName);
    this.emailTextField.setText(email);
    this.firstNameTextField.setText(firstName);
    this.lastNameTextField.setText(lastName);
    this.passwordField.setText(password);
  }

  /**
   * Update user input once he pressed the validate button.
   */
  @FXML
  private void validateButtonPressed() {
    this.listener.updateUser(new User(INVALID_USER_ID,
            this.usernameTextField.getText(),
            this.emailTextField.getText(),
            this.lastNameTextField.getText(),
            this.firstNameTextField.getText(),
            this.passwordField.getText()),
        this.passwordFieldConfirm.getText());
  }

  /**
   * Confirmation dialog for user's inputs.
   *
   * @return true if user confirms changes, false otherwise
   */
  public boolean userConfirmation() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
    alert.setTitle("Confirmation");
    alert.setHeaderText("Êtes-vous sûr de vos modifications ?");
    alert.setContentText("Veuillez vérifier vos informations avant de confirmer.");

    alert.showAndWait();

    return (alert.getResult() == ButtonType.YES);
  }


  /**
   * Sets the listener.
   *
   * @param listener the listener to set
   */
  public void setListener(EditProfileViewController.Listener listener) {
    this.listener = listener;
  }

  /**
   * Show error messages.
   *
   * @param error the error description
   */
  public void setErrorLabelText(String error) {
    errorLabel.setText(error);
    errorLabel.setTextFill(Color.web("#ff0000", 0.8));
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * This setter method allows to set the textFields by the profile's data.
     *
     * @param user         the user to update
     * @param passwordConf the password confirmation.
     */
    void updateUser(User user, String passwordConf);
  }

}

package be.ac.ulb.infof307.g09.application.view.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;


/**
 * ProfileViewController class that controls the view of a Profile and handles all actions
 * when a button is pressed (logout and edit button).
 */
public class ProfileViewController {

  private static final String CONFIRMATION_TITLE = "Confirmation";
  private static final String CONFIRMATION_MESSAGE = "Êtes-vous sûr de vouloir vous déconnecter ?";

  private Listener listener;

  @FXML
  private Label lastNameLabel;
  @FXML
  private Label firstNameLabel;
  @FXML
  private Label usernameLabel;
  @FXML
  private Label emailLabel;


  /**
   * Set user information labels in view.
   *
   * @param username  the username
   * @param email     the email
   * @param lastName  the lastname
   * @param firstName the firstname
   */
  public void fillOutProfile(String username, String email, String lastName, String firstName) {
    this.usernameLabel.setText(username);
    this.emailLabel.setText(email);
    this.lastNameLabel.setText(lastName);
    this.firstNameLabel.setText(firstName);
  }

  /**
   * The edit profile button pressed event.
   */
  @FXML
  private void editProfileButtonPress() {
    listener.showEditProfile();
  }

  /**
   * The logout button pressed event.
   */
  @FXML
  private void logoutButtonPressed() {
    listener.disconnectUser();
  }

  /**
   * Confirmation dialog for user log out.
   *
   * @return true if user confirms, false otherwise
   */
  public boolean userLogoutConfirmation() {

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
    alert.setTitle(CONFIRMATION_TITLE);
    alert.setHeaderText(CONFIRMATION_MESSAGE);
    alert.showAndWait();

    return alert.getResult() == ButtonType.YES;
  }


  /**
   * Sets the listener.
   *
   * @param listener the listener to set
   */
  public void setListener(ProfileViewController.Listener listener) {
    this.listener = listener;
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * Show the Edit Profile Scene.
     */
    void showEditProfile();

    /**
     * Disconnect the user.
     */
    void disconnectUser();
  }
}
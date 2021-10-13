package be.ac.ulb.infof307.g09.application.controllers.user;

import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.user.EditProfileViewController;
import be.ac.ulb.infof307.g09.application.view.controllers.user.ProfileViewController;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The profile controller class handle all the data of the user's profile (username,
 * firstname, lastname, email, password). It is possible to edit the profile with
 * the EditProfileController class once the user wants to change his data profile.
 */
public class ProfileController
    implements ProfileViewController.Listener, EditProfileViewController.Listener {

  private final Stage stage;
  private final Application application;
  private Stage editProfileStage;
  private ProfileViewController profileViewController;
  private EditProfileViewController editProfileViewController;

  private static final String EMPTY_FIELDS = "Vous devez compléter tous les champs !";
  private static final String USERNAME_TAKEN = "Ce username existe déjà";
  private static final String INVALID_EMAIL = "Veuillez entrer une adresse email valide";
  private static final String PASSWORD_ERROR = "Les deux mots de passe ne correspondent pas";

  /**
   * Profile controller constructor.
   *
   * @param stage       the Stage for the new Scene
   * @param application the application model
   */
  public ProfileController(Stage stage, Application application) {
    this.stage = stage;
    this.application = application;
  }

  /**
   * Show the Profile Scene.
   *
   * @return the view to show, if there is no view then return null
   */
  public Parent getView() {
    Parent view = null;
    try {
      FXMLLoader loader =
          new FXMLLoader(ProfileViewController.class.getResource("/views/user/Profile.fxml"));
      loader.load();

      initProfileViewController(loader);
      view = loader.getRoot();
    } catch (IOException e) {

      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }
    return view;
  }

  @Override
  public void showEditProfile() {
    try {
      FXMLLoader loader =
          new FXMLLoader(
              EditProfileViewController.class.getResource("/views/user/EditProfile.fxml"));
      loader.load();

      initEditProfileController(loader);
      Parent root = loader.getRoot();
      this.editProfileStage = new Stage();
      this.editProfileStage.setScene(new Scene(root));
      this.editProfileStage.setTitle("Profil");
      this.editProfileStage.initModality(Modality.APPLICATION_MODAL);
      this.editProfileStage.setMinWidth(stage.getMinWidth());
      this.editProfileStage.setMinHeight(stage.getMinHeight());
      this.editProfileStage.show();
    } catch (IOException e) {

      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }
  }

  /**
   * Initialises the profile view controller by getting the controller, setting the listener
   * and filling out the profile.
   *
   * @param loader the fxml loader of the profile
   */
  private void initProfileViewController(FXMLLoader loader) {
    profileViewController = loader.getController();
    profileViewController.setListener(this);
    profileViewController.fillOutProfile(
        application.getUsername(),
        application.getEmail(),
        application.getLastName(),
        application.getFirstName());
  }

  /**
   * Initialises the edit profile view controller by getting the controller, setting the listener
   * and filling out the profile.
   *
   * @param loader the fxml loader of the profile
   */
  private void initEditProfileController(FXMLLoader loader) {
    editProfileViewController = loader.getController();
    editProfileViewController.setListener(this);
    editProfileViewController.setTextFields(
        application.getUsername(),
        application.getEmail(),
        application.getLastName(),
        application.getFirstName(),
        application.getPassword());
  }

  @Override
  public void disconnectUser() {
    if (profileViewController.userLogoutConfirmation()) {
      application.disconnect();
      stage.close();
      HomeController.resetViewController();
      LoginController login = new LoginController(stage);
      login.show();
    }
  }

  @Override
  public void updateUser(User user, String passwordConfirm) {
    try {
      boolean fieldsEmpty = user.isUserInvalid(passwordConfirm);

      if (fieldsEmpty) {
        this.editProfileViewController.setErrorLabelText(EMPTY_FIELDS);
      } else if (!application.getUsername().equals(user.getUsername())
          && application.checkIfUsernameExists(user.getUsername())) {
        this.editProfileViewController.setErrorLabelText(USERNAME_TAKEN);
      } else if (user.isMailInvalid()) {
        this.editProfileViewController.setErrorLabelText(INVALID_EMAIL);
      } else if (!(user.getPassword().equals(passwordConfirm))) {
        this.editProfileViewController.setErrorLabelText(PASSWORD_ERROR);
      } else {
        if (editProfileViewController.userConfirmation()) {
          application.updateUser(user);
          this.profileViewController.fillOutProfile(user.getUsername(), user.getEmail(),
              user.getLastName(), user.getFirstName());
          this.editProfileStage.close();
        }
      }
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
  }
}

package be.ac.ulb.infof307.g09.application.controllers.user;

import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.user.RegisterViewController;
import be.ac.ulb.infof307.g09.application.view.controllers.user.TermsOfUseViewController;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Register controller handles the register scene with all the fields that
 * the new user has to field his new profile data.
 */
public class RegisterController
    implements RegisterViewController.Listener, TermsOfUseViewController.Listener {

  private static final int NONEXISTENT_USER = -1;
  private final Stage stage;
  private Stage termsOfUseStage;
  private Scene oldScene;
  private String oldTitle;
  private final Application application;

  private static final String EMAIL_TAKEN =
      "Cette adresse email est déjà attachée à un utilisateur";

  private static final String USERNAME_TAKEN = "Ce nom de utilisateur est utilisé";
  private static final String EMPTY_FIELDS = "Vous devez compléter tous les champs !";
  private static final String INVALID_EMAIL = "Le mail n'est pas valide !";
  private static final String PASSWORD_ERROR = "Les champs mot de passe doivent être identique";

  private RegisterViewController viewController;

  /**
   * RegisterController constructor.
   *
   * @param stage       current stage
   * @param application the application model
   */
  public RegisterController(Stage stage, Application application) {
    this.stage = stage;
    this.application = application;
  }

  /**
   * Show register Scene.
   */
  public void show() {
    oldScene = stage.getScene();
    oldTitle = stage.getTitle();
    try {
      FXMLLoader loader =
          new FXMLLoader(RegisterViewController.class.getResource("/views/user/Register.fxml"));
      loader.load();

      viewController = loader.getController();
      viewController.setListener(this);

      Parent root = loader.getRoot();
      stage.setScene(new Scene(root));
      stage.setTitle("Inscription");
      stage.show();
    } catch (IOException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }
  }

  @Override
  public void registerUser(String username, String firstName, String lastName,
                           String email, String password, String passwordConfirm) {
    try {
      if (isInputValid(username, firstName, lastName, email, password, passwordConfirm)) {
        if (application.checkIfEmailExists(email)) {
          viewController.setErrorLabelText(EMAIL_TAKEN);
        } else if (application.checkIfUsernameExists(username)) {
          viewController.setErrorLabelText(USERNAME_TAKEN);
        } else {
          application.register(new User(
              NONEXISTENT_USER, username, email, lastName, firstName, password));
          if (application.getUser() != null) {
            HomeController home = new HomeController(this.stage, this.application);
            home.show();
          }
        }
      }
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
  }

  @Override
  public void showTermsOfUse() {
    InputStream inputStream = getClass().getResourceAsStream("/termsOfUse/termsOfUse.txt");
    try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
      StringBuilder termsOfUse = new StringBuilder();
      while (in.ready()) {
        termsOfUse.append(in.readLine());
      }

      FXMLLoader loader =
          new FXMLLoader(TermsOfUseViewController.class.getResource("/views/user/TermsOfUse.fxml"));
      loader.load();

      TermsOfUseViewController controller = loader.getController();
      controller.setTermsOfUseText(termsOfUse.toString());
      controller.setListener(this);

      Parent root = loader.getRoot();
      termsOfUseStage = new Stage();
      termsOfUseStage.setScene(new Scene(root));
      termsOfUseStage.setTitle("Termes de utilisation");
      termsOfUseStage.initModality(Modality.APPLICATION_MODAL);
      termsOfUseStage.show();
    } catch (IOException e) {

      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }

  }

  /**
   * Check if the users inputs are valid.
   *
   * @param username        the username
   * @param firstName       the first name
   * @param lastName        the last name
   * @param email           the email
   * @param password        the password
   * @param passwordConfirm the password confirmation
   * @return true if the users input are valid, false otherwise
   */
  private boolean isInputValid(String username, String firstName, String lastName,
                               String email, String password, String passwordConfirm) {
    User newUser = new User(NONEXISTENT_USER, username, email, lastName, firstName, password);
    boolean fieldsEmpty = newUser.isUserInvalid(passwordConfirm);
    boolean res = false;
    if (fieldsEmpty) {
      viewController.setErrorLabelText(EMPTY_FIELDS);
    } else if (newUser.isMailInvalid()) {
      viewController.setErrorLabelText(INVALID_EMAIL);
    } else if (!password.equals(passwordConfirm)) {
      viewController.setErrorLabelText(PASSWORD_ERROR);
    } else {
      res = true;
    }

    return res;
  }

  @Override
  public void goBack() {
    stage.setScene(oldScene);
    stage.setTitle(oldTitle);
  }

  @Override
  public void closeTermsOfUse() {
    termsOfUseStage.close();
  }
}

package be.ac.ulb.infof307.g09;

import be.ac.ulb.infof307.g09.application.controllers.user.LoginController;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.database.UserDatabase;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * The class Main from which we can launch our program.
 */
public class Main extends Application {

  private static final String START_PATH = "src/be/ac/ulb/infof307/g09/";

  /**
   * The application's second entry point.
   *
   * @param args an array of command-line arguments for the application
   */
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      if (!UserDatabase.getInstance().checkIfDatabaseExists()) {
        UserDatabase.createNewDatabaseFile(START_PATH);
      }
    } catch (DatabaseException | ConnectionFailedException e) {
      displayErrorAlert();
    }
    LoginController login = new LoginController(primaryStage);
    login.show();
  }

  /**
   * Display an error alert with the specified message,
   * title and header text.
   */
  public void displayErrorAlert() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Erreur");
    alert.setHeaderText(ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    alert.setContentText(ErrorMessagesUtils.ERROR_MESSAGE);

    alert.showAndWait();
  }
}

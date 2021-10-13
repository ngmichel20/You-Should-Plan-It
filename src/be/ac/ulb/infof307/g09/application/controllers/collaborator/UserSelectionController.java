package be.ac.ulb.infof307.g09.application.controllers.collaborator;

import be.ac.ulb.infof307.g09.application.controllers.user.HomeController;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.collaborator.UserSelectionViewController;
import java.io.IOException;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Handles the user assignations to a task. Users can be selected on the interface.
 */
public class UserSelectionController implements UserSelectionViewController.Listener {

  private Stage stage;
  private Map<User, Boolean> userSelectionMap;
  private static final int MIN_STAGE_WIDTH = 600;
  private static final int MIN_STAGE_HEIGHT = 450;
  private boolean isConfirmButtonClicked;

  /**
   * Initializes a new user selection controller with the specified task.
   *
   * @param userSelection the map containing the user and a boolean that
   *                      determines if a user is assigned to the task
   */
  public UserSelectionController(Map<User, Boolean> userSelection) {

    this.userSelectionMap = userSelection;
    this.isConfirmButtonClicked = false;
  }

  /**
   * Shows the user selection dialog and wait for the user to click on one of the buttons.
   *
   * @param parentStage the parent stage
   * @return true if the user has clicked on the confirm button, false otherwise
   */
  public boolean showAndWaitForConfirmation(Stage parentStage) {
    try {
      FXMLLoader loader = new FXMLLoader(
          UserSelectionController.class.getResource("/views/collaborator/UserSelection.fxml"));
      loader.load();

      UserSelectionViewController viewController = loader.getController();
      viewController.setListener(this);
      viewController.initListView(userSelectionMap);

      stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.initOwner(parentStage);
      stage.setScene(new Scene(loader.getRoot()));
      this.stage.setMinHeight(MIN_STAGE_HEIGHT);
      this.stage.setMinWidth(MIN_STAGE_WIDTH);
      stage.setTitle("Sélection des utilisateurs");
      stage.showAndWait();
    } catch (IOException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }
    return isConfirmButtonClicked;
  }

  @Override
  public void confirmSelection(Map<User, Boolean> userSelection) {
    this.userSelectionMap = userSelection;
    this.isConfirmButtonClicked = true;
    stage.close();
  }

  /**
   * Returns the user selection map containing the users and their corresponding boolean value
   * determining whether the user was selected.
   *
   * @return map containing the users and whether they where selected
   */
  public Map<User, Boolean> getUserSelectionMap() {
    return userSelectionMap;
  }

  @Override
  public void cancel() {
    stage.close();
  }
}

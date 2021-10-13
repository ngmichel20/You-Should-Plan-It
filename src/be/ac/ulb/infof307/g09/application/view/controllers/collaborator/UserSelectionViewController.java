package be.ac.ulb.infof307.g09.application.view.controllers.collaborator;

import be.ac.ulb.infof307.g09.application.models.User;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

/**
 * View for the selection of users.
 */
public class UserSelectionViewController {

  private Listener listener;
  private final Map<User, ObservableValue<Boolean>> collaboratorSelectionMap = new HashMap<>();

  @FXML
  private ListView<User> collaboratorListView;

  /**
   * Initialise the ListView with users and checkboxes in it.
   *
   * @param userSelectionMap a map containing the users to show and the current state of selection
   */
  public void initListView(Map<User, Boolean> userSelectionMap) {
    userSelectionMap.keySet().forEach(
        user -> collaboratorSelectionMap.put(
            user, new SimpleBooleanProperty(userSelectionMap.get(user))));
    collaboratorListView.getItems().addAll(userSelectionMap.keySet());
    Callback<User, ObservableValue<Boolean>> isUserSelected =
        collaboratorSelectionMap::get;
    collaboratorListView.setCellFactory(CheckBoxListCell.forListView(isUserSelected));
  }

  /**
   * Initialise the listener of this view.
   *
   * @param listener the listener to set
   */
  public void setListener(Listener listener) {
    this.listener = listener;
  }

  /**
   * Cancel the selection (= don't change anything).
   */
  @FXML
  public void cancelButtonPressed() {
    listener.cancel();
  }

  /**
   * Pass confirmed selection to the listener.
   */
  @FXML
  public void confirmationButtonPressed() {
    Map<User, Boolean> userSelectionMap = new HashMap<>();
    collaboratorSelectionMap.forEach((key, value) -> userSelectionMap.put(key, value.getValue()));

    listener.confirmSelection(userSelectionMap);
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * Confirms the selection of the checked collaborators to the task.
     *
     * @param userSelectionMap the map containing the collaborators and if the user has checked them
     */
    void confirmSelection(Map<User, Boolean> userSelectionMap);

    /**
     * Closes the stage when the user clicks on the exit button.
     */
    void cancel();
  }
}

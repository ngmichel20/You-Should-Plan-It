package be.ac.ulb.infof307.g09.application.view.controllers.collaborator;

import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;


/**
 * View for collaborator controller.
 * This is the view when the user application wants to add a collaborator
 * to his selected project.
 */
public class CollaboratorViewController {

  @FXML
  private CheckComboBox<User> collaboratorsCheckComboBox;

  @FXML
  private Text projectTitleText;

  private Listener listener;

  /**
   * Once the confirm button action is pressed, all the selected collaborators(users)
   * will be notified by a invitation from the sender user.
   *
   * @throws DatabaseException if an error occurs during the access in the database
   * @throws ConnectionFailedException If the connection to the database fails
   */
  @FXML
  private void confirmCollaborationButton() throws DatabaseException, ConnectionFailedException {
    ObservableList<User> allCollaborators =
        collaboratorsCheckComboBox.getCheckModel().getCheckedItems();
    this.listener.insertNotification(allCollaborators);
    this.listener.closeTab();
  }

  /**
   * Load all the users from the database to the UI.
   *
   * @param listOfUsers the list of the users too add in the collaboratorsCheckComboBox
   */
  public void initCollaboratorCheckComboBox(List<User> listOfUsers) {
    collaboratorsCheckComboBox.getItems().addAll(listOfUsers);
  }

  /**
   * Selects the collaborators in the collaboratorsCheckComboBox.
   *
   * @param collaboratorsToCheck the list of collaborators to check
   */
  public void selectCollaboratorCheckComboBox(List<User> collaboratorsToCheck) {
    IndexedCheckModel<User> checkModel = collaboratorsCheckComboBox.getCheckModel();
    for (User collaborator : collaboratorsToCheck) {
      checkModel.check(collaborator);
    }
  }

  /**
   * Initialise the listener of this view.
   *
   * @param listener the listener to set
   */
  public void setListener(CollaboratorViewController.Listener listener) {
    this.listener = listener;
  }

  /**
   * Set project title text.
   *
   * @param projectTitle the project title
   */
  public void setProjectTitleText(String projectTitle) {
    this.projectTitleText.setText(projectTitle);
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * Inserts the notification which is a row of project collaboration.
     *
     * @param collaborators the list of collaborators
     * @throws DatabaseException if an error occur during the fetching of the sql query
     * @throws ConnectionFailedException If the connection to the database fails
     */
    void insertNotification(ObservableList<User> collaborators)
        throws DatabaseException, ConnectionFailedException;

    /**
     * Closes the tab of adding a collaborator.
     */
    void closeTab();
  }
}

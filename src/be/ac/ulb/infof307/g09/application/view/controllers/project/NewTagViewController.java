package be.ac.ulb.infof307.g09.application.view.controllers.project;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Handler of adding a new Tag to a project.
 */
public class NewTagViewController {

  @FXML
  private TextField descriptionTextField;

  private Listener listener;

  /**
   * Clears the user input.
   */
  @FXML
  private void clearNewTagButton() {
    descriptionTextField.clear();
  }

  /**
   * Passes the description of the task to the controller.
   */
  @FXML
  private void confirmNewTagButton() {
    this.listener.confirmNewTagButton(descriptionTextField.getText());
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
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {
    /**
     * Handle the action when the confirm button is pressed in UI.
     *
     * @param description the description of the new tag to insert
     */
    void confirmNewTagButton(String description);
  }
}

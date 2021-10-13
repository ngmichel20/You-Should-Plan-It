package be.ac.ulb.infof307.g09.application.view.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;


/**
 * The Class that controls the Home panel where we can set
 * and view the different tabs that the user has access to.
 */
public class HomeViewController {
  private static final int MIN_STAGE_WIDTH = 1067;
  private static final int MIN_STAGE_HEIGHT = 580;
  @FXML
  private TabPane tabPane;
  private static final String ERROR_WINDOW_TITLE = "Erreur";
  private static final String INFORMATION_WINDOW_TITLE = "Information";


  /**
   * Sets the min size of the stage.
   *
   * @param stage the stage in which the min size is changed
   */
  public void setMinStageSize(Stage stage) {
    stage.setMinWidth(MIN_STAGE_WIDTH);
    stage.setMinHeight(MIN_STAGE_HEIGHT);
  }

  /**
   * Add a new tab.
   *
   * @param pane       pane to insert into view
   * @param title      title of the tab
   * @param isClosable true if the tab is closable
   * @return the index of the last inserted tab
   */
  public Tab addTab(Parent pane, String title, boolean isClosable) {
    Tab tab = new Tab(title);
    tab.setClosable(isClosable);
    tab.setContent(pane);
    tab.setId(this.getNumberOfTabs().toString());
    this.tabPane.getTabs().add(tab);
    return tab;
  }

  /**
   * Disable a tab.
   *
   * @param tabIndex   index of tab to disable
   * @param isDisabled true if the tab is disable
   */
  public void disableTab(int tabIndex, boolean isDisabled) {
    tabPane.getTabs().get(tabIndex).setDisable(isDisabled);
  }

  /**
   * Switch to tab at tabIndex.
   *
   * @param tabIndex the tab index to switch on it
   */
  public void switchToTab(int tabIndex) {
    tabPane.getSelectionModel().select(tabIndex);
  }

  /**
   * Get the number of tabs.
   *
   * @return the number of tabs
   */
  public Integer getNumberOfTabs() {
    return tabPane.getTabs().size();
  }

  /**
   * Close the tab.
   *
   * @param tabIndex the index of tab to remove
   */
  public void closeTab(int tabIndex) {
    this.tabPane.getTabs().remove(tabIndex);
  }

  /**
   * Display an error alert with the specified message,
   * title and header text.
   *
   * @param headerText the header text of the header window
   * @param errorMessage error message to display
   */
  public void displayErrorAlert(String headerText, String errorMessage) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    displayMessage(headerText, errorMessage, alert, ERROR_WINDOW_TITLE);
  }

  /**
   * Display an information alert with the specified message,
   * title and header text.
   *
   * @param headerText the header text of the header window
   * @param informationMessage information message to display
   */
  public void displayInformationAlert(String headerText, String informationMessage) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    displayMessage(headerText, informationMessage, alert, INFORMATION_WINDOW_TITLE);
  }


  private void displayMessage(String headerText, String errorMessage, Alert alert, String title) {
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(errorMessage);

    alert.showAndWait();
  }

}

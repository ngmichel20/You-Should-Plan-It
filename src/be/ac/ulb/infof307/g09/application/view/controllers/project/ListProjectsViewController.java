package be.ac.ulb.infof307.g09.application.view.controllers.project;

import be.ac.ulb.infof307.g09.application.controllers.user.HomeController;
import be.ac.ulb.infof307.g09.application.models.Project;
import java.io.File;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 * Class which controls the ListProject View. Displays the ListProject View in a Tab
 * in HomeView.
 */
public class ListProjectsViewController {
  private static final String ERROR_EXPORT_HEADER_TEXT = "Échec de l'exportation";
  private static final String ERROR_EXPORT_MESSAGE_TEXT = "Impossible d'exporter un sous projet";
  private static final String PROJECT_DESCRIPTION = "Description : ";
  private static final String PROJECT_TAGS = "Etiquette(s) : \n";
  private static final String PROJECT_COMPLETED_MESSAGE = "Projet Terminé";
  private static final String PROJECT_END_DATE_MESSAGE = "Le projet se termine le : ";
  private static final String PROJECT_AUTHOR = "Auteur : ";
  private static final String PROJECT_COLLABORATORS = "Collaborators : ";
  private static final String CONFIRMATION_TITLE = "Confirmation";
  private static final String CONFIRMATION_MESSAGE =
          "Êtes-vous sûr de vouloir quitter la collaboration ?";

  private Listener listener;

  @FXML
  private Text projectDescriptionText;
  @FXML
  private Text projectTagsText;
  @FXML
  private Text projectEndTimeText;
  @FXML
  private Text projectAuthorText;
  @FXML
  private Text projectCollaboratorsText;
  @FXML
  private Button projectCollaboratorsButton;
  @FXML
  private TreeView<Project> treeProjects;

  /**
   * Adds the subprojects into the tree view.
   *
   * @param treeItem The treeItem that corresponds to the parent project
   * @param project  The parent project
   */
  public void addSubProjects(TreeItem<Project> treeItem, Project project) {

    List<Project> list = project.getSubProjects();

    if (!list.isEmpty()) {
      for (Project subProject : list) {
        TreeItem<Project> tmp = new TreeItem<>(subProject);
        treeItem.getChildren().add(tmp);
        addSubProjects(tmp, subProject);
      }
    }
  }

  /**
   * Initialises the treeView with the projects that have no parent.
   *
   * @param projectsList the project list that will initialise the treeView
   */
  public void initialiseTree(List<Project> projectsList) {
    TreeItem<Project> root = new TreeItem<>();
    this.treeProjects.setRoot(root);

    for (Project project : projectsList) {
      TreeItem<Project> tmp = new TreeItem<>(project);
      this.treeProjects.getRoot().getChildren().add(tmp);
      addSubProjects(tmp, project);
    }
    this.treeProjects.setShowRoot(false);
  }

  /**
   * Opens a new tab for adding a collaborator to the selected project
   * if you are the author of the project.
   * Removes the collaboration if you are a collaborator of the project.
   */
  @FXML
  public void handleCollaborationButton() {
    TreeItem<Project> treeItem = treeProjects.getSelectionModel().getSelectedItem();
    if (treeItem == null) {
      return;
    }
    if (treeItem.getParent() == null) {
      return;
    }

    Project project  = treeItem.getValue();
    this.listener.handleCollaborations(project);
  }

  /**
   * Delete the selected project from the database and from the treeView.
   */
  @FXML
  public void deleteButtonAction() {
    TreeItem<Project> treeItem = getSelectedProject();
    if (treeItem != null) {
      this.treeProjects.getRoot().getChildren().remove(treeItem);
      this.listener.deleteProject(treeItem.getValue());
    }

  }

  /**
   * Set project tags description.
   *
   * @param description the description
   */
  public void setProjectTagsDescription(String description) {
    projectDescriptionText.setText(PROJECT_DESCRIPTION + description);
  }

  /**
   * Set project tags text.
   *
   * @param tagsToDisplay the tags to display
   */
  public void setProjectTagsText(String tagsToDisplay) {
    projectTagsText.setText(PROJECT_TAGS + tagsToDisplay);
  }

  /**
   * Sets the selected project's endTime on screen.
   *
   * @param projectEndTime the endTime of a project.
   */
  public void setProjectEndTime(String projectEndTime) {
    projectEndTimeText.setText(PROJECT_END_DATE_MESSAGE + projectEndTime);
  }

  /**
   * Sets the selected project's endTime on screen to finished.
   */
  public void setProjectFinished() {
    projectEndTimeText.setText(PROJECT_COMPLETED_MESSAGE);
  }

  /**
   * Set the project author text.
   *
   * @param author the author username
   */
  public void setProjectAuthorText(String author) {
    projectAuthorText.setText(PROJECT_AUTHOR + author);
  }

  /**
   * Set project collaborators text.
   *
   * @param collaborator the collaborator
   */
  public void setProjectCollaboratorsText(String collaborator) {
    projectCollaboratorsText.setText(PROJECT_COLLABORATORS + collaborator);
  }

  /**
   * Sets the collaboration button text.
   *
   * @param text the text
   */
  public void setProjectCollaboratorsButtonText(String text) {
    projectCollaboratorsButton.setText(text);
  }

  /**
   * Called when the user clicks the modify button.
   * Opens a new tab where the selected project can be edited.
   */
  @FXML
  public void modifyButtonAction() {
    TreeItem<Project> treeItem = getSelectedProject();
    if (treeItem != null) {
      this.listener.modifyProject(treeItem.getValue());
    }
  }

  private TreeItem<Project> getSelectedProject() {
    TreeItem<Project> treeItem = treeProjects.getSelectionModel().getSelectedItem();
    if (treeItem == null) {
      return null;
    }
    if (treeItem.getParent() == null) {
      return null;
    }
    return treeItem;
  }

  /**
   * Exports a project.
   */
  @FXML
  private void exportButtonAction() {
    TreeItem<Project> treeItem = getSelectedProject();
    if (treeItem != null) {
      if (treeItem.getValue().isSubProject()) {
        HomeController.displayErrorAlert(ERROR_EXPORT_HEADER_TEXT,
            ERROR_EXPORT_MESSAGE_TEXT);
      } else {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File selectedPath = dirChooser.showDialog(this.listener.getStage());
        if (selectedPath != null) {
          this.listener.exportProject(selectedPath, treeItem.getValue());
        }
      }
    }
  }

  /**
   * Import a project.
   */
  @FXML
  private void importButtonAction() {
    FileChooser fileChooser = new FileChooser();
    File selectedArchive = fileChooser.showOpenDialog(this.listener.getStage());
    if (selectedArchive != null) {
      this.listener.importProject(selectedArchive);
    }
  }

  /**
   * Called when the user clicks the create button.
   * Opens a new closeable tab where a new project can be created.
   */
  @FXML
  private void createButtonAction() {
    this.listener.addProject();
  }

  /**
   * Displays the project description, tags, end date, author and collaborators.
   */
  @FXML
  public void openProject() {

    TreeItem<Project> treeItem = treeProjects.getSelectionModel().getSelectedItem();

    if (treeItem == null) {
      return;
    }
    if (treeItem.getParent() == null) {
      projectDescriptionText.setText("");
      projectTagsText.setText("");
      return;
    }

    this.listener.openProject(treeItem.getValue());
  }


  /**
   * Called when the Task Button is pressed.
   */
  @FXML
  public void taskButtonEvent() {
    TreeItem<Project> treeItem = getSelectedProject();
    if (treeItem != null) {
      this.listener.displayTasks(treeItem.getValue());
    }
  }

  /**
   * Displays confirmation Pop up when a collaborator leaves a collaboration.
   *
   * @return true if the collaborator wants to leave false otherwise
   */
  public boolean displayRemoveCollaboration() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
    alert.setTitle(CONFIRMATION_TITLE);
    alert.setHeaderText(CONFIRMATION_MESSAGE);
    alert.showAndWait();

    return alert.getResult() == ButtonType.YES;
  }

  /**
   * Disables the collaboration button.
   */
  public void disableCollaborationButton() {
    this.projectCollaboratorsButton.setDisable(true);
  }

  /**
   * Enables the collaboration button.
   */
  public void enableCollaborationButton() {
    this.projectCollaboratorsButton.setDisable(false);
  }

  /**
   * Sets the listener.
   *
   * @param listener the listener to set
   */
  public void setListener(ListProjectsViewController.Listener listener) {
    this.listener = listener;
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * Delete the selected project from database and view.
     *
     * @param projectToRemove the project to remove
     */
    void deleteProject(Project projectToRemove);

    /**
     * Modify the selected project.
     *
     * @param project the project
     */
    void modifyProject(Project project);

    /**
     * Load the view for adding new project.
     */
    void addProject();

    /**
     * Displays the project Description, its Tags and if the project is still ongoing.
     *
     * @param project the project
     */
    void openProject(Project project);

    /**
     * Display the tasks list view.
     *
     * @param project the project to which the tasks are associated
     */
    void displayTasks(Project project);

    /**
     * Add a collaborator to the selected project if you are the author of the project.
     * Removes the collaboration if you are a collaborator of the project.
     *
     * @param project the project
     */
    void handleCollaborations(Project project);

    /**
     * Access to the database and retrieves all the subprojects of a given project.
     *
     * @param project the given project
     * @return The subprojects of a given project.
     */
    List<Project> getSubProjects(Project project);

    /**
     * Import a project from a .tar.gz file.
     *
     * @param archive path to .tar.gz file
     */
    void importProject(File archive);

    /**
     * Export a project to .tar.gz file.
     *
     * @param path the selected path
     * @param project project to export
     */
    void exportProject(File path, Project project);

    /**
     * Get the stage.
     *
     * @return the stage
     */
    Stage getStage();
  }
}
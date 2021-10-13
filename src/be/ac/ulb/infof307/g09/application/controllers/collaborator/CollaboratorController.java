package be.ac.ulb.infof307.g09.application.controllers.collaborator;

import be.ac.ulb.infof307.g09.application.controllers.user.HomeController;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.collaborator.CollaboratorViewController;
import be.ac.ulb.infof307.g09.application.view.controllers.project.ListProjectsViewController;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.io.IOException;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

/**
 * CollaboratorController handles the tab for adding a new collaborator
 * to the project.
 *
 * @author Nguyen Khanh-Michel
 * @author Bernard Loïc
 */
public class CollaboratorController implements CollaboratorViewController.Listener {

  private final Project project;
  private Tab collaboratorTab;
  private CollaboratorViewController viewController;
  private final Application application;


  /**
   * The constructor of CollaboratorController.
   *
   * @param projectToShare the project
   * @param application the application
   */
  public CollaboratorController(Project projectToShare, Application application) {
    this.project = projectToShare;
    this.application = application;
  }

  /**
   * Get the collaborator view.
   *
   * @return the collaborator view
   */
  public Parent getView() {
    Parent view = null;
    try {
      FXMLLoader loader = new FXMLLoader(
          ListProjectsViewController.class.getResource("/views/collaborator/Collaborator.fxml"));
      loader.load();
      viewController = loader.getController();
      viewController.setListener(this);
      initializeCollaboratorCheckComboBox();
      setProjectTitleText();
      setCollaboratorsInCheckComboBox();
      view = loader.getRoot();

    } catch (IOException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
    return view;
  }

  /**
   * Initializes the collaboratorCheckComboBox by filling it with all the users
   * available in the database.
   *
   * @throws DatabaseException throws a exception if an error occur in the UserDatabase request sql
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private void initializeCollaboratorCheckComboBox()
      throws DatabaseException, ConnectionFailedException {
    List<User> listOfAllUsers = this.application.loadUsersFromDatabase();
    listOfAllUsers.remove(project.getAuthor());
    this.viewController.initCollaboratorCheckComboBox(listOfAllUsers);
  }

  /**
   * Set the tab which contains the view.
   *
   * @param newTab the tab to set.
   */
  public void setCollaboratorTab(Tab newTab) {
    this.collaboratorTab = newTab;
    HomeController.enableTabsOnCloseRequest(this.collaboratorTab);
  }

  /**
   * Sets the collaborators that have been checked in the checkComboBox.
   *
   * @throws DatabaseException throws a exception if an error occur in the UserDatabase request sql
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void setCollaboratorsInCheckComboBox()
      throws DatabaseException, ConnectionFailedException {
    List<User> listOfCollaboratorsToCheck =
            application.getCollaboratorsInvitedByProject(this.project);
    this.viewController.selectCollaboratorCheckComboBox(listOfCollaboratorsToCheck);
  }

  /**
   * Set the projectTitleText.
   */
  private void setProjectTitleText() {
    this.viewController.setProjectTitleText(this.project.getTitle());
  }

  @Override
  public void insertNotification(ObservableList<User> collaborators)
      throws DatabaseException, ConnectionFailedException {

    for (User collaborator : collaborators) {
      Project project = this.application.getProjectByCollaborator(this.project, collaborator);
      if (project == null || project.getId() != this.project.getId()) {
        this.application.insertProjectCollaboratorRow(this.project, collaborator);
      }
    }
  }

  @Override
  public void closeTab() {
    HomeController.closeTab(Integer.parseInt(this.collaboratorTab.getId()));
    HomeController.close(this.collaboratorTab);
  }
}

package be.ac.ulb.infof307.g09.application.controllers.collaborator;

import be.ac.ulb.infof307.g09.application.controllers.user.HomeController;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.collaborator.InvitationProjectViewController;
import be.ac.ulb.infof307.g09.application.view.controllers.project.ListProjectsViewController;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * InvitationProjectController handles the window with all the invitations project
 * with the current user if he has unread notifications for a collaboration with the inviter(s).
 *
 * @author Nguyen Khanh-Michel
 * @author Bernard Loïc
 */
public class InvitationProjectController implements InvitationProjectViewController.Listener {

  private InvitationProjectViewController viewController;
  private Stage stage;
  private User collaborator;
  private final Application application;
  private static final int ACCEPTED = 1;
  private static final int MIN_STAGE_WIDTH = 600;
  private static final int MIN_STAGE_HEIGHT = 450;

  /**
   * The constructor of InvitationProjectController.
   *
   * @param stage       the stage to set
   * @param application the application
   */
  public InvitationProjectController(Stage stage, Application application) {
    this.stage = stage;
    this.application = application;
  }

  /**
   * Initializes the InvitationProject view. (to be shown later on).
   */
  public void initView() {
    Parent view;
    try {
      FXMLLoader loader = new FXMLLoader(
          ListProjectsViewController.class
              .getResource("/views/collaborator/InvitationProject.fxml"));
      loader.load();
      viewController = loader.getController();
      viewController.setListener(this);
      view = loader.getRoot();
      this.stage = new Stage();
      this.stage.setScene(new Scene(view));
      this.stage.setMinHeight(MIN_STAGE_HEIGHT);
      this.stage.setMinWidth(MIN_STAGE_WIDTH);
      this.stage.setTitle("Collaboration Projets");
      this.stage.initModality(Modality.APPLICATION_MODAL);
    } catch (IOException e) {

      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    }
  }

  /**
   * Shows the stage and wait for button confirmation.
   */
  public void showAndWait() {
    this.stage.showAndWait();
  }

  /**
   * Adds the projects invitations into the view.
   *
   * @param projectList  the list of projects invited
   * @param newCollaborator is the user application who can be potentially a collaborator.
   */
  public void addProjectsInvitations(List<Project> projectList, User newCollaborator) {
    this.collaborator = newCollaborator;
    viewController.addRowsInvitations(projectList);
  }

  @Override
  public void confirmButton(List<Integer> listOfProjectIds) {
    int index = 0;
    try {
      handleCollaboration(listOfProjectIds, index);
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
    this.stage.close();
  }

  /**
   * Handle a collaboration.
   *
   * @param listOfProjectIds the list of projects ID
   * @param index the current index
   * @throws DatabaseException If there is an error during the execution of the query
   *                           or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private void handleCollaboration(List<Integer> listOfProjectIds,
                                   int index) throws DatabaseException,
      ConnectionFailedException {
    int currentIndex = index;
    int answer;
    for (int projectId : listOfProjectIds) {
      answer = viewController.invitationAnswer(currentIndex);
      Project project =
          this.application.updateAcceptedColumn(projectId, this.collaborator, answer);
      if (answer == ACCEPTED) {
        project.addCollaborator(this.collaborator);
        application.addCollaboratorProject(project);
      }
      currentIndex++;
    }
  }
}

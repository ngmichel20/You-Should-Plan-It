package be.ac.ulb.infof307.g09.application.controllers.collaborator;

import be.ac.ulb.infof307.g09.application.controllers.user.HomeController;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.collaborator.NotificationsViewController;
import be.ac.ulb.infof307.g09.application.view.controllers.project.ListProjectsViewController;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * This is the invitationAnsweredController.
 * It handles the invitations that have been answered by the collaborators
 * that have been invited by the inviter (actual user application).
 *
 * @author Nguyen Khanh-Michel
 * @author Bernard Loïc
 */
public class NotificationsController implements NotificationsViewController.Listener {

  public static final String NOTIFICATIONS_WINDOWS_TITLE = "Notifications";
  private NotificationsViewController viewController;
  private Stage stage;
  private final Application application;
  private final List<Pair<Project, User>> listProjectUserAccepted = new ArrayList<>();
  private final List<Pair<Project, User>> listProjectUserRefused = new ArrayList<>();
  private final List<Pair<Project, User>> listProjectUserWaiting = new ArrayList<>();
  private static final int MIN_STAGE_WIDTH = 600;
  private static final int MIN_STAGE_HEIGHT = 450;

  /**
   * The constructor of InvitationProjectController.
   *
   * @param stage the stage to set
   * @param application the application model
   */
  public NotificationsController(Stage stage, Application application) {
    this.stage = stage;
    this.application = application;
  }

  /**
   * Get the InvitationProject view.
   *
   * @return the InvitationProject view
   */
  public Parent getView() {
    Parent view = null;
    try {
      FXMLLoader loader = new FXMLLoader(
          ListProjectsViewController.class
              .getResource("/views/collaborator/InvitationAnswered.fxml"));
      loader.load();

      viewController = loader.getController();
      viewController.setListener(this);

      view = loader.getRoot();
      this.stage = new Stage();
      this.stage.setScene(new Scene(view));
      this.stage.setMinHeight(MIN_STAGE_HEIGHT);
      this.stage.setMinWidth(MIN_STAGE_WIDTH);
      this.stage.setTitle(NOTIFICATIONS_WINDOWS_TITLE);
      this.stage.initModality(Modality.APPLICATION_MODAL);
      this.stage.show();
    } catch (IOException e) {

      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    }
    return view;
  }

  /**
   * Adds the projects invitations answered into the view.
   *
   * @param projectsAccepted the list of projects invited
   * @param projectsRefused the list of projects refused
   * @param projectsWaiting the list of projects that has not answered yet
   * @throws DatabaseException if an error occurs during the access in the database
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void addProjectsInvitations(List<Project> projectsAccepted,
                                     List<Project> projectsRefused,
                                     List<Project> projectsWaiting)
      throws DatabaseException, ConnectionFailedException {

    addAcceptedProjects(projectsAccepted);
    addRefusedProjects(projectsRefused);
    addWaitingProjects(projectsWaiting);

    viewController.addInvitationsAnsweredRows(this.listProjectUserAccepted,
                                              this.listProjectUserRefused,
                                              this.listProjectUserWaiting);
  }

  /**
   * Adds all the waiting projects.
   *
   * @param projectsWaiting the list of waiting projects
   * @throws DatabaseException If there is an error during the execution of the query
   *                           or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private void addWaitingProjects(List<Project> projectsWaiting)
      throws DatabaseException, ConnectionFailedException {

    for (Project projectWaiting : projectsWaiting) {
      List<User> usersWhoHaveNotAcceptedProject =
          this.application.getCollaboratorsByProjectWaiting(projectWaiting);
      for (User user : usersWhoHaveNotAcceptedProject) {
        this.listProjectUserWaiting.add(new Pair<>(projectWaiting, user));
      }
    }
  }

  /**
   * Adds all the refused projects.
   *
   * @param projectsRefused the list of refused projects
   * @throws DatabaseException If there is an error during the execution of the query
   *                           or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private void addRefusedProjects(List<Project> projectsRefused)
      throws DatabaseException, ConnectionFailedException {

    for (Project projectNotAccepted : projectsRefused) {
      List<User> usersWhoHaveNotAcceptedProject =
          this.application.getCollaboratorsByProjectRefused(projectNotAccepted);
      for (User user : usersWhoHaveNotAcceptedProject) {
        this.listProjectUserRefused.add(new Pair<>(projectNotAccepted, user));
      }
    }
  }

  /**
   * Adds all the accepted projects.
   *
   * @param projectsAccepted the list of accepted projects
   * @throws DatabaseException If there is an error during the execution of the query
   *                           or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private void addAcceptedProjects(List<Project> projectsAccepted)
      throws DatabaseException, ConnectionFailedException {
    for (Project projectAccepted : projectsAccepted) {
      List<User> usersWhoAcceptedProject =
          this.application.getCollaboratorsByProjectAccepted(projectAccepted);
      for (User user : usersWhoAcceptedProject) {
        this.listProjectUserAccepted.add(new Pair<>(projectAccepted, user));
      }
    }
  }

  /**
   * Adds the tasks close to their deadline into the view.
   *
   * @param tasksCloseToDeadline tasks close to their deadline
   */
  public void addTasksNotifications(List<Task> tasksCloseToDeadline) {
    viewController.addTasksCloseToDeadlineRows(tasksCloseToDeadline);
  }

  @Override
  public void closeButtonAction() throws DatabaseException, ConnectionFailedException {
    for (Pair<Project, User> projectCollaboratorPair : this.listProjectUserRefused) {
      Project projectToDelete = projectCollaboratorPair.getKey();
      User collaborator = projectCollaboratorPair.getValue();
      this.application.deleteRowWithRefusedInvitation(
          projectToDelete, collaborator);
    }

    for (Pair<Project, User> projectCollaboratorPair : this.listProjectUserAccepted) {
      Project projectToUpdate = projectCollaboratorPair.getKey();
      User collaborator = projectCollaboratorPair.getValue();
      this.application.updateInvitationRead(
          projectToUpdate, collaborator);
    }

    this.stage.close();

  }
}

package be.ac.ulb.infof307.g09.application.controllers.user;

import be.ac.ulb.infof307.g09.application.controllers.collaborator.InvitationProjectController;
import be.ac.ulb.infof307.g09.application.controllers.collaborator.NotificationsController;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.user.LoginViewController;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Login controller class handles the login view with a register
 * or a connection option of a user. Once the register of a user option is chosen,
 * the RegisterController class will handle it.
 * For the connection option, it will delegates the Home Scene to the HomeController class.
 */
public class LoginController implements LoginViewController.Listener {
  private static final String APPLICATION_TITLE = "I(Should)PlanAll";

  private final Stage stage;

  private LoginViewController viewController;
  private Application application;
  private static final String INVALID_CREDENTIALS = "Identifiants invalides";

  /**
   * Login controller constructor.
   *
   * @param stage the Stage for the new Scene
   */
  public LoginController(Stage stage) {
    this.stage = stage;
  }

  /**
   * Show the Login Scene.
   */
  public void show() {
    try {
      FXMLLoader loader =
          new FXMLLoader(LoginViewController.class.getResource("/views/user/Login.fxml"));
      loader.load();

      this.application = new Application();

      viewController = loader.getController();
      viewController.setListener(this);

      Parent root = loader.getRoot();
      viewController.setMinStageSize(this.stage);
      stage.setScene(new Scene(root));
      stage.setTitle(APPLICATION_TITLE);
      stage.show();

    } catch (IOException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }
  }

  @Override
  public void showRegister() {
    RegisterController register = new RegisterController(this.stage, this.application);
    register.show();
  }

  @Override
  public void connectUser(String username, String password) {
    try {
      this.application.connect(username, password);

      HomeController home = new HomeController(this.stage, this.application);
      home.show();

      User user = this.application.getUser();

      User userWithUnreadNotifications = this.application.getUserWithUnreadNotifications(user);
      openInvitationProjectView(userWithUnreadNotifications);


      List<Project> projectsWithInvitationsAccepted =
          this.application.getProjectsWithInvitationsAccepted(user);

      List<Project> projectsWithInvitationsRefused =
          this.application.getProjectsWithInvitationsRefused(user);

      List<Project> projectsInvitationsWaiting =
          this.application.getProjectsWithInvitationsWaiting(user);

      List<Task> tasksCloseToDeadline =
          this.application.getUserTasksCloseToDeadline();

      checkIfUserHasInvitationAnswered(user,
          projectsWithInvitationsAccepted,
          projectsWithInvitationsRefused,
          projectsInvitationsWaiting,
          tasksCloseToDeadline);

    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    } catch (IllegalArgumentException e) {
      viewController.setErrorLabelText(INVALID_CREDENTIALS);
    }
  }

  /**
   * Checks if the current user application has unread notifications collaboration first
   * with "checkIfUserHasUnreadNotifications" method.
   * If he has, then opens the invitation project view.
   *
   * @param userWithUnreadNotifications the user to check if he
   *                                    has unread notifications collaborations
   * @throws DatabaseException if an error occur during the fetching of the sql query
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private void openInvitationProjectView(User userWithUnreadNotifications)
          throws DatabaseException, ConnectionFailedException {
    List<Project> projectList =
        this.application.getAllUnansweredProjects(userWithUnreadNotifications);

    checkIfInvitationProjectViewCanPopUp(userWithUnreadNotifications, projectList);
  }

  /**
   * Verify if the invitation project view can pop up with
   * the list of projects invited to, in the argument.
   *
   * @param userWithUnreadNotifications the user application who has unread notifications
   * @param projectsInvitedTo the list of projects invited to
   */
  private void checkIfInvitationProjectViewCanPopUp(User userWithUnreadNotifications,
                                                    List<Project> projectsInvitedTo) {
    if (projectsInvitedTo.size() > 0) {

      InvitationProjectController invitationProjectController =
          new InvitationProjectController(this.stage, this.application);

      invitationProjectController.initView();
      invitationProjectController.addProjectsInvitations(
          projectsInvitedTo, userWithUnreadNotifications);

      invitationProjectController.showAndWait();
    }
  }

  /**
   * Check if the user application has invitations answered.
   *
   * @param userWithUnreadAnsweredInvitations the user with unread answered invitations
   * @param projectsAccepted the accepted projects
   * @param projectsRefused the refused projects
   * @param projectsWaiting the projects with no answers yet
   * @param tasksCloseToDeadline the list of task closed to the deadline
   * @throws DatabaseException if an error occur during the fetching of the sql query
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private void checkIfUserHasInvitationAnswered(User userWithUnreadAnsweredInvitations,
                                                List<Project> projectsAccepted,
                                                List<Project> projectsRefused,
                                                List<Project> projectsWaiting,
                                                List<Task> tasksCloseToDeadline)
          throws DatabaseException, ConnectionFailedException {
    NotificationsController notificationsController =
        new NotificationsController(this.stage, this.application);
    boolean hasInvitationsNotifications = userWithUnreadAnsweredInvitations != null
        && (projectsAccepted.size() > 0
        || projectsRefused.size() > 0
        || projectsWaiting.size() > 0);
    boolean hasTasksNotifications = tasksCloseToDeadline.size() > 0;

    if (hasTasksNotifications || hasInvitationsNotifications) {
      notificationsController.getView();
    }

    if (hasInvitationsNotifications) {
      notificationsController.addProjectsInvitations(projectsAccepted,
          projectsRefused, projectsWaiting);
    }

    if (hasTasksNotifications) {
      notificationsController.addTasksNotifications(tasksCloseToDeadline);
    }
  }
}

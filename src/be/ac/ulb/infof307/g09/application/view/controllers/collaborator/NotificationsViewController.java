package be.ac.ulb.infof307.g09.application.view.controllers.collaborator;

import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.utilities.DateTimeUtils;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

/**
 * This is the view for the controller of InvitationAnswered.
 * It will display when the the user application (the one who has sent invitations)
 * when his invited collaborators have answered (or not) his invitations.
 */
public class NotificationsViewController {
  private static final String INVITATIONS_HEADER_MESSAGE =
      "Voici les utilisateurs qui ont répondus à vos invitations :";

  private static final String TASKS_HEADER_MESSAGE =
      "Voici les tâches qui vont bientôt se terminer :";

  private static final String TASKS_ROW_MESSAGE = " : Date de fin : ";

  private Listener listener;
  @FXML
  private ScrollPane scrollPaneAnsweredInvitation;
  private static final String ACCEPTED_COLOR = "Green";
  private static final String REFUSED_COLOR = "Red";
  private static final String WAITING_COLOR = "Grey";
  private static final String ANSWER_FOR_ACCEPTED = " a accepté ";
  private static final String ANSWER_FOR_REFUSED = " a refusé ";
  private static final String ANSWER_FOR_WAITING = " n'a pas encore répondu à ";
  private static final String TEXT_FOR_INVITATION_PROJECT = "votre invitation sur le projet : ";

  private VBox notificationsVerticalBox;

  @FXML
  private void initialize() {
    this.notificationsVerticalBox = new VBox();
    this.notificationsVerticalBox.setSpacing(20);
    scrollPaneAnsweredInvitation.setContent(notificationsVerticalBox);
    scrollPaneAnsweredInvitation.setPadding(new Insets(10, 10, 10, 20));
  }

  /**
   * Action to execute when close button is pressed.
   *
   * @throws DatabaseException         if an error occurs during the access in the database
   * @throws ConnectionFailedException If the connection to the database fails
   */
  @FXML
  void closeButtonPressed() throws DatabaseException, ConnectionFailedException {
    this.listener.closeButtonAction();
  }

  /**
   * Initialise the listener of this view.
   *
   * @param listener the listener to set
   */
  public void setListener(NotificationsViewController.Listener listener) {
    this.listener = listener;
  }

  /**
   * Adds the rows invitations answered into the window.
   *
   * @param listOfProjectsAccepted the list of projects that have been accepted
   * @param listOfProjectsRefused  the list of projects that have been refused
   * @param listOfProjectsWaiting  the list of projects that are waiting
   */
  public void addInvitationsAnsweredRows(List<Pair<Project, User>> listOfProjectsAccepted,
                                         List<Pair<Project, User>> listOfProjectsRefused,
                                         List<Pair<Project, User>> listOfProjectsWaiting) {
    VBox invitationsVerticalBox = new VBox();

    Label invitationsHeader = new Label(INVITATIONS_HEADER_MESSAGE);
    invitationsHeader.setStyle("-fx-font-weight: bold");
    HBox rowInvitationMessage = new HBox(invitationsHeader);
    invitationsVerticalBox.getChildren().add(rowInvitationMessage);

    createInvitationsRows(listOfProjectsAccepted, invitationsVerticalBox,
        ANSWER_FOR_ACCEPTED, ACCEPTED_COLOR);

    createInvitationsRows(listOfProjectsRefused, invitationsVerticalBox,
        ANSWER_FOR_REFUSED, REFUSED_COLOR);

    createInvitationsRows(listOfProjectsWaiting, invitationsVerticalBox,
        ANSWER_FOR_WAITING, WAITING_COLOR);

    notificationsVerticalBox.getChildren().add(invitationsVerticalBox);

  }

  /**
   * Adds the rows of tasks close to deadline into the window.
   *
   * @param tasksCloseToDeadline tasks close to their deadline
   */
  public void addTasksCloseToDeadlineRows(List<Task> tasksCloseToDeadline) {
    VBox tasksVerticalBox = new VBox();

    Label tasksHeader = new Label(TASKS_HEADER_MESSAGE);
    tasksHeader.setStyle("-fx-font-weight: bold");
    HBox rowTaskMessage = new HBox(tasksHeader);
    tasksVerticalBox.getChildren().add(rowTaskMessage);

    createTaskRows(tasksCloseToDeadline, tasksVerticalBox);


    notificationsVerticalBox.getChildren().add(tasksVerticalBox);
  }


  /**
   * Creates rows for the Invitations. A row is specified by the name of a collaborator,
   * if he has answered the invitation or not, the project that he has been invited to.
   *
   * @param listOfProjects the list of projects accepted or refused or in waiting
   * @param verticalWindow the verticalWindow that contains all the rows
   * @param answer         the answer of the collaborator of the invited project
   * @param color          the color for the answer for the view
   */
  private void createInvitationsRows(List<Pair<Project, User>> listOfProjects,
                                     VBox verticalWindow, String answer, String color) {
    for (Pair<Project, User> listOfProject : listOfProjects) {

      String collaboratorUsername = listOfProject.getValue().getUsername();
      String projectTitle = listOfProject.getKey().getTitle();
      Label accepted = new Label(answer);
      accepted.setStyle("-fx-text-fill: " + color);

      Label text = new Label(TEXT_FOR_INVITATION_PROJECT);
      HBox rowInvitation = new HBox(new Label(collaboratorUsername), accepted, text,
          new Label(projectTitle));
      verticalWindow.getChildren().add(rowInvitation);
    }
  }

  /**
   * Creates the rows for the tasks.
   * A row is specified by the description of the task and its end date.
   *
   * @param tasks          the tasks close to their deadline
   * @param verticalWindow the window that contains all the rows
   */
  private void createTaskRows(List<Task> tasks, VBox verticalWindow) {
    for (Task task : tasks) {
      Label taskEndDateLabel = new Label(DateTimeUtils.formatDateToString(task.getEndDate()));
      Label messageToDisplay = new Label(TASKS_ROW_MESSAGE);

      Label taskDescription = new Label(task.getDescription());
      taskDescription.setStyle("-fx-font-weight: bold");

      HBox rowInvitation = new HBox(taskDescription, messageToDisplay, taskEndDateLabel);
      verticalWindow.getChildren().add(rowInvitation);
    }
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * The close button action of the view.
     * It will deletes the rows with all the projects that have been refused.
     * It will also update the invitation read column of the rows that have been read
     * for the first time to 1.
     *
     * @throws DatabaseException         if an error occur during the fetching of the sql query
     * @throws ConnectionFailedException If the connection to the database fails
     */
    void closeButtonAction() throws DatabaseException, ConnectionFailedException;
  }
}

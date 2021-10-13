package be.ac.ulb.infof307.g09.application.view.controllers.collaborator;

import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.User;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Controller of createTask view.
 */
public class InvitationProjectViewController {
  private static final int ACCEPTED_RESPONSE = 3;

  @FXML
  private ScrollPane scrollPaneInvitation;

  private Listener listener;
  private Label userInviterLabel;
  private Label projectTitleLabel;
  private Text invitationCollaborationText;
  private RadioButton radioButton1;
  private RadioButton radioButton2;
  private List<HBox> listOfAnswers;
  private List<Integer> listOfProjectIds;

  /**
   * Adds the rows invitations project to the UI. Every row is a project from
   * the parameter "project".
   *
   * @param projectList the list of projects invited to add in a row for every project
   */
  public void addRowsInvitations(List<Project> projectList) {
    VBox vbox = new VBox();
    listOfAnswers = new ArrayList<>();
    listOfProjectIds = new ArrayList<>();


    for (Project project : projectList) {
      initializeAttributes();
      setLabels(project.getAuthor(), project);
      HBox rowInvitation = new HBox(userInviterLabel, invitationCollaborationText,
          projectTitleLabel, radioButton1, radioButton2);
      rowInvitation.setSpacing(20);
      listOfProjectIds.add(project.getId());
      listOfAnswers.add(rowInvitation);
      vbox.getChildren().add(rowInvitation);
    }
    vbox.setSpacing(20);
    scrollPaneInvitation.setContent(vbox);
    scrollPaneInvitation.setPadding(new Insets(10, 10, 10, 20));
  }


  /**
   * The invitation with the answer.
   *
   * @param index the index for the list of answers.
   * @return the answer in an int
   */
  public int invitationAnswer(int index) {
    RadioButton answer = (RadioButton) listOfAnswers.get(index)
        .getChildren().get(ACCEPTED_RESPONSE);
    return (answer.isSelected()) ? 1 : 0;
  }

  /**
   * The confirm button action when it is pressed.
   */
  @FXML
  private void confirmButtonPressed() {
    this.listener.confirmButton(this.listOfProjectIds);
  }

  /**
   * Initializes all the attributes FX.
   */
  private void initializeAttributes() {
    userInviterLabel = new Label();
    invitationCollaborationText = new Text("vous a invité à collaborer sur le projet : ");
    projectTitleLabel = new Label();
    ToggleGroup group = new ToggleGroup();
    radioButton1 = new RadioButton("Accepter");
    radioButton2 = new RadioButton("Refuser");
    radioButton1.setToggleGroup(group);
    radioButton2.setToggleGroup(group);
    radioButton1.setSelected(true);
    userInviterLabel.setStyle("-fx-font-weight: bold");
    projectTitleLabel.setStyle("-fx-font-weight: bold");
  }

  /**
   * Sets the labels.
   *
   * @param user    the user inviter to set
   * @param project the project title to set
   */
  private void setLabels(User user, Project project) {
    this.userInviterLabel.setText(user.getUsername());
    this.projectTitleLabel.setText(project.getTitle());
  }

  /**
   * Sets the listener.
   *
   * @param listener the listener to set
   */
  public void setListener(InvitationProjectViewController.Listener listener) {
    this.listener = listener;
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * Once the confirm button is pressed, the below method will launch and
     * it will check which project has been accepted to collaborate by the current user
     * and then handles all the database (from ProjectCollaborationDatabase) changes.
     *
     * @param listOfProjectIds the list of ProjectID's
     */
    void confirmButton(List<Integer> listOfProjectIds);
  }

}

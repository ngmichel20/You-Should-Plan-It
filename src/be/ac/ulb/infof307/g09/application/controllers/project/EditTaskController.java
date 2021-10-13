package be.ac.ulb.infof307.g09.application.controllers.project;

import be.ac.ulb.infof307.g09.application.controllers.user.HomeController;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.DateTimeUtils;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.project.EditTaskViewController;
import be.ac.ulb.infof307.g09.application.view.controllers.project.ListProjectsViewController;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

/**
 * Edit Task Controller handles the control of the edit task scene.
 * It is the class where the user can edit the tasks of his project.
 */
public class EditTaskController implements EditTaskViewController.Listener {
  private EditTaskViewController viewController;
  private Tab tab;
  private Task task;
  private final Project project;
  private final Application application;
  private static final String TASK_START_DATE_HOUR_ERROR_MESSAGE =
      "La date de début de une tâche doit être "
      + "inférieure à la date de fin de la tâche!\n";
  private static final String TASK_START_DATE_ERROR_MESSAGE =
      "La date de début de une tâche doit être "
      + "supérieur ou égale à la date de début du projet!\n";
  private static final String DESCRIPTION_INVALID_ERROR_MESSAGE = "Description invalide!\n";

  private static final String TASK_END_DATE_ERROR_MESSAGE =
      "La date de fin de une tâche doit être inférieure à"
      + "la date de fin du projet!\n";


  /**
   * Constructor of the EditTaskController.
   *
   * @param project the project to which task are assigned
   * @param application the application model
   */
  public EditTaskController(Project project, Application application) {
    this.application = application;
    this.project = project;
  }

  /**
   * Get the Edit task view.
   *
   * @return the Edit task view
   */
  public Parent getView() {
    this.task = null;
    Parent view = null;
    try {
      FXMLLoader loader =
          new FXMLLoader(
              ListProjectsViewController.class.getResource("/views/project/CreateTask.fxml"));
      loader.load();

      viewController = loader.getController();
      viewController.setListener(this);
      initEditTask();

      view = loader.getRoot();
    } catch (IOException e) {

      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }
    return view;
  }

  /**
   * Set task information labels in view.
   */
  private void initEditTask() {
    viewController.fillSelectModeComboBox();
    if (this.task != null) {
      viewController.fillDescriptionField(this.task.getDescription());
    }
    viewController.setProject(this.project);
    viewController.setPromptTextHours();
    viewController.handleModeSelection();
    viewController.setEndDatePickerDefaultValues(project.getStartDate(), project.getEndDate());
    viewController.disableInvalidDates();
    viewController.handleDurationSpinner();
  }

  /**
   * Sets the tabs of the window.
   *
   * @param newTab the tab to set
   */
  public void setTab(Tab newTab) {
    this.tab = newTab;
    HomeController.enableTabsOnCloseRequest(this.tab);
  }

  /**
   * Sets the task.
   *
   * @param newTask the task to set
   */
  public void setTask(Task newTask) {
    this.task = newTask;
    viewController.setDescriptionTextField(newTask.getDescription());
    viewController.setEndDatePickerDefaultValues(newTask.getStartDate(), newTask.getEndDate());
  }

  /**
   * Check if the user's inputs are valid or not.
   *
   * @param description   the description of the task
   * @param startDateHour the start date hour of the task
   * @param endDateHour   the end date hour of the task
   * @return true if the inputs are valid, false otherwise
   */
  private boolean isInputValid(String description, Long startDateHour, Long endDateHour) {
    String errorMessage = "";
    boolean res;

    errorMessage = validateTaskDescription(description, errorMessage);
    errorMessage = validateTaskStartDate(startDateHour, errorMessage);
    errorMessage = validateTaskStartDateHour(startDateHour, endDateHour, errorMessage);
    errorMessage = validateTaskEndDate(endDateHour, errorMessage);

    if (errorMessage.isEmpty()) {
      res = true;
    } else {
      viewController.showErrorMessage(errorMessage);
      res = false;
    }
    return res;
  }

  /**
   * Validates the end date of the task. The end date of the task cannot exceed
   * the end date of the project.
   *
   * @param endDateHour  the end date and hour of the task
   * @param errorMessage the error message to whom the new error is appended
   * @return the error message
   */
  private String validateTaskEndDate(Long endDateHour, String errorMessage) {
    String newErrorMessage = errorMessage;
    if (endDateHour > this.project.getEndDate()) {
      newErrorMessage += TASK_END_DATE_ERROR_MESSAGE;
    }
    return newErrorMessage;
  }

  /**
   * Validates the start date and hour of the task. The start date of the task
   * cannot exceed the end date of the task.
   *
   * @param startDateHour the start date and hour of the task
   * @param endDateHour   the end date and hour of the task
   * @param errorMessage  the error message to whom the new error is appended
   * @return the error message
   */
  private String validateTaskStartDateHour(Long startDateHour,
                                           Long endDateHour, String errorMessage) {
    String newErrorMessage = errorMessage;
    if (startDateHour > endDateHour) {
      newErrorMessage += TASK_START_DATE_HOUR_ERROR_MESSAGE;
    }
    return newErrorMessage;
  }

  /**
   * Validates the start date of the task. The start date of the task has to exceed
   * the start date of the project.
   *
   * @param startDateHour the start date and hour of the task
   * @param errorMessage  the error message to whom the new error is appended
   * @return the error message
   */
  private String validateTaskStartDate(Long startDateHour, String errorMessage) {
    long startDate = this.project.getStartDate();

    startDate = Instant.ofEpochMilli(startDate).atZone(DateTimeUtils.ZONE_ID)
        .truncatedTo(ChronoUnit.HOURS).toInstant().toEpochMilli();

    String newErrorMessage = errorMessage;
    if (startDateHour < startDate) {
      newErrorMessage += TASK_START_DATE_ERROR_MESSAGE;
    }
    return newErrorMessage;
  }

  /**
   * Validates the description of the task.
   *
   * @param description  the description of the task
   * @param errorMessage the error message to whom the new error is appended
   * @return the error message
   */
  private String validateTaskDescription(String description, String errorMessage) {
    String newErrorMessage = errorMessage;
    if (description == null || description.length() == 0) {
      newErrorMessage += DESCRIPTION_INVALID_ERROR_MESSAGE;
    }
    return newErrorMessage;
  }

  @Override
  public void validate(String newDescription, Long startDateHour,
                       Long endDateHour) throws DatabaseException, ConnectionFailedException {
    viewController.showErrorMessage("");

    if (this.isInputValid(newDescription, startDateHour, endDateHour)) {
      if (this.task != null) {
        this.application.updateTaskInProject(
            this.project, this.task, newDescription, startDateHour, endDateHour);
      } else {
        this.application.addTaskToProject(this.project, newDescription, startDateHour,
            endDateHour);
      }

      HomeController.closeTab(Integer.parseInt(this.tab.getId()));
      HomeController.close(this.tab);
    }
  }
}

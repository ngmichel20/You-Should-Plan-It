package be.ac.ulb.infof307.g09.application.view.controllers.project;

import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Task;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.MonthView;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.controlsfx.control.CheckComboBox;



/**
 * Displays a calendar showing the tasks of the selected projects.
 */
public class CalendarViewController {

  @FXML
  private MonthView calendarView;
  @FXML
  private CheckComboBox<Project> projectCheckComboBox;
  @FXML
  private Label dateLabel;
  private CalendarViewController.Listener listener;


  /**
   * Updates the currentDateLabel to show the year and month shown on the calendar.
   */
  public void updateCurrentDateLabel() {
    String yearMonth = this.calendarView.getYearMonth().toString();
    this.dateLabel.setText(yearMonth);
  }

  /**
   * Adds the tasks of the selected projects on the calendar.
   *
   * @param projectsSelected list of projects
   */
  private void initializeCalendar(ObservableList<Project> projectsSelected) {
    CalendarSource calendarSource = new CalendarSource("I(should)PlanAll");

    for (Project project : projectsSelected) {
      Calendar taskCalendar = this.listener.getCalendar(project);
      calendarSource.getCalendars().addAll(taskCalendar);
    }

    this.calendarView.getCalendarSources().add(calendarSource);
  }

  /**
   * Clears the check combobox then fills it with user's projects.
   *
   * @param listOfProjects the list of projects to to add in the parentProjectComboBox
   */
  public void loadUserProjects(List<Project> listOfProjects) {
    projectCheckComboBox.getItems().clear();
    projectCheckComboBox.getItems().addAll(listOfProjects);
  }


  /**
   * Refreshes the project comboBox.
   */
  public void refreshProjectComboBox() {
    for (Project checkedItem : projectCheckComboBox.getCheckModel().getCheckedItems()) {
      projectCheckComboBox.getCheckModel().check(checkedItem);
    }
  }

  /**
   * Displays the next month.
   */
  @FXML
  private void showNextMonth() {
    this.calendarView.goForward();
    this.updateCurrentDateLabel();
  }

  /**
   * Displays the previous month.
   */
  @FXML
  private void showPreviousMonth() {
    this.calendarView.goBack();
    this.updateCurrentDateLabel();
  }

  /**
   * Displays current month.
   */
  @FXML
  private void todayActionButton() {
    this.calendarView.goToday();
    this.updateCurrentDateLabel();
  }

  /**
   * Updates the calendar on screen.
   */
  @FXML
  private void updateCalendar() {
    if (projectCheckComboBox.getItems() != null) {
      this.resetCalendarView();
      ObservableList<Project> projectsSelected =
          this.projectCheckComboBox.getCheckModel().getCheckedItems();
      this.initializeCalendar(projectsSelected);
    }
  }

  /**
   * Clears everything on the calendarView by removing the calendarSources.
   */
  public void resetCalendarView() {
    if (this.calendarView.getCalendarSources() != null) {
      this.calendarView.getCalendarSources().clear();
    }
  }

  /**
   * Initialise the listener of this view.
   *
   * @param listener the listener to set
   */
  public void setListener(CalendarViewController.Listener listener) {
    this.listener = listener;
  }

  /**
   * Updates the checkComboBox when notified that a new project is created
   * by adding it to the combobox.
   *
   * @param project the project to add
   */
  public void addToCheckBox(Project project) {
    projectCheckComboBox.getItems().add(project);
  }

  /**
   * Reloads the checkComboBoc when notified that a project has been deleted or modified.
   */
  public void reloadCheckCombobox() {
    this.listener.initialiseCheckComboBox();
    this.resetCalendarView();
  }

  /**
   * Adds task to calendar if its project is selected.
   *
   * @param task the new task
   */
  public void taskModified(Task task) {
    ObservableList<Project> selectedProjects =
        this.projectCheckComboBox.getCheckModel().getCheckedItems();
    int taskProjectId = task.getProjectId();
    for (Project selectedProject : selectedProjects) {
      if (selectedProject.getId() == taskProjectId) {
        this.resetCalendarView();
        this.initializeCalendar(selectedProjects);
      }
    }
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * Loads the CheckComboBox with the user's projects.
     */
    void initialiseCheckComboBox();

    /**
     * Returns the calendar of the project's tasks.
     *
     * @param project the project
     * @return Calendar with tasks
     */
    Calendar getCalendar(Project project);

  }
}

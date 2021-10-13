package be.ac.ulb.infof307.g09.application.controllers.project;

import be.ac.ulb.infof307.g09.application.controllers.user.HomeController;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.project.CalendarViewController;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Displays a calendar containing the tasks of the user projects.
 * It is located on a tab called "Calendrier" of the application.
 */
public class CalendarController implements CalendarViewController.Listener, Observer {

  private CalendarViewController viewController;
  private final Application application;
  private static final String CALENDAR_NAME = "Tasks";

  /**
   * Initializes the Calendar controller.
   *
   * @param application the application model
   */
  public CalendarController(Application application) {
    this.application = application;
    this.application.addObserver(this);
  }

  /**
   * Show the Calendar Scene.
   *
   * @return null when there is no view to show.
   */
  public Parent getView() {
    Parent view = null;
    try {
      FXMLLoader loader = new FXMLLoader(
          CalendarViewController.class.getResource("/views/project/Calendar.fxml"));
      loader.load();
      viewController = loader.getController();
      viewController.setListener(this);
      this.initialiseCheckComboBox();
      viewController.updateCurrentDateLabel();
      view = loader.getRoot();
    } catch (IOException e) {

      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }
    return view;
  }

  @Override
  public void initialiseCheckComboBox() {
    List<Project> listOfProjects = this.application.getUserProjects();
    this.viewController.loadUserProjects(listOfProjects);
    this.viewController.updateCurrentDateLabel();
  }

  @Override
  public Calendar getCalendar(Project project) {
    Calendar myCalendar = new Calendar(CALENDAR_NAME);

    for (Task task : project.getTasks()) {
      Entry<String> entry = createTaskEntry(task);

      myCalendar.addEntry(entry);
    }
    myCalendar.setStyle(Calendar.Style.getStyle(project.getColorCode()));
    return myCalendar;
  }

  /**
   * Creates a task entry in the calendar.
   *
   * @param task the task to create
   * @return a entry of task
   */
  private Entry<String> createTaskEntry(Task task) {
    Entry<String> entry = new Entry<>(task.getDescription());
    LocalDateTime startDateTime = Instant.ofEpochMilli(task.getStartDate())
        .atZone(ZoneId.systemDefault()).toLocalDateTime();
    LocalDateTime endDateTime =
        Instant.ofEpochMilli(task.getEndDate()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    entry.changeStartDate(startDateTime.toLocalDate());
    entry.changeStartTime(startDateTime.toLocalTime());
    entry.changeEndDate(endDateTime.toLocalDate());
    entry.changeEndTime(endDateTime.toLocalTime());
    return entry;
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof Application && (arg instanceof Project || arg instanceof Task)) {
      switch (((Application) o).getState()) {
        case PROJECT_CREATED:
          if (arg instanceof Project) {
            viewController.addToCheckBox((Project) arg);
            viewController.refreshProjectComboBox();
          }
          break;
        case PROJECT_MODIFIED:
        case PROJECT_DELETED:
        case COLLABORATOR_REMOVED:
          viewController.reloadCheckCombobox();
          viewController.refreshProjectComboBox();
          break;
        case TASK_CREATED:
        case TASK_MODIFIED:
        case TASK_DELETED:
          if (arg instanceof Task) {
            viewController.taskModified((Task) arg);
          }
          break;
        default:
          break;
      }
    }
  }
}

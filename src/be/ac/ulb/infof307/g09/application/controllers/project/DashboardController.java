package be.ac.ulb.infof307.g09.application.controllers.project;

import static be.ac.ulb.infof307.g09.application.view.controllers.project.DashboardViewController.NEGATIVE_VALUE;

import be.ac.ulb.infof307.g09.application.controllers.user.HomeController;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.project.DashboardViewController;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Handles the statistics of different projects.
 * It is located on a tab called "Statistiques" of the application.
 */
public class DashboardController implements DashboardViewController.Listener, Observer {

  private static final String CSV_HEADER =
          "Username; Project title; Initial duration (hours); Current duration (hours); Tasks";

  private static final String FILE_NAME = "I(Should)PlanAll_Statistics";
  private static final String FILE_EXTENSION = ".csv";
  private static final String SEPARATOR = File.separator;
  private static final String NEWLINE = System.getProperty("line.separator");

  private final Stage stage;
  private final Application application;
  private DashboardViewController viewController;

  /**
   * Constructor of Dashboard controller class.
   *
   * @param stage       the stage of the new scene
   * @param application the application model
   */
  public DashboardController(Stage stage, Application application) {
    this.application = application;
    this.application.addObserver(this);
    this.stage = stage;
  }

  /**
   * Show the Dashboard Scene.
   *
   * @return null when there is no view to show.
   */
  public Parent getView() {
    Parent view = null;
    try {
      FXMLLoader loader = new FXMLLoader(
          DashboardViewController.class.getResource("/views/project/Dashboard.fxml"));
      loader.load();

      this.viewController = loader.getController();
      viewController.setListener(this);
      this.updateComboBox();

      view = loader.getRoot();
    } catch (IOException e) {

      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }
    return view;
  }

  /**
   * Writes the statistics in the csv file.
   *
   * @param filePath the path of the file
   * @param project  the project
   */
  public void createCsvFile(String filePath, Project project) {
    String data;
    if (project.getId() == NEGATIVE_VALUE) {
      data = this.getAllProjectsDataToCsv();
    } else {
      data = CSV_HEADER + NEWLINE + project.toCsvFormat() + NEWLINE;
    }
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.write(data);
    } catch (IOException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    }
  }

  /**
   * Returns the data of all projects as a String in the CSV format.
   *
   * @return the formatted string containing the statistics of all the user's project.
   */
  public String getAllProjectsDataToCsv() {
    StringBuilder data = new StringBuilder(CSV_HEADER + NEWLINE);

    for (Project project : this.application.getUserProjects()) {
      data.append(project.toCsvFormat()).append(NEWLINE);
    }
    return data.toString();
  }

  @Override
  public long getTotalTime() {
    long totalTime = 0;
    for (Project project : application.getUserProjects()) {
      totalTime += project.getCurrentDuration();
    }
    return totalTime;
  }

  @Override
  public Stage getStage() {
    return this.stage;
  }

  @Override
  public void exportStatistics(String path, Project project) {
    String newFilePath =
        project.getId() == NEGATIVE_VALUE
            ? path + SEPARATOR + FILE_NAME + FILE_EXTENSION :
            path + SEPARATOR + project.getTitle() + FILE_EXTENSION;

    this.createCsvFile(newFilePath, project);
  }

  @Override
  public Pair<Integer, Integer> getTotalTasksCount() {
    int tasksDone = 0;
    int tasksLeft = 0;

    for (Project project : application.getUserProjects()) {
      Pair<Integer, Integer> tasks = project.getCompletedTasksCount();
      tasksDone += tasks.getKey();
      tasksLeft += tasks.getValue();
    }
    return new Pair<>(tasksDone, tasksLeft);
  }

  @Override
  public void updateComboBox() {
    this.viewController.fillComboBox(application.getUserProjects());
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof Application && (arg instanceof Project || arg instanceof Task)) {
      switch (((Application) o).getState()) {
        case PROJECT_CREATED:
        case PROJECT_MODIFIED:
        case PROJECT_DELETED:
        case COLLABORATOR_REMOVED:
        case TASK_CREATED:
        case TASK_MODIFIED:
        case TASK_DELETED:
          viewController.observableNotification();
          break;
        default:
          break;
      }
    }
  }
}

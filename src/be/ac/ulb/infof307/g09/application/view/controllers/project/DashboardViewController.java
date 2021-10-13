package be.ac.ulb.infof307.g09.application.view.controllers.project;

import be.ac.ulb.infof307.g09.application.controllers.user.HomeController;
import be.ac.ulb.infof307.g09.application.models.Color;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.utilities.DateTimeUtils;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;


/**
 * View Controller for the Dashboard TAB.
 */
public class DashboardViewController {

  public static final String ALL_PROJECTS = "Statistiques globales";
  public static final int NEGATIVE_VALUE = -1;
  private Listener listener;
  private static final String EXPORTING_ERROR_MESSAGE = "Aucun projet sélectionné !";

  @FXML
  private ComboBox<Project> projectComboBox;

  @FXML
  private Label projectLabel;

  @FXML
  private Label tasksLabel;

  @FXML
  private Label estimatedTimeLabel;

  @FXML
  private BarChart<String, Double> elapsedTimeBarChart;

  @FXML
  private PieChart taskPieChart;

  /**
   * This method is attached to the export button in dashboard tab.
   * Opens a new directory chooser for the user to select the directory where
   * the statistics file will be saved, then sends its path to exportStatistics
   * in Dashboard Controller.
   * Shows an error window if no project is selected when the button is clicked
   */
  @FXML
  private void exportButtonPressed() {
    Project selectedProject = projectComboBox.getValue();
    if (selectedProject == null) {
      HomeController.displayErrorAlert(null, EXPORTING_ERROR_MESSAGE);
    } else {
      DirectoryChooser dirChooser = new DirectoryChooser();
      File selectedPath = dirChooser.showDialog(this.listener.getStage());
      if (selectedPath != null) {
        this.listener.exportStatistics(selectedPath.getAbsolutePath(), selectedProject);
      }
    }
  }

  /**
   * Set project name into label.
   *
   * @param projectName the project name
   */
  public void setProjectLabel(String projectName) {
    this.projectLabel.setText(projectName);
  }

  /**
   * Set tasks number into label.
   *
   * @param tasksNumber collaborators number
   */
  public void setTasksLabel(String tasksNumber) {
    this.tasksLabel.setText(tasksNumber);
  }

  /**
   * Set the estimated time into label.
   *
   * @param estimatedTimeNumber sub project number
   */
  public void setEstimatedTimeLabel(String estimatedTimeNumber) {
    this.estimatedTimeLabel.setText(estimatedTimeNumber);
  }

  /**
   * Initialise the listener of this view.
   *
   * @param listener the listener to set
   */
  public void setListener(Listener listener) {
    this.listener = listener;
  }

  /**
   * Clears then loads the combobox with the list given as parameter.
   * The first item is a dummy project that will serve to select the global statistics.
   *
   * @param list List of String to fill
   */
  public void fillComboBox(List<Project> list) {
    this.projectComboBox.getItems().clear();
    this.projectComboBox.getItems().add(
        new Project(
            NEGATIVE_VALUE,
            ALL_PROJECTS,
            ALL_PROJECTS,
            NEGATIVE_VALUE,
            NEGATIVE_VALUE,
            NEGATIVE_VALUE,
            null,
            null, Color.GREEN));
    this.projectComboBox.getItems().addAll(list);
    this.openAllProjectsStats();
  }

  /**
   * Returns a new series to add to the barChart.
   *
   * @param title of the series
   * @param time  to add to series
   * @return XYChart series for the barChart
   */
  public XYChart.Series<String, Double> getSeries(String title, double time) {
    XYChart.Series<String, Double> newSeries = new XYChart.Series<>();
    newSeries.setName(title);
    newSeries.getData().add(new XYChart.Data<>(title, time));
    return newSeries;
  }

  /**
   * Set content of barChart.
   *
   * @param initialDurationTimeOfProject the initial duration of the project
   * @param durationTimeOfProject        the current duration of the project
   * @param estimatedTimeOfProject       the estimated time left to the project
   */
  public void setBarChart(long initialDurationTimeOfProject,
                          long durationTimeOfProject, long estimatedTimeOfProject) {

    double initialDurationTime = getDaysDouble(initialDurationTimeOfProject);
    double durationTime = getDaysDouble(durationTimeOfProject);
    double estimatedTime = getDaysDouble(estimatedTimeOfProject);

    XYChart.Series<String, Double> initialDurationDataSeries =
        getSeries("Durée initiale", initialDurationTime);
    XYChart.Series<String, Double> durationDataSeries = getSeries("Durée actuelle", durationTime);
    XYChart.Series<String, Double> estimatedDataSeries = getSeries("Temps restant", estimatedTime);

    this.elapsedTimeBarChart.getData().add(initialDurationDataSeries);
    this.elapsedTimeBarChart.getData().add(durationDataSeries);
    this.elapsedTimeBarChart.getData().add(estimatedDataSeries);
    this.elapsedTimeBarChart.setLegendSide(Side.TOP);
    this.elapsedTimeBarChart.setLegendVisible(true);
  }

  /**
   * Formats the milliseconds to days in double.
   *
   * @param milliseconds the milliseconds
   * @return the number of days in double format
   */
  private double getDaysDouble(long milliseconds) {
    return milliseconds / (double) TimeUnit.DAYS.toMillis(1);
  }

  /**
   * Set content of pie chart.
   *
   * @param completedTasks number of completed tasks
   * @param remainingTasks number of remaining tasks
   */
  public void setTaskPieChart(Integer completedTasks, Integer remainingTasks) {
    this.taskPieChart.getData().clear();
    PieChart.Data finishedSlice =
        new PieChart.Data("Terminées (" + completedTasks.toString() + ")", completedTasks);
    PieChart.Data remainingSlice =
        new PieChart.Data("Restantes (" + remainingTasks.toString() + ")", remainingTasks);

    this.taskPieChart.getData().add(finishedSlice);
    this.taskPieChart.getData().add(remainingSlice);

    this.taskPieChart.setLegendSide(Side.BOTTOM);
  }


  /**
   * Reloads the combobox and refreshes the statistics on screen.
   */
  public void refreshStats() {
    this.listener.updateComboBox();
  }

  /**
   * Shows the statistics for all projects on screen.
   */
  public void openAllProjectsStats() {
    setProjectLabel(ALL_PROJECTS);
    long totalTime = this.listener.getTotalTime();
    Pair<Integer, Integer> tasks = this.listener.getTotalTasksCount();
    int tasksDone = tasks.getKey();
    int tasksLeft = tasks.getValue();
    int totalTasks = tasksLeft + tasksDone;
    this.setTasksLabel(Integer.toString(totalTasks));
    this.elapsedTimeBarChart.getData().clear();
    setBarChart(0, totalTime, 0);
    this.setEstimatedTimeLabel(DateTimeUtils.formatTime(totalTime));
    if (totalTasks == 0) {
      this.taskPieChart.getData().clear();
    } else {
      this.setTaskPieChart(tasksDone, tasksLeft);
    }
  }

  /**
   * Shows the statistics for the selected project on screen.
   *
   * @param project the title of the project
   */
  public void openProjectStats(Project project) {
    long initialDuration = project.getInitialDuration();
    long durationTime = project.getCurrentDuration();
    long timeLeft = project.getTimeLeft();

    this.elapsedTimeBarChart.getData().clear();
    setBarChart(initialDuration, durationTime, timeLeft);
    Pair<Integer, Integer> tasks = project.getCompletedTasksCount();
    int tasksDone = tasks.getKey();
    int tasksLeft = tasks.getValue();
    int totalTasks = tasksLeft + tasksDone;

    this.setProjectLabel(project.getTitle());
    this.setEstimatedTimeLabel(DateTimeUtils.formatTime(durationTime));
    this.setTasksLabel(String.valueOf(totalTasks));
    if (totalTasks == 0) {
      this.taskPieChart.getData().clear();
    } else {
      this.setTaskPieChart(tasksDone, tasksLeft);
    }
  }


  /**
   * Grabs the name of the selected project in the combobox by the user.
   */
  @FXML
  public void getSelectedProject() {
    Project selectedProject = projectComboBox.getValue();
    if (selectedProject != null) {
      this.updateInfo(selectedProject);
    }
  }

  /**
   * Changes the stats for the selected project by the user.
   *
   * @param project name or GLOBAL_STATISTICS
   */
  public void updateInfo(Project project) {
    if (project.getId() != NEGATIVE_VALUE) {
      this.openProjectStats(project);
    } else {
      this.openAllProjectsStats();
    }
  }

  /**
   * This method is called when a modification is done to a project
   * or a task and reloads the statistics.
   */
  public void observableNotification() {
    this.refreshStats();
  }


  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * Returns the sum of the current duration of all projects.
     *
     * @return the sum of the current duration of all projects
     */
    long getTotalTime();

    /**
     * Passes the Stage to the viewController(for the directory selection window).
     *
     * @return the stage.
     */
    Stage getStage();

    /**
     * This method receives the path to the directory selected by the user, it then creates a path
     * for the new csv file.
     * For the stats of all projects : [path]/I(Should)PlanAll_Statistics.csv.
     * For the stats of individual project : [path]/projectTitle.csv.
     *
     * @param path    the absolute path to the directory for the new csv file
     * @param project project
     */
    void exportStatistics(String path, Project project);

    /**
     * Returns the amount of tasks the user has across all projects.
     *
     * @return a pair of integers containing the total amount of tasks done and tasks left/ongoing.
     */
    Pair<Integer, Integer> getTotalTasksCount();

    /**
     * Reloads the combobox containing the projects.
     */
    void updateComboBox();
  }


}

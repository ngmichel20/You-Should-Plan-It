package be.ac.ulb.infof307.g09.application.view.controllers.project;

import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.utilities.DateTimeUtils;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


/**
 * View controller of task list. Handles the display of the project tasks.
 */
public class ListTasksViewController {
  private static final double DESC_COLUMN_WIDTH = 0.59;
  private static final double DATE_COLUMN_WIDTH = 0.2;
  private static final int OUT_OF_BOUNDS_SELECTION = -1;

  private Listener listener;

  @FXML
  private TableView<Task> tasksTable;
  @FXML
  private TableColumn<Task, String> descriptionColumn;
  @FXML
  private TableColumn<Task, String> startDateColumn;
  @FXML
  private TableColumn<Task, String> endDateColumn;

  /**
   * Sets the tasks list view with tasks from database.
   *
   * @param projectTasks     the project tasks to set
   * @param tasksToHighlight the list of tasks to highlight
   */
  public void setTasksListView(List<Task> projectTasks, List<Task> tasksToHighlight) {
    this.descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

    this.startDateColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
        DateTimeUtils.formatDateToString(cellData.getValue().getStartDate())));
    this.endDateColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
        DateTimeUtils.formatDateToString(cellData.getValue().getEndDate())));
    tasksTable.getItems().setAll(projectTasks);

    highlightUserTasks(tasksToHighlight);
    descriptionColumn.prefWidthProperty().bind(
        tasksTable.widthProperty().multiply(DESC_COLUMN_WIDTH));
    startDateColumn.prefWidthProperty().bind(
        tasksTable.widthProperty().multiply(DATE_COLUMN_WIDTH));
    endDateColumn.prefWidthProperty().bind(tasksTable.widthProperty().multiply(DATE_COLUMN_WIDTH));
  }

  /**
   * Refresh the tasks table.
   */
  public void refreshTasksTable() {
    this.tasksTable.refresh();
  }

  /**
   * Highlight the rows containing the tasks that have been assigned to the user.
   *
   * @param tasksToHighlight tasks to highlight
   */
  private void highlightUserTasks(List<Task> tasksToHighlight) {
    this.tasksTable.setRowFactory(tv -> new TableRow<Task>() {
      @Override
      protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);
        if (task == null) {
          setStyle("");
        } else if (tasksToHighlight.contains(task)) {
          setStyle("-fx-font-weight: bold");
        } else {
          setStyle("");
        }
      }
    });
  }

  /**
   * Handle the action of the create button.
   */
  @FXML
  public void createButtonAction() {
    this.listener.createTask();
  }

  /**
   * Handle the action of the modify button.
   */
  @FXML
  public void modifyButtonAction() {
    Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();
    if (selectedTask != null) {
      this.listener.modifyTask(selectedTask);
    }
  }

  /**
   * Handle the action of the delete button.
   */
  @FXML
  public void deleteButtonAction() {
    Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();
    if (selectedTask != null) {
      this.listener.deleteTask(selectedTask);
    }
  }

  /**
   * Handle the action of the "Collaborator" button.
   */
  @FXML
  public void selectCollaboratorsButtonAction() {
    Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();
    if (selectedTask != null) {
      listener.selectCollaborators(selectedTask);
    }
  }

  /**
   * Sets the listener.
   *
   * @param listener the listener to set
   */
  public void setListener(Listener listener) {
    this.listener = listener;
  }

  /**
   * Adds a task to the table view.
   *
   * @param task the task to add
   */
  public void addTask(Task task) {
    this.tasksTable.getItems().add(task);
  }

  /**
   * Removes a task from the table view.
   *
   * @param task the task to remove
   */
  public void removeTask(Task task) {
    this.tasksTable.getItems().remove(task);
  }

  /**
   * Updates a task from the table view.
   *
   * @param task the task to update
   */
  public void updateTask(Task task) {
    int selectedTaskIndex = this.tasksTable.getSelectionModel().getSelectedIndex();
    if (selectedTaskIndex != OUT_OF_BOUNDS_SELECTION) {
      ObservableList<Task> tasks = this.tasksTable.getItems();
      tasks.set(selectedTaskIndex, task);
    }
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * Creates the task.
     */
    void createTask();

    /**
     * Modifies the task.
     *
     * @param taskToEdit the task to edit
     */
    void modifyTask(Task taskToEdit);

    /**
     * Delete the task.
     *
     * @param task the task to delete
     */
    void deleteTask(Task task);

    /**
     * Select collaborators that are assigned to the task.
     *
     * @param task the task
     */
    void selectCollaborators(Task task);
  }
}
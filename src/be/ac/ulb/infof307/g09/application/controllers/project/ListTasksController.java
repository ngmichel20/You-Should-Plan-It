package be.ac.ulb.infof307.g09.application.controllers.project;

import be.ac.ulb.infof307.g09.application.controllers.collaborator.UserSelectionController;
import be.ac.ulb.infof307.g09.application.controllers.user.HomeController;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.project.ListProjectsViewController;
import be.ac.ulb.infof307.g09.application.view.controllers.project.ListTasksViewController;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;


/**
 * ListTasksController class allows to view all tasks of a specific
 * project, to delete a task and delegate the addition and modification
 * of a task to EditTaskController.
 */
public class ListTasksController implements ListTasksViewController.Listener, Observer {
  private ListTasksViewController viewController;
  private final Project project;
  private final EditTaskController editTaskController;
  private final Application application;

  private static final String TASK_TAB_NAME = "Créer tâche";


  /**
   * The constructor of ListTasksController.
   *
   * @param project     the selected project
   * @param application the application model
   */
  public ListTasksController(Project project, Application application) {
    this.project = project;
    this.application = application;
    this.editTaskController = new EditTaskController(project, this.application);
    this.application.addObserver(this);
  }

  /**
   * Get the tasks list view.
   *
   * @return the tasks list view
   */
  public Parent getView() {
    Parent view = null;
    try {
      FXMLLoader loader = new FXMLLoader(
          ListProjectsViewController.class.getResource("/views/project/ListTasks.fxml"));
      loader.load();

      viewController = loader.getController();
      viewController.setListener(this);
      this.refreshTasks();
      view = loader.getRoot();
    } catch (IOException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }
    return view;
  }

  /**
   * Update the list of tasks into the view.
   */
  private void refreshTasks() {
    List<Task> tasksToHighlight = new ArrayList<>();

    for (Task task : project.getTasks()) {
      if (task.getAssignedUsers().contains(application.getUser())) {
        tasksToHighlight.add(task);
      }
    }

    this.viewController.setTasksListView(project.getTasks(), tasksToHighlight);
    this.viewController.refreshTasksTable();
  }

  @Override
  public void createTask() {
    this.openEditTask();
  }

  @Override
  public void modifyTask(Task taskToEdit) {
    this.openEditTask();
    editTaskController.setTask(taskToEdit);
  }

  /**
   * Open the edit task view.
   */
  private void openEditTask() {
    Tab tab = HomeController.addTab(editTaskController.getView(), TASK_TAB_NAME, true);
    editTaskController.setTab(tab);
    HomeController.disableLeftTabs(tab);
  }

  @Override
  public void deleteTask(Task task) {
    try {
      this.application.removeTaskFromProject(this.project, task);
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
  }

  @Override
  public void selectCollaborators(Task task) {
    Map<User, Boolean> userSelectionMap = new HashMap<>();

    List<User> collaborators = project.getCollaborators();
    collaborators.add(project.getAuthor());

    for (User user : collaborators) {
      userSelectionMap.put(user, task.getAssignedUsers().contains(user));
    }

    handleUserSelection(task, userSelectionMap);
  }

  /**
   * Handles the user assignations to tasks.
   *
   * @param task             the task in which the assignations are handled
   * @param userSelectionMap a map containing the users and for each user,
   *                         a boolean indicating whether the user is checked.
   */
  private void handleUserSelection(Task task, Map<User, Boolean> userSelectionMap) {
    try {
      UserSelectionController controller =
          new UserSelectionController(userSelectionMap);

      boolean hasUserConfirmedSelection = controller.showAndWaitForConfirmation(null);

      if (hasUserConfirmedSelection) {
        List<User> assignedUsers = new ArrayList<>();
        for (Map.Entry<User, Boolean> entry : controller.getUserSelectionMap().entrySet()) {
          if (entry.getValue()) {
            assignedUsers.add(entry.getKey());
          }
        }

        application.handleAssignations(task, assignedUsers);
        // viewController.refreshTasksTable();
        refreshTasks();
      }
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    }
  }

  /**
   * Set the tab which contains the view.
   *
   * @param tab the tab to set.
   */
  public void setTab(Tab tab) {
    HomeController.enableTabsOnCloseRequest(tab);
    tab.setOnSelectionChanged(event -> this.refreshTasks());
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof Application && arg instanceof Task) {
      switch (((Application) o).getState()) {
        case TASK_CREATED:
          viewController.addTask((Task) arg);
          break;
        case TASK_MODIFIED:
          viewController.updateTask((Task) arg);
          break;
        case TASK_DELETED:
          viewController.removeTask((Task) arg);
          break;
        default:
          break;
      }
    }
  }
}

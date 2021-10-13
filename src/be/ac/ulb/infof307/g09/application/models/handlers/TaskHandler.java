package be.ac.ulb.infof307.g09.application.models.handlers;

import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.database.CollaboratorTaskDatabase;
import be.ac.ulb.infof307.g09.database.TaskDatabase;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sub Facade that handles the tasks.
 */
public class TaskHandler {
  private final User user;
  private final TaskDatabase taskDatabase;
  private final CollaboratorTaskDatabase collaboratorTaskDatabase;

  /**
   * Initialises the task facade with the given user.
   *
   * @param user the user
   */
  TaskHandler(User user) {
    this.user = user;
    this.taskDatabase = TaskDatabase.getInstance();
    this.collaboratorTaskDatabase = CollaboratorTaskDatabase.getInstance();
  }

  /**
   * Adds a new task to a project then notifies all the observers.
   *
   * @param project        the project
   * @param newDescription the task description
   * @param startDateHour  the task start date
   * @param endDateHour    the task end date
   * @return the created task
   * @throws DatabaseException         throws when something wrong happens during
   *                                   a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  Task addTaskToProject(Project project, String newDescription,
                        Long startDateHour, Long endDateHour)
      throws DatabaseException, ConnectionFailedException {
    Task createdTask;
    try {
      createdTask =
          this.taskDatabase.createTask(newDescription, startDateHour, endDateHour, project.getId());
      project.addTask(createdTask);
      handleAssignations(createdTask, Collections.singletonList(user));
      createdTask.addAssignedUser(user);
    } catch (DatabaseException e) {
      throw new DatabaseException(ErrorMessagesUtils.ERROR_MESSAGE, e);
    }
    return createdTask;
  }

  /**
   * Handles the assignations of the users to the tasks.
   *
   * @param task         the task in which the assignations are handled
   * @param assignations assignations to add or remove
   * @throws DatabaseException         if an error occurs during the insertion or deletion of a task
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void handleAssignations(Task task, List<User> assignations)
      throws DatabaseException, ConnectionFailedException {
    List<User> usersToAdd = new ArrayList<>();
    List<User> usersToRemove = new ArrayList<>();

    for (User userToAssign : assignations) {
      if (!task.getAssignedUsers().contains(userToAssign)) {
        this.collaboratorTaskDatabase.insertCollaboratorTask(userToAssign, task);
        usersToAdd.add(userToAssign);
      }
    }

    for (User assignedUser : task.getAssignedUsers()) {
      if (!assignations.contains(assignedUser)) {
        this.collaboratorTaskDatabase.deleteCollaboratorTask(assignedUser, task);
        usersToRemove.add(assignedUser);
      }
    }

    usersToAdd.forEach(task::addAssignedUser);
    usersToRemove.forEach(task::removeAssignedUser);
  }

  /**
   * Updates the task of a project.
   *
   * @param project        the project in which the task is updated
   * @param taskToEdit     the task that is being updated
   * @param newDescription the new description of the task
   * @param startDateHour  the start date and hour of the task in epoch milli format
   * @param endDateHour    the end date and hour of the task in epoch milli format
   * @throws DatabaseException         if an error occurs during the updating of the task
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void updateTaskInProject(Project project, Task taskToEdit,
                           String newDescription, Long startDateHour, Long endDateHour)
      throws DatabaseException, ConnectionFailedException {
    try {
      this.taskDatabase.updateTask(taskToEdit, newDescription, startDateHour, endDateHour);

      taskToEdit.setDescription(newDescription);
      taskToEdit.setStartDate(startDateHour);
      taskToEdit.setEndDate(endDateHour);
      project.updateTask(taskToEdit);
    } catch (DatabaseException e) {
      throw new DatabaseException(ErrorMessagesUtils.ERROR_MESSAGE, e);
    }
  }

  /**
   * Removes the task from the project and notifies all the observers.
   *
   * @param project Project
   * @param task    to be modified
   * @throws DatabaseException         throws if a problem occurs while removing
   *                                   the task from TaskDatabase.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void removeTaskFromProject(Project project, Task task)
      throws DatabaseException, ConnectionFailedException {
    try {
      this.taskDatabase.deleteTask(task.getId());
      project.removeTask(task);
    } catch (DatabaseException e) {
      throw new DatabaseException(ErrorMessagesUtils.ERROR_MESSAGE, e);
    }
  }

  /**
   * Returns the tasks that will end in less than 24 hours.
   *
   * @return the list of user's tasks that will end in less than 24 hours
   */
  List<Task> getUserTasksCloseToDeadline() {
    List<Task> tasksAssignedToUser = new ArrayList<>();
    for (Project project : user.getProjectList()) {
      for (Task task : project.getTasksCloseToDeadline()) {
        if (task.getAssignedUsers().contains(user)) {
          tasksAssignedToUser.add(task);
        }
      }
    }
    return tasksAssignedToUser;
  }

}

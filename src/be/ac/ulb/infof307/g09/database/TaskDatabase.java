package be.ac.ulb.infof307.g09.database;

import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class is used to handle CRUD actions with the Task table.
 *
 */
public final class TaskDatabase extends Database {

  private static TaskDatabase instance;

  private TaskDatabase() {}

  /**
   * Returns the singleton instance.
   *
   * @return The singleton instance.
   */
  public static TaskDatabase getInstance() {
    if (instance == null) {
      instance = new TaskDatabase();
    }
    return instance;
  }

  /**
   * Add task to the Task database with a a start and end date.
   *
   * @param description task content
   * @param startDate   beginning date of the task
   * @param endDate     ending date of the task
   * @param projectId   id of project
   * @return new created task
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Task createTask(String description, Long startDate,
                         Long endDate, int projectId)
      throws DatabaseException, ConnectionFailedException {

    Task task = null;
    String sql = "INSERT INTO Task(Description,StartDate,EndDate,ProjectId) VALUES(?,?,?,?); ";
    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setString(1, description);
      preparedStatement.setLong(2, startDate);
      preparedStatement.setLong(3, endDate);
      preparedStatement.setInt(4, projectId);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Creating task failed, no rows affected.");
      }
      task = new Task(findLastInsertedRow("Task"), description, startDate, endDate, projectId);
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return task;
  }

  /**
   * Get tasks from "Task" table database by a project Id.
   *
   * @param project id of project
   * @return all tasks
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Task> getAllTasksOfProject(Project project)
      throws DatabaseException, ConnectionFailedException {

    List<Task> list = new ArrayList<>();

    String sql = "SELECT *  FROM Task WHERE ProjectId = ?";
    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setInt(1, project.getId());
      try (ResultSet rs = preparedStatement.executeQuery()) {

        while (rs.next()) {
          list.add(new Task(rs.getInt("Id"), rs.getString("Description"),
              rs.getLong("StartDate"), rs.getLong("EndDate"),
              rs.getInt("ProjectId")));
        }
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return list;
  }

  /**
   * Update the task from database.
   *
   * @param task           the task
   * @param newDescription new description of task
   * @param startDate first day of the task
   * @param endDate   last day of the task
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void updateTask(Task task, String newDescription, long startDate, long endDate)
      throws DatabaseException, ConnectionFailedException {

    String sql1 = "UPDATE Task SET Description = ? , StartDate = ? , EndDate = ? WHERE Id = ?";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql1)) {
      preparedStatement.setString(1, newDescription);
      preparedStatement.setLong(2, startDate);
      preparedStatement.setLong(3, endDate);
      preparedStatement.setInt(4, task.getId());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }

  /**
   * Delete a task from database by a task id.
   *
   * @param taskId id of task we want to delete
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void deleteTask(int taskId) throws DatabaseException, ConnectionFailedException {

    String sql1 = "DELETE FROM Task WHERE Id = ?";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql1)) {
      preparedStatement.setInt(1, taskId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }
}

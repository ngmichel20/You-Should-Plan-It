package be.ac.ulb.infof307.g09.database;

import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class is used to handle CRUD actions with the "CollaboratorTask" table.
 */
public final class CollaboratorTaskDatabase extends Database {

  private static CollaboratorTaskDatabase instance;

  private CollaboratorTaskDatabase() {
  }

  /**
   * Returns the singleton instance.
   *
   * @return The singleton instance.
   */
  public static CollaboratorTaskDatabase getInstance() {
    if (instance == null) {
      instance = new CollaboratorTaskDatabase();
    }
    return instance;
  }

  /**
   * Gets all the users assigned to a task.
   *
   * @param task the task to assign
   * @return All the tasks of a given user
   * @throws DatabaseException if there is an error during execution of the query.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<User> getAssignedCollaboratorsToTask(Task task)
      throws DatabaseException, ConnectionFailedException {
    String sql = "SELECT * FROM CollaboratorTask WHERE TaskId = ?";
    List<User> assignedUsers = new ArrayList<>();
    User user;

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setInt(1, task.getId());
      try (ResultSet rs = preparedStatement.executeQuery()) {
        while (rs.next()) {
          user = UserDatabase.getInstance().getUserById(rs.getInt("UserId"));
          assignedUsers.add(user);
        }
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return assignedUsers;
  }


  /**
   * Links a user with a task.
   *
   * @param user The user that will handle the task
   * @param task The task
   * @throws DatabaseException if there is an error during execution of the query.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void insertCollaboratorTask(User user, Task task)
      throws DatabaseException, ConnectionFailedException {
    String sql = "INSERT INTO CollaboratorTask(UserId, TaskId) VALUES(?, ?)";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setInt(1, user.getId());
      preparedStatement.setInt(2, task.getId());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }


  /**
   * Unlinks a use with a task.
   *
   * @param user The user that will no longer handle the task
   * @param task The task
   * @throws DatabaseException if there is an error during execution of the query.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void deleteCollaboratorTask(User user, Task task)
      throws DatabaseException, ConnectionFailedException {
    String sql = "DELETE FROM CollaboratorTask WHERE UserId = ? AND TaskId = ?";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setInt(1, user.getId());
      preparedStatement.setInt(2, task.getId());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }
}

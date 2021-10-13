package be.ac.ulb.infof307.g09.database;

import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The class is used to handle CRUD actions with the ProjectTag table.
 *
 * @author Alexios Konstantopoulos
 * @author Maciej Piekarski
 */
public final class ProjectTagDatabase extends Database {

  private static ProjectTagDatabase instance;

  private ProjectTagDatabase(){}

  /**
   * Returns the singleton instance.
   *
   * @return The singleton instance.
   */
  public static ProjectTagDatabase getInstance() {
    if (instance == null) {
      instance = new ProjectTagDatabase();
    }
    return instance;
  }

  /**
   * Link a specific project to a specific tag.
   *
   * @param projectId id of project
   * @param tagId     id of tag
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void addProjectTag(int projectId, int tagId)
      throws DatabaseException, ConnectionFailedException {

    String sql = "INSERT INTO ProjectTag(ProjectId,TagId) VALUES(?,?); ";
    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setInt(1, projectId);
      preparedStatement.setInt(2, tagId);

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }

  /**
   * Removes a specific tag from a specific project.
   *
   * @param projectId id of project
   * @param tagId     id of tag
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void removeProjectTag(int projectId, int tagId)
      throws DatabaseException, ConnectionFailedException {

    String sql = "DELETE FROM ProjectTag WHERE ProjectId = ? AND TagId = ?";
    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setInt(1, projectId);
      preparedStatement.setInt(2, tagId);

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }
}

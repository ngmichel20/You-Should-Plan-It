package be.ac.ulb.infof307.g09.database;

import be.ac.ulb.infof307.g09.application.models.Tag;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Tag database.
 *
 * @author Benoît Haal
 * @author Pap Sanou
 */
public final class TagDatabase extends Database {

  private static TagDatabase instance;

  private TagDatabase(){}

  /**
   * Returns the singleton instance.
   *
   * @return The singleton instance.
   */
  public static TagDatabase getInstance() {
    if (instance == null) {
      instance = new TagDatabase();
    }
    return instance;
  }

  /**
   * Add a tag to Tag database.
   *
   * @param description tag content
   * @return the new tag created
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Tag createTag(String description) throws DatabaseException, ConnectionFailedException {
    Tag tag = null;
    String sql = "INSERT INTO Tag(Description) VALUES(?); ";
    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setString(1, description);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("Creating user failed, no rows affected.");
      }
      tag = new Tag(findLastInsertedRow("Tag"), description);
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return tag;
  }

  /**
   * Get a tag by a tag text given.
   *
   * @param text text of tag
   * @return a tag object
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Tag getTag(String text) throws DatabaseException, ConnectionFailedException {
    Tag tag = null;
    String sql = "SELECT * FROM Tag WHERE Description = ?";
    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setString(1, text);
      try (ResultSet rs = preparedStatement.executeQuery()) {
        if (rs.next()) {
          tag = new Tag(rs.getInt("Id"), rs.getString("Description"));
        }
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return tag;
  }


  /**
   * Delete a tag from database.
   *
   * @param tagId id of the tag we want to delete
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void deleteTag(int tagId) throws DatabaseException, ConnectionFailedException {

    String sql1 = "DELETE FROM Tag WHERE Id = ?";
    String sql2 = "DELETE FROM ProjectTag WHERE TagId = ? ";

    try (Connection conn = connect();
         PreparedStatement firstPreparedStatement = conn.prepareStatement(sql1);
         PreparedStatement secondPreparedStatement = conn.prepareStatement(sql2)) {
      firstPreparedStatement.setInt(1, tagId);
      secondPreparedStatement.setInt(1, tagId);
      firstPreparedStatement.executeUpdate();
      secondPreparedStatement.executeUpdate();
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }
}


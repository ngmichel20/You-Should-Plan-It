package be.ac.ulb.infof307.g09.database;

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
 * The class is used to handle CRUD actions with the User table.
 *
 * @author Piekarski Maciej
 * @author El Bakkali Soufian
 */
public final class UserDatabase extends Database {

  private static UserDatabase instance;

  private UserDatabase() {}

  /**
   * Returns the singleton instance.
   *
   * @return The singleton instance.
   */
  public static UserDatabase getInstance() {
    if (instance == null) {
      instance = new UserDatabase();
    }
    return instance;
  }

  /**
   * Get the user by id.
   *
   * @param id id of the user
   * @return the user if it was found, null otherwise
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public User getUserById(int id) throws DatabaseException, ConnectionFailedException {
    User user = null;

    String sql = "SELECT * FROM User WHERE id = ?";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setInt(1, id);
      try (ResultSet rs = preparedStatement.executeQuery()) {

        if (rs.next()) {
          user = getUserFromResultSet(rs);
        }
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }

    return user;
  }

  /**
   * Get the user from the corresponding result set.
   *
   * @param rs result of the sql statement
   * @return the user found
   * @throws SQLException throws when something wrong happens during
   *                      the fetching of data from the database
   */
  private User getUserFromResultSet(ResultSet rs) throws SQLException {
    return new User(
        rs.getInt("Id"),
        rs.getString("Username"),
        rs.getString("Email"),
        rs.getString("LastName"),
        rs.getString("FirstName"),
        rs.getString("Password")
    );
  }

  /**
   * Get the user by username.
   *
   * @param username the username
   * @return the user if it was found, null otherwise
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public User getByUsername(String username) throws DatabaseException, ConnectionFailedException {
    User user = null;

    String sql = "SELECT * FROM User WHERE Username = ?";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setString(1, username);

      try (ResultSet rs = preparedStatement.executeQuery()) {
        if (rs.next()) {
          user = getUserFromResultSet(rs);
        }
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }

    return user;
  }

  /**
   * Check if the email exists.
   *
   * @param email the email to check
   * @return true if the email exists, false otherwise
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public boolean checkIfEmailExists(String email)
      throws DatabaseException, ConnectionFailedException {

    boolean found = false;
    String sql = "SELECT * FROM User WHERE Email = ?";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setString(1, email);
      try (ResultSet rs = preparedStatement.executeQuery()) {
        if (rs.next()) {
          found = true;
        }
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return found;
  }

  /**
   * Get the user.
   *
   * @param username the username
   * @param password the password
   * @return the user if it was found, null otherwise
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public User getUser(String username, String password)
      throws DatabaseException, ConnectionFailedException {

    User user = null;

    String sql = "SELECT * FROM User WHERE Username = ? AND Password = ?";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setString(1, username);
      preparedStatement.setString(2, password);
      try (ResultSet rs = preparedStatement.executeQuery()) {

        if (rs.next()) {
          user = getUserFromResultSet(rs);
        }
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }

    return user;
  }

  /**
   * Extracts all the users.
   *
   * @param username the username of the current user to not add among the users to add
   * @return A list of all the users.
   * @throws DatabaseException If the resultSet does not contain users
   *                           or if there is an error during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<User> getAllUsers(String username)
      throws DatabaseException, ConnectionFailedException {

    String sql = "SELECT * FROM User";
    List<User> list = new ArrayList<>();
    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      try (ResultSet rs = preparedStatement.executeQuery()) {
        User user;

        while (rs.next()) {
          user = getUserFromResultSet(rs);
          if (!user.getUsername().equals(username)) {
            list.add(user);
          }
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
   * Update the user in the database.
   *
   * @param oldUser the user (object)
   * @param newUser new user with the parameters to update
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void updateUser(User oldUser, User newUser)
      throws DatabaseException, ConnectionFailedException {

    if (checkIfUsernameExists(newUser.getUsername())
        && !newUser.getUsername().equals(oldUser.getUsername())) {
      throw new IllegalArgumentException("The username is already taken!");
    } else if (checkIfEmailExists(newUser.getEmail())
        && !newUser.getEmail().equals(oldUser.getEmail())) {
      throw new IllegalArgumentException("The email is already taken!");
    }

    String sql = "UPDATE User SET Username = ?, Email = ?,"
        + " LastName = ?, FirstName = ?, Password = ?"
        + " WHERE Username = ?";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setString(1, newUser.getUsername());
      preparedStatement.setString(2, newUser.getEmail());
      preparedStatement.setString(3, newUser.getLastName());
      preparedStatement.setString(4, newUser.getFirstName());
      preparedStatement.setString(5, newUser.getPassword());
      preparedStatement.setString(6, oldUser.getUsername());

      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }

  /**
   * Insert a new user in the User table.
   *
   * @param user the user
   * @return the new inserted user
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public User insert(User user) throws DatabaseException, ConnectionFailedException {
    User newUser = null;
    if (checkIfUsernameExists(user.getUsername())) {
      throw new IllegalArgumentException("The username is already taken!");
    } else if (checkIfEmailExists(user.getEmail())) {
      throw new IllegalArgumentException("The email is already taken!");
    }

    String sql = "INSERT INTO User(Username,Email,LastName,FirstName,Password) VALUES(?,?,?,?,?)";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setString(1, user.getUsername());
      preparedStatement.setString(2, user.getEmail());
      preparedStatement.setString(3, user.getLastName());
      preparedStatement.setString(4, user.getFirstName());
      preparedStatement.setString(5, user.getPassword());
      preparedStatement.executeUpdate();
      int id = findLastInsertedRow("User");
      newUser = new User(id, user.getUsername(), user.getEmail(),
          user.getLastName(), user.getFirstName(), user.getPassword());
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return newUser;
  }

  /**
   * Delete a user by username.
   *
   * @param username username of the user to delete
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void delete(String username) throws DatabaseException, ConnectionFailedException {
    String sql = "DELETE FROM User WHERE Username = ?";

    if (!checkIfUsernameExists(username)) {
      throw new IllegalArgumentException("The username does not exist ! ( " + username + " )");
    }

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setString(1, username);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }

  /**
   * Check if the username exists.
   *
   * @param username the username to check
   * @return true if the username exists, false otherwise
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public boolean checkIfUsernameExists(String username)
          throws DatabaseException, ConnectionFailedException {
    return getByUsername(username) != null;
  }

}

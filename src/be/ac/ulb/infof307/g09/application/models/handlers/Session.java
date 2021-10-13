package be.ac.ulb.infof307.g09.application.models.handlers;

import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.database.ProjectCollaborationDatabase;
import be.ac.ulb.infof307.g09.database.ProjectDatabase;
import be.ac.ulb.infof307.g09.database.UserDatabase;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.util.List;

/**
 * Sub Facade that handles the user.
 */
class Session {
  private User user;
  private final UserDatabase userDatabase;
  private final ProjectCollaborationDatabase projectCollaborationDatabase;
  private final ProjectDatabase projectDatabase;

  /**
   * Initialises the session.
   */
  Session() {
    this.user = null;
    this.userDatabase = UserDatabase.getInstance();
    this.projectCollaborationDatabase = ProjectCollaborationDatabase.getInstance();
    this.projectDatabase = ProjectDatabase.getInstance();
  }

  /**
   * Connects the user to the application and loads his projects.
   *
   * @param username the username of the user
   * @param password password of the user
   * @throws DatabaseException         if a problem occurs while fetching the user's projects.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void connect(String username, String password)
      throws DatabaseException, ConnectionFailedException {
    this.user = userDatabase.getUser(username, password);

    if (user != null) {
      user.setProjectList(projectDatabase.getAllParentProjectsByAuthor(user.getUsername()));
      user.getAuthorProjects().addAll(user.getProjectList());
      user.getProjectList().addAll(projectCollaborationDatabase.getProjectsByCollaborator(user));
    } else {
      throw new IllegalArgumentException(ErrorMessagesUtils.ERROR_MESSAGE);
    }
  }

  /**
   * Register a new user to the application.
   *
   * @param newUser the user to register
   * @throws DatabaseException         This exception is thrown to let the user
   *                                   know that there is a database failure
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void register(User newUser) throws DatabaseException, ConnectionFailedException {
    this.user = this.userDatabase.insert(newUser);
  }

  /**
   * Disconnects a user.
   */
  void disconnect() {
    this.user.setProjectList(null);
    this.user = null;
  }

  /**
   * Updates the user.
   *
   * @param newUser user with the new parameters
   * @throws DatabaseException         This exception is thrown to let the user
   *                                   know that there is a database failure
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void updateUser(User newUser)
      throws DatabaseException, ConnectionFailedException {
    user.updateUser(newUser);
    userDatabase.updateUser(user, newUser);
  }

  /**
   * Returns the connected user.
   *
   * @return User object
   */
  User getUser() {
    return user;
  }

  /**
   * Returns the username of the user.
   *
   * @return the username
   */
  String getUsername() {
    return user.getUsername();
  }

  /**
   * Returns the email of the user.
   *
   * @return the email
   */
  String getEmail() {
    return user.getEmail();
  }

  /**
   * Returns the first name of the user.
   *
   * @return the first name
   */
  String getFirstName() {
    return user.getFirstName();
  }

  /**
   * Returns the last name of the user.
   *
   * @return the last name
   */
  String getLastName() {
    return user.getLastName();
  }

  /**
   * Returns the password of the user.
   *
   * @return the password
   */
  String getPassword() {
    return user.getPassword();
  }

  /**
   * Check if the user exists by username.
   *
   * @param username the username to check
   * @return true if user exists, false otherwise
   * @throws DatabaseException         if an error occurs during the verification
   * @throws ConnectionFailedException If the connection to the database fails
   */
  boolean checkIfUsernameExists(String username)
      throws DatabaseException, ConnectionFailedException {
    try {
      return userDatabase.checkIfUsernameExists(username);
    } catch (DatabaseException e) {
      throw new DatabaseException(ErrorMessagesUtils.ERROR_USER, e);
    }
  }

  /**
   * Check if the given email exists.
   *
   * @param email the email to check
   * @return true if the email exists, false otherwise
   * @throws DatabaseException         if an error occurs during the verification
   * @throws ConnectionFailedException If the connection to the database fails
   */
  boolean checkIfEmailExists(String email)
      throws DatabaseException, ConnectionFailedException {
    return userDatabase.checkIfEmailExists(email);
  }

  /**
   * Initializes the collaboratorCheckComboBox by filling it with all the users
   * available in the database.
   *
   * @return a list of all the users in the database
   * @throws DatabaseException         throws an exception if an error occurs in
   *                                   the UserDatabase request sql
   * @throws ConnectionFailedException If the connection to the database fails
   */
  List<User> loadUsersFromDatabase()
      throws DatabaseException, ConnectionFailedException {
    return this.userDatabase.getAllUsers(user.getUsername());
  }
}

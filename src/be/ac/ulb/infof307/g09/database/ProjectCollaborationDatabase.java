package be.ac.ulb.infof307.g09.database;

import be.ac.ulb.infof307.g09.application.models.Project;
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
 * This class enables the retrieval of project collaborations information.
 * It makes use of the Singleton Design Pattern.
 *
 * @author Benoît
 * @author Soufian
 */
public final class ProjectCollaborationDatabase extends Database {

  private static final int PENDING_ANSWER = -1;
  private static final int UNREAD_INVITATION = 0;

  public static final String SELECT_COLLABORATORS_QUERY =
      "SELECT * FROM User JOIN ProjectCollaboration ON User.Id = ProjectCollaboration.UserId ";

  public static final String SELECT_PROJECT_OF_COLLABORATOR_QUERY =
      "SELECT * FROM Project JOIN ProjectCollaboration "
          + "ON Project.Id = ProjectCollaboration.ProjectId ";

  private static ProjectCollaborationDatabase instance;

  private ProjectCollaborationDatabase() {
  }

  /**
   * Returns the singleton instance.
   *
   * @return The singleton instance.
   */
  public static ProjectCollaborationDatabase getInstance() {
    if (instance == null) {
      instance = new ProjectCollaborationDatabase();
    }
    return instance;
  }

  /**
   * Add a user as a collaborator to a project.
   *
   * @param project      the project.
   * @param collaborator the user.
   * @throws DatabaseException         if there is an error during execution of the query.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void insertProjectCollaboratorRow(Project project, User collaborator)
      throws DatabaseException, ConnectionFailedException {

    List<String> parameters = new ArrayList<>();
    parameters.add(project.getId() + "");
    parameters.add(collaborator.getId() + "");
    parameters.add(PENDING_ANSWER + "");
    parameters.add(UNREAD_INVITATION + "");

    String sql = "INSERT INTO ProjectCollaboration("
        + "ProjectId, UserId, Accepted, InvitationRead) VALUES(?, ?, ?, ?)";

    executeUpdate(sql, parameters);
  }

  /**
   * Retrieves all the collaborators of a project.
   *
   * @param project The given project.
   * @return All the collaborators of a project.
   * @throws DatabaseException         If there is an error during the execution of the query
   *                                   or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<User> getCollaboratorsByProjectAccepted(Project project)
      throws DatabaseException, ConnectionFailedException {
    List<User> userList = null;

    String sql = SELECT_COLLABORATORS_QUERY
        + "WHERE ProjectId = ? AND Accepted = 1 AND InvitationRead = 0";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setInt(1, project.getId());
      try (ResultSet rs = preparedStatement.executeQuery()) {
        userList = extractUsersFromResultSet(rs);
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return userList;
  }

  /**
   * Retrieves all the collaborators of a project.
   *
   * @param project The given project.
   * @return All the collaborators of a project.
   * @throws DatabaseException         If there is an error during the execution of the query
   *                                   or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<User> getCollaboratorsOfProjects(Project project)
      throws DatabaseException, ConnectionFailedException {
    List<User> userList = null;

    String sql = SELECT_COLLABORATORS_QUERY
        + "WHERE ProjectId = ? AND Accepted = 1";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setInt(1, project.getId());
      try (ResultSet rs = preparedStatement.executeQuery()) {
        userList = extractUsersFromResultSet(rs);
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return userList;
  }

  /**
   * Gets the collaborators by project that have been refused.
   *
   * @param project the project to check if it has been refused
   * @return the list of users that refused the project
   * @throws DatabaseException         If there is an error during the execution of the query
   *                                   or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<User> getCollaboratorsByProjectRefused(Project project)
      throws DatabaseException, ConnectionFailedException {

    List<User> userList = null;

    String sql = SELECT_COLLABORATORS_QUERY
        + "WHERE ProjectId = ? AND Accepted = 0";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setInt(1, project.getId());
      try (ResultSet rs = preparedStatement.executeQuery()) {
        userList = extractUsersFromResultSet(rs);
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return userList;
  }

  /**
   * Gets the collaborators by project that have not answered to the invitations yet.
   *
   * @param project the project to check if it has not been answered yet
   * @return the list of users that have not answered the project yet
   * @throws DatabaseException         If there is an error during the execution of the query
   *                                   or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<User> getCollaboratorsByProjectWaiting(Project project)
      throws DatabaseException, ConnectionFailedException {

    List<User> userList = null;

    String sql = SELECT_COLLABORATORS_QUERY
        + "WHERE ProjectId = ? AND Accepted = -1";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setInt(1, project.getId());
      try (ResultSet rs = preparedStatement.executeQuery()) {
        userList = extractUsersFromResultSet(rs);
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return userList;
  }

  /**
   * Retrieves all the projects of a collaborator.
   *
   * @param collaborator The given collaborator (as an existing user).
   * @return All the projects of a collaborator.
   * @throws DatabaseException         If there is an error during the execution of the query
   *                                   or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Project> getProjectsByCollaborator(User collaborator)
      throws DatabaseException, ConnectionFailedException {

    List<Project> projectList = null;

    String sql = SELECT_PROJECT_OF_COLLABORATOR_QUERY
        + "WHERE UserId = ? AND Accepted = 1 ";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setInt(1, collaborator.getId());
      try (ResultSet rs = preparedStatement.executeQuery()) {
        projectList = extractProjectsFromResultSet(rs);
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return projectList;
  }

  /**
   * Retrieves all the projects of a collaborator that are still waiting for answer.
   *
   * @param collaborator The given collaborator (as an existing user).
   * @return All the projects of a collaborator.
   * @throws DatabaseException         If there is an error during the execution of the query
   *                                   or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Project> getAllUnansweredProjectsByCollaborator(User collaborator)
      throws DatabaseException, ConnectionFailedException {
    List<Project> projectList = null;

    String sql = SELECT_PROJECT_OF_COLLABORATOR_QUERY
        + "WHERE UserId = ? AND Accepted = -1 ";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setInt(1, collaborator.getId());
      try (ResultSet rs = preparedStatement.executeQuery()) {
        projectList = extractProjectsFromResultSet(rs);
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return projectList;
  }

  /**
   * Retrieves all the collaborators that have been invited of a project.
   *
   * @param project The given project.
   * @return All the collaborators of a project.
   * @throws DatabaseException         If there is an error during the execution of the query
   *                                   or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<User> getCollaboratorsInvitedByProject(Project project)
      throws DatabaseException, ConnectionFailedException {
    List<User> userList = null;

    String sql = SELECT_COLLABORATORS_QUERY
        + "WHERE ProjectId = ? AND (Accepted = 1 Or Accepted = -1) ";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setInt(1, project.getId());
      try (ResultSet rs = preparedStatement.executeQuery()) {
        userList = extractUsersFromResultSet(rs);
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return userList;
  }

  /**
   * Update the accepted column of a project collaboration.
   *
   * @param projectId    The projectId of the project to find in the where clause
   * @param collaborator the collaborator
   * @param accepted     The accepted column to update
   * @throws DatabaseException         when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void updateAcceptedColumn(int projectId, User collaborator, int accepted)
      throws DatabaseException, ConnectionFailedException {

    String sql = "UPDATE ProjectCollaboration SET Accepted = ? "
        + "WHERE ProjectId = ? AND UserId = ?";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setInt(1, accepted);
      preparedStatement.setInt(2, projectId);
      preparedStatement.setInt(3, collaborator.getId());

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }

  /**
   * Get user who has unread notifications (so he is a collaborator).
   *
   * @param userApplication the userApplication.
   * @return userApplication who has unread notifications.
   * @throws DatabaseException         If there is an issue during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public User getUserWithUnreadNotifications(User userApplication)
      throws DatabaseException, ConnectionFailedException {

    String sql = "SELECT * FROM User "
        + "JOIN ProjectCollaboration ON User.Id = ProjectCollaboration.UserId "
        + "WHERE Accepted = -1 AND ProjectCollaboration.UserId = ? "
        + "GROUP BY User.Id";

    User userWithUnreadNotifications = userApplication;
    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setInt(1, userApplication.getId());
      try (ResultSet rs = preparedStatement.executeQuery()) {
        userWithUnreadNotifications = extractOneUserFromResultSet(rs);
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return userWithUnreadNotifications;
  }


  /**
   * Retrieves a project of a collaborator specified in the argument.
   *
   * @param project      The project to find
   * @param collaborator The given collaborator (as an existing user).
   * @return A project of a collaborator.
   * @throws DatabaseException         If there is an error during the execution of the query
   *                                   or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Project getProjectByCollaborator(Project project, User collaborator)
      throws DatabaseException, ConnectionFailedException {
    Project projectExtractedFromResultSet = null;

    String sql = SELECT_PROJECT_OF_COLLABORATOR_QUERY
        + "WHERE UserId = ? AND ProjectId = ? AND (Accepted = -1 Or Accepted = 1)";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setInt(1, collaborator.getId());
      preparedStatement.setInt(2, project.getId());
      try (ResultSet rs = preparedStatement.executeQuery()) {
        projectExtractedFromResultSet = extractProjectFromResultSet(rs);
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return projectExtractedFromResultSet;
  }


  /**
   * Executes an SQL update query.
   *
   * @param sql        The SQL query.
   * @param parameters The parameters of the query.
   * @throws DatabaseException         If there is an issue during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private void executeUpdate(String sql, List<String> parameters)
      throws DatabaseException, ConnectionFailedException {

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      for (int i = 1; i <= parameters.size(); i++) {
        preparedStatement.setString(i, parameters.get(i - 1));
      }

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }

  /**
   * Extracts all the projects from a resultSet.
   *
   * @param resultSet The given result set.
   * @return A list of all the projects coming from the result set.
   * @throws DatabaseException         If the resultSet does not contain projects
   *                                   or if there is an error during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private List<Project> extractProjectsFromResultSet(ResultSet resultSet)
      throws DatabaseException, ConnectionFailedException {
    List<Project> list = new ArrayList<>();
    try {
      Project project;
      while (resultSet.next()) {
        project = getProjectFromResultSet(resultSet);

        list.add(project);
      }
    } catch (SQLException e) {
      throwException(e);
    }
    return list;
  }

  /**
   * Gets a project from a result set.
   *
   * @param resultSet the result set given
   * @return a project
   * @throws SQLException              if a sql request fails
   * @throws DatabaseException         If there is an issue during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private Project getProjectFromResultSet(ResultSet resultSet)
      throws SQLException, DatabaseException, ConnectionFailedException {
    Project project;
    project = ProjectDatabase.getInstance().getProject(resultSet);

    List<Task> projectTasks = TaskDatabase.getInstance().getAllTasksOfProject(project);
    for (Task task : projectTasks) {
      task.setAssignedUsers(CollaboratorTaskDatabase.getInstance()
          .getAssignedCollaboratorsToTask(task));
    }
    project.setTasks(projectTasks);
    project.setTags(ProjectDatabase.getInstance().getTagsOfProject(project));
    project.setCollaborators(
        ProjectCollaborationDatabase.getInstance().getCollaboratorsOfProjects(project));
    return project;
  }

  /**
   * Extracts one project from a resultSet.
   *
   * @param resultSet The given result set.
   * @return A list of all the projects coming from the result set.
   * @throws DatabaseException         If the resultSet does not contain projects
   *                                   or if there is an error during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private Project extractProjectFromResultSet(ResultSet resultSet)
      throws DatabaseException, ConnectionFailedException {

    Project project = null;
    try {
      if (resultSet.next()) {
        project = ProjectDatabase.getInstance().getProject(resultSet);
      }
    } catch (SQLException e) {
      throwException(e);
    }
    return project;
  }

  /**
   * Extracts all the users from a resultSet.
   *
   * @param resultSet The given result set.
   * @return A list of all the users coming from the result set.
   * @throws DatabaseException If the resultSet does not contain users
   *                           or if there is an error during the access to the database.
   */
  private List<User> extractUsersFromResultSet(ResultSet resultSet) throws DatabaseException {
    List<User> list = new ArrayList<>();
    try {
      User user;
      while (resultSet.next()) {
        user = new User(
            resultSet.getInt("Id"),
            resultSet.getString("Username"),
            resultSet.getString("Email"),
            resultSet.getString("LastName"),
            resultSet.getString("FirstName"),
            resultSet.getString("Password")
        );

        list.add(user);
      }
    } catch (SQLException e) {
      throwException(e);
    }
    return list;
  }

  /**
   * Extract a user from the result set.
   *
   * @param resultSet the resultSet
   * @return the collaborator (user)
   * @throws DatabaseException If the resultSet does not contain one user
   *                           or if there is an error during the access to the database.
   */
  private User extractOneUserFromResultSet(ResultSet resultSet) throws DatabaseException {
    User user = null;
    try {
      while (resultSet.next()) {
        user = new User(
            resultSet.getInt("Id"),
            resultSet.getString("Username"),
            resultSet.getString("Email"),
            resultSet.getString("LastName"),
            resultSet.getString("FirstName"),
            resultSet.getString("Password")
        );
      }
    } catch (SQLException e) {
      throwException(e);
    }
    return user;
  }

  /**
   * Gets the projects that have been accepted and that the sender
   * has not read his invitations sent to his collaborators yet.
   *
   * @param sender the sender who has sent the invitations.
   * @return the list of projects that have been accepted
   * @throws DatabaseException         If the resultSet does not contain one user
   *                                   or if there is an error during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Project> getProjectsWithInvitationAccepted(User sender)
      throws DatabaseException, ConnectionFailedException {
    String sql = "SELECT * FROM Project "
        + "JOIN ProjectCollaboration ON Project.Id = ProjectCollaboration.ProjectId "
        + "WHERE Accepted = 1 AND InvitationRead = 0 AND Author = ? "
        + "GROUP BY ProjectId";

    return getProjectsWithInvitations(sender, sql);
  }

  /**
   * Gets the projects with invitations.
   *
   * @param sender the sender who has sent the invitations
   * @param sql    the sql query to execute
   * @return the list of projects that have invitations
   * @throws DatabaseException         If the resultSet does not contain one user
   *                                   or if there is an error during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private List<Project> getProjectsWithInvitations(User sender, String sql)
      throws DatabaseException, ConnectionFailedException {
    List<Project> projectList = null;

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      preparedStatement.setString(1, sender.getUsername());
      try (ResultSet rs = preparedStatement.executeQuery()) {
        projectList = extractProjectsFromResultSet(rs);
      }
    } catch (SQLException | DatabaseException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return projectList;
  }

  /**
   * Gets the projects with invitations refused.
   *
   * @param sender the sender who has sent the invitations
   * @return the list of projects with invitations refused
   * @throws DatabaseException         If the resultSet does not contain one user
   *                                   or if there is an error during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Project> getProjectsWithInvitationRefused(User sender)
      throws DatabaseException, ConnectionFailedException {
    String sql = "SELECT * FROM Project "
        + "JOIN ProjectCollaboration ON Project.Id = ProjectCollaboration.ProjectId "
        + "WHERE Accepted = 0 AND InvitationRead = 0 AND Author = ? "
        + "GROUP BY ProjectId";

    return getProjectsWithInvitations(sender, sql);
  }

  /**
   * Gets the projects with invitations that have not been answered yet.
   *
   * @param sender the sender who has sent the invitations
   * @return the list of projects that have not been answered yet
   * @throws DatabaseException         If the resultSet does not contain one user
   *                                   or if there is an error during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Project> getProjectsWithInvitationWaiting(User sender)
      throws DatabaseException, ConnectionFailedException {
    String sql = "SELECT * FROM Project "
        + "JOIN ProjectCollaboration ON Project.Id = ProjectCollaboration.ProjectId "
        + "WHERE Accepted = -1 AND Author = ? "
        + "GROUP BY ProjectId";

    return getProjectsWithInvitations(sender, sql);
  }

  /**
   * Deletes a collaboration.
   *
   * @param project      the project
   * @param collaborator the collaborator
   * @throws DatabaseException         If the resultSet does not contain one user
   *                                   or if there is an error during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void deleteProjectCollaboration(Project project, User collaborator)
      throws DatabaseException, ConnectionFailedException {
    List<String> param = new ArrayList<>();
    param.add(project.getId() + "");
    param.add(collaborator.getId() + "");

    String sql = "DELETE FROM ProjectCollaboration "
        + "WHERE ProjectId = ? AND UserId = ?";

    executeUpdate(sql, param);
  }

  /**
   * Deletes the row with a refused invitation.
   *
   * @param project      project to delete.
   * @param collaborator the collaborator to the project that has refused the invitation.
   * @throws DatabaseException         If the resultSet does not contain one user
   *                                   or if there is an error during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void deleteRowWithRefusedInvitation(Project project, User collaborator)
      throws DatabaseException, ConnectionFailedException {
    List<String> param = new ArrayList<>();
    param.add(project.getId() + "");
    param.add(collaborator.getId() + "");

    String sql = "DELETE FROM ProjectCollaboration "
        + "WHERE ProjectId = ? AND UserId = ? AND Accepted = 0";

    executeUpdate(sql, param);
  }

  /**
   * Updates the InvitationRead column in the database to 1.
   * It means that the sender of invitation has read the window with
   * all the invitations that he sent to his collaboration.
   *
   * @param project      the project which is the ProjectID column.
   * @param collaborator the collaborator which is the UserId column
   * @throws DatabaseException         If the resultSet does not contain one user
   *                                   or if there is an error during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void updateInvitationRead(Project project, User collaborator)
      throws DatabaseException, ConnectionFailedException {
    List<String> param = new ArrayList<>();
    param.add(project.getId() + "");
    param.add(collaborator.getId() + "");

    String sql = "UPDATE ProjectCollaboration "
        + "SET InvitationRead = 1 "
        + "WHERE ProjectId = ? AND UserId = ? AND Accepted = 1";

    executeUpdate(sql, param);
  }

}

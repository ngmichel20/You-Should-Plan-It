package be.ac.ulb.infof307.g09.database;

import be.ac.ulb.infof307.g09.application.models.Color;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Tag;
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
import javafx.util.Pair;

/**
 * The class is used to handle CRUD actions with the "Project" table.
 *
 * @author Nguyen Khanh-Michel
 * @author El Bakkali Soufian
 */
public final class ProjectDatabase extends Database {

  public static final String INSERT_PROJECT_QUERY = "INSERT INTO "
      + "Project(Title,Description,StartDate,EndDate,InitialDuration,Author,ParentProject,Color)"
      + " VALUES(?,?,?,?,?,?,?,?)";
  private static ProjectDatabase instance;

  private ProjectDatabase() {
  }

  /**
   * Returns the singleton instance.
   *
   * @return The singleton instance.
   */
  public static ProjectDatabase getInstance() {
    if (instance == null) {
      instance = new ProjectDatabase();
    }
    return instance;
  }

  /**
   * Get the project from database.
   *
   * @param resultSet result of the sql statement
   * @return the project from the resultSet
   * @throws SQLException      throws when something wrong happens during
   *                           the fetching of data from the database
   * @throws DatabaseException throws when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Project getProject(ResultSet resultSet)
      throws SQLException, DatabaseException, ConnectionFailedException {
    Project project;
    Project parent = null;
    if (resultSet.getString("ParentProject") != null) {
      parent = getProjectById(resultSet.getInt("ParentProject"));
    }
    project = new Project(
        resultSet.getInt("Id"),
        resultSet.getString("Title"),
        resultSet.getString("Description"),
        resultSet.getLong("StartDate"),
        resultSet.getLong("EndDate"),
        resultSet.getLong("InitialDuration"),
        UserDatabase.getInstance().getByUsername(resultSet.getString("Author")),
        parent,
        Color.fromInteger(resultSet.getInt("Color"))
    );

    initProject(project);
    return project;
  }

  /**
   * Initialises the project with all its tasks, tags, collaborators and sub projects.
   *
   * @param project the project to initialise
   * @throws DatabaseException throws when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private void initProject(Project project) throws DatabaseException, ConnectionFailedException {
    List<Task> projectTasks = TaskDatabase.getInstance().getAllTasksOfProject(project);
    for (Task task : projectTasks) {
      task.setAssignedUsers(CollaboratorTaskDatabase.getInstance()
          .getAssignedCollaboratorsToTask(task));
    }
    project.setTasks(projectTasks);
    project.setTags(this.getTagsOfProject(project));
    project.setCollaborators(
        ProjectCollaborationDatabase.getInstance().getCollaboratorsOfProjects(project));
    project.setSubProjects(this.findSubProjects(project));
    //Recursive call for the sub project.
    for (Project subProject : project.getSubProjects()) {
      initProject(subProject);
    }
  }

  /**
   * Get the project by their id.
   *
   * @param id The id of the project (int)
   * @return The project corresponding to the id, null otherwise
   * @throws DatabaseException throws when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Project getProjectById(int id) throws DatabaseException, ConnectionFailedException {
    Project project = null;

    String sql = "SELECT * "
        + "FROM Project WHERE Id = ?";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setInt(1, id);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        project = getProject(resultSet);
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }

    return project;
  }

  /**
   * Get the all the parent projects of the author.
   *
   * @param author The author of the projects
   * @return All projects corresponding to the author, null otherwise
   * @throws DatabaseException throws when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Project> getAllParentProjectsByAuthor(String author)
      throws DatabaseException, ConnectionFailedException {

    Project project;

    String sql = "SELECT * "
        + "FROM Project WHERE Author = ? AND ParentProject IS NULL";

    List<Project> list = new ArrayList<>();

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setString(1, author);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {

        while (resultSet.next()) {
          project = getProject(resultSet);

          list.add(project);
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
   * Returns the subprojects of a certain project.
   *
   * @param parent The parent project
   * @return The subprojects of a certain project, null otherwise
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Project> findSubProjects(Project parent)
      throws DatabaseException, ConnectionFailedException {

    Project project;
    String sql = "SELECT * FROM Project where ParentProject = ?";
    List<Project> list = new ArrayList<>();

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setInt(1, parent.getId());
      try (ResultSet resultSet = preparedStatement.executeQuery()) {

        while (resultSet.next()) {
          User userAuthor = UserDatabase.getInstance().getByUsername(resultSet.getString("Author"));

          project = new Project(
              resultSet.getInt("Id"),
              resultSet.getString("Title"),
              resultSet.getString("Description"),
              resultSet.getLong("StartDate"),
              resultSet.getLong("EndDate"),
              resultSet.getLong("InitialDuration"),
              userAuthor,
              parent,
              Color.fromInteger(resultSet.getInt("Color"))
          );

          list.add(project);
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
   * Insert a new project with NO parent in the Project table.
   *
   * @param project the project to insert
   * @return The freshly inserted project
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Project insertProject(Project project)
      throws DatabaseException, ConnectionFailedException {

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(INSERT_PROJECT_QUERY)) {
      preparedStatement.setString(1, project.getTitle());
      preparedStatement.setString(2, project.getDescription());
      preparedStatement.setLong(3, project.getStartDate());
      preparedStatement.setLong(4, project.getEndDate());
      preparedStatement.setLong(5, project.getInitialDuration());
      preparedStatement.setString(6, project.getAuthor().getUsername());
      preparedStatement.setString(7, null);
      preparedStatement.setInt(8, project.getColorCode());

      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }

    return getProjectById(findLastInsertedRow("Project"));
  }

  /**
   * Insert a new project with ONE parent in the Project table.
   *
   * @param subProject the sub project to insert
   * @return The freshly inserted project
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Project insertSubProject(Project subProject)
      throws DatabaseException, ConnectionFailedException {

    checkIfProjectExists(subProject.getParentProject());

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(INSERT_PROJECT_QUERY)) {
      setProject(subProject, preparedStatement);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return getProjectById(findLastInsertedRow("Project"));
  }

  /**
   * Defines parameters for the SQL query.
   *
   * @param project           the given project
   * @param preparedStatement the statement of the SQL query
   * @throws SQLException throws when something wrong happens during
   *                      the fetching of data from the database
   */
  private void setProject(Project project, PreparedStatement preparedStatement)
      throws SQLException {
    preparedStatement.setString(1, project.getTitle());
    preparedStatement.setString(2, project.getDescription());
    preparedStatement.setLong(3, project.getStartDate());
    preparedStatement.setLong(4, project.getEndDate());
    preparedStatement.setLong(5, project.getInitialDuration());
    preparedStatement.setString(6, project.getAuthor().getUsername());
    preparedStatement.setInt(7, project.getParentProject().getId());
    preparedStatement.setInt(8, project.getColorCode());
  }

  /**
   * Update the project title, description and the end date.
   *
   * @param id          The id of the project to update
   * @param title       The title of the project
   * @param description The description of the project
   * @param endDate     The end date of the project
   * @param color              the project color
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void updateProject(int id, String title, String description, long endDate, Color color)
      throws DatabaseException, ConnectionFailedException {

    checkIfProjectExistsById(id);
    String sql = "UPDATE Project SET Title = ?, Description = ?, EndDate = ?, Color = ? "
        + "WHERE Id = ?";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setString(1, title);
      preparedStatement.setString(2, description);
      preparedStatement.setLong(3, endDate);
      preparedStatement.setInt(4, color.getColorCode());
      preparedStatement.setInt(5, id);

      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }

  /**
   * Deletes a project.
   *
   * @param id The id of the project to delete
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void delete(int id) throws DatabaseException, ConnectionFailedException {
    String sql = "DELETE FROM Project WHERE Id = ?";

    checkIfProjectExistsById(id);

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }

  /**
   * Checks if a project exists.
   *
   * @param id The id of the project to check
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void checkIfProjectExistsById(int id) throws DatabaseException, ConnectionFailedException {
    if (getProjectById(id) == null) {
      throw new IllegalArgumentException("This project does not exist !");
    }
  }

  /**
   * Checks if a project has a parent.
   *
   * @param project The id of the project to check
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void checkIfProjectExists(Project project) throws DatabaseException, ConnectionFailedException {
    if (project == null || getProjectById(project.getId()) == null) {
      throw new IllegalArgumentException("This parent project does not exist !");
    }
  }

  /**
   * Get the tags from the database (from the table "ProjectTag").
   *
   * @param project the project
   * @return list of tags
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Tag> getTagsOfProject(Project project)
      throws DatabaseException, ConnectionFailedException {
    List<Tag> list = new ArrayList<>();

    String sql = "SELECT *  FROM Tag INNER JOIN ProjectTag"
        + " ON Tag.Id = ProjectTag.TagId WHERE ProjectTag.ProjectId = ?";
    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setInt(1, project.getId());
      try (ResultSet resultSet = preparedStatement.executeQuery()) {

        while (resultSet.next()) {
          list.add(new Tag(resultSet.getInt("Id"), resultSet.getString("Description")));
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
   * Gives the id of the Projects with the id of the Tags that are Unique.
   *
   * @return An array list of the pair (ProjectID,TagID)
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Pair<Integer, Integer>> getProjectsWithUniqueTags()
      throws DatabaseException, ConnectionFailedException {
    List<Pair<Integer, Integer>> list = new ArrayList<>();
    String sql = "SELECT *"
        + "From ProjectTag GROUP BY ProjectTag.TagId HAVING COUNT(*)=1";
    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {

        while (resultSet.next()) {
          list.add(new Pair<>(resultSet.getInt("projectId"), resultSet.getInt("tagId")));
        }
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return list;
  }
}
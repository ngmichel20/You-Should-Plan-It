package be.ac.ulb.infof307.g09.application.models.handlers;

import be.ac.ulb.infof307.g09.application.models.Color;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.State;
import be.ac.ulb.infof307.g09.application.models.Tag;
import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.util.List;
import java.util.Observable;
import javafx.util.Pair;

/**
 * Handles the application and keeps all user data up to date.
 */
public class Application extends Observable {
  private User user;
  private final Session session;
  private ProjectHandler projectHandler;
  private TaskHandler taskHandler;
  private TagHandler tagHandler;
  private CollaborationHandler collaborationHandler;
  private State state;

  /**
   * Initialises the application. By default, the user is not connected.
   */
  public Application() {
    this.user = null;
    this.projectHandler = null;
    this.taskHandler = null;
    this.taskHandler = null;
    this.collaborationHandler = null;
    this.state = State.APPLICATION_CREATED;
    this.session = new Session();
  }

  /**
   * Connects the user to the application and loads his projects.
   *
   * @param username the username of the user
   * @param password password of the user
   * @throws DatabaseException         if a problem occurs while fetching the user's projects.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void connect(String username, String password)
      throws DatabaseException, ConnectionFailedException {
    session.connect(username, password);
    this.user = session.getUser();
    initHandlers();
  }

  /**
   * Register a new user to the application.
   *
   * @param newUser the user to register
   * @throws DatabaseException         This exception is thrown to let the user
   *                                   know that there is a database failure
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void register(User newUser) throws DatabaseException, ConnectionFailedException {
    session.register(newUser);
    this.user = session.getUser();
    initHandlers();
  }

  /**
   * Initializes the sub facades.
   */
  private void initHandlers() {
    this.projectHandler = new ProjectHandler(user);
    this.taskHandler = new TaskHandler(user);
    this.tagHandler = new TagHandler(user);
    this.collaborationHandler = new CollaborationHandler(user);
  }

  /**
   * Disconnects a user.
   */
  public void disconnect() {
    session.disconnect();
    this.user = null;
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
  public List<User> loadUsersFromDatabase()
      throws DatabaseException, ConnectionFailedException {
    return this.session.loadUsersFromDatabase();
  }

  /**
   * Updates the user.
   *
   * @param newUser user with the new parameters
   * @throws DatabaseException         This exception is thrown to let the user
   *                                   know that there is a database failure
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void updateUser(User newUser)
      throws DatabaseException, ConnectionFailedException {
    session.updateUser(newUser);
  }

  /**
   * Returns the connected user.
   *
   * @return User object
   */
  public User getUser() {
    return session.getUser();
  }

  /**
   * Returns the username of the user.
   *
   * @return the username
   */
  public String getUsername() {
    return session.getUsername();
  }

  /**
   * Returns the email of the user.
   *
   * @return the email
   */
  public String getEmail() {
    return session.getEmail();
  }

  /**
   * Returns the first name of the user.
   *
   * @return the first name
   */
  public String getFirstName() {
    return session.getFirstName();
  }

  /**
   * Returns the last name of the user.
   *
   * @return the last name
   */
  public String getLastName() {
    return session.getLastName();
  }

  /**
   * Returns the password of the user.
   *
   * @return the password
   */
  public String getPassword() {
    return session.getPassword();
  }

  /**
   * Returns the user's projects and the projects which he is a collaborator of.
   *
   * @return List of projects
   */
  public List<Project> getUserProjects() {
    return projectHandler.getUserProjects();
  }

  /**
   * Returns the user parent projects.
   *
   * @return List of parent projects
   */
  public List<Project> getUserParentProjects() {
    return user.getProjectList();
  }

  /**
   * Returns the user's projects which he is the author of.
   *
   * @return List of projects
   */
  public List<Project> getAuthorProjects() {
    return user.getAuthorProjects();
  }

  /**
   * Check if the user exists by username.
   *
   * @param username the username to check
   * @return true if user exists, false otherwise
   * @throws DatabaseException         if an error occurs during the verification
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public boolean checkIfUsernameExists(String username)
      throws DatabaseException, ConnectionFailedException {
    return this.session.checkIfUsernameExists(username);
  }

  /**
   * Check if the given email exists.
   *
   * @param email the email to check
   * @return true if the email exists, false otherwise
   * @throws DatabaseException         if an error occurs during the verification
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public boolean checkIfEmailExists(String email)
      throws DatabaseException, ConnectionFailedException {
    return this.session.checkIfEmailExists(email);
  }

  /**
   * Calls the function in ProjectDatabase that inserts the project,
   * inserts the project in the list of projects and notifies all the observers.
   *
   * @param newProject   project to insert
   * @param isSubProject True if we want to insert a subproject, false for a parent project
   * @return the inserted project
   * @throws DatabaseException         if a problem occurs while adding the new project
   *                                   to the ProjectDatabase.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Project insertProject(Project newProject, boolean isSubProject)
      throws DatabaseException, ConnectionFailedException {
    Project createdProject = this.projectHandler.insertProject(newProject, isSubProject);
    setState(State.PROJECT_CREATED);
    notifyObservers(createdProject);
    return createdProject;
  }

  /**
   * Adds a project to the application without inserting it to the database.
   *
   * @param newProject the new project to add
   */
  public void addCollaboratorProject(Project newProject) {
    this.collaborationHandler.addCollaboratorProject(newProject);
    setState(State.PROJECT_CREATED);
    notifyObservers(newProject);
  }

  /**
   * Removes the collaboration of the connected user from the selected project.
   *
   * @param project the project
   * @throws DatabaseException         if a problem occurs while deleting
   *                                   the project in projectDatabase
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void removeCollaborationFromProject(Project project)
      throws DatabaseException, ConnectionFailedException {
    this.collaborationHandler.removeCollaborationFromProject(project);
    setState(State.COLLABORATOR_REMOVED);
    notifyObservers(project);
  }

  /**
   * Retrieves all the collaborators that have been invited to a project.
   *
   * @param project The given project.
   * @return All the collaborators of a project.
   * @throws DatabaseException         If there is an error during the execution of the query
   *                                   or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<User> getCollaboratorsInvitedByProject(Project project)
      throws DatabaseException, ConnectionFailedException {
    return this.collaborationHandler.getCollaboratorsInvitedByProject(project);
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
    return this.collaborationHandler.getCollaboratorsByProjectWaiting(project);
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
    return this.collaborationHandler.getCollaboratorsByProjectRefused(project);
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
    return this.collaborationHandler.getCollaboratorsByProjectAccepted(project);
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
    this.collaborationHandler.deleteRowWithRefusedInvitation(project, collaborator);
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
    this.collaborationHandler.updateInvitationRead(project, collaborator);
  }

  /**
   * Deletes the project from the application.
   *
   * @param projectToDelete project to delete
   * @throws DatabaseException         if a problem occurs while deleting
   *                                   the project in projectDatabase
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void deleteProject(Project projectToDelete)
      throws DatabaseException, ConnectionFailedException {
    this.projectHandler.deleteProject(projectToDelete);
    setState(State.PROJECT_DELETED);
    notifyObservers(projectToDelete);
  }

  /**
   * Calls the function in ProjectDatabase that updates the parent/sub project and
   * also calls the function that updates the project in the list of projects.
   *
   * @param projectToModify    the project to modify
   * @param projectTitle       the new title of the project
   * @param projectDescription the new description of the project
   * @param endDate            the new end date
   * @param color              the new color
   * @throws DatabaseException         if a problem occurs while updating
   *                                   the parent/sub project in the ProjectDatabase
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void updateProject(Project projectToModify, String projectTitle,
                            String projectDescription, long endDate, Color color)
      throws DatabaseException, ConnectionFailedException {
    this.projectHandler.updateProject(
        projectToModify, projectTitle, projectDescription, endDate, color);
    setState(State.PROJECT_MODIFIED);
    notifyObservers(projectToModify);
  }

  /**
   * Returns the sub projects of the specific project.
   *
   * @param parentProject the parent project
   * @return a list containing all the sub projects of the parent project
   */
  public List<Project> getSubProjects(Project parentProject) {
    return this.projectHandler.getSubProjects(parentProject);
  }

  /**
   * Returns the state of the application.
   *
   * @return the state
   */
  public State getState() {
    return state;
  }

  /**
   * Sets the state of the application.
   *
   * @param state the state to set
   */
  public void setState(State state) {
    setChanged();
    this.state = state;
  }

  /**
   * Adds a new task to a project then notifies all the observers.
   *
   * @param project        the project
   * @param newDescription the task description
   * @param startDateHour  the task start date
   * @param endDateHour    the task end date
   * @throws DatabaseException         throws when something wrong happens during
   *                                   a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void addTaskToProject(Project project, String newDescription,
                               Long startDateHour, Long endDateHour)
      throws DatabaseException, ConnectionFailedException {
    Task createdTask =
        this.taskHandler.addTaskToProject(project, newDescription, startDateHour, endDateHour);
    setState(State.TASK_CREATED);
    notifyObservers(createdTask);
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
  public void updateTaskInProject(Project project, Task taskToEdit,
                                  String newDescription, Long startDateHour, Long endDateHour)
      throws DatabaseException, ConnectionFailedException {
    this.taskHandler.updateTaskInProject(
        project, taskToEdit, newDescription, startDateHour, endDateHour);
    setState(State.TASK_MODIFIED);
    notifyObservers(taskToEdit);
  }

  /**
   * Handles the assignations of the users to the tasks.
   *
   * @param task         the task in which the assignations are handled
   * @param assignations assignations to add or remove
   * @throws DatabaseException         if an error occurs during the insertion or deletion of a task
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void handleAssignations(Task task, List<User> assignations)
      throws DatabaseException, ConnectionFailedException {
    this.taskHandler.handleAssignations(task, assignations);
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
  public void removeTaskFromProject(Project project, Task task)
      throws DatabaseException, ConnectionFailedException {
    this.taskHandler.removeTaskFromProject(project, task);
    setState(State.TASK_DELETED);
    notifyObservers(task);
  }

  /**
   * Returns the collaborators of a project.
   *
   * @param project The project
   * @return The collaborators of a project.
   * @throws DatabaseException         if there is an issue during the access of the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<User> getCollaboratorsByProject(Project project)
      throws DatabaseException, ConnectionFailedException {
    return this.collaborationHandler.getCollaboratorsByProject(project);
  }

  /**
   * Get user who has unread notifications.
   *
   * @param userWithUnreadNotifications the current user who has unread notifications
   * @return collaborator who has unread notifications
   * @throws DatabaseException         If there is an issue during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public User getUserWithUnreadNotifications(User userWithUnreadNotifications)
      throws DatabaseException, ConnectionFailedException {
    return this.collaborationHandler.getUserWithUnreadNotifications(userWithUnreadNotifications);
  }

  /**
   * Gets all unanswered projects of a collaborator.
   *
   * @param userWithUnreadNotifications the user to check if he
   *                                    has unread notifications collaborations
   * @return the list of projects unanswered
   * @throws DatabaseException         if an error occur during the fetching of the sql query
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Project> getAllUnansweredProjects(User userWithUnreadNotifications)
      throws DatabaseException, ConnectionFailedException {
    return this.collaborationHandler.getAllUnansweredProjects(userWithUnreadNotifications);
  }

  /**
   * Retrieves a project of a collaborator specified in the argument.
   *
   * @param actualProject The project to find
   * @param collaborator  The given collaborator (as an existing user).
   * @return A project of a collaborator.
   * @throws DatabaseException         If there is an error during the execution of the query
   *                                   or during the extraction of the result.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Project getProjectByCollaborator(Project actualProject, User collaborator)
      throws DatabaseException, ConnectionFailedException {
    return this.collaborationHandler.getProjectByCollaborator(actualProject, collaborator);
  }

  /**
   * Add a user as a collaborator to a project.
   *
   * @param actualProject the project
   * @param collaborator  the user
   * @throws DatabaseException         if there is an error during execution of the query.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void insertProjectCollaboratorRow(Project actualProject, User collaborator)
      throws DatabaseException, ConnectionFailedException {
    this.collaborationHandler.insertProjectCollaboratorRow(actualProject, collaborator);
  }

  /**
   * Update the accepted column of a project collaboration.
   *
   * @param projectId    The id of the project
   * @param collaborator The collaborator
   * @param answer       The answer of the invitation
   * @return the project that has been updated
   * @throws DatabaseException         when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Project updateAcceptedColumn(int projectId, User collaborator, int answer)
      throws DatabaseException, ConnectionFailedException {
    return this.collaborationHandler.updateAcceptedColumn(projectId, collaborator, answer);
  }

  /**
   * Gets the projects with refused invitations.
   *
   * @param sender the sender user who has sent the invitation
   * @return all the projects that have been refused
   * @throws DatabaseException         when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Project> getProjectsWithInvitationsRefused(User sender)
      throws DatabaseException, ConnectionFailedException {
    return this.collaborationHandler.getProjectsWithInvitationsRefused(sender);
  }

  /**
   * Gets the projects with accepted invitations.
   *
   * @param sender the sender user who has sent the invitation
   * @return all the projects that have been accepted
   * @throws DatabaseException         when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Project> getProjectsWithInvitationsAccepted(User sender)
      throws DatabaseException, ConnectionFailedException {
    return this.collaborationHandler.getProjectsWithInvitationsAccepted(sender);
  }

  /**
   * Gets the projects with waiting invitations.
   *
   * @param sender the sender user who has sent the invitation
   * @return all the projects that are in waiting
   * @throws DatabaseException         when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Project> getProjectsWithInvitationsWaiting(User sender)
      throws DatabaseException, ConnectionFailedException {
    return this.collaborationHandler.getProjectsWithInvitationsWaiting(sender);
  }

  /**
   * Get all the Tags that exist in all the projects of the user.
   *
   * @return All tags of user
   */
  public List<Tag> getAllUserTags() {
    return this.tagHandler.getAllUserTags();
  }

  /**
   * Adds a tag to the project.
   *
   * @param project the project
   * @param tag     the tag
   * @throws DatabaseException         if an error occurs during the insertion of the tag
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void addTagToProject(Project project, Tag tag)
      throws DatabaseException, ConnectionFailedException {
    this.tagHandler.addTagToProject(project, tag);
    setState(State.TAG_CREATED);
    notifyObservers(project);
  }

  /**
   * Gets the tag from the database.
   *
   * @param tagDescription the description of the tag
   * @return the tag found
   * @throws DatabaseException         if a problem occurs during the access to the database
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Tag getTagFromDatabase(String tagDescription)
      throws DatabaseException, ConnectionFailedException {
    return this.tagHandler.getTagFromDatabase(tagDescription);
  }

  /**
   * Deletes a tag from the project.
   *
   * @param project the project
   * @param tag     the tag
   * @throws DatabaseException         if an error occurs during the deletion of the tag
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void deleteTagFromProject(Project project, Tag tag)
      throws DatabaseException, ConnectionFailedException {
    this.tagHandler.deleteTagFromProject(project, tag);
    setState(State.TAG_DELETED);
    notifyObservers(project);
  }

  /**
   * Returns the projects with unique tags.
   *
   * @return a list containing projects with unique tags
   * @throws DatabaseException         if an error occurs during the fetching of the projects
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public List<Pair<Integer, Integer>> getProjectsWithUniqueTags()
      throws DatabaseException, ConnectionFailedException {
    return this.projectHandler.getProjectsWithUniqueTags();
  }

  /**
   * Returns the tag from the database by the specified description.
   *
   * @param description the description of the tag
   * @return the tag found in the database
   * @throws DatabaseException         if an error occurs during the fetching of the tag
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Tag getTag(String description) throws DatabaseException, ConnectionFailedException {
    return this.tagHandler.getTag(description);
  }

  /**
   * Creates a new tag in the database specified by the description.
   *
   * @param description the description of the new tag
   * @return the created tag in the database
   * @throws DatabaseException         if an error occurs during the creation of the tag
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public Tag createTag(String description) throws DatabaseException, ConnectionFailedException {
    return this.tagHandler.createTag(description);
  }

  /**
   * Deletes a tag from the database specified by the id.
   *
   * @param tagId the id of the tag
   * @throws DatabaseException         if an error occurs during the deletion of the tag
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public void deleteTag(int tagId) throws DatabaseException, ConnectionFailedException {
    this.tagHandler.deleteTag(tagId);
  }

  /**
   * Returns the tasks that will end in less than 24 hours.
   *
   * @return the list of user's tasks that will end in less than 24 hours
   */
  public List<Task> getUserTasksCloseToDeadline() {
    return taskHandler.getUserTasksCloseToDeadline();
  }
}

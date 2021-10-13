package be.ac.ulb.infof307.g09.application.models.handlers;

import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.database.ProjectCollaborationDatabase;
import be.ac.ulb.infof307.g09.database.ProjectDatabase;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sub Facade that handles the collaborations.
 */
class CollaborationHandler {
  private final User user;
  private final ProjectDatabase projectDatabase;
  private final ProjectCollaborationDatabase projectCollaborationDatabase;

  /**
   * Initialises the collaboration facade with the given user.
   *
   * @param currentUser the user
   */
  CollaborationHandler(User currentUser) {
    this.user = currentUser;
    this.projectDatabase = ProjectDatabase.getInstance();
    this.projectCollaborationDatabase = ProjectCollaborationDatabase.getInstance();
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
  Project getProjectByCollaborator(Project actualProject, User collaborator)
      throws DatabaseException, ConnectionFailedException {
    return this.projectCollaborationDatabase.getProjectByCollaborator(actualProject, collaborator);
  }

  /**
   * Add a user as a collaborator to a project.
   *
   * @param actualProject the project
   * @param collaborator  the user
   * @throws DatabaseException         if there is an error during execution of the query.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void insertProjectCollaboratorRow(Project actualProject, User collaborator)
      throws DatabaseException, ConnectionFailedException {
    this.projectCollaborationDatabase.insertProjectCollaboratorRow(actualProject, collaborator);
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
  Project updateAcceptedColumn(int projectId, User collaborator, int answer)
      throws DatabaseException, ConnectionFailedException {
    ProjectCollaborationDatabase.getInstance().updateAcceptedColumn(
        projectId, collaborator, answer);
    return this.projectDatabase.getProjectById(projectId);
  }


  /**
   * Gets the projects with refused invitations.
   *
   * @param sender the sender user who has sent the invitation
   * @return all the projects that have been refused
   * @throws DatabaseException         when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  List<Project> getProjectsWithInvitationsRefused(User sender)
      throws DatabaseException, ConnectionFailedException {

    List<Project> projectsWithRefusedInvitation =
        ProjectCollaborationDatabase.getInstance().getProjectsWithInvitationRefused(sender);
    List<Project> projectsRefused = new ArrayList<>();
    for (Project project : projectsWithRefusedInvitation) {
      for (Project userProject : user.getProjectList()) {
        if (userProject.equals(project)) {
          projectsRefused.add(userProject);
        }
      }
    }
    return projectsRefused;
  }

  /**
   * Gets the projects with accepted invitations.
   *
   * @param sender the sender user who has sent the invitation
   * @return all the projects that have been accepted
   * @throws DatabaseException         when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  List<Project> getProjectsWithInvitationsAccepted(User sender)
      throws DatabaseException, ConnectionFailedException {

    List<Project> projectsWithAcceptedInvitation =
        ProjectCollaborationDatabase.getInstance().getProjectsWithInvitationAccepted(sender);
    List<Project> projectsAccepted = new ArrayList<>();
    for (Project project : projectsWithAcceptedInvitation) {
      for (Project userProject : user.getProjectList()) {
        if (userProject.equals(project)) {
          projectsAccepted.add(userProject);
        }
      }
    }
    return projectsAccepted;
  }

  /**
   * Gets the projects with waiting invitations.
   *
   * @param sender the sender user who has sent the invitation
   * @return all the projects that are in waiting
   * @throws DatabaseException         when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  List<Project> getProjectsWithInvitationsWaiting(User sender)
      throws DatabaseException, ConnectionFailedException {

    List<Project> projectsWithInvitationWaiting =
        ProjectCollaborationDatabase.getInstance().getProjectsWithInvitationWaiting(sender);
    List<Project> projectsNotAccepted = new ArrayList<>();
    for (Project project : projectsWithInvitationWaiting) {
      for (Project userProject : user.getProjectList()) {
        if (userProject.equals(project)) {
          projectsNotAccepted.add(userProject);
        }
      }
    }
    return projectsNotAccepted;
  }


  /**
   * Returns the collaborators of a project.
   *
   * @param project The project
   * @return The collaborators of a project.
   * @throws DatabaseException         if there is an issue during the access of the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  List<User> getCollaboratorsByProject(Project project)
      throws DatabaseException, ConnectionFailedException {
    List<User> collaborators;
    try {
      collaborators = projectCollaborationDatabase.getCollaboratorsOfProjects(project);
    } catch (DatabaseException e) {
      throw new DatabaseException(ErrorMessagesUtils.ERROR_COLLABORATORS_ACCESS, e);
    }
    return collaborators;
  }

  /**
   * Get user who has unread notifications.
   *
   * @param userWithUnreadNotifications the current user who has unread notifications
   * @return collaborator who has unread notifications
   * @throws DatabaseException         If there is an issue during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  User getUserWithUnreadNotifications(User userWithUnreadNotifications)
      throws DatabaseException, ConnectionFailedException {
    return this.projectCollaborationDatabase
        .getUserWithUnreadNotifications(userWithUnreadNotifications);
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
  List<Project> getAllUnansweredProjects(User userWithUnreadNotifications)
      throws DatabaseException, ConnectionFailedException {
    List<Project> projectList = new ArrayList<>();
    if (userWithUnreadNotifications != null) {
      projectList = ProjectCollaborationDatabase.getInstance()
          .getAllUnansweredProjectsByCollaborator(userWithUnreadNotifications);
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
  List<User> getCollaboratorsInvitedByProject(Project project)
      throws DatabaseException, ConnectionFailedException {
    return projectCollaborationDatabase.getCollaboratorsInvitedByProject(project);
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
  List<User> getCollaboratorsByProjectWaiting(Project project)
      throws DatabaseException, ConnectionFailedException {
    return projectCollaborationDatabase.getCollaboratorsByProjectWaiting(project);
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
  List<User> getCollaboratorsByProjectRefused(Project project)
      throws DatabaseException, ConnectionFailedException {
    return projectCollaborationDatabase.getCollaboratorsByProjectRefused(project);
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
  List<User> getCollaboratorsByProjectAccepted(Project project)
      throws DatabaseException, ConnectionFailedException {
    return projectCollaborationDatabase.getCollaboratorsByProjectAccepted(project);
  }

  /**
   * Deletes the row with a refused invitation.
   *
   * @param project      project to delete.
   * @param collaborator the collaborator to the project that has refused the invitation.
   * @throws DatabaseException If the resultSet does not contain one user
   *                           or if there is an error during the access to the database.
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void deleteRowWithRefusedInvitation(Project project, User collaborator)
      throws DatabaseException, ConnectionFailedException {
    this.projectCollaborationDatabase.deleteRowWithRefusedInvitation(project, collaborator);
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
  void updateInvitationRead(Project project, User collaborator)
      throws DatabaseException, ConnectionFailedException {
    this.projectCollaborationDatabase.updateInvitationRead(project, collaborator);
  }

  /**
   * Adds a project to the application without inserting it to the database.
   *
   * @param newProject the new project to add
   */
  void addCollaboratorProject(Project newProject) {
    user.addProjectList(newProject);
  }

  /**
   * Removes the collaboration of the connected user from the selected project.
   *
   * @param project the project
   * @throws DatabaseException         if a problem occurs while deleting
   *                                   the project in projectDatabase
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void removeCollaborationFromProject(Project project)
      throws DatabaseException, ConnectionFailedException {
    projectCollaborationDatabase.deleteProjectCollaboration(project, user);
    user.removeProjectList(project);
    project.removeCollaborator(user);
  }
}

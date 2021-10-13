package be.ac.ulb.infof307.g09.application.models.handlers;

import be.ac.ulb.infof307.g09.application.models.Color;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.database.ProjectDatabase;
import be.ac.ulb.infof307.g09.database.TagDatabase;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 * Sub Facade that handles the projects.
 */
class ProjectHandler {
  private final User user;
  private final ProjectDatabase projectDatabase;
  private final TagDatabase tagDatabase;

  /**
   * Initialises the project facade with the given user.
   *
   * @param currentUser the user
   */
  ProjectHandler(User currentUser) {
    this.user = currentUser;
    this.projectDatabase = ProjectDatabase.getInstance();
    this.tagDatabase = TagDatabase.getInstance();
  }

  /**
   * Returns the user's projects and the projects which he is a collaborator of.
   *
   * @return List of projects
   */
  List<Project> getUserProjects() {
    List<Project> result = new ArrayList<>();
    findSubProjects(this.user.getProjectList(), result);
    return result;
  }

  /**
   * Find all subprojects of the user and put them into a list.
   *
   * @param userProjectList the user projects list
   * @param result          the list of all projects
   */
  private void findSubProjects(List<Project> userProjectList, List<Project> result) {
    for (Project project : userProjectList) {
      result.add(project);
      findSubProjects(project.getSubProjects(), result);
    }
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
  Project insertProject(Project newProject, boolean isSubProject)
      throws DatabaseException, ConnectionFailedException {
    Project createdProject;
    try {
      if (isSubProject) {
        createdProject = projectDatabase.insertSubProject(newProject);
        newProject.getParentProject().addSubProjects(createdProject);
      } else {
        createdProject = projectDatabase.insertProject(newProject);
        user.addProjectList(createdProject);
      }
    } catch (DatabaseException e) {
      throw new DatabaseException(ErrorMessagesUtils.ERROR_INSERT_PROJECT, e);
    }
    return createdProject;
  }

  /**
   * Deletes the project from the application.
   *
   * @param projectToDelete project to delete
   * @throws DatabaseException         if a problem occurs while deleting
   *                                   the project in projectDatabase
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void deleteProject(Project projectToDelete)
      throws DatabaseException, ConnectionFailedException {
    try {
      deleteUniqueTagsFromDatabase(projectToDelete);
      projectDatabase.delete(projectToDelete.getId());

      if (!projectToDelete.isSubProject()) {
        user.getProjectList().remove(projectToDelete);
        if (user.isAuthor(projectToDelete)) {
          user.removeAuthorProjects(projectToDelete);
        }
      } else {
        user.removeSubProject(projectToDelete);
      }
    } catch (DatabaseException e) {
      throw new DatabaseException(ErrorMessagesUtils.ERROR_DELETE_PROJECT, e);
    }
  }

  /**
   * Deletes the unique tags of the project from database.
   *
   * @param projectToDelete project
   * @throws DatabaseException         if a problem occurs while deleting the project
   *                                   in projectDatabase
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private void deleteUniqueTagsFromDatabase(Project projectToDelete)
      throws DatabaseException, ConnectionFailedException {
    List<Pair<Integer, Integer>> uniqueTags;
    uniqueTags = projectDatabase.getProjectsWithUniqueTags();

    for (Pair<Integer, Integer> projectTagPair : uniqueTags) {
      if (projectTagPair.getKey() == projectToDelete.getId()) {
        this.tagDatabase.deleteTag(projectTagPair.getValue());
      }
    }
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
  void updateProject(Project projectToModify, String projectTitle,
                     String projectDescription, long endDate, Color color)
      throws DatabaseException, ConnectionFailedException {
    try {
      projectDatabase.updateProject(
          projectToModify.getId(),
          projectTitle,
          projectDescription,
          endDate,
          color);
      updateUserProjects(projectToModify, projectTitle, projectDescription, endDate, color);
    } catch (DatabaseException e) {
      throw new DatabaseException(ErrorMessagesUtils.ERROR_UPDATE_PROJECT, e);
    }
  }

  /**
   * Updates the project in the list of projects and notifies all the observers.
   *
   * @param projectToModify    the project to modify
   * @param projectTitle       the new title of the project
   * @param projectDescription the new description of the project
   * @param endDate            the new end date
   * @param color              the new color
   */
  private void updateUserProjects(Project projectToModify, String projectTitle,
                                  String projectDescription, long endDate, Color color) {
    this.user.getProject(projectToModify).updateProject(
        projectTitle, projectDescription, endDate, color);

    for (Project project : this.user.getProjectList()) {
      if (project.getParentProject() != null
          && project.getParentProject().getId() == projectToModify.getId()) {
        project.setParentProject(projectToModify);
      }
    }
  }

  /**
   * Returns the sub projects of the specific project.
   *
   * @param parentProject the parent project
   * @return a list containing all the sub projects of the parent project
   */
  List<Project> getSubProjects(Project parentProject) {
    return parentProject.getSubProjects();
  }

  /**
   * Returns the projects with unique tags.
   *
   * @return a list containing projects with unique tags
   * @throws DatabaseException         if an error occurs during the fetching of the projects
   * @throws ConnectionFailedException If the connection to the database fails
   */
  List<Pair<Integer, Integer>> getProjectsWithUniqueTags()
      throws DatabaseException, ConnectionFailedException {
    try {
      return projectDatabase.getProjectsWithUniqueTags();
    } catch (DatabaseException e) {
      throw new DatabaseException(ErrorMessagesUtils.ERROR_PROJECT_UNIQUE_TAG, e);
    }
  }
}

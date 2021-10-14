package be.ac.ulb.infof307.g09.database;

import be.ac.ulb.infof307.g09.application.models.Color;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectCollaborationDatabaseTest {

  private static final String DATABASE_TEST_PATH = Paths.get("").toAbsolutePath() + "/test/test.db";
  private static final String START_PATH = "src/be/ac/ulb/infof307/g09/";
  private static File file;

  private static User collaborator1;
  private static User collaborator2;

  private static Project project;

  private static ProjectCollaborationDatabase db;

  @BeforeEach
  void setUp() throws DatabaseException, ConnectionFailedException {
    ProjectDatabase.setDatabasePath(DATABASE_TEST_PATH);
    UserDatabase.createNewDatabaseFile(START_PATH);
    file = UserDatabase.getInstance().getDatabase();

    User author = UserDatabase.getInstance().insert(
        new User(-1, "testUser", "email1@email.fr", "lastName",
            "firstName", "password"));

    collaborator1 = UserDatabase.getInstance().insert(
        new User(-1, "testUser2", "email2@email.fr", "lastName",
            "firstName", "password"));
    collaborator2 = UserDatabase.getInstance().insert(
        new User(-1, "testUser3", "email3@email.fr", "lastName",
            "firstName", "password"));

    project = ProjectDatabase.getInstance().insertProject(new Project(-1, "newProject", "", 0, 0, 0, author, null, Color.BLUE));
    project.addSubProjects(ProjectDatabase.getInstance().insertProject(new Project(-1, "subProject", "", 0, 0, 0, author, project, Color.YELLOW)));
    db = ProjectCollaborationDatabase.getInstance();
    db.insertProjectCollaboratorRow(project, collaborator2);
  }

  @Test
  void testGetCollaboratorsByProjectAccepted() throws DatabaseException, ConnectionFailedException {
    db.updateAcceptedColumn(project.getId(), collaborator2, 1);
    List<User> userList = db.getCollaboratorsByProjectAccepted(project);
    assertTrue(userList.contains(collaborator2));
  }

  @Test
  void testGetCollaboratorsByProjectRefused() throws DatabaseException, ConnectionFailedException {
    db.updateAcceptedColumn(project.getId(), collaborator2, 0);
    List<User> userList = db.getCollaboratorsByProjectRefused(project);
    assertTrue(userList.contains(collaborator2));
  }

  @Test
  void testGetCollaboratorsByProjectWaiting() throws DatabaseException, ConnectionFailedException {
    List<User> userList = db.getCollaboratorsByProjectWaiting(project);
    assertTrue(userList.contains(collaborator2));
  }

  @Test
  void testGetProjectByCollaborator() throws DatabaseException, ConnectionFailedException {
    db.insertProjectCollaboratorRow(project, collaborator1);
    Project projectTest = db.getProjectByCollaborator(project, collaborator1);
    assertEquals(projectTest, project);
  }

  @Test
  void testGetProjectsByCollaborator() throws DatabaseException, ConnectionFailedException {
    db.updateAcceptedColumn(project.getId(), collaborator2, 1);
    List<Project> projectList = db.getProjectsByCollaborator(collaborator2);
    assertTrue(projectList.contains(project));
  }

  @Test
  void testInsertProjectCollaboratorRow() throws DatabaseException, ConnectionFailedException {
    db.insertProjectCollaboratorRow(project, collaborator1);
    db.updateAcceptedColumn(project.getId(), collaborator1, 1);
    List<User> userList = db.getCollaboratorsByProjectAccepted(project);
    assertTrue(userList.contains(collaborator1));
  }

  @Test
  void testGetUserWithUnreadNotifications() throws DatabaseException, ConnectionFailedException {
    db.insertProjectCollaboratorRow(project, collaborator1);
    User user = db.getUserWithUnreadNotifications(collaborator1);
    List<User> userList = db.getCollaboratorsInvitedByProject(project);
    assertTrue(userList.contains(user));
  }

  @Test
  void testGetProjectsWithInvitationAccepted() throws DatabaseException, ConnectionFailedException {
    db.insertProjectCollaboratorRow(project, collaborator1);
    db.updateAcceptedColumn(project.getId(), collaborator1, 1);
    List<Project> listOfProjectAccepted = db.getProjectsWithInvitationAccepted(project.getAuthor());
    assertTrue(listOfProjectAccepted.contains(project));
  }

  @Test
  void testGetProjectsWithInvitationRefused() throws DatabaseException, ConnectionFailedException {
    db.insertProjectCollaboratorRow(project, collaborator1);
    db.updateAcceptedColumn(project.getId(), collaborator1, 0);
    List<Project> listOfProjectRefused = db.getProjectsWithInvitationRefused(project.getAuthor());
    assertTrue(listOfProjectRefused.contains(project));
  }

  @Test
  void testGetProjectsWithInvitationWaiting() throws DatabaseException, ConnectionFailedException {
    db.insertProjectCollaboratorRow(project, collaborator1);
    List<Project> listOfProjectWithoutAnswers = db.getProjectsWithInvitationWaiting(project.getAuthor());
    assertTrue(listOfProjectWithoutAnswers.contains(project));
  }

  @Test
  void testDeleteProjectCollaboration() throws DatabaseException, ConnectionFailedException {
    db.insertProjectCollaboratorRow(project, collaborator1);
    db.deleteProjectCollaboration(project, collaborator1);

    List<User> userList = db.getCollaboratorsByProjectAccepted(project);
    assertFalse(userList.contains(collaborator1));
  }

  @Test
  void testDeleteRowWithRefusedInvitation() throws DatabaseException, ConnectionFailedException {
    db.insertProjectCollaboratorRow(project, collaborator1);
    db.updateAcceptedColumn(project.getId(), collaborator1, 0);
    db.deleteRowWithRefusedInvitation(project, collaborator1);
    List<User> userList = db.getCollaboratorsByProjectAccepted(project);
    assertFalse(userList.contains(collaborator1));
  }

  @Test
  void testUpdateInvitationRead() throws DatabaseException, ConnectionFailedException {
    db.insertProjectCollaboratorRow(project, collaborator1);
    db.updateInvitationRead(project, collaborator1);
    Project tmp = db.getProjectByCollaborator(project, collaborator1);
    assertEquals(project, tmp);
  }

  @Test
  void testGetAllUnansweredProjectsByCollaborator() throws DatabaseException, ConnectionFailedException {
    db.insertProjectCollaboratorRow(project, collaborator1);
    List<Project> listProjectsUnanswered = db.getAllUnansweredProjectsByCollaborator(collaborator1);
    assertTrue(listProjectsUnanswered.contains(project));
  }

  @Test
  void testGetCollaboratorsInvitedByProject() throws DatabaseException, ConnectionFailedException {
    db.insertProjectCollaboratorRow(project, collaborator1);
    List<User> users = db.getCollaboratorsInvitedByProject(project);
    assertTrue(users.contains(collaborator1));
  }

  @Test
  void testUpdateAcceptedColumn() throws DatabaseException, ConnectionFailedException {
    db.insertProjectCollaboratorRow(project, collaborator1);
    db.updateAcceptedColumn(project.getId(), collaborator1, 1);
    Project tmp = db.getProjectByCollaborator(project, collaborator1);
    assertEquals(project, tmp);
  }

  @AfterEach
  void deleteDatabase() throws IOException {
    boolean deletedFile = file.delete();
    if (!deletedFile) {
      throw new IOException("Cannot delete file");
    }
  }
}
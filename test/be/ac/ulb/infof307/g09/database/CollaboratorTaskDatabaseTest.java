package be.ac.ulb.infof307.g09.database;

import be.ac.ulb.infof307.g09.application.models.Color;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Task;
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

class CollaboratorTaskDatabaseTest {

  private static final String DATABASE_TEST_PATH = Paths.get("").toAbsolutePath() + "/test/test.db";
  private static final String START_PATH = "src/be/ac/ulb/infof307/g09/";
  private static File file;

  private static User collaborator1;
  private static User collaborator2;

  private static Task task1;
  private static final String TASK_DESCRIPTION = "task";
  private static Task task2;

  private static CollaboratorTaskDatabase collaboratorTaskDatabase;

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

    Project project = ProjectDatabase.getInstance().insertProject(new Project(-1, "newProject", "", 0, 0, 0, author, null, Color.GREEN));

    long start = 0;
    long end = 22;
    task1 = TaskDatabase.getInstance().createTask(TASK_DESCRIPTION + "1", start, end, project.getId());
    task2 = TaskDatabase.getInstance().createTask("task2", start, end, project.getId());


    collaboratorTaskDatabase = CollaboratorTaskDatabase.getInstance();
    collaboratorTaskDatabase.insertCollaboratorTask(collaborator1, task1);
  }

  @Test
  void testGetAssignedCollaboratorsToTask() throws DatabaseException, ConnectionFailedException {
    List<User> userTasks = collaboratorTaskDatabase.getAssignedCollaboratorsToTask(task1);

    assertEquals(userTasks.get(0).getId(), collaborator1.getId());
  }

  @Test
  void testGetAssignedCollaboratorsToTaskFail() throws DatabaseException, ConnectionFailedException {
    List<User> userTasks = collaboratorTaskDatabase.getAssignedCollaboratorsToTask(task2);

    assertTrue(userTasks.isEmpty());
  }

  @Test
  void insertCollaboratorTask() throws DatabaseException, ConnectionFailedException {
    collaboratorTaskDatabase.insertCollaboratorTask(collaborator2, task2);
    List<User> taskUsers = collaboratorTaskDatabase.getAssignedCollaboratorsToTask(task2);

    assertEquals(taskUsers.get(0), collaborator2);
  }

  @Test
  void testDeleteCollaboratorTask() throws DatabaseException, ConnectionFailedException {
    collaboratorTaskDatabase.deleteCollaboratorTask(collaborator2, task2);
    List<User> taskUsers = collaboratorTaskDatabase.getAssignedCollaboratorsToTask(task2);

    assertTrue(taskUsers.isEmpty());
    collaboratorTaskDatabase.insertCollaboratorTask(collaborator2, task2);
  }

  @AfterEach
  void deleteDatabase() throws IOException {
    boolean deletedFile = file.delete();
    if (!deletedFile) {
      throw new IOException("Cannot delete file");
    }
  }
}
package be.ac.ulb.infof307.g09.database;

import be.ac.ulb.infof307.g09.application.models.Color;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskDatabaseTest {

  //TASK
  private static final Integer TASK_ID = 1;
  private static final String DESCRIPTION = "Test task description";
  private static final Long TASK_START_DATE = 1639134000000L; //10-12-2021 12:00
  private static final Long TASK_END_DATE = 1639220400000L; //11-12-2021 12:00

  //NEW TASK
  private static final String NEW_DESCRIPTION = "Test new task description";
  private static final Long NEW_START_DATE = 1639047600000L; //9-12-2021 12:00
  private static final Long NEW_END_DATE = 1639306800000L; //12-12-2021 12:00

  //PROJECT
  private static final Integer PROJECT_ID = 1;
  private static final String TITLE = "Test parentProject";
  private static final String PARENT_DESCRIPTION = "This is a test of a parentProject";
  private static final Long PARENT_START_DATE = 1639047600000L; //9-12-2021 12:00
  private static final Long PARENT_END_DATE = 1639306800000L; //12-12-2021 12:00
  private static final Long PARENT_INITIAL_DURATION = PARENT_END_DATE - PARENT_START_DATE;
  private static final Color PARENT_COLOR = Color.BLUE;

  //USER
  private static final int USER_ID = 1;
  private static final String USERNAME = "JohnDoe";
  private static final String EMAIL = "dummy@gmail.com";
  private static final String LAST_NAME = "Doe";
  private static final String FIRST_NAME = "John";
  private static final String PASSWORD = "dummy";

  private static TaskDatabase taskDatabase;
  private static final String DATABASE_TEST_PATH = Paths.get("").toAbsolutePath() + "/test/test.db";
  private static final String START_PATH = "src/be/ac/ulb/infof307/g09/";
  private static File file;
  private Project project;

  @BeforeEach
  void setUp() throws DatabaseException, ConnectionFailedException {
    if (taskDatabase == null) {
      taskDatabase = TaskDatabase.getInstance();
    }
    TaskDatabase.setDatabasePath(DATABASE_TEST_PATH);
    TaskDatabase.createNewDatabaseFile(START_PATH);
    file = TaskDatabase.getInstance().getDatabase();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    UserDatabase.getInstance().insert(user);
    this.project = new Project(PROJECT_ID, TITLE, PARENT_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    ProjectDatabase.getInstance().insertProject(project);
  }

  @Test
  void testCreateAndGetTask() throws DatabaseException, ConnectionFailedException {
    Task taskTest = taskDatabase.createTask(DESCRIPTION, TASK_START_DATE, TASK_END_DATE, PROJECT_ID);
    project.addTask(taskTest);
    List<Task> projectTasks = taskDatabase.getAllTasksOfProject(project);
    List<Task> list = new ArrayList<>();
    list.add(taskTest);
    assertEquals(list, projectTasks);
  }

  @Test
  void testUpdateTask() throws DatabaseException, ConnectionFailedException {
    Task taskTest = taskDatabase.createTask(DESCRIPTION, TASK_START_DATE, TASK_END_DATE, PROJECT_ID);
    taskDatabase.updateTask(taskTest, NEW_DESCRIPTION, NEW_START_DATE, NEW_END_DATE);
    project.addTask(taskTest);
    List<Task> projectTasks = taskDatabase.getAllTasksOfProject(project);
    assertEquals(projectTasks.get(0).getDescription(), NEW_DESCRIPTION);
  }

  @Test
  void testGetAllTasksOfProject() throws DatabaseException, ConnectionFailedException {
    Task taskTest = taskDatabase.createTask(DESCRIPTION, TASK_START_DATE, TASK_END_DATE, PROJECT_ID);
    ArrayList<Task> list = new ArrayList<>();
    list.add(taskTest);
    assertEquals(taskDatabase.getAllTasksOfProject(this.project), list);
  }

  @Test
  void testDeleteTask() throws DatabaseException, ConnectionFailedException {
    taskDatabase.createTask(DESCRIPTION, TASK_START_DATE, TASK_END_DATE, PROJECT_ID);
    taskDatabase.deleteTask(TASK_ID);
    assertEquals(taskDatabase.getAllTasksOfProject(this.project), new ArrayList<>());
  }

  @AfterEach
  void deleteDatabase() throws IOException {
    boolean deletedFile = file.delete();
    if (!deletedFile) {
      throw new IOException("Cannot delete file");
    }
  }
}
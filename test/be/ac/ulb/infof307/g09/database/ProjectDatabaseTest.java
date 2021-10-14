package be.ac.ulb.infof307.g09.database;

import be.ac.ulb.infof307.g09.application.models.Color;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Tag;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import javafx.util.Pair;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectDatabaseTest {

  //TAG
  private static final int TAG_ID = 1;
  private static final String TAG_TEXT = "Test text Tag!";

  //PARENT PROJECT
  private static final Integer PARENT_PROJECT_ID = 1;
  private static final String PARENT_TITLE = "Test parentProject";
  private static final String PARENT_DESCRIPTION = "This is a test of a parentProject";
  private static final Long PARENT_START_DATE = 1639047600000L; //9-12-2021 12:00
  private static final Long PARENT_END_DATE = 1639306800000L; //12-12-2021 12:00
  private static final Long PARENT_INITIAL_DURATION = PARENT_END_DATE - PARENT_START_DATE;
  private static final Color PARENT_COLOR = Color.BLUE;

  //SUB PROJECT
  private static final Integer SUB_PROJECT_ID = 2;
  private static final String SUB_TITLE = "Test subProject";
  private static final String SUB_DESCRIPTION = "This is a test of a subProject";
  private static final Long SUB_START_DATE = 1639134000000L; //10-12-2021 12:00
  private static final Long SUB_END_DATE = 1639220400000L; //11-12-2021 12:00
  private static final Long SUB_INITIAL_DURATION = SUB_END_DATE - SUB_START_DATE;
  private static final Color SUB_COLOR = Color.GREEN;


  //UPDATE PROJECT
  private static final String NEW_TITLE = "Test New Title";
  private static final String NEW_DESCRIPTION = "This is a new description";
  private static final Long NEW_END_DATE = 1640084400000L; //21-12-2021 12:00

  //USER
  private static final int USER_ID = 0;
  private static final String USERNAME = "JohnDoe";
  private static final String EMAIL = "dummy@gmail.com";
  private static final String LAST_NAME = "Doe";
  private static final String FIRST_NAME = "John";
  private static final String PASSWORD = "dummy";
  private User user;

  private ProjectDatabase projectDatabase;
  private static final String DATABASE_TEST_PATH = Paths.get("").toAbsolutePath() + "/test/test.db";
  private static final String START_PATH = "src/be/ac/ulb/infof307/g09/";
  private static File file;

  @BeforeEach
  void setUp() throws DatabaseException, ConnectionFailedException {
    if (projectDatabase == null) {
      projectDatabase = ProjectDatabase.getInstance();
    }
    ProjectDatabase.setDatabasePath(DATABASE_TEST_PATH);
    UserDatabase.createNewDatabaseFile(START_PATH);
    file = UserDatabase.getInstance().getDatabase();
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    user = UserDatabase.getInstance().insert(tmp);
  }

  @Test
  void testInsertAndGetById() throws DatabaseException, ConnectionFailedException {
    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE,
        PARENT_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    projectDatabase.insertProject(projectTest);

    assertEquals(projectDatabase.getProjectById(PARENT_PROJECT_ID), projectTest);
  }

  @Test
  void testInsertByTitle() throws DatabaseException, ConnectionFailedException {
    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE,
        PARENT_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    projectDatabase.insertProject(projectTest);
    assertEquals(projectDatabase.getProjectById(PARENT_PROJECT_ID), projectTest);
  }

  @Test
  void testDelete() throws DatabaseException, ConnectionFailedException {
    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE,
        PARENT_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    projectDatabase.insertProject(projectTest);

    projectDatabase.delete(PARENT_PROJECT_ID);
    assertThrows(DatabaseException.class, () -> projectDatabase.getProjectById(PARENT_PROJECT_ID));
  }

  @Test
  void testUpdateProject() throws DatabaseException, ConnectionFailedException {
    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE,
        PARENT_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    projectDatabase.insertProject(projectTest);

    projectDatabase.updateProject(PARENT_PROJECT_ID, NEW_TITLE, NEW_DESCRIPTION, NEW_END_DATE, PARENT_COLOR);

    Project temp = new Project(PARENT_PROJECT_ID, NEW_TITLE,
        NEW_DESCRIPTION, PARENT_START_DATE, NEW_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    assertEquals(projectDatabase.getProjectById(PARENT_PROJECT_ID), temp);
  }

  @Test
  void testInsertSubProject() throws DatabaseException, ConnectionFailedException {
    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE,
        PARENT_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    projectDatabase.insertProject(projectTest);

    Project updateProject = projectDatabase.insertSubProject(new Project(SUB_PROJECT_ID, SUB_TITLE,
        SUB_DESCRIPTION, SUB_START_DATE, SUB_END_DATE, SUB_INITIAL_DURATION, user, projectTest, PARENT_COLOR));

    assertEquals(projectDatabase.getProjectById(SUB_PROJECT_ID), updateProject);
  }

  @Test
  void testFindSubProjects() throws DatabaseException, ConnectionFailedException {
    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE,
        PARENT_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    projectDatabase.insertProject(projectTest);

    Project subProject = projectDatabase.insertSubProject(
        new Project(SUB_PROJECT_ID, SUB_TITLE, SUB_DESCRIPTION,
            SUB_START_DATE, SUB_END_DATE, SUB_INITIAL_DURATION, user, projectTest, SUB_COLOR));
    ArrayList<Project> list = new ArrayList<>();
    list.add(subProject);
    assertEquals(projectDatabase.findSubProjects(projectTest), list);
  }

  @Test
  void testGetAllParentProjectsByAuthor() throws DatabaseException, ConnectionFailedException {
    Project projectTest = projectDatabase.insertProject(
        new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
            PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR));
    projectDatabase.insertSubProject(
        new Project(SUB_PROJECT_ID, SUB_TITLE,
            SUB_DESCRIPTION, SUB_START_DATE, SUB_END_DATE, SUB_INITIAL_DURATION, user, projectTest, SUB_COLOR));

    ArrayList<Project> list = new ArrayList<>();
    list.add(projectTest);
    assertEquals(list, projectDatabase.getAllParentProjectsByAuthor(USERNAME));
  }

  @Test
  void testGetProjectsWithUniqueTags() throws DatabaseException, ConnectionFailedException {
    Project projectTest = projectDatabase.insertProject(
        new Project(PARENT_PROJECT_ID, PARENT_TITLE,
            PARENT_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR));
    Tag tagTest = TagDatabase.getInstance().createTag(TAG_TEXT);
    ProjectTagDatabase.getInstance().addProjectTag(PARENT_PROJECT_ID, TAG_ID);

    ArrayList<Pair<Integer, Integer>> list = new ArrayList<>();
    list.add(new Pair<>(projectTest.getId(), tagTest.getId()));
    assertEquals(projectDatabase.getProjectsWithUniqueTags(), list);
  }

  @Test
  void testCheckIfProjectExistsById() {
    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE,
        PARENT_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    assertThrows(DatabaseException.class, () ->
        projectDatabase.checkIfProjectExistsById(projectTest.getId()));
  }

  @Test
  void testCheckIfProjectExists() {
    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE,
        PARENT_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    assertThrows(DatabaseException.class, () ->
        projectDatabase.checkIfProjectExists(projectTest));
  }

  @AfterEach
  void deleteDatabase() throws IOException {
    boolean deletedFile = file.delete();
    if (!deletedFile) {
      throw new IOException("Cannot delete file");
    }
  }
}
package be.ac.ulb.infof307.g09.database;

import be.ac.ulb.infof307.g09.application.models.Color;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Tag;
import be.ac.ulb.infof307.g09.application.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectTagDatabaseTest {

  //TAG
  private static final int TAG_ID = 1;
  private static final String TAG_TEXT = "Test text Tag!";

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

  private static ProjectTagDatabase projectTagDatabase;
  private static final String DATABASE_TEST_PATH = Paths.get("").toAbsolutePath() + "/test/test.db";
  private static final String START_PATH = "src/be/ac/ulb/infof307/g09/";
  private static File file;

  private Project project;

  @BeforeEach
  void setUp() throws DatabaseException, ConnectionFailedException {
    if (projectTagDatabase == null) {
      projectTagDatabase = ProjectTagDatabase.getInstance();
    }
    ProjectTagDatabase.setDatabasePath(DATABASE_TEST_PATH);
    UserDatabase.createNewDatabaseFile(START_PATH);
    file = UserDatabase.getInstance().getDatabase();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    UserDatabase.getInstance().insert(user);
    project = new Project(PROJECT_ID, TITLE, PARENT_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    ProjectDatabase.getInstance().insertProject(project);
    TagDatabase.getInstance().createTag(TAG_TEXT);
  }

  @Test
  void testAddProjectTag() throws DatabaseException, ConnectionFailedException {
    projectTagDatabase.addProjectTag(PROJECT_ID, TAG_ID);
    List<Tag> list = new ArrayList<>();
    list.add(new Tag(TAG_ID, TAG_TEXT));
    List<Tag> tags = ProjectDatabase.getInstance().getTagsOfProject(this.project);

    assertEquals(list, tags);
  }

  @Test
  void testRemoveProjectTag() throws DatabaseException, ConnectionFailedException {
    projectTagDatabase.addProjectTag(PROJECT_ID, TAG_ID);
    projectTagDatabase.removeProjectTag(PROJECT_ID, TAG_ID);

    List<Tag> tags = ProjectDatabase.getInstance().getTagsOfProject(this.project);

    assertEquals(new ArrayList<>(), tags);
  }

  @AfterEach
  void deleteDatabase() throws IOException {
    boolean deletedFile = file.delete();
    if (!deletedFile) {
      throw new IOException("Cannot delete file");
    }
  }
}
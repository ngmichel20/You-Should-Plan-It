package be.ac.ulb.infof307.g09.application.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

  private static final int USER_ID = 0;
  private static final String USERNAME = "JohnDoe";
  private static final String NONEXISTENT_USERNAME = "nonExistentUsername";
  private static final String VALID_EMAIL = "JohnDoe@gmail.com";
  private static final String NONEXISTENT_EMAIL = "nonExistentEmail";
  private static final String LAST_NAME = "Doe";
  private static final String NONEXISTENT_LAST_NAME = "NotDoe";
  private static final String FIRST_NAME = "John";
  private static final String NONEXISTENT_FIRST_NAME = "NotJohn";
  private static final String PASSWORD = "dummy";
  private static final String NONEXISTENT_PASSWORD = "nonExistentPassword";

  private static final User USER_TEST = new User(USER_ID, USERNAME, VALID_EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

  private static final String REPLACED_AT_EMAIL = "JohnDoe+gmail.com";
  private static final String NO_AT_EMAIL = "JohnEmail.com";
  private static final String REPLACED_DOT_EMAIL = "JonhDoe@gmail@com";
  private static final String NO_DOT_EMAIL = "JonhDoe@gmailcom";
  private static final String NO_EXTENSION_EMAIL = "JonhDoe@gmail.";

  private static final Integer PROJECT_ID = 1;
  private static final String PROJECT_TITLE = "Titre";
  private static final String PROJECT_DESCRIPTION = "Description";
  private static final Long PROJECT_START_DATE = 1639047600000L; //9-12-2021 12:00
  private static final Long PROJECT_END_DATE = 1639306800000L; //12-12-2021 12:00
  private static final Long PROJECT_INITIAL_DURATION = PROJECT_END_DATE - PROJECT_START_DATE;
  private static final Color PROJECT_COLOR = Color.BLUE;

  private static final Integer SUB_PROJECT_ID = 2;
  private static final String SUB_TITLE = "Test subProject";
  private static final String SUB_DESCRIPTION = "This is a test of a subProject";
  private static final Long SUB_START_DATE = 1639134000000L; //10-12-2021 12:00
  private static final Long SUB_END_DATE = 1639220400000L; //11-12-2021 12:00
  private static final Long SUB_INITIAL_DURATION = SUB_END_DATE - SUB_START_DATE;
  private static final Color SUB_COLOR = Color.GREEN;

  private static final Integer SUB_SUB_PROJECT_ID = 3;
  private static final String SUB_SUB_TITLE = "Test subSubProject";
  private static final String SUB_SUB_DESCRIPTION = "This is a test of a subSubProject";
  private static final Long SUB_SUB_START_DATE = 1639135000000L;
  private static final Long SUB_SUB_END_DATE = 1639220300000L;
  private static final Long SUB_SUB_INITIAL_DURATION = SUB_SUB_END_DATE - SUB_SUB_START_DATE;
  private static final Color SUB_SUB_COLOR = Color.GREEN;

  private static final Project PROJECT_TEST = new Project(PROJECT_ID, PROJECT_TITLE, PROJECT_DESCRIPTION,
      PROJECT_START_DATE, PROJECT_END_DATE, PROJECT_INITIAL_DURATION, USER_TEST, null, PROJECT_COLOR);

  private static final Project SUB_PROJECT = new Project(SUB_PROJECT_ID, SUB_TITLE, SUB_DESCRIPTION,
      SUB_START_DATE, SUB_END_DATE, SUB_INITIAL_DURATION, USER_TEST, PROJECT_TEST, SUB_COLOR);

  private static final Project SUB_SUB_PROJECT = new Project(SUB_SUB_PROJECT_ID, SUB_SUB_TITLE, SUB_SUB_DESCRIPTION,
      SUB_SUB_START_DATE, SUB_SUB_END_DATE, SUB_SUB_INITIAL_DURATION, USER_TEST, SUB_PROJECT, SUB_SUB_COLOR);

  @Test
  void testValidEmail() {
    assertFalse(USER_TEST.isMailInvalid());
  }

  @Test
  void testInvalidEmailReplacedAt() {
    USER_TEST.setEmail(REPLACED_AT_EMAIL);
    assertTrue(USER_TEST.isMailInvalid());
    USER_TEST.setEmail(VALID_EMAIL);
  }

  @Test
  void testInvalidEmailNoAt() {
    USER_TEST.setEmail(NO_AT_EMAIL);
    assertTrue(USER_TEST.isMailInvalid());
    USER_TEST.setEmail(VALID_EMAIL);
  }

  @Test
  void testInvalidEmailReplacedDot() {
    USER_TEST.setEmail(REPLACED_DOT_EMAIL);
    assertTrue(USER_TEST.isMailInvalid());
    USER_TEST.setEmail(VALID_EMAIL);
  }

  @Test
  void testInvalidEmailNoDot() {
    USER_TEST.setEmail(NO_DOT_EMAIL);
    assertTrue(USER_TEST.isMailInvalid());
    USER_TEST.setEmail(VALID_EMAIL);
  }

  @Test
  void testInvalidEmailNoExtension() {
    USER_TEST.setEmail(NO_EXTENSION_EMAIL);
    assertTrue(USER_TEST.isMailInvalid());
    USER_TEST.setEmail(VALID_EMAIL);
  }

  @Test
  void testEqualsNull() {
    assertNotEquals(USER_TEST, null);
  }

  @Test
  void testEqualsDifferentObject() {
    Object testInt = 69;
    assertNotEquals(USER_TEST, testInt);
  }

  @Test
  void testEqualsThis() {
    assertEquals(USER_TEST, USER_TEST);
  }

  @Test
  void testEqualsNewObjectSame() {
    User tmp = new User(USER_ID, USERNAME, VALID_EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    assertEquals(USER_TEST, tmp);
  }

  @Test
  void testEqualsNewObjectDifferent() {
    User tmp = new User(USER_ID, NONEXISTENT_USERNAME, VALID_EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    assertNotEquals(USER_TEST, tmp);
  }

  @Test
  void testUpdateUser() {
    User nonExistent = new User(USER_ID, NONEXISTENT_USERNAME, NONEXISTENT_EMAIL, NONEXISTENT_LAST_NAME, NONEXISTENT_FIRST_NAME, NONEXISTENT_PASSWORD);
    nonExistent.updateUser(new User(USER_ID, USERNAME, VALID_EMAIL, LAST_NAME, FIRST_NAME, PASSWORD));
    assertEquals(USER_TEST, nonExistent);
  }

  @Test
  void testSetGetUsername() {
    USER_TEST.setUsername("Test");
    assertEquals(USER_TEST.getUsername(), "Test");
    USER_TEST.setUsername(USERNAME);
  }

  @Test
  void testSetGetEmail() {
    USER_TEST.setEmail("Test@gmail.com");
    assertEquals(USER_TEST.getEmail(), "Test@gmail.com");
    USER_TEST.setEmail(VALID_EMAIL);
  }

  @Test
  void testSetGetLastName() {
    USER_TEST.setLastName(FIRST_NAME);
    assertEquals(USER_TEST.getLastName(), FIRST_NAME);
    USER_TEST.setLastName(LAST_NAME);
  }

  @Test
  void testSetGetFirstName() {
    USER_TEST.setFirstName(LAST_NAME);
    assertEquals(USER_TEST.getFirstName(), LAST_NAME);
    USER_TEST.setFirstName(FIRST_NAME);
  }

  @Test
  void testSetGetPassword() {
    USER_TEST.setPassword(USERNAME);
    assertEquals(USER_TEST.getPassword(), USERNAME);
    USER_TEST.setPassword(PASSWORD);
  }

  @Test
  void testSetGetProjectList() {
    List<Project> projectList = new ArrayList<>();
    projectList.add(PROJECT_TEST);
    USER_TEST.setProjectList(projectList);
    assertEquals(projectList, USER_TEST.getProjectList());
    USER_TEST.setProjectList(new ArrayList<>());
  }

  @Test
  void testGetAuthorProjects() {
    List<Project> projectList = new ArrayList<>();
    projectList.add(PROJECT_TEST);
    USER_TEST.getAuthorProjects().add(PROJECT_TEST);
    assertEquals(projectList, USER_TEST.getAuthorProjects());
    USER_TEST.removeAuthorProjects(PROJECT_TEST);
  }

  @Test
  void testAddProjectList() {
    List<Project> projectList = new ArrayList<>();
    projectList.add(PROJECT_TEST);
    USER_TEST.addProjectList(PROJECT_TEST);
    assertEquals(projectList, USER_TEST.getProjectList());
    USER_TEST.setProjectList(new ArrayList<>());
  }

  @Test
  void testRemoveProjectList() {
    List<Project> projectList = new ArrayList<>();
    USER_TEST.addProjectList(PROJECT_TEST);
    USER_TEST.removeProjectList(PROJECT_TEST);
    assertEquals(projectList, USER_TEST.getProjectList());
    USER_TEST.setProjectList(new ArrayList<>());
  }

  @Test
  void testRemoveAuthorProjects() {
    List<Project> projectList = new ArrayList<>();
    USER_TEST.getAuthorProjects().add(PROJECT_TEST);
    USER_TEST.removeAuthorProjects(PROJECT_TEST);
    assertEquals(projectList, USER_TEST.getAuthorProjects());
  }

  @Test
  void testIsAuthor() {
    Project project = new Project(0, PROJECT_TITLE,
        PROJECT_DESCRIPTION, 0, 0, 0, USER_TEST, null, null);
    assertTrue(USER_TEST.isAuthor(project));
  }

  @Test
  void testIsNotAuthor() {
    Project project = new Project(0, PROJECT_TITLE,
        PROJECT_DESCRIPTION, 0, 0, 0, null, null, null);
    assertFalse(USER_TEST.isAuthor(project));
  }

  @Test
  void testGetId() {
    assertEquals(USER_TEST.getId(), USER_ID);
  }

  @Test
  void testToString() {
    assertEquals(USER_TEST.toString(), USERNAME);
  }

  @Test
  void testRemoveSubProject() {
    PROJECT_TEST.addSubProjects(SUB_PROJECT);
    USER_TEST.addProjectList(PROJECT_TEST);
    USER_TEST.removeSubProject(SUB_PROJECT);
    List<Project> listProjects = USER_TEST.getProjectList();
    Project project = listProjects.get(0);
    List<Project> subProjects = project.getSubProjects();
    assertTrue(subProjects.isEmpty());
    USER_TEST.setProjectList(new ArrayList<>());
  }

  @Test
  void testGetProjectParent() {
    USER_TEST.addProjectList(PROJECT_TEST);
    assertEquals(PROJECT_TEST, USER_TEST.getProject(PROJECT_TEST));
    USER_TEST.setProjectList(new ArrayList<>());
  }

  @Test
  void testGetProjectSubSubProject() {
    SUB_PROJECT.addSubProjects(SUB_SUB_PROJECT);
    PROJECT_TEST.addSubProjects(SUB_PROJECT);
    USER_TEST.addProjectList(PROJECT_TEST);
    assertEquals(SUB_SUB_PROJECT, USER_TEST.getProject(SUB_SUB_PROJECT));
    USER_TEST.setProjectList(new ArrayList<>());
    PROJECT_TEST.setSubProjects(new ArrayList<>());
    SUB_PROJECT.setSubProjects(new ArrayList<>());
  }

  @Test
  void testGetProjectNullProject() {
    USER_TEST.addProjectList(PROJECT_TEST);
    assertNull(USER_TEST.getProject(SUB_SUB_PROJECT));
    USER_TEST.setProjectList(new ArrayList<>());
  }

  @Test
  void testIsUserInvalidFalse() {
    assertFalse(USER_TEST.isUserInvalid(PASSWORD));
  }

  @Test
  void testIsUserInvalidTrue() {
    assertTrue(USER_TEST.isUserInvalid(""));
  }

  @Test
  void testHashCode() {
    assertEquals(USER_TEST.hashCode(), new User(USER_ID, USERNAME, VALID_EMAIL, LAST_NAME, FIRST_NAME, PASSWORD).hashCode());
  }
}
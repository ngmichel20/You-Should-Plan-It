package be.ac.ulb.infof307.g09.application.models.handlers;

import be.ac.ulb.infof307.g09.application.models.*;
import be.ac.ulb.infof307.g09.application.utilities.DateTimeUtils;
import be.ac.ulb.infof307.g09.database.*;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

  private static final String DATABASE_TEST_PATH = Paths.get("").toAbsolutePath() + "/test/test.db";
  private static final String START_PATH = "src/be/ac/ulb/infof307/g09/";
  private static final int MILLISECONDS_IN_DAY = (1000 * 60 * 60 * 24);

  //USER
  private static final int USER_ID = 3;
  private static final String USERNAME = "JohnDoe";
  private static final String EMAIL = "dummy@gmail.com";
  private static final String LAST_NAME = "Doe";
  private static final String FIRST_NAME = "John";
  private static final String PASSWORD = "dummy";

  private static final int NONEXISTENT_USER_ID = 0;
  private static final String NONEXISTENT_FIRST_NAME = "NotJohn";
  private static final String NONEXISTENT_PASSWORD = "nonExistentPassword";

  public static final String TEST_USER = "testUser";
  public static final String FIRST_EMAIL_FR = "email1@email.fr";
  public static final String UNORIGINAL_LAST_NAME = "lastName";
  public static final String UNORIGINAL_FIRST_NAME = "firstName";
  public static final String UNORIGINAL_PASSWORD = "password";

  //COLLABORATOR
  private static final int COLLABORATOR_USER_ID = 4;
  private static final String COLLABORATOR_USERNAME = "MisterCollaborator";
  private static final String COLLABORATOR_EMAIL = "MisterCollaborator@gmail.com";
  private static final String COLLABORATOR_LAST_NAME = "Mister";
  private static final String COLLABORATOR_FIRST_NAME = "Collaborator";
  private static final String COLLABORATOR_PASSWORD = "MisterCollaborator";

  //SECOND COLLABORATOR
  private static final int SECOND_COLLABORATOR_USER_ID = 5;
  private static final String SECOND_COLLABORATOR_USERNAME = "MisterCollaborator2";
  private static final String SECOND_COLLABORATOR_EMAIL = "MisterCollaborator2@gmail.com";
  private static final String SECOND_COLLABORATOR_LAST_NAME = "Mister2";
  private static final String SECOND_COLLABORATOR_FIRST_NAME = "Collaborator2";
  private static final String SECOND_COLLABORATOR_PASSWORD = "MisterCollaborator2";

  //PROJECT
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

  //TAG
  private static final int TAG_ID = 1;
  private static final String TAG_TEXT = "Test text Tag!";
  private static final String SECOND_TAG_TEXT = "Second test text Tag!";

  // TASK
  private static final int TASK_ID = 1;
  private static final String TASK_DESCRIPTION = "This is a test for junit";
  private static final Long TASK_START_DATE = 1639134000000L; //10-12-2021 12:00
  private static final Long TASK_END_DATE = 1639220400000L; //11-12-2021 12:00
  private static final String TASK_NEW_DESCRIPTION = "This is a modified test for junit";
  private static final Long TASK_NEW_START_DATE = 1839134000000L; //10-12-2021 12:00
  private static final Long TASK_NEW_END_DATE = 1839220400000L; //11-12-2021 12:00

  //Collaborators
  private static User collaborator1;
  private static ProjectCollaborationDatabase db;

  private static File file;
  private UserDatabase userDatabase;

  @BeforeEach
  void setUp() throws ConnectionFailedException, DatabaseException {
    if (userDatabase == null) {
      userDatabase = UserDatabase.getInstance();
    }
    UserDatabase.setDatabasePath(DATABASE_TEST_PATH);
    UserDatabase.createNewDatabaseFile(START_PATH);
    file = UserDatabase.getInstance().getDatabase();

    collaborator1 = UserDatabase.getInstance().insert(
        new User(-1, "testUser2", "email2@email.fr", UNORIGINAL_LAST_NAME,
            UNORIGINAL_FIRST_NAME, UNORIGINAL_PASSWORD));
    UserDatabase.getInstance().insert(
        new User(-1, "testUser3", "email3@email.fr", UNORIGINAL_LAST_NAME,
            UNORIGINAL_FIRST_NAME, UNORIGINAL_PASSWORD));

    db = ProjectCollaborationDatabase.getInstance();
  }

  @Test
  void testGetUserParentProjects() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);

    Project projectToInsert = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    Project project = application.insertProject(projectToInsert, false);
    ArrayList<Project> userProjects = new ArrayList<>();
    userProjects.add(project);
    assertEquals(userProjects, application.getUserParentProjects());
  }

  @Test
  void testGetCollaboratorsByProjectWaiting()
      throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(-1, TEST_USER, FIRST_EMAIL_FR, UNORIGINAL_LAST_NAME,
        UNORIGINAL_FIRST_NAME, UNORIGINAL_PASSWORD);
    application.register(user);

    Project project = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    project = ProjectDatabase.getInstance().insertProject(project);

    db.insertProjectCollaboratorRow(project, collaborator1);

    List<User> userList = application.getCollaboratorsByProjectWaiting(project);
    assertTrue(userList.contains(collaborator1));
  }

  @Test
  void testGetCollaboratorsInvitedByProject()
      throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(-1, TEST_USER, FIRST_EMAIL_FR, UNORIGINAL_LAST_NAME,
        UNORIGINAL_FIRST_NAME, UNORIGINAL_PASSWORD);
    application.register(user);

    Project project = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    project = ProjectDatabase.getInstance().insertProject(project);

    db.insertProjectCollaboratorRow(project, collaborator1);
    List<User> expectedCollaborators = Collections.singletonList(collaborator1);
    assertEquals(expectedCollaborators, application.getCollaboratorsInvitedByProject(project));
  }

  @Test
  void testGetCollaboratorsByProjectRefused()
      throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(-1, TEST_USER, FIRST_EMAIL_FR, UNORIGINAL_LAST_NAME,
        UNORIGINAL_FIRST_NAME, UNORIGINAL_PASSWORD);
    application.register(user);

    Project project = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    project = ProjectDatabase.getInstance().insertProject(project);

    db.insertProjectCollaboratorRow(project, collaborator1);
    db.updateAcceptedColumn(project.getId(), collaborator1, 0);

    List<User> expectedCollaborators = Collections.singletonList(collaborator1);
    assertEquals(expectedCollaborators, application.getCollaboratorsByProjectRefused(project));
  }

  @Test
  void testGetCollaboratorsByProjectAccepted()
      throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(-1, TEST_USER, FIRST_EMAIL_FR, UNORIGINAL_LAST_NAME,
        UNORIGINAL_FIRST_NAME, UNORIGINAL_PASSWORD);
    application.register(user);

    Project project = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    project = ProjectDatabase.getInstance().insertProject(project);

    db.insertProjectCollaboratorRow(project, collaborator1);
    db.updateAcceptedColumn(project.getId(), collaborator1, 1);

    List<User> expectedCollaborators = Collections.singletonList(collaborator1);
    assertEquals(expectedCollaborators, application.getCollaboratorsByProjectAccepted(project));
  }

  @Test
  void testDeleteRowWithRefusedInvitation()
      throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(-1, TEST_USER, FIRST_EMAIL_FR, UNORIGINAL_LAST_NAME,
        UNORIGINAL_FIRST_NAME, UNORIGINAL_PASSWORD);
    application.register(user);

    Project project = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    project = ProjectDatabase.getInstance().insertProject(project);

    db.insertProjectCollaboratorRow(project, collaborator1);
    db.updateAcceptedColumn(project.getId(), collaborator1, 0);
    application.deleteRowWithRefusedInvitation(project, collaborator1);

    List<User> userList = db.getCollaboratorsByProjectAccepted(project);
    assertFalse(userList.contains(collaborator1));
  }

  @Test
  void testUpdateInvitationRead()
      throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(-1, TEST_USER, FIRST_EMAIL_FR, UNORIGINAL_LAST_NAME,
        UNORIGINAL_FIRST_NAME, UNORIGINAL_PASSWORD);
    application.register(user);

    Project project = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    project = ProjectDatabase.getInstance().insertProject(project);

    db.insertProjectCollaboratorRow(project, collaborator1);
    application.updateInvitationRead(project, collaborator1);

    Project tmp = db.getProjectByCollaborator(project, collaborator1);
    assertEquals(project, tmp);
  }

  @Test
  void testCheckIfUsernameExists() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);

    assertTrue(application.checkIfUsernameExists(USERNAME));
  }

  @Test
  void testCheckIfEmailExists() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);

    assertTrue(application.checkIfEmailExists(EMAIL));
  }

  @Test
  void testDisconnect() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User userToConnect = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    UserDatabase.getInstance().insert(userToConnect);
    application.connect(userToConnect.getUsername(), userToConnect.getPassword());
    application.disconnect();

    assertNull(application.getUser());
    assertThrows(NullPointerException.class, application::getUserProjects);
  }

  @Test
  void testConnectWithValidUser() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User userToConnect = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    UserDatabase.getInstance().insert(userToConnect);
    application.connect(userToConnect.getUsername(), userToConnect.getPassword());


    assertEquals(userToConnect, application.getUser());
    assertEquals(new ArrayList<Project>(), application.getUserProjects());
  }

  @Test
  void testConnectWithInvalidUser() {
    Application application = new Application();
    User userToConnect = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    assertThrows(IllegalArgumentException.class, () -> application.connect(userToConnect.getUsername(), userToConnect.getPassword()));
  }

  @Test
  void testRegisterValidUser() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User userToCreate = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(userToCreate);

    assertEquals(userToCreate.getId(), application.getUser().getId());
    assertEquals(new ArrayList<Project>(), application.getUserProjects());
  }

  @Test
  void testRegisterInvalidUser() {
    Application application = new Application();
    User userInvalid = new User(NONEXISTENT_USER_ID, USERNAME, EMAIL, null, NONEXISTENT_FIRST_NAME, NONEXISTENT_PASSWORD);

    assertThrows(DatabaseException.class, () -> application.register(userInvalid));
  }

  @Test
  void testGetUserProjects() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());
    assertEquals(application.getUserProjects(), new ArrayList<Project>());
  }

  @Test
  void testGetAuthorProjects() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());
    assertEquals(application.getAuthorProjects(), new ArrayList<Project>());
  }

  @Test
  void testInsertProject() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    Project projectToInsert = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    Project project = application.insertProject(projectToInsert, false);
    ArrayList<Project> userProjects = new ArrayList<>();
    userProjects.add(project);
    assertEquals(application.getUserProjects(), userProjects);
  }

  @Test
  void testInsertProjectException() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    Project projectToInsert = new Project(PARENT_PROJECT_ID, null, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    assertThrows(DatabaseException.class, () -> application.insertProject(projectToInsert, false));
  }

  @Test
  void testAddCollaboratorProject() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    Project projectToInsert = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    application.addCollaboratorProject(projectToInsert);
    Project project = application.getUserProjects().get(0);
    ArrayList<Project> userProjects = new ArrayList<>();
    userProjects.add(project);

    assertEquals(application.getUserProjects(), userProjects);
  }

  @Test
  void testRemoveCollaborationFromProject() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    Project projectToRemove = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    application.addCollaboratorProject(projectToRemove);
    application.removeCollaborationFromProject(projectToRemove);

    ArrayList<Project> userProjects = new ArrayList<>();

    assertEquals(application.getUserProjects(), userProjects);
  }

  @Test
  void testDeleteProject() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User userToConnect = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    Project projectToDelete = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, userToConnect, null, PARENT_COLOR);

    UserDatabase.getInstance().insert(userToConnect);
    application.connect(userToConnect.getUsername(), userToConnect.getPassword());

    Tag tagFirstProject = application.createTag(TAG_TEXT);

    Project firstProject = application.insertProject(projectToDelete, false);

    application.addTagToProject(firstProject, tagFirstProject);
    application.deleteProject(firstProject);

    assertEquals(new ArrayList<Project>(), application.getUserProjects());
  }

  @Test
  void testDeleteProjectException() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User userToConnect = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    Project projectToDelete = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, userToConnect, null, PARENT_COLOR);

    UserDatabase.getInstance().insert(userToConnect);
    application.connect(userToConnect.getUsername(), userToConnect.getPassword());

    assertThrows(DatabaseException.class, () -> application.deleteProject(projectToDelete));
  }

  @Test
  void testUpdateProject() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User userToConnect = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    Project projectToUpdate = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, userToConnect, null, PARENT_COLOR);

    UserDatabase.getInstance().insert(userToConnect);
    application.connect(userToConnect.getUsername(), userToConnect.getPassword());
    Project project = application.insertProject(projectToUpdate, false);

    Project subProject = new Project(SUB_PROJECT_ID, SUB_TITLE, SUB_DESCRIPTION,
        SUB_START_DATE, SUB_END_DATE, SUB_INITIAL_DURATION, userToConnect, project, SUB_COLOR);

    application.insertProject(subProject, true);
    application.updateProject(project, SUB_TITLE, SUB_DESCRIPTION, SUB_END_DATE, SUB_COLOR);

    Project expectedProject = new Project(PARENT_PROJECT_ID, SUB_TITLE, SUB_DESCRIPTION,
        PARENT_START_DATE, SUB_END_DATE, PARENT_INITIAL_DURATION, userToConnect, null, SUB_COLOR);
    assertEquals(expectedProject, project);
  }

  @Test
  void testUpdateProjectException() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User userToConnect = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    Project projectToModify = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, userToConnect, null, PARENT_COLOR);

    UserDatabase.getInstance().insert(userToConnect);
    application.connect(userToConnect.getUsername(), userToConnect.getPassword());

    assertThrows(DatabaseException.class, () -> application.updateProject(projectToModify, SUB_TITLE, SUB_DESCRIPTION, SUB_END_DATE, SUB_COLOR));
  }

  @Test
  void testSetGetState() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    application.setState(State.PROJECT_MODIFIED);
    assertEquals(application.getState(), State.PROJECT_MODIFIED);
  }

  @Test
  void testUpdateUser() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    User newUser = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.updateUser(newUser);

    assertEquals(application.getUser().getUsername(), newUser.getUsername());
  }

  @Test
  void testGetUserNotConnected() {
    Application application = new Application();

    assertNull(application.getUser());
  }

  @Test
  void testGetUser() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User userToConnect = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    UserDatabase.getInstance().insert(userToConnect);
    application.connect(userToConnect.getUsername(), userToConnect.getPassword());

    assertEquals(userToConnect, application.getUser());
  }

  @Test
  void testGetUsername() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    assertEquals(application.getUsername(), user.getUsername());
  }

  @Test
  void testGetEmail() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    assertEquals(application.getEmail(), user.getEmail());
  }

  @Test
  void testGetFirstName() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    assertEquals(application.getFirstName(), user.getFirstName());
  }

  @Test
  void testGetLastName() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    assertEquals(application.getLastName(), user.getLastName());
  }

  @Test
  void testGetPassword() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    assertEquals(application.getPassword(), user.getPassword());
  }

  @Test
  void testAddTaskToProject() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());
    Project projectToInsert = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    Project project = application.insertProject(projectToInsert, false);

    Task taskToInsert = new Task(TASK_ID, TASK_DESCRIPTION, TASK_START_DATE, TASK_END_DATE, project.getId());
    application.addTaskToProject(project, TASK_DESCRIPTION, TASK_START_DATE, TASK_END_DATE);
    List<Task> projectTasks = project.getTasks();
    List<Task> list = new ArrayList<>();
    list.add(taskToInsert);
    assertEquals(list, projectTasks);
  }

  @Test
  void testAddTaskToProjectException() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());
    Project projectToInsert = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    Project project = application.insertProject(projectToInsert, false);

    assertThrows(DatabaseException.class, () -> application.addTaskToProject(project, null, TASK_START_DATE, TASK_END_DATE));
  }

  @Test
  void testUpdateTaskInProject() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());
    Project projectToInsert = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    Project project = application.insertProject(projectToInsert, false);

    application.addTaskToProject(project, TASK_DESCRIPTION, TASK_START_DATE, TASK_END_DATE);
    List<Task> projectTasks = project.getTasks();

    application.updateTaskInProject(project, projectTasks.get(0), TASK_NEW_DESCRIPTION, TASK_NEW_START_DATE, TASK_NEW_END_DATE);

    Task expectedTask = new Task(TASK_ID, TASK_NEW_DESCRIPTION, TASK_NEW_START_DATE, TASK_NEW_END_DATE, project.getId());
    assertEquals(projectTasks.get(0), expectedTask);
  }

  @Test
  void testUpdateTaskInProjectException() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());
    Project projectToInsert = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    Project project = application.insertProject(projectToInsert, false);

    application.addTaskToProject(project, TASK_DESCRIPTION, TASK_START_DATE, TASK_END_DATE);
    List<Task> projectTasks = project.getTasks();

    assertThrows(DatabaseException.class, () -> application.updateTaskInProject(project, projectTasks.get(0), null, TASK_NEW_START_DATE, TASK_NEW_END_DATE));
  }

  @Test
  void testHandleAssignationsAddCollaborator() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    User secondCollaborator = new User(SECOND_COLLABORATOR_USER_ID, SECOND_COLLABORATOR_USERNAME, SECOND_COLLABORATOR_EMAIL,
        SECOND_COLLABORATOR_LAST_NAME, SECOND_COLLABORATOR_FIRST_NAME, SECOND_COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.register(secondCollaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.addTaskToProject(projectTest, TASK_DESCRIPTION, TASK_START_DATE, TASK_END_DATE);
    List<Task> projectTasks = projectTest.getTasks();
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    application.insertProjectCollaboratorRow(projectTest, secondCollaborator);
    application.updateAcceptedColumn(projectTest.getId(), collaborator, 1);
    application.updateAcceptedColumn(projectTest.getId(), secondCollaborator, 1);

    List<User> collaboratorsToAssign = Arrays.asList(collaborator, secondCollaborator);
    application.handleAssignations(projectTasks.get(0), collaboratorsToAssign);

    assertEquals(collaboratorsToAssign, projectTasks.get(0).getAssignedUsers());
  }

  @Test
  void testHandleAssignationsRemoveCollaborator() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    User secondCollaborator = new User(SECOND_COLLABORATOR_USER_ID, SECOND_COLLABORATOR_USERNAME, SECOND_COLLABORATOR_EMAIL,
        SECOND_COLLABORATOR_LAST_NAME, SECOND_COLLABORATOR_FIRST_NAME, SECOND_COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.register(secondCollaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.addTaskToProject(projectTest, TASK_DESCRIPTION, TASK_START_DATE, TASK_END_DATE);
    List<Task> projectTasks = projectTest.getTasks();
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    application.insertProjectCollaboratorRow(projectTest, secondCollaborator);
    application.updateAcceptedColumn(projectTest.getId(), collaborator, 1);
    application.updateAcceptedColumn(projectTest.getId(), secondCollaborator, 1);

    List<User> oldCollaboratorsToAssign = new ArrayList<>();
    oldCollaboratorsToAssign.add(collaborator);
    oldCollaboratorsToAssign.add(secondCollaborator);
    List<User> newCollaboratorsToAssign = Collections.singletonList(collaborator);
    projectTasks.get(0).setAssignedUsers(oldCollaboratorsToAssign);
    application.handleAssignations(projectTasks.get(0), newCollaboratorsToAssign);

    assertEquals(Collections.singletonList(collaborator), projectTasks.get(0).getAssignedUsers());
  }

  @Test
  void testRemoveTaskFromProject() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());
    Project projectToInsert = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);
    Project project = application.insertProject(projectToInsert, false);

    application.addTaskToProject(project, TASK_DESCRIPTION, TASK_START_DATE, TASK_END_DATE);
    List<Task> projectTasks = project.getTasks();
    application.removeTaskFromProject(project, projectTasks.get(0));

    assertEquals(project.getTasks(), new ArrayList<>());
  }

  @Test
  void testInsertGetSubProject() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    Project parentProjectToInsert = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    Project parentProject = application.insertProject(parentProjectToInsert, false);

    Project subProjectToInsert = new Project(SUB_PROJECT_ID, SUB_TITLE, SUB_DESCRIPTION,
        SUB_START_DATE, SUB_END_DATE, SUB_INITIAL_DURATION, user, parentProject, SUB_COLOR);
    Project subProject = application.insertProject(subProjectToInsert, true);

    ArrayList<Project> subProjectList = new ArrayList<>();
    subProjectList.add(subProject);
    assertEquals(subProjectList, application.getSubProjects(parentProject));
  }

  @Test
  void testGetAllUserTags() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());
    user = application.getUser();
    Project firstProjectToAdd = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    Project secondProjectToAdd = new Project(SUB_PROJECT_ID, SUB_TITLE, SUB_DESCRIPTION,
        SUB_START_DATE, SUB_END_DATE, SUB_INITIAL_DURATION, user, null, PARENT_COLOR);

    Tag tagFirstProject = application.createTag(TAG_TEXT);
    Tag tagSecondProject = application.createTag(SECOND_TAG_TEXT);

    Project firstProject = application.insertProject(firstProjectToAdd, false);
    Project secondProject = application.insertProject(secondProjectToAdd, false);

    application.addTagToProject(firstProject, tagFirstProject);
    application.addTagToProject(secondProject, tagSecondProject);

    ArrayList<Tag> expectedTags = new ArrayList<>();
    expectedTags.add(tagSecondProject);
    expectedTags.add(tagFirstProject);

    assertEquals(expectedTags, application.getAllUserTags());
  }

  @Test
  void testAddTagToProject() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    Project firstProjectToAdd = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    Tag tagFirstProject = TagDatabase.getInstance().createTag(TAG_TEXT);

    Project firstProject = application.insertProject(firstProjectToAdd, false);

    application.addTagToProject(firstProject, tagFirstProject);

    ArrayList<Tag> expectedTags = new ArrayList<>();
    expectedTags.add(tagFirstProject);
    assertEquals(expectedTags, application.getUserProjects().get(0).getTags());
  }

  @Test
  void testAddTagToProjectException() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    Project firstProjectToAdd = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    Project firstProject = application.insertProject(firstProjectToAdd, false);

    Tag tagFirstProject = new Tag(TAG_ID, TAG_TEXT);
    assertThrows(DatabaseException.class, () -> application.addTagToProject(firstProject, tagFirstProject));
  }

  @Test
  void testDeleteTagFromProject() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());

    Project firstProjectToAdd = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    Tag tagFirstProject = TagDatabase.getInstance().createTag(TAG_TEXT);

    Project firstProject = application.insertProject(firstProjectToAdd, false);

    application.addTagToProject(firstProject, tagFirstProject);
    application.deleteTagFromProject(firstProject, tagFirstProject);
    assertEquals(new ArrayList<>(), application.getUserProjects().get(0).getTags());
  }

  @Test
  void testGetProjectsWithUniqueTags() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);
    application.connect(user.getUsername(), user.getPassword());
    user = application.getUser();
    Project firstProjectToAdd = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    Project secondProjectToAdd = new Project(SUB_PROJECT_ID, SUB_TITLE, SUB_DESCRIPTION,
        SUB_START_DATE, SUB_END_DATE, SUB_INITIAL_DURATION, user, null, PARENT_COLOR);

    Tag tagFirstProject = TagDatabase.getInstance().createTag(TAG_TEXT);

    Project firstProject = application.insertProject(firstProjectToAdd, false);
    Project secondProject = application.insertProject(secondProjectToAdd, false);

    application.addTagToProject(firstProject, tagFirstProject);
    application.addTagToProject(secondProject, tagFirstProject);

    assertEquals(new ArrayList<>(), application.getProjectsWithUniqueTags());
  }

  @Test
  void testGetTag() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    application.register(new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD));
    Tag createdTag = TagDatabase.getInstance().createTag(TAG_TEXT);
    Tag foundTag = application.getTag(createdTag.getDescription());
    assertEquals(createdTag, foundTag);
  }

  @Test
  void testCreateTag() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    application.register(new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD));
    Tag createdTag = application.createTag(TAG_TEXT);
    Tag foundTag = TagDatabase.getInstance().getTag(TAG_TEXT);
    assertEquals(createdTag, foundTag);
  }

  @Test
  void testCreateTagException() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    application.register(new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD));
    assertThrows(DatabaseException.class, () -> application.createTag(null));
  }

  @Test
  void testDeleteTag() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    application.register(new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD));
    Tag createdTag = TagDatabase.getInstance().createTag(TAG_TEXT);
    application.deleteTag(createdTag.getId());
    Tag foundTag = application.getTag(TAG_TEXT);
    assertNull(foundTag);
  }

  @Test
  void testGetCollaboratorsByProject() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    application.updateAcceptedColumn(projectTest.getId(), collaborator, 1);
    List<User> users = application.getCollaboratorsByProject(projectTest);
    assertTrue(users.contains(collaborator));
  }

  @Test
  void testGetUserWithUnreadNotifications() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();

    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    User userWithUnreadNotifications = application.getUserWithUnreadNotifications(collaborator);
    List<User> userList =
        ProjectCollaborationDatabase.getInstance().getCollaboratorsInvitedByProject(projectTest);
    assertTrue(userList.contains(userWithUnreadNotifications));
  }

  @Test
  void testCheckIfUserHasUnreadNotificationsNotNull() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();

    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    User userWithUnreadNotifications = application.getUserWithUnreadNotifications(collaborator);
    List<Project> listProjects = application.getAllUnansweredProjects(userWithUnreadNotifications);
    assertNotNull(listProjects);
  }

  @Test
  void testCheckIfUserHasUnreadNotificationsSizeZero() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();

    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    application.updateAcceptedColumn(projectTest.getId(), collaborator, 1);
    User userWithUnreadNotifications = application.getUserWithUnreadNotifications(collaborator);
    List<Project> listProjects = application.getAllUnansweredProjects(userWithUnreadNotifications);
    assertEquals(0, listProjects.size());
  }

  @Test
  void testGetProjectByCollaborator() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();

    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    assertEquals(projectTest, application.getProjectByCollaborator(projectTest, collaborator));
  }

  @Test
  void testInsertProjectCollaboratorRow() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();

    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    application.updateAcceptedColumn(projectTest.getId(), collaborator, 1);
    ArrayList<User> users = new ArrayList<>();
    users.add(collaborator);
    assertEquals(application.getCollaboratorsByProject(projectTest), users);
  }

  @Test
  void insertProjectCollaboratorRowException() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();

    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    assertThrows(DatabaseException.class, () -> application.insertProjectCollaboratorRow(projectTest, collaborator));
  }

  @Test
  void testUpdateAcceptedColumn() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();

    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    application.updateAcceptedColumn(projectTest.getId(), collaborator, 1);
    ArrayList<User> users = new ArrayList<>();
    users.add(collaborator);
    assertEquals(application.getCollaboratorsByProject(projectTest), users);
  }

  @Test
  void testGetProjectsWithInvitationsRefused() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();

    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    application.updateAcceptedColumn(projectTest.getId(), collaborator, 0);
    List<Project> listProjects = application.getProjectsWithInvitationsRefused(user);
    assertTrue(listProjects.contains(projectTest));
  }

  @Test
  void testGetProjectsWithInvitationsAccepted() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();

    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    application.updateAcceptedColumn(projectTest.getId(), collaborator, 1);
    List<Project> listProjects = application.getProjectsWithInvitationsAccepted(user);
    assertTrue(listProjects.contains(projectTest));
  }

  @Test
  void testGetProjectsWithInvitationsWaiting() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();

    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    Project projectTest = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    application.register(user);
    application.register(collaborator);
    application.connect(user.getUsername(), user.getPassword());

    application.insertProject(projectTest, false);
    application.insertProjectCollaboratorRow(projectTest, collaborator);
    List<Project> listProjects = application.getProjectsWithInvitationsWaiting(user);
    assertTrue(listProjects.contains(projectTest));
  }

  @Test
  void testLoadUsersFromDatabase() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();

    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User collaborator = new User(COLLABORATOR_USER_ID, COLLABORATOR_USERNAME, COLLABORATOR_EMAIL,
        COLLABORATOR_LAST_NAME, COLLABORATOR_FIRST_NAME, COLLABORATOR_PASSWORD);

    application.register(user);
    application.register(collaborator);
    List<User> users = application.loadUsersFromDatabase();

    assertNotNull(users);
  }

  @Test
  void testGetTagFromDatabase() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();
    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);

    Tag tagTest = new Tag(TAG_ID, TAG_TEXT);
    application.createTag(TAG_TEXT);
    assertEquals(tagTest, application.getTagFromDatabase(TAG_TEXT));
  }

  @Test
  void testGetUserTasksCloseToDeadline() throws DatabaseException, ConnectionFailedException {
    Application application = new Application();

    User user = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    application.register(user);

    Long startDate = DateTimeUtils.getCurrentTime();
    Long endDate = startDate + 21600000; // adds 6 hours
    Long secondEndDate = startDate + MILLISECONDS_IN_DAY + (1000); // Adds 24 hours
    Project userProject = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, user, null, PARENT_COLOR);

    Task firstTask = new Task(TASK_ID, TASK_DESCRIPTION, startDate, endDate, PARENT_PROJECT_ID);

    Project insertedProject = application.insertProject(userProject, false);
    application.addTaskToProject(insertedProject, TASK_DESCRIPTION, startDate, endDate);
    application.addTaskToProject(insertedProject, TASK_DESCRIPTION, startDate, TASK_END_DATE);
    application.addTaskToProject(insertedProject, TASK_DESCRIPTION, startDate, secondEndDate);

    List<Task> expectedList = Collections.singletonList(firstTask);
    assertEquals(expectedList, application.getUserTasksCloseToDeadline());
  }

  @AfterEach
  void deleteDatabase() throws IOException {
    boolean deletedFile = file.delete();
    if (!deletedFile) {
      throw new IOException("Cannot delete file");
    }
  }
}
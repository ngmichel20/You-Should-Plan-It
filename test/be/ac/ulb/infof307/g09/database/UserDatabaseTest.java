package be.ac.ulb.infof307.g09.database;

import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import be.ac.ulb.infof307.g09.application.models.User;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDatabaseTest {

  //USER
  private static final int USER_ID = 1;
  private static final String USERNAME = "JohnDoe";
  private static final String EMAIL = "dummy@gmail.com";
  private static final String LAST_NAME = "Doe";
  private static final String FIRST_NAME = "John";
  private static final String PASSWORD = "dummy";

  //NONEXISTENT USER
  private static final String NONEXISTENT_USERNAME = "nonExistentUsername";
  private static final String NONEXISTENT_EMAIL = "nonExistentEmail";
  private static final String NONEXISTENT_LAST_NAME = "NotDoe";
  private static final String NONEXISTENT_FIRST_NAME = "NotJohn";
  private static final String NONEXISTENT_PASSWORD = "nonExistentPassword";

  private static UserDatabase userDatabase;
  private static final String DATABASE_TEST_PATH = Paths.get("").toAbsolutePath() + "/test/test.db";
  private static final String START_PATH = "src/be/ac/ulb/infof307/g09/";
  private static File file;

  @BeforeEach
  void setUp() throws ConnectionFailedException, DatabaseException {
    if (userDatabase == null) {
      userDatabase = UserDatabase.getInstance();
    }
    UserDatabase.setDatabasePath(DATABASE_TEST_PATH);
    UserDatabase.createNewDatabaseFile(START_PATH);
    file = UserDatabase.getInstance().getDatabase();
  }

  @Test
  void testInsertAndGet() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    User newUser = userDatabase.insert(tmp);
    assertEquals(userDatabase.getUser(USERNAME, PASSWORD), newUser);
  }

  @Test
  void testGetByUsername() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    userDatabase.insert(tmp);
    User result = userDatabase.getByUsername(USERNAME);

    assertEquals(USERNAME, result.getUsername());
  }

  @Test
  void testGetByUsernameFail() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    userDatabase.insert(tmp);
    User result = userDatabase.getByUsername(USERNAME);

    assertNotEquals(result.getUsername(), NONEXISTENT_USERNAME);
  }

  @Test
  void testGetById() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    userDatabase.insert(tmp);
    User result = userDatabase.getUserById(USER_ID);

    assertEquals(USERNAME, result.getUsername());
  }

  @Test
  void testGetByIdFail() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    userDatabase.insert(tmp);
    User result = userDatabase.getUserById(USER_ID);

    assertNotEquals(result.getUsername(), NONEXISTENT_USERNAME);
  }

  @Test
  void testCheckIfEmailExists() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    userDatabase.insert(tmp);
    assertTrue(userDatabase.checkIfEmailExists(EMAIL));
  }

  @Test
  void testCheckIfEmailExistsFail() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    userDatabase.insert(tmp);
    assertFalse(userDatabase.checkIfEmailExists(NONEXISTENT_EMAIL));
  }

  @Test
  void testGetAllUsers() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    userDatabase.insert(tmp);
    List<User> list = new ArrayList<>();
    list.add(tmp);
    assertEquals(list, userDatabase.getAllUsers(NONEXISTENT_USERNAME));
  }

  @Test
  void testUpdateUser() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    userDatabase.insert(tmp);
    userDatabase.updateUser(tmp, new User(USER_ID, NONEXISTENT_USERNAME, NONEXISTENT_EMAIL, LAST_NAME, FIRST_NAME, PASSWORD));
    User result = userDatabase.getByUsername(NONEXISTENT_USERNAME);

    assertEquals(result.getUsername(), NONEXISTENT_USERNAME);
  }

  @Test
  void testUpdateUserFailUsernameNotExist() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    userDatabase.insert(tmp);
    userDatabase.updateUser(tmp, new User(USER_ID, NONEXISTENT_USERNAME, NONEXISTENT_EMAIL, LAST_NAME, FIRST_NAME, PASSWORD));
    User result = userDatabase.getByUsername(USERNAME);

    assertNull(result);
  }

  @Test
  void testUpdateUserFailUsernameAlreadyExist() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    User testUser = new User(USER_ID + 1, NONEXISTENT_USERNAME, NONEXISTENT_EMAIL, NONEXISTENT_LAST_NAME,
        NONEXISTENT_FIRST_NAME, NONEXISTENT_PASSWORD);

    userDatabase.insert(tmp);
    userDatabase.insert(testUser);
    assertThrows(IllegalArgumentException.class,
        () -> userDatabase.updateUser(tmp, new User(USER_ID, NONEXISTENT_USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD)));
  }

  @Test
  void testUpdateUserFailEmailAlreadyExist() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);
    User testUser = new User(USER_ID + 1, NONEXISTENT_USERNAME, NONEXISTENT_EMAIL, NONEXISTENT_LAST_NAME,
        NONEXISTENT_FIRST_NAME, NONEXISTENT_PASSWORD);

    userDatabase.insert(tmp);
    userDatabase.insert(testUser);
    assertThrows(IllegalArgumentException.class,
        () -> userDatabase.updateUser(tmp, new User(USER_ID, USERNAME, NONEXISTENT_EMAIL, LAST_NAME, FIRST_NAME, PASSWORD)));
  }

  @Test
  void testInsertUsernameException() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    userDatabase.insert(tmp);

    assertThrows(IllegalArgumentException.class,
        () -> userDatabase.insert(new User(USER_ID, USERNAME, NONEXISTENT_EMAIL, LAST_NAME, FIRST_NAME, PASSWORD)));
  }

  @Test
  void testInsertEmailException() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    userDatabase.insert(tmp);

    assertThrows(IllegalArgumentException.class,
        () -> userDatabase.insert(new User(USER_ID, NONEXISTENT_USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD)));
  }

  @Test
  void testDeleteException() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    userDatabase.insert(tmp);
    assertThrows(IllegalArgumentException.class,
        () -> userDatabase.delete(NONEXISTENT_USERNAME));
  }

  @Test
  void testDelete() throws DatabaseException, ConnectionFailedException {
    User tmp = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

    userDatabase.insert(tmp);
    userDatabase.delete(USERNAME);
    User result = userDatabase.getByUsername(USERNAME);

    assertNull(result);
  }

  @Test
  void testThrowExceptionDatabase() {
    Assertions.assertThrows(DatabaseException.class, () ->
        UserDatabase.throwException(new SQLException()));
  }

  @Test
  void testThrowConnectionException() {
    Assertions.assertThrows(ConnectionFailedException.class, () ->
        UserDatabase.throwConnectionException(new ClassNotFoundException()));
  }

  @Test
  void testCheckIfDatabaseExists() {
    assertTrue(userDatabase.checkIfDatabaseExists());
  }

  @AfterEach
  void deleteDatabase() throws IOException {
    boolean deletedFile = file.delete();
    if (!deletedFile) {
      throw new IOException("Cannot delete file");
    }
  }
}
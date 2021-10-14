package be.ac.ulb.infof307.g09.application.models;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

  private static final int TASK_ID = 1;
  private static final int PROJECT_ID = 11;
  private static final String DESCRIPTION = "This is a test for junit";
  private static final Long START_DATE = 1639134000000L; //10-12-2021 12:00
  private static final Long END_DATE = 1639220400000L; //11-12-2021 12:00
  private static final Task TASK = new Task(TASK_ID, DESCRIPTION, START_DATE, END_DATE, PROJECT_ID);

  private static final int USER_ID = 0;
  private static final String USERNAME = "JohnDoe";
  private static final String EMAIL = "dummy@gmail.com";
  private static final String LAST_NAME = "Doe";
  private static final String FIRST_NAME = "John";
  private static final String PASSWORD = "dummy";
  private static final User USER_TEST = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

  @Test
  void testToString() {
    assertEquals(TASK.toString(), DESCRIPTION + ", start: " + START_DATE + ", end: " + END_DATE);
  }

  @Test
  void testEqualsSame() {
    assertEquals(TASK, TASK);
  }

  @Test
  void testEqualsDifferentClass() {
    Object testInt = 3;
    assertNotEquals(TASK, testInt);
  }

  @Test
  void testEqualsNull() {
    assertNotEquals(TASK, null);
  }

  @Test
  void testEqualsNewObjectSame() {
    Task tmp = new Task(TASK_ID, DESCRIPTION, START_DATE, END_DATE, PROJECT_ID);
    assertEquals(TASK, tmp);
  }

  @Test
  void testSetGetProjectId() {
    TASK.setProjectId(3);
    assertEquals(TASK.getProjectId(), 3);
    TASK.setProjectId(PROJECT_ID);
  }

  @Test
  void testGetId() {
    assertEquals(TASK.getId(), TASK_ID);
  }

  @Test
  void testSetGetDescription() {
    TASK.setDescription("Not a test");
    assertEquals(TASK.getDescription(), "Not a test");
    TASK.setDescription(DESCRIPTION);
  }

  @Test
  void testSetGetStartDate() {
    TASK.setStartDate(END_DATE);
    assertEquals(TASK.getStartDate(), END_DATE);
    TASK.setStartDate(START_DATE);
  }

  @Test
  void testSetGetEndDate() {
    TASK.setEndDate(START_DATE);
    assertEquals(TASK.getEndDate(), START_DATE);
    TASK.setEndDate(END_DATE);
  }

  @Test
  void testGetAssignedUsers() {
    TASK.addAssignedUser(USER_TEST);
    List<User> expectedAssignedUsers = Collections.singletonList(USER_TEST);
    assertEquals(expectedAssignedUsers, TASK.getAssignedUsers());
  }

  @Test
  void testSetAssignedUsers() {
    TASK.setAssignedUsers(Collections.singletonList(USER_TEST));
    List<User> expectedAssignedUsers = Collections.singletonList(USER_TEST);
    assertEquals(expectedAssignedUsers, TASK.getAssignedUsers());
  }

  @Test
  void testAddAssignedUser() {
    TASK.addAssignedUser(USER_TEST);
    List<User> expectedAssignedUsers = Collections.singletonList(USER_TEST);
    assertEquals(expectedAssignedUsers, TASK.getAssignedUsers());
  }

  @Test
  void testAddAssignedUserAddedTwiceNoError() {
    TASK.addAssignedUser(USER_TEST);
    TASK.addAssignedUser(USER_TEST);
    List<User> expectedAssignedUsers = Collections.singletonList(USER_TEST);
    assertEquals(expectedAssignedUsers, TASK.getAssignedUsers());
  }

  @Test
  void testRemoveAssignedUser() {
    TASK.addAssignedUser(USER_TEST);
    TASK.removeAssignedUser(USER_TEST);
    List<User> expectedAssignedUsers = Collections.emptyList();
    assertEquals(expectedAssignedUsers, TASK.getAssignedUsers());
  }

  @Test
  void testRemoveAssignedUserRemovedTwiceNoError() {
    TASK.addAssignedUser(USER_TEST);
    TASK.removeAssignedUser(USER_TEST);
    TASK.removeAssignedUser(USER_TEST);
    List<User> expectedAssignedUsers = Collections.emptyList();
    assertEquals(expectedAssignedUsers, TASK.getAssignedUsers());
  }

  @Test
  void testHashCode() {
    assertEquals(TASK.hashCode(), new Task(TASK_ID, DESCRIPTION, START_DATE, END_DATE, PROJECT_ID).hashCode());
  }
}
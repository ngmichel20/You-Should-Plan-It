package be.ac.ulb.infof307.g09.application.models;

import be.ac.ulb.infof307.g09.application.utilities.DateTimeUtils;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

  //PARENT_PROJECT
  private static final Integer PARENT_PROJECT_ID = 1;
  private static final String PARENT_TITLE = "Test parentProject";
  private static final String PARENT_DESCRIPTION = "This is a test of a parentProject";
  private static final Long PARENT_START_DATE = 1639047600000L; //9-12-2021 12:00
  private static final Long PARENT_END_DATE = 1639306800000L; //12-12-2021 12:00
  private static final Long PARENT_INITIAL_DURATION = PARENT_END_DATE - PARENT_START_DATE;
  private static final Color PARENT_COLOR = Color.BLUE;

  //SUB_PROJECT
  private static final Integer SUB_PROJECT_ID = 2;
  private static final String SUB_TITLE = "Test subProject";
  private static final String SUB_DESCRIPTION = "This is a test of a subProject";
  private static final Long SUB_START_DATE = 1639134000000L; //10-12-2021 12:00
  private static final Long SUB_END_DATE = 1639220400000L; //11-12-2021 12:00
  private static final Long SUB_INITIAL_DURATION = SUB_END_DATE - SUB_START_DATE;
  private static final Color SUB_COLOR = Color.GREEN;

  //SUB_SUB_PROJECT
  private static final Integer SUB_SUB_PROJECT_ID = 3;
  private static final String SUB_SUB_TITLE = "Test subSubProject";
  private static final String SUB_SUB_DESCRIPTION = "This is a test of a subSubProject";
  private static final Long SUB_SUB_START_DATE = 1639134000000L; //10-12-2021 12:00
  private static final Long SUB_SUB_END_DATE = 1639220400000L; //11-12-2021 12:00
  private static final Long SUB_SUB_INITIAL_DURATION = SUB_SUB_START_DATE - SUB_SUB_END_DATE;
  private static final Color SUB_SUB_COLOR = Color.GREEN;

  //USER
  private static final Integer USER_ID = 1;
  private static final String USERNAME = "JohnDoe";
  private static final String EMAIL = "dummy@gmail.com";
  private static final String LAST_NAME = "Doe";
  private static final String FIRST_NAME = "John";
  private static final String PASSWORD = "dummy";

  //SECOND_USER
  private static final int SECOND_USER_ID = 2;
  private static final String SECOND_USERNAME = "secondUser";
  private static final String SECOND_EMAIL = "secondUser@gmail.com";
  private static final String SECOND_LAST_NAME = "lastname";
  private static final String SECOND_FIRST_NAME = "firstname";
  private static final String SECOND_PASSWORD = "secondUser";

  //TASK
  private static final int TASK_ID = 1;
  private static final int PROJECT_ID = 11;
  private static final String TASK_DESCRIPTION = "This is a test for junit";
  private static final Long START_DATE = 1639134000000L; //10-12-2021 12:00
  private static final Long END_DATE = 1639220400000L; //11-12-2021 12:00

  //USER_TEST
  private static final int MILLISECONDS_IN_DAY = (1000 * 60 * 60 * 24);

  private static final User USER_TEST = new User(USER_ID, USERNAME, EMAIL, LAST_NAME, FIRST_NAME, PASSWORD);

  //SECOND_USER_TEST
  private static final User SECOND_USER_TEST = new User(SECOND_USER_ID, SECOND_USERNAME, SECOND_EMAIL,
      SECOND_LAST_NAME, SECOND_FIRST_NAME, SECOND_PASSWORD);

  //PARENT_PROJECT
  private static final Project PARENT_PROJECT = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
      PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, USER_TEST, null, PARENT_COLOR);

  //SUB_PROJECT
  private static final Project SUB_PROJECT = new Project(SUB_PROJECT_ID, SUB_TITLE, SUB_DESCRIPTION,
      SUB_START_DATE, SUB_END_DATE, SUB_INITIAL_DURATION, USER_TEST, PARENT_PROJECT, SUB_COLOR);

  //SUB_SUB_PROJECT
  private static final Project SUB_SUB_PROJECT = new Project(SUB_SUB_PROJECT_ID, SUB_SUB_TITLE, SUB_SUB_DESCRIPTION,
      SUB_SUB_START_DATE, SUB_SUB_END_DATE, SUB_SUB_INITIAL_DURATION, USER_TEST, SUB_PROJECT, SUB_SUB_COLOR);

  @Test
  void testGetParentProjectId() {
    assertEquals(PARENT_PROJECT.getId(), PARENT_PROJECT_ID);
  }

  @Test
  void testGetSubProjectId() {
    assertEquals(SUB_PROJECT.getId(), SUB_PROJECT_ID);
  }

  @Test
  void testSetGetParentTitle() {
    PARENT_PROJECT.setTitle(SUB_TITLE);
    assertEquals(PARENT_PROJECT.getTitle(), SUB_TITLE);
    PARENT_PROJECT.setTitle(PARENT_TITLE);
  }

  @Test
  void testSetGetSubTitle() {
    SUB_PROJECT.setTitle(PARENT_TITLE);
    assertEquals(SUB_PROJECT.getTitle(), PARENT_TITLE);
    SUB_PROJECT.setTitle(SUB_TITLE);
  }

  @Test
  void testSetGetParentProject() {
    SUB_PROJECT.setParentProject(SUB_PROJECT);
    assertEquals(SUB_PROJECT.getParentProject(), SUB_PROJECT);
    SUB_PROJECT.setParentProject(PARENT_PROJECT);
  }

  @Test
  void testSetGetParentDescription() {
    PARENT_PROJECT.setDescription(SUB_DESCRIPTION);
    assertEquals(PARENT_PROJECT.getDescription(), SUB_DESCRIPTION);
    PARENT_PROJECT.setDescription(PARENT_DESCRIPTION);
  }

  @Test
  void testSetGetSubDescription() {
    SUB_PROJECT.setDescription(PARENT_DESCRIPTION);
    assertEquals(SUB_PROJECT.getDescription(), PARENT_DESCRIPTION);
    SUB_PROJECT.setDescription(SUB_DESCRIPTION);
  }

  @Test
  void testGetParentStartDate() {
    assertEquals(PARENT_PROJECT.getStartDate(), PARENT_START_DATE);
  }

  @Test
  void testGetSubStartDate() {
    assertEquals(SUB_PROJECT.getStartDate(), SUB_START_DATE);
  }

  @Test
  void testSetGetParentEndDate() {
    PARENT_PROJECT.setEndDate(SUB_END_DATE);
    assertEquals(PARENT_PROJECT.getEndDate(), SUB_END_DATE);
    PARENT_PROJECT.setEndDate(PARENT_END_DATE);
  }

  @Test
  void testSetGetSubEndDate() {
    SUB_PROJECT.setEndDate(PARENT_END_DATE);
    assertEquals(SUB_PROJECT.getEndDate(), PARENT_END_DATE);
    SUB_PROJECT.setEndDate(SUB_END_DATE);
  }

  @Test
  void testGetInitialDuration() {
    long expectedValue = PARENT_END_DATE - PARENT_START_DATE;
    assertEquals(expectedValue, PARENT_PROJECT.getInitialDuration());
  }

  @Test
  void testGetCurrentDuration() {
    long expectedValue = PARENT_END_DATE - PARENT_START_DATE;
    assertEquals(expectedValue, PARENT_PROJECT.getCurrentDuration());
  }

  @Test
  void testGetTimeLeft() {
    long current = DateTimeUtils.getCurrentTime();
    long expectedValue = PARENT_END_DATE - current;
    assertEquals(expectedValue, PARENT_PROJECT.getTimeLeft());
  }

  @Test
  void testGetParentAuthor() {
    assertEquals(PARENT_PROJECT.getAuthor(), USER_TEST);
  }

  @Test
  void testGetSubAuthor() {
    assertEquals(SUB_PROJECT.getAuthor(), USER_TEST);
  }

  @Test
  void testSetGetColor() {
    PARENT_PROJECT.setColor(Color.PEACH);
    assertEquals(Color.PEACH, PARENT_PROJECT.getColor());
    PARENT_PROJECT.setColor(Color.BLUE);
  }

  @Test
  void testGetColorCode() {
    assertEquals(1, PARENT_PROJECT.getColorCode());
  }

  @Test
  void testSetGetTasks() {
    PARENT_PROJECT.setTasks(new ArrayList<>());
    assertEquals(PARENT_PROJECT.getTasks(), new ArrayList<>());
  }

  @Test
  void testAddTask() {
    ArrayList<Task> testTask = new ArrayList<>();
    testTask.add(new Task(0, TASK_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_PROJECT_ID));
    PARENT_PROJECT.setTasks(new ArrayList<>());
    PARENT_PROJECT.addTask(new Task(0, TASK_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_PROJECT_ID));
    assertEquals(PARENT_PROJECT.getTasks(), testTask);
  }

  @Test
  void testUpdateTask() {
    Task testTask = new Task(0, TASK_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_PROJECT_ID);
    PARENT_PROJECT.setTasks(new ArrayList<>());
    PARENT_PROJECT.addTask(testTask);
    testTask.setDescription("New test description");
    PARENT_PROJECT.updateTask(testTask);
    ArrayList<Task> testTasks = new ArrayList<>();
    testTasks.add(testTask);
    assertEquals(PARENT_PROJECT.getTasks(), testTasks);
  }

  @Test
  void testRemoveTask() {
    Task testTask = new Task(0, TASK_DESCRIPTION, PARENT_START_DATE, PARENT_END_DATE, PARENT_PROJECT_ID);
    PARENT_PROJECT.setTasks(new ArrayList<>());
    PARENT_PROJECT.addTask(testTask);
    PARENT_PROJECT.removeTask(testTask);
    assertEquals(PARENT_PROJECT.getTasks(), new ArrayList<>());
  }

  @Test
  void testGetTasksCompleted() {
    int expectedValue = 1;
    PARENT_PROJECT.addTask(new Task(0, TASK_DESCRIPTION, 0L, 0L, PARENT_PROJECT_ID));
    assertEquals(expectedValue, PARENT_PROJECT.getTasksCompleted());
  }

  @Test
  void testGetCompletedTasksCountWithCompletedTasks() {
    Pair<Integer, Integer> expectedValue = new Pair<>(0, 1);
    PARENT_PROJECT.addTask(new Task(TASK_ID, TASK_DESCRIPTION, START_DATE, END_DATE, PROJECT_ID));
    assertEquals(expectedValue, PARENT_PROJECT.getCompletedTasksCount());
  }

  @Test
  void testGetCompletedTasksCountWithoutTasks() {
    Pair<Integer, Integer> expectedValue = new Pair<>(0, 0);
    assertEquals(expectedValue, PARENT_PROJECT.getCompletedTasksCount());
  }

  @Test
  void testSetGetTags() {
    PARENT_PROJECT.setTags(new ArrayList<>());
    assertEquals(PARENT_PROJECT.getTags(), new ArrayList<>());
  }

  @Test
  void testAddTag() {
    ArrayList<Tag> testTags = new ArrayList<>();
    testTags.add(new Tag(0, "Test tag"));
    PARENT_PROJECT.setTags(new ArrayList<>());
    PARENT_PROJECT.addTag(new Tag(0, "Test tag"));

    assertEquals(PARENT_PROJECT.getTags(), testTags);
  }

  @Test
  void testRemoveTag() {
    Tag testTag = new Tag(0, "Test tag");
    PARENT_PROJECT.setTags(new ArrayList<>());
    PARENT_PROJECT.addTag(testTag);
    PARENT_PROJECT.removeTag(testTag);
    assertEquals(PARENT_PROJECT.getTags(), new ArrayList<>());
  }

  @Test
  void testIsSubProject() {
    assertTrue(SUB_PROJECT.isSubProject());
  }

  @Test
  void testIsSubProjectFail() {
    assertFalse(PARENT_PROJECT.isSubProject());
  }

  @Test
  void testUpdateProject() {
    PARENT_PROJECT.updateProject(SUB_TITLE, SUB_DESCRIPTION, SUB_END_DATE, SUB_COLOR);
    assertEquals(PARENT_PROJECT, new Project(PARENT_PROJECT_ID, SUB_TITLE, SUB_DESCRIPTION, PARENT_START_DATE, SUB_END_DATE, PARENT_INITIAL_DURATION, USER_TEST, null, SUB_COLOR));
    PARENT_PROJECT.updateProject(PARENT_TITLE, PARENT_DESCRIPTION, PARENT_END_DATE, PARENT_COLOR);
  }

  @Test
  void testToCsvFormat() {
    String expectedString = USERNAME + ";" +
        PARENT_TITLE + ";" +
        TimeUnit.MILLISECONDS.toHours(PARENT_INITIAL_DURATION) + ";" +
        TimeUnit.MILLISECONDS.toHours(PARENT_INITIAL_DURATION) + ";" + 0 + ";";
    assertEquals(PARENT_PROJECT.toCsvFormat(), expectedString);
  }

  @Test
  void testToStringParentProject() {
    assertEquals(PARENT_PROJECT.toString(), PARENT_TITLE);
  }

  @Test
  void testToStringSubProject() {
    assertEquals(SUB_PROJECT.toString(), SUB_TITLE);
  }

  @Test
  void testEqualsNull() {
    assertNotEquals(PARENT_PROJECT, null);
  }

  @Test
  void testEqualsSame() {
    assertEquals(PARENT_PROJECT, PARENT_PROJECT);
  }

  @Test
  void testEqualsDifferentClass() {
    Object testEmptyString = "";
    assertNotEquals(PARENT_PROJECT, testEmptyString);
  }

  @Test
  void testEqualsDifferentProject() {
    assertNotEquals(PARENT_PROJECT, SUB_PROJECT);
  }

  @Test
  void testEqualsSameParametersParentProject() {
    Project tmp = new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, USER_TEST, null, PARENT_COLOR);
    assertEquals(PARENT_PROJECT, tmp);
  }

  @Test
  void testEqualsSameParametersSubProject() {
    Project tmp = new Project(SUB_PROJECT_ID, SUB_TITLE, SUB_DESCRIPTION,
        SUB_START_DATE, SUB_END_DATE, SUB_INITIAL_DURATION, USER_TEST, PARENT_PROJECT, SUB_COLOR);
    assertEquals(SUB_PROJECT, tmp);
  }

  @Test
  void testSetGetCollaborators() {
    ArrayList<User> users = new ArrayList<>();
    users.add(SECOND_USER_TEST);
    PARENT_PROJECT.addSubProjects(SUB_PROJECT);
    PARENT_PROJECT.setCollaborators(users);
    assertEquals(PARENT_PROJECT.getCollaborators(), users);
    PARENT_PROJECT.setCollaborators(new ArrayList<>());
    PARENT_PROJECT.setSubProjects(new ArrayList<>());
  }

  @Test
  void testRemoveCollaborator() {
    ArrayList<User> users = new ArrayList<>();
    users.add(SECOND_USER_TEST);
    PARENT_PROJECT.addSubProjects(SUB_PROJECT);
    PARENT_PROJECT.setCollaborators(users);
    PARENT_PROJECT.removeCollaborator(SECOND_USER_TEST);
    assertEquals(PARENT_PROJECT.getCollaborators(), new ArrayList<>());
    PARENT_PROJECT.setCollaborators(new ArrayList<>());
    PARENT_PROJECT.setSubProjects(new ArrayList<>());
  }

  @Test
  void testAddCollaborator() {
    ArrayList<User> secondUser = new ArrayList<>();
    secondUser.add(SECOND_USER_TEST);
    PARENT_PROJECT.addSubProjects(SUB_PROJECT);
    PARENT_PROJECT.addCollaborator(SECOND_USER_TEST);
    assertEquals(PARENT_PROJECT.getCollaborators(), secondUser);
    PARENT_PROJECT.setCollaborators(new ArrayList<>());
    PARENT_PROJECT.setSubProjects(new ArrayList<>());
  }

  @Test
  void testGetTasksCloseToDeadline() {
    long startDate = DateTimeUtils.getCurrentTime();
    Long endDate = startDate + 21600000; // adds 6 hours
    Long secondEndDate = startDate + MILLISECONDS_IN_DAY + (1000 * 60 * 60); // Adds 25 hours

    Task firstTask = new Task(TASK_ID, TASK_DESCRIPTION, startDate, endDate, PARENT_PROJECT_ID);
    Task secondTask = new Task(3, TASK_DESCRIPTION, startDate, secondEndDate, PARENT_PROJECT_ID);
    PARENT_PROJECT.addTask(firstTask);
    PARENT_PROJECT.addTask(secondTask);

    List<Task> expectedList = Collections.singletonList(firstTask);

    assertEquals(expectedList, PARENT_PROJECT.getTasksCloseToDeadline());
  }

  @Test
  void testSetGetSubProjects() {
    ArrayList<Project> subProjects = new ArrayList<>();
    subProjects.add(SUB_PROJECT);
    PARENT_PROJECT.setSubProjects(subProjects);
    assertEquals(PARENT_PROJECT.getSubProjects(), subProjects);
  }

  @Test
  void testRemoveSubProject() {
    PARENT_PROJECT.addSubProjects(SUB_PROJECT);
    PARENT_PROJECT.removeSubProject(SUB_PROJECT);
    assertTrue(PARENT_PROJECT.getSubProjects().isEmpty());
  }

  @Test
  void testHashCode() {
    assertEquals(PARENT_PROJECT.hashCode(), new Project(PARENT_PROJECT_ID, PARENT_TITLE, PARENT_DESCRIPTION,
        PARENT_START_DATE, PARENT_END_DATE, PARENT_INITIAL_DURATION, USER_TEST, null, PARENT_COLOR).hashCode());
  }

  @Test
  void testGetAllChildren() {
    PARENT_PROJECT.addSubProjects(SUB_PROJECT);
    SUB_PROJECT.addSubProjects(SUB_SUB_PROJECT);
    List<Project> expectedProjects = Arrays.asList(SUB_PROJECT, SUB_SUB_PROJECT);
    assertEquals(expectedProjects, PARENT_PROJECT.getAllChildren());
    PARENT_PROJECT.setSubProjects(new ArrayList<>());
    SUB_PROJECT.setSubProjects(new ArrayList<>());
  }
}
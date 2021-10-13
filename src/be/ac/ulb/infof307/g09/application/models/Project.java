package be.ac.ulb.infof307.g09.application.models;

import be.ac.ulb.infof307.g09.application.utilities.DateTimeUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.util.Pair;

/**
 * The Project class.
 *
 * @author Nguyen Khanh-Michel
 * @author El Bakkali Soufian
 */
public class Project implements Serializable {

  public static final int MILLISECONDS_IN_DAY = (1000 * 60 * 60 * 24);
  private final int id;
  private String title;
  private String description;
  private final long startDate;
  private long endDate;
  private final long initialDuration;
  private final User author;
  private Project parentProject;
  private Color color;
  private List<Task> tasks;
  private List<Tag> tags;
  private List<User> collaborators;
  private List<Project> subProjects;
  private static final String CSV_SEPARATOR = ";";

  /**
   * Constructor of a project.
   *
   * @param projectId              The number that identifies the project
   * @param projectTitle           The title of the project
   * @param projectDescription     The description of the project
   * @param projectStartDate       The start date of the project
   * @param projectEndDate         The end date of the project
   * @param projectInitialDuration The initial duration of the project
   * @param projectAuthor          The author of the project
   * @param projectParent          The parent of the project
   * @param projectColor           The color of the project
   */
  public Project(int projectId, String projectTitle, String projectDescription,
                 long projectStartDate, long projectEndDate, long projectInitialDuration,
                 User projectAuthor, Project projectParent, Color projectColor) {
    this.id = projectId;
    this.title = projectTitle;
    this.description = projectDescription;
    this.startDate = projectStartDate;
    this.endDate = projectEndDate;
    this.initialDuration = projectInitialDuration;
    this.author = projectAuthor;
    this.parentProject = projectParent;
    this.color = projectColor;
    this.tasks = new ArrayList<>();
    this.tags = new ArrayList<>();
    this.collaborators = new ArrayList<>();
    this.subProjects = new ArrayList<>();
  }


  /**
   * Returns the id of the project.
   *
   * @return The id of the project
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the project's title.
   *
   * @param newTitle project title
   */
  public void setTitle(String newTitle) {
    this.title = newTitle;
  }

  /**
   * Returns the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the project's description.
   *
   * @param newDescription project description
   */
  public void setDescription(String newDescription) {
    this.description = newDescription;
  }

  /**
   * Returns the start date.
   *
   * @return the start date
   */
  public long getStartDate() {
    return startDate;
  }

  /**
   * Returns the end date.
   *
   * @return the end date
   */
  public long getEndDate() {
    return endDate;
  }

  /**
   * Sets the endDate of the project.
   *
   * @param newEndDate end date in long
   */
  public void setEndDate(long newEndDate) {
    this.endDate = newEndDate;
  }

  /**
   * Returns all the sub projects of the project.
   *
   * @return list of projects (which are the sub projects)
   */
  public List<Project> getSubProjects() {
    return subProjects;
  }

  /**
   * Returns all the children of the project.
   *
   * @return list of projects (which are the sub projects)
   */
  public List<Project> getAllChildren() {
    List<Project> projects = new ArrayList<>();

    for (Project subProject : subProjects) {
      projects.add(subProject);
      projects.addAll(subProject.getAllChildren());
    }

    return projects;
  }

  /**
   * Sets the sub projects.
   *
   * @param newSubProjects the sub projects to set
   */
  public void setSubProjects(List<Project> newSubProjects) {
    this.subProjects = newSubProjects;
  }

  /**
   * Returns the initial duration of the project.
   *
   * @return the initial duration
   */
  public long getInitialDuration() {
    return initialDuration;
  }

  /**
   * Returns the current duration of the project.
   *
   * @return the current duration
   */
  public long getCurrentDuration() {
    return endDate - startDate;
  }

  /**
   * Returns the time left to the project.
   *
   * @return the time left
   */
  public long getTimeLeft() {
    long current = DateTimeUtils.getCurrentTime();
    return current > endDate ? 0 : (endDate - current);
  }

  /**
   * Returns the author as an User.
   *
   * @return the author as an User
   */
  public User getAuthor() {
    return author;
  }

  /**
   * Returns the parent project.
   *
   * @return the parent project
   */
  public Project getParentProject() {
    return parentProject;
  }

  /**
   * Sets the parent project.
   *
   * @param projectParent the parent project
   */
  public void setParentProject(Project projectParent) {
    this.parentProject = projectParent;
  }

  /**
   * Returns the project's color.
   *
   * @return project color
   */
  public Color getColor() {
    return color;
  }

  /**
   * Returns the color's code.
   *
   * @return the color code
   */
  public int getColorCode() {
    return color.getColorCode();
  }

  /**
   * Sets the color of the project.
   *
   * @param newColor the color to set
   */
  public void setColor(Color newColor) {
    this.color = newColor;
  }


  /**
   * Returns the tasks of the project.
   *
   * @return List containing the project's tasks
   */
  public List<Task> getTasks() {
    return tasks;
  }

  /**
   * Returns the  tasks ending in the next 24 hours.
   *
   * @return list of tasks ending in the next 24 hours.
   */
  public List<Task> getTasksCloseToDeadline() {
    List<Task> tasksCloseToDeadline = new ArrayList<>();
    long current = DateTimeUtils.getCurrentTime();
    for (Task task : tasks) {
      long timeLeft = task.getEndDate() - current;
      if (timeLeft < MILLISECONDS_IN_DAY) {
        tasksCloseToDeadline.add(task);
      }
    }
    return tasksCloseToDeadline;
  }

  /**
   * Sets tasks list of the project.
   *
   * @param newTasks List of tasks
   */
  public void setTasks(List<Task> newTasks) {
    this.tasks = newTasks;
  }

  /**
   * Adds the task to the list of project's tasks list.
   *
   * @param task new task
   */
  public void addTask(Task task) {
    if (!this.tasks.contains(task)) {
      this.tasks.add(task);
    }
  }

  /**
   * Updates the task in project's tasks list.
   *
   * @param task task to update
   */
  public void updateTask(Task task) {
    int foundIndex = -1;

    for (int i = 0; i < this.tasks.size(); i++) {
      if (this.tasks.get(i).getId() == task.getId()) {
        foundIndex = i;
      }
    }

    if (foundIndex >= 0) {
      this.tasks.set(foundIndex, task);
    }
  }

  /**
   * Removes the task from project's tasks.
   *
   * @param task task to remove
   */
  public void removeTask(Task task) {
    this.tasks.remove(task);
  }

  /**
   * Removes the collaborator from the collaboration list.
   *
   * @param collaborator user to remove
   */
  public void removeCollaborator(User collaborator) {
    this.collaborators.remove(collaborator);
    for (Project subProject : this.subProjects) {
      subProject.removeCollaborator(collaborator);
    }
  }

  /**
   * Returns the amount of tasks that are completed.
   *
   * @return amount of tasks completed.
   */
  public int getTasksCompleted() {
    int tasksCompleted = 0;
    long currentTime = DateTimeUtils.getCurrentTime();

    for (Task task : tasks) {
      if (task.getEndDate() < currentTime) {
        tasksCompleted++;
      }
    }
    return tasksCompleted;
  }

  /**
   * Returns the completed and ongoing tasks of the project.
   *
   * @return a pair containing the completed and ongoing tasks of the project.
   */
  public Pair<Integer, Integer> getCompletedTasksCount() {
    Pair<Integer, Integer> res;
    if (!tasks.isEmpty()) {
      int tasksCompleted = getTasksCompleted();
      res = new Pair<>(tasksCompleted, tasks.size() - tasksCompleted);
    } else {
      res = new Pair<>(0, 0);
    }
    return res;
  }

  /**
   * Returns the project's tags.
   *
   * @return project's tags
   */
  public List<Tag> getTags() {
    return tags;
  }

  /**
   * Set the list of tags as the project's tags list.
   *
   * @param newTags List of tags
   */
  public void setTags(List<Tag> newTags) {
    this.tags = newTags;
  }

  /**
   * Adds the tag to the project's tags list.
   *
   * @param tagToAdd tag to add
   */
  public void addTag(Tag tagToAdd) {
    if (!this.tags.contains(tagToAdd)) {
      this.tags.add(tagToAdd);
    }
  }

  /**
   * Removes the tag from the project's tags list.
   *
   * @param tagToRemove tag to remove
   */
  public void removeTag(Tag tagToRemove) {
    tags.remove(tagToRemove);
  }

  /**
   * Checks if the project is a subproject.
   *
   * @return true if the project is a subproject, false otherwise
   */
  public boolean isSubProject() {
    return this.parentProject != null;
  }

  /**
   * Returns the collaborators of the project.
   *
   * @return List containing the project's collaborators.
   */
  public List<User> getCollaborators() {
    return collaborators;
  }

  /**
   * Sets the collaborators list.
   *
   * @param newCollaborators the list of collaborators
   */
  public void setCollaborators(List<User> newCollaborators) {
    this.collaborators = newCollaborators;
    for (Project subProject : this.subProjects) {
      subProject.setCollaborators(newCollaborators);
    }
  }

  /**
   * Adds the collaborator to the project's collaborators list.
   *
   * @param collaboratorToAdd collaborator to add
   */
  public void addCollaborator(User collaboratorToAdd) {
    if (!this.collaborators.contains(collaboratorToAdd)) {
      this.collaborators.add(collaboratorToAdd);
    }
    for (Project subProject : this.subProjects) {
      subProject.addCollaborator(collaboratorToAdd);
    }
  }

  /**
   * Adds the sub projects to the subProject's list.
   *
   * @param subProject collaborator to add
   */
  public void addSubProjects(Project subProject) {
    if (!this.subProjects.contains(subProject)) {
      this.subProjects.add(subProject);
    }
  }

  /**
   * Removes the sub project.
   *
   * @param subProject the sub Project to remove
   */
  public void removeSubProject(Project subProject) {
    this.subProjects.remove(subProject);
  }

  /**
   * Updates the title, description and end date of the project.
   *
   * @param newTitle       project title
   * @param newDescription project description
   * @param newEndDate     project end date
   * @param newColor       the new project color
   */
  public void updateProject(String newTitle, String newDescription,
                            long newEndDate, Color newColor) {
    this.setTitle(newTitle);
    this.setDescription(newDescription);
    this.setEndDate(newEndDate);
    this.setColor(newColor);
  }

  /**
   * Returns the data of the project formatted in the CSV format in a String.
   *
   * @return data of the project in a String
   */
  public String toCsvFormat() {
    StringBuilder data = new StringBuilder();
    long initialDuration = TimeUnit.MILLISECONDS.toHours(this.getInitialDuration());
    long currentDuration = TimeUnit.MILLISECONDS.toHours((this.getEndDate() - this.getStartDate()));
    int tasksSize = this.getTasks().size();
    data.append(this.getAuthor().getUsername()).append(CSV_SEPARATOR);
    data.append(this.getTitle()).append(CSV_SEPARATOR);
    data.append(initialDuration).append(CSV_SEPARATOR);
    data.append(currentDuration).append(CSV_SEPARATOR);
    data.append(tasksSize).append(CSV_SEPARATOR);
    return data.toString();
  }

  @Override
  public String toString() {
    return title;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (object == this) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    Project other = (Project) object;
    boolean res = this.getId() == other.getId()
        && this.getTitle().equals(other.getTitle())
        && this.getDescription().equals(other.getDescription())
        && this.getStartDate() == other.getStartDate()
        && this.getEndDate() == other.getEndDate()
        && this.getInitialDuration() == other.getInitialDuration()
        && this.getAuthor().equals(other.getAuthor())
        && this.getColor() == other.getColor();

    if (this.getParentProject() == null) {
      return res && this.getParentProject() == (other.getParentProject());
    } else {
      return res && this.getParentProject().equals((other.getParentProject()));
    }
  }

  @Override
  public int hashCode() {
    return id;
  }
}
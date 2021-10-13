package be.ac.ulb.infof307.g09.application.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Task class.
 *
 * @author Pap Sanou
 */
public class Task implements Serializable {
  private final int id;
  private String description;
  private Long startDate;
  private Long endDate;
  private int projectId;
  private List<User> assignedUsers;

  /**
   * Task constructor.
   *
   * @param taskId          id of the task
   * @param taskDescription description of the task
   * @param taskStartDate   start date of the task
   * @param taskEndDate     end date of the task
   * @param taskProjectId   id of its project
   */
  public Task(int taskId, String taskDescription, Long taskStartDate,
              Long taskEndDate, int taskProjectId) {
    this.id = taskId;
    this.description = taskDescription;
    this.startDate = taskStartDate;
    this.endDate = taskEndDate;
    this.projectId = taskProjectId;
    this.assignedUsers = new ArrayList<>();
  }

  /**
   * Returns the id of Task.
   *
   * @return the id of task
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the description of Task.
   *
   * @return the description of task
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of the Task.
   *
   * @param newDescription the description of the Task
   */
  public void setDescription(String newDescription) {
    this.description = newDescription;
  }

  /**
   * Returns the id of the project associated to the Task.
   *
   * @return the id of the project
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * Sets the id of the project associated to the Task.
   *
   * @param newProjectId the id of the project
   */
  public void setProjectId(int newProjectId) {
    this.projectId = newProjectId;
  }

  /**
   * Returns the date the Task begun.
   *
   * @return the start date
   */
  public Long getStartDate() {
    return startDate;
  }

  /**
   * Sets the date the Task begun.
   *
   * @param newStartDate the start date
   */
  public void setStartDate(long newStartDate) {
    this.startDate = newStartDate;
  }

  /**
   * Returns the date the Task should be completed.
   *
   * @return the end date
   */
  public Long getEndDate() {
    return endDate;
  }

  /**
   * Sets the date the Task should be completed.
   *
   * @param newEndDate the end date
   */
  public void setEndDate(Long newEndDate) {
    this.endDate = newEndDate;
  }

  /**
   * Returns the list of assigned users to the task.
   *
   * @return the list containing the assigned users
   */
  public List<User> getAssignedUsers() {
    return assignedUsers;
  }

  /**
   * Sets the list of assigned users to the task.
   *
   * @param newAssignedUsers the list containing the assigned users
   */
  public void setAssignedUsers(List<User> newAssignedUsers) {
    this.assignedUsers = newAssignedUsers;
  }

  /**
   * Assign an user to the task.
   *
   * @param user the user to assign
   */
  public void addAssignedUser(User user) {
    if (!this.assignedUsers.contains(user)) {
      this.assignedUsers.add(user);
    }
  }

  /**
   * Remove an user from the task.
   *
   * @param user the user to remove
   */
  public void removeAssignedUser(User user) {
    this.assignedUsers.remove(user);
  }

  @Override
  public String toString() {
    String res = this.description;

    res += ", start: "
        + this.startDate
        + ", end: "
        + this.endDate;

    return res;
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
    Task other = (Task) object;
    return this.getId() == (other.getId())
        && this.getDescription().equals(other.getDescription())
        && this.getStartDate().equals(other.getStartDate())
        && this.getEndDate().equals(other.getEndDate());
  }

  @Override
  public int hashCode() {
    return id;
  }
}


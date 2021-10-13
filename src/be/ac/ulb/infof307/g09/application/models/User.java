package be.ac.ulb.infof307.g09.application.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The User Class.
 *
 * @author Piekarski Maciej
 * @author El Bakkali Soufian
 */
public class User implements Serializable {
  private static final String VALID_EMAIL = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
  private final int id;
  private String username;
  private String email;
  private String lastName;
  private String firstName;
  private String password;
  private List<Project> projectList;
  private final List<Project> authorProjects;

  /**
   * Creates a new user with the specified parameters.
   *
   * @param userId        the id of the user
   * @param userUsername  the username of the user
   * @param userEmail     the email of the user
   * @param userLastName  the last name of the user
   * @param userFirstName the first name of the user
   * @param userPassword  the password of the user
   */
  public User(int userId, String userUsername, String userEmail,
              String userLastName, String userFirstName, String userPassword) {
    this.id = userId;
    this.username = userUsername;
    this.email = userEmail;
    this.lastName = userLastName;
    this.firstName = userFirstName;
    this.password = userPassword;
    this.projectList = new ArrayList<>();
    this.authorProjects = new ArrayList<>();
  }

  /**
   * Set the username of the user.
   *
   * @param newUsername the username
   */
  public void setUsername(String newUsername) {
    this.username = newUsername;
  }

  /**
   * Set the email of the user.
   *
   * @param newEmail the email
   */
  public void setEmail(String newEmail) {
    this.email = newEmail;
  }

  /**
   * Set the last name of the user.
   *
   * @param newLastName the last name
   */
  public void setLastName(String newLastName) {
    this.lastName = newLastName;
  }

  /**
   * Set the first name of the user.
   *
   * @param newFirstName the first name
   */
  public void setFirstName(String newFirstName) {
    this.firstName = newFirstName;
  }

  /**
   * Set the password of the user.
   *
   * @param newPassword the password
   */
  public void setPassword(String newPassword) {
    this.password = newPassword;
  }

  /**
   * Returns id of User.
   *
   * @return id of User
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns the email.
   *
   * @return the email.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Returns the last name.
   *
   * @return the last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Returns the first name.
   *
   * @return the first name.
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Returns the password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Returns the project list.
   *
   * @return The project list
   */
  public List<Project> getProjectList() {
    return this.projectList;
  }

  /**
   * Gets a user project.
   *
   * @param projectToGet the project to get
   * @return the project if found, null otherwise
   */
  public Project getProject(Project projectToGet) {
    Project foundProject;
    for (Project parentProject : this.projectList) {
      if (parentProject.equals(projectToGet)) {
        return parentProject;
      }
      foundProject = findSubProject(parentProject, projectToGet);
      if (foundProject != null) {
        return foundProject;
      }
    }
    return null;
  }

  /**
   * Finds a subproject in project's tree structure.
   *
   * @param parentProject the parent project
   * @param projectToFind the project to find
   * @return the project if found, null otherwise
   */
  private Project findSubProject(Project parentProject, Project projectToFind) {
    for (Project subProject : parentProject.getSubProjects()) {
      if (subProject.equals(projectToFind)) {
        return subProject;
      }
      return findSubProject(subProject, projectToFind);
    }
    return null;
  }

  /**
   * Sets the project list with a given project list.
   *
   * @param newProjectList the given project list
   */
  public void setProjectList(List<Project> newProjectList) {
    this.projectList = newProjectList;
  }

  /**
   * Returns the projects in which the user is an author of.
   *
   * @return the projects in which the user is an author of.
   */
  public List<Project> getAuthorProjects() {
    return this.authorProjects;
  }

  /**
   * Adds a project to the project list.
   *
   * @param project the project to add
   */
  public void addProjectList(Project project) {
    this.projectList.add(project);
  }

  /**
   * Removes a project from the project list.
   *
   * @param project the project to remove
   */
  public void removeProjectList(Project project) {
    projectList.remove(project);
  }

  /**
   * Removes a project from the author project list.
   *
   * @param project the project to remove
   */
  public void removeAuthorProjects(Project project) {
    authorProjects.remove(project);
  }

  /**
   * Removes the sub project.
   *
   * @param subProject the sub project to remove
   */
  public void removeSubProject(Project subProject) {
    int index = this.projectList.indexOf(subProject.getParentProject());
    this.projectList.get(index).removeSubProject(subProject);
  }

  /**
   * Returns true if the user is the author of the given project.
   *
   * @param project the project
   * @return true if the user is the author of the given project.
   */
  public boolean isAuthor(Project project) {
    return this.equals(project.getAuthor());
  }

  /**
   * Updates the user.
   *
   * @param newUser user with the new parameters
   */
  public void updateUser(User newUser) {
    this.setUsername(newUser.getUsername());
    this.setEmail(newUser.getEmail());
    this.setLastName(newUser.getLastName());
    this.setFirstName(newUser.getFirstName());
    this.setPassword(newUser.getPassword());
  }

  /**
   * Check if email is invalid.
   *
   * @return true if email is invalid, false otherwise
   */
  public boolean isMailInvalid() {
    Pattern pattern = Pattern.compile(VALID_EMAIL, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(this.email);
    return !matcher.find();
  }

  /**
   * Check if the fields of the user are empty.
   *
   * @param passwordConfirm the password confirmation
   * @return true if all the fields are not empty, false otherwise
   */
  public boolean isUserInvalid(String passwordConfirm) {
    boolean res = true;
    if (!(passwordConfirm.isEmpty() || this.password.isEmpty())) {
      res = this.username.isEmpty() || this.email.isEmpty()
          || this.lastName.isEmpty() || this.firstName.isEmpty();
    }
    return res;
  }

  @Override
  public String toString() {
    return this.username;
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
    User other = (User) object;
    return this.getId() == other.getId()
        && this.getUsername().equals(other.getUsername())
        && this.getEmail().equals(other.getEmail())
        && this.getLastName().equals(other.getLastName())
        && this.getFirstName().equals(other.getFirstName())
        && this.getPassword().equals(other.getPassword());
  }

  @Override
  public int hashCode() {
    return id * username.hashCode() * email.hashCode();
  }
}


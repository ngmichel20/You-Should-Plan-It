package be.ac.ulb.infof307.g09.application.controllers.project;

import be.ac.ulb.infof307.g09.application.controllers.user.HomeController;
import be.ac.ulb.infof307.g09.application.models.Color;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Tag;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.DateTimeUtils;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.project.ListProjectsViewController;
import be.ac.ulb.infof307.g09.application.view.controllers.project.NewTagViewController;
import be.ac.ulb.infof307.g09.application.view.controllers.project.ProjectViewController;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.util.Pair;

/**
 * ProjectController handles the ProjectViewController logic, CRUD of a project.
 */
public class ProjectController implements
    ProjectViewController.Listener, NewTagViewController.Listener {
  private static final int NONEXISTENT = -1;
  private static final int FINAL_HOUR = 23;
  private static final String WRONG_INPUT_MESSAGE = "Veuillez corriger les champs";
  private static final String NAME_ALREADY_TAKEN = "Ce nom du projet est deja utilisé!\n";
  private static final String INVALID_PARENT_PROJECT = "Projet parent invalide!\n";
  private static final String END_DATE_INVALID = "Date de fin invalide!\n";
  private static final String END_HOUR_INVALID = "Heure de fin invalide!\n";
  private static final String COLOR_INVALID = "Couleur invalide!\n";
  private static final String DESCRIPTION_INVALID = "Description invalide!\n";
  private static final String NAME_INVALID = "Nom de projet invalide!\n";
  private static final String TAG_NULL_INVALID = "Étiquette null invalide!\n";
  private static final String TAG_ALREADY_TAKEN = "La étiquette est déjà prise!\n";
  private static final String ERROR_SUB_PROJECT_END_DATE =
      "Le sous-projet ne peut pas se terminer après le projet parent!\n";
  private ProjectViewController projectViewController;
  private Tab projectTab;
  private Project project;
  private List<Tag> listOfTagsToDisplay;
  private Tab newTagTab;
  private List<Tag> projectTags;
  private final Application application;

  /**
   * Controller of ProjectController which initializes the databases instances.
   *
   * @param application the application model
   */
  public ProjectController(Application application) {
    this.application = application;
  }

  /**
   * Get the Project view.
   *
   * @return the view to load.
   */
  public Parent getProjectView() {
    this.project = null;
    Parent view = null;
    try {
      FXMLLoader loader = new FXMLLoader(
          ListProjectsViewController.class.getResource("/views/project/Project.fxml"));
      loader.load();

      initProjectViewController(loader);
      view = loader.getRoot();
    } catch (IOException e) {

      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    }

    return view;
  }

  /**
   * Initialises the project view controller.
   * The tags and projects are loaded from the database.
   *
   * @param loader the fxml loader of the project
   * @throws DatabaseException if an error occur during the fetching of the tags or the projects
   */
  private void initProjectViewController(FXMLLoader loader) throws DatabaseException {
    this.projectViewController = loader.getController();
    this.projectViewController.setListener(this);
    this.projectViewController.bindParentProjectSpinner();

    this.listOfTagsToDisplay = application.getAllUserTags();
    this.projectViewController.loadTagsFromDatabase(this.listOfTagsToDisplay);
    this.projectViewController.fillColors();

    List<Project> listOfProjects = application.getUserProjects();

    this.projectViewController.loadProjectsFromDatabase(listOfProjects);
    this.projectViewController.disablePastDates();
  }

  /**
   * Set the tab which contains the view.
   *
   * @param projectTab the tab to set
   */
  public void setProjectTab(Tab projectTab) {
    this.projectTab = projectTab;
    HomeController.enableTabsOnCloseRequest(this.projectTab);
  }

  /**
   * Sets the project to be edited in the tab.
   *
   * @param projectToEdit project to set
   * @throws DatabaseException if an error occurs during the fetching of the tags
   */
  public void setProject(Project projectToEdit) throws DatabaseException {
    this.project = projectToEdit;
    this.projectViewController.setProjectTitleTextField(projectToEdit.getTitle());
    this.projectViewController.setProjectDescriptionTextField(projectToEdit.getDescription());

    setProjectEndDateValues(projectToEdit.getEndDate());

    this.projectTags = projectToEdit.getTags();
    this.projectViewController.selectProjectTags(this.projectTags);
    this.projectViewController.disableSubProjectSection(this.project);
    this.projectViewController.setColor(projectToEdit.getColor());
  }

  /**
   * Sets the project end date in the UI by transforming it to a readable format from the database.
   *
   * @param endDate the end date of the project
   */
  private void setProjectEndDateValues(long endDate) {
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endDate), zoneId);

    int day = dateTime.getDayOfMonth();
    int month = dateTime.getMonthValue();
    int year = dateTime.getYear();
    int hour = dateTime.getHour();

    String date = day + "/" + month + '/' + year;
    this.projectViewController.setDatePickerValue(date);
    this.projectViewController.setEndHourSpinnerValue(hour);
  }

  /**
   * Update a parent/sub project.
   *
   * @param projectTitle       the new title
   * @param projectDescription the new description
   * @param endDate            the new endDate
   * @param projectParent      the project parent if it exists, null otherwise
   * @param color              the project color
   * @param checkedTags        the new tags to add
   * @param uncheckedTags      the unchecked tags to remove
   */
  public void updateProject(String projectTitle, String projectDescription,
                            Long endDate, Project projectParent, Color color,
                            List<Tag> checkedTags, List<Tag> uncheckedTags) {
    try {
      this.application.updateProject(this.project, projectTitle,
          projectDescription, endDate, color);
      this.deleteUniqueUncheckedProjectTags(uncheckedTags);
      removeUnselectedTags(checkedTags);
      if (projectParent != null) {
        handleSubProjectTagsInsertion(projectParent, checkedTags);
      } else {
        updateSelectedTags(checkedTags);
      }
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
  }

  /**
   * Remove unselected tags from the database.
   *
   * @param checkedTags Tags checked by the user
   */
  private void removeUnselectedTags(List<Tag> checkedTags) {
    try {
      List<Tag> projectTags = this.project.getTags();
      for (int i = projectTags.size() - 1; i >= 0; i--) {
        if (!checkedTags.contains(projectTags.get(i))) {
          application.deleteTagFromProject(project, projectTags.get(i));
        }
      }
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
  }

  /**
   * Add the parent tags to the current project tags list.
   *
   * @param parentProject the project parent
   * @param checkedTags   Tags checked by the user
   */
  private void handleSubProjectTagsInsertion(Project parentProject, List<Tag> checkedTags) {
    List<Tag> parentTags = parentProject.getTags();
    for (Tag tag : parentTags) {
      if (!checkedTags.contains(tag)) {
        checkedTags.add(tag);
      }
    }
    updateSelectedTags(checkedTags);
  }

  /**
   * Update selected tags into the database.
   *
   * @param checkedTags Tags checked by the user
   */
  private void updateSelectedTags(List<Tag> checkedTags) {
    try {
      List<Tag> projectTags = this.project.getTags();
      for (Tag tag : checkedTags) {
        if (!projectTags.contains(tag)) {
          application.addTagToProject(this.project, tag);
        }
      }
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
  }

  /**
   * Add selected tags into the database.
   *
   * @param checkedTags Tags checked by the user
   */
  private void insertSelectedTags(List<Tag> checkedTags) {
    try {
      for (Tag tag : checkedTags) {
        application.addTagToProject(this.project, tag);
      }
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
  }


  /**
   * Get the current time, aka the start date of a project.
   *
   * @return the current time without minutes and seconds in milliseconds format
   */
  private Long getStartDate() {
    return Instant.now().atZone(DateTimeUtils.ZONE_ID)
        .truncatedTo(ChronoUnit.MINUTES).toInstant().toEpochMilli();
  }

  /**
   * Insert a sub project.
   *
   * @param projectTitle       the project title
   * @param projectDescription the project description
   * @param endDate            the project end date
   * @param projectParent      the project parent
   * @param color              the project color
   * @param checkedTag         the tags to add
   */
  public void insertSubProject(String projectTitle, String projectDescription, Long endDate,
                               Project projectParent, Color color, List<Tag> checkedTag) {
    try {
      long startDate = this.getStartDate();
      this.project = this.application.insertProject(
          new Project(NONEXISTENT, projectTitle,
              projectDescription,
              startDate,
              endDate,
              this.getInitialDuration(endDate, startDate),
              application.getUser(),
              projectParent, color), true
      );
      handleSubProjectTagsInsertion(projectParent, checkedTag);
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
  }

  /**
   * Returns the initial duration of a project.
   *
   * @param endDate   the end date
   * @param startDate the start date
   * @return the initial duration
   */
  private long getInitialDuration(long endDate, long startDate) {
    return endDate - startDate;
  }

  @Override
  public void confirmButtonAction(String projectTitle, String projectDescription,
                                  Long endDate, Integer endHour,
                                  Project parentProject, Color color, List<Tag> checkedTags,
                                  List<Tag> uncheckedTags) {
    boolean valid = this.isProjectInputValid(projectTitle, projectDescription,
        endDate, endHour, parentProject, color);
    if (valid) {
      Long endDateHour = getEndDateHour(endDate, endHour);

      if (this.project != null) {
        if (this.projectViewController.isSubProjectCheckboxChecked()) {
          this.updateProject(projectTitle, projectDescription,
              endDateHour, parentProject, color, checkedTags, uncheckedTags);
        } else {
          this.updateProject(projectTitle, projectDescription,
              endDateHour, null, color, checkedTags, uncheckedTags);
        }
      } else {
        if (this.projectViewController.isSubProjectCheckboxChecked()) {
          this.insertSubProject(projectTitle, projectDescription,
              endDateHour, parentProject, color, checkedTags);
        } else {
          this.insertProject(projectTitle, projectDescription, endDateHour, color, checkedTags);
        }
      }

      HomeController.closeTab(Integer.parseInt(this.projectTab.getId()));
      HomeController.close(this.projectTab);
    }
  }

  /**
   * Get the end date and the hour of a project.
   *
   * @param endDate the end date of the project
   * @param endHour the project end hour
   * @return the end date and the hour of a project
   */
  private long getEndDateHour(Long endDate, long endHour) {
    return (endDate + endHour * DateTimeUtils.SECONDS_IN_HOUR) * DateTimeUtils.MILLI_SECONDS;
  }

  /**
   * Insert a new project.
   *
   * @param projectTitle       the project title
   * @param projectDescription the project description
   * @param endDate            the project end date
   * @param color              the project color
   * @param checkedTag         the tags to add
   */
  public void insertProject(String projectTitle, String projectDescription,
                            Long endDate, Color color, List<Tag> checkedTag) {
    try {
      long startDate = this.getStartDate();

      this.project = this.application.insertProject(new Project(NONEXISTENT, projectTitle,
          projectDescription,
          startDate,
          endDate,
          this.getInitialDuration(endDate, startDate),
          application.getUser(),
          null, color), false);
      insertSelectedTags(checkedTag);
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
  }

  @Override
  public void createNewTag() {
    Tab tab = HomeController.addTab(getNewTagView(), "New Tag", true);
    setNewTagTab(tab);
    HomeController.disableLeftTabs(tab);
  }

  /**
   * Deletes the tags of the project that are unique.
   *
   * @param uncheckedTags the tags that are unchecked
   */
  private void deleteUniqueUncheckedProjectTags(List<Tag> uncheckedTags) {
    List<Pair<Integer, Integer>> uniqueTags;

    try {
      uniqueTags = application.getProjectsWithUniqueTags();
      for (Tag uncheckedTag : uncheckedTags) {
        for (Pair<Integer, Integer> projectTagPair : uniqueTags) {
          if (projectTagPair.getKey() == project.getId()
              && projectTagPair.getValue() == uncheckedTag.getId()) {
            application.deleteTag(projectTagPair.getValue());
          }
        }
      }
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
  }

  /**
   * Check if the project title is already taken by user when updating,
   * taking into account if the user kept the same title for the project.
   *
   * @param projectTitle title of project
   * @return true if it is taken, false otherwise
   */
  private boolean projectTitleInUseWhenUpdating(String projectTitle) {
    boolean userFound = false;
    List<Project> listOfProjects;

    listOfProjects = application.getUserProjects();
    int index = 0;

    while (!userFound && index < listOfProjects.size()) {
      Project currProject = listOfProjects.get(index);

      if (currProject.getId() != project.getId()
          && currProject.getTitle().equals(projectTitle)) {
        userFound = true;
      }
      index++;
    }

    return userFound;
  }

  /**
   * Check if the project title is already taken by user.
   *
   * @param projectTitle the title of the project
   * @return true if it is taken, false otherwise
   */
  private boolean projectTitleInUse(String projectTitle) {
    List<Project> listOfProjects = application.getAuthorProjects();
    Project projectFound = listOfProjects.stream()
        .filter(project -> projectTitle.equals(project.getTitle()))
        .findAny()
        .orElse(null);
    return projectFound != null;
  }

  /**
   * Check if the new sub project date is before the parent project date.
   *
   * @param errorMessage   the error message to display
   * @param parentProject  the project parent
   * @param currentEndDate the end date of the project
   * @return the error message
   */
  private String checkDateBeforeParent(String errorMessage,
                                       Project parentProject, Long currentEndDate) {
    Long parentEndDate = parentProject.getEndDate();

    String newErrorMessage = errorMessage;
    if (currentEndDate > parentEndDate) {
      newErrorMessage += ERROR_SUB_PROJECT_END_DATE;
    }
    return newErrorMessage;
  }

  /**
   * Check if the user's inputs are valid.
   *
   * @param projectTitle       the project title
   * @param projectDescription the project description
   * @param endDate            the project end date
   * @param endHour            the project end hour
   * @param parentProject      the project parent
   * @param color              the project color
   * @return true if the input from the user valid, false otherwise
   */
  public boolean isProjectInputValid(String projectTitle, String projectDescription,
                                     Long endDate, Integer endHour, Project parentProject,
                                     Color color) {
    String errorMessage = "";

    errorMessage = validateProjectTitle(projectTitle, errorMessage);
    errorMessage = validateProjectDescription(projectDescription, errorMessage);
    errorMessage = validateProjectDates(endDate, endHour, errorMessage);
    errorMessage = validateProjectTime(endHour, errorMessage);
    errorMessage = validateSubProjectDates(endDate, endHour, parentProject, errorMessage);
    errorMessage = validateProjectUniqueness(projectTitle, errorMessage);
    errorMessage = validateProjectColor(color, errorMessage);

    if (errorMessage.length() == 0) {
      return true;
    } else {
      HomeController.displayErrorAlert(WRONG_INPUT_MESSAGE, errorMessage);
      return false;
    }
  }

  /**
   * Validates the uniqueness of the project title.
   *
   * @param projectTitle the project title to validate
   * @param errorMessage the error message to whom the new error is appended
   * @return the error message
   */
  private String validateProjectUniqueness(String projectTitle, String errorMessage) {
    String newErrorMessage = errorMessage;
    if (this.project != null) {
      if (this.projectTitleInUseWhenUpdating(projectTitle)) {
        newErrorMessage += NAME_ALREADY_TAKEN;
      }
    } else {
      if (projectTitleInUse(projectTitle)) {
        newErrorMessage += NAME_ALREADY_TAKEN;
      }
    }
    return newErrorMessage;
  }


  /**
   * Validates the date of the subproject if the subproject checkbox is selected.
   *
   * @param endDate       the end date of the project
   * @param endHour       the end hour of the project
   * @param parentProject the parent project of the current project
   * @param errorMessage  the error message to whom the new error is appended
   * @return the error message
   */
  private String validateSubProjectDates(Long endDate, Integer endHour,
                                         Project parentProject, String errorMessage) {
    String newErrorMessage = errorMessage;
    if (this.projectViewController.isSubProjectCheckboxChecked()) {
      if (parentProject == null) {
        newErrorMessage += INVALID_PARENT_PROJECT;
      } else if (endHour != null) {
        if (endDate == null && 0 <= endHour && endHour <= FINAL_HOUR) {
          newErrorMessage += END_DATE_INVALID;
          newErrorMessage += END_HOUR_INVALID;
        } else if (endDate != null) {
          Long endDateHour = getEndDateHour(endDate, endHour);
          newErrorMessage = checkDateBeforeParent(errorMessage, parentProject, endDateHour);
        }
      }
    }
    return newErrorMessage;
  }

  /**
   * Validates the project end hour.
   *
   * @param endHour      the end hour of the project
   * @param errorMessage the error message to whom the new error is appended
   * @return the error message
   */
  private String validateProjectTime(Integer endHour, String errorMessage) {
    String newErrorMessage = errorMessage;
    if (endHour == null || !(endHour >= 0 && endHour <= FINAL_HOUR)) {
      newErrorMessage += END_HOUR_INVALID;
    }
    return newErrorMessage;
  }

  /**
   * Validates the project color.
   *
   * @param color        the color of the project
   * @param errorMessage the error message to whom the new error is appended
   * @return the error message
   */
  private String validateProjectColor(Color color, String errorMessage) {
    String newErrorMessage = errorMessage;
    if (color == null) {
      newErrorMessage += COLOR_INVALID;
    }
    return newErrorMessage;
  }

  /**
   * Validates the project end date and end hour.
   *
   * @param endDate      the end date of the project
   * @param endHour      the end hour of the project
   * @param errorMessage the error message to whom the new error is appended
   * @return the error message
   */
  private String validateProjectDates(Long endDate, Integer endHour, String errorMessage) {
    String newErrorMessage = errorMessage;
    if (endDate == null) {
      newErrorMessage += END_DATE_INVALID;
    } else {
      long endDateHour = getEndDateHour(endDate, endHour);
      long currentDateHour = DateTimeUtils.getCurrentTime();
      if (endDateHour < currentDateHour) {
        newErrorMessage += END_HOUR_INVALID;
      }
    }
    return newErrorMessage;
  }

  /**
   * Validates the project description.
   *
   * @param projectDescription the description of the project
   * @param errorMessage       the error message to whom the new error is appended
   * @return the error message
   */
  private String validateProjectDescription(String projectDescription, String errorMessage) {
    String newErrorMessage = errorMessage;
    if (projectDescription == null || projectDescription.length() == 0) {
      newErrorMessage += DESCRIPTION_INVALID;
    }
    return newErrorMessage;
  }

  /**
   * Validates the project title.
   *
   * @param projectTitle the title of the project
   * @param errorMessage the error message to whom the new error is appended
   * @return the error message
   */
  private String validateProjectTitle(String projectTitle, String errorMessage) {
    String newErrorMessage = errorMessage;
    if (projectTitle == null || projectTitle.length() == 0) {
      newErrorMessage += NAME_INVALID;
    }
    return newErrorMessage;
  }

  /**
   * Set the new tag tab and handle the closure of the new tag tab.
   *
   * @param newTag the tab of new tag
   */
  public void setNewTagTab(Tab newTag) {
    this.newTagTab = newTag;
    HomeController.enableTabsOnCloseRequest(this.newTagTab);
  }

  /**
   * Get the tags view.
   *
   * @return the tags view
   */
  public Parent getNewTagView() {
    Parent view = null;
    try {
      FXMLLoader loader = new FXMLLoader(
          ListProjectsViewController.class.getResource("/views/project/NewTag.fxml"));
      loader.load();

      NewTagViewController newTagViewController = loader.getController();
      newTagViewController.setListener(this);
      view = loader.getRoot();
    } catch (IOException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }
    return view;
  }

  @Override
  public void confirmNewTagButton(String description) {
    if (isTagInputValid(description)) {
      this.listOfTagsToDisplay.add(new Tag(NONEXISTENT, description));
      this.projectViewController.addNewTag(new Tag(NONEXISTENT, description));
      if (this.projectTags != null) {
        this.projectViewController.selectProjectTags(this.projectTags);
      }
      this.projectViewController.refreshTagComboBox();
      HomeController.closeTab(Integer.parseInt(this.newTagTab.getId()));
      HomeController.close(this.newTagTab);
    }
  }

  /**
   * Check if the input in new tag tab is valid.
   *
   * @param description description of the tag
   * @return true if the tag input is valid, false otherwise
   */
  private boolean isTagInputValid(String description) {
    StringBuilder errorMessage = new StringBuilder();

    if (description.equals("")) {
      errorMessage.append(TAG_NULL_INVALID);
    }

    for (Tag tag : this.listOfTagsToDisplay) {
      if (tag.getDescription().equals(description)) {
        errorMessage.append(TAG_ALREADY_TAKEN);
      }
    }

    if (errorMessage.length() == 0) {
      return true;
    } else {
      HomeController.displayErrorAlert(errorMessage.toString(),
          ErrorMessagesUtils.ERROR_MESSAGE);
      return false;
    }
  }

  @Override
  public List<Tag> getListOfTagsToInsert(ObservableList<Tag> listOfTagsSelected)
      throws DatabaseException, ConnectionFailedException {
    List<Tag> list = new ArrayList<>();

    for (Tag tagSelected : listOfTagsSelected) {
      if (application.getTag(tagSelected.getDescription()) != null) {
        list.add(application.getTag(tagSelected.getDescription()));
      } else if (tagSelected.getId() == NONEXISTENT) {
        list.add(application.createTag(tagSelected.getDescription()));
      } else {
        list.add(tagSelected);
      }
    }
    return list;
  }

  @Override
  public List<Tag> getListOfTagsToDelete(List<Tag> listOfTags) {
    List<Tag> unselectedTags = new ArrayList<>();

    for (Tag tag : listOfTags) {
      if (tag.getId() != NONEXISTENT) {
        unselectedTags.add(tag);
      }
    }
    return unselectedTags;
  }

  @Override
  public List<Tag> getUncheckedTags(ObservableList<Tag> allTags,
                                    ObservableList<Tag> checkedTags) {
    List<Tag> unselectedTags = new ArrayList<>();
    for (Tag tag : allTags) {
      if (!checkedTags.contains(tag)) {
        unselectedTags.add(tag);
      }
    }
    return unselectedTags;
  }
}

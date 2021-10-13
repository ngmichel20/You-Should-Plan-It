package be.ac.ulb.infof307.g09.application.controllers.project;

import be.ac.ulb.infof307.g09.application.controllers.collaborator.CollaboratorController;
import be.ac.ulb.infof307.g09.application.controllers.user.HomeController;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Tag;
import be.ac.ulb.infof307.g09.application.models.Task;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.DateTimeUtils;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.project.ListProjectsViewController;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

/**
 * ListProjectController class handles one action directly, deletion,
 * in the projects list UI, and delegates the addition and modification of a project
 * to ProjectController, as well as the handling of tasks of a project to TaskController.
 */
public class ListProjectsController implements ListProjectsViewController.Listener, Observer {
  private static final String ERROR_EXPORT_HEADER_TEXT = "Échec de l'exportation";
  private static final String ERROR_EXPORT_MESSAGE_TEXT = "Votre projet n'a pas pu être exporté !";
  private static final String ERROR_IMPORT_HEADER_TEXT = "Échec de l'importation";
  private static final String ERROR_IMPORT_MESSAGE_TEXT = "Votre projet n'a pas pu être importé !";
  private static final String ERROR_IMPORT_AUTHOR_MESSAGE_TEXT =
      "Vous ne pouvez pas importer un projet dont vous n'êtes pas l'auteur !";
  private static final String SUCCESS_EXPORT_HEADER_TEXT = "Exportation réussie";
  private static final String SUCCESS_EXPORT_MESSAGE_TEXT = "Votre projet a bien été exporté !";
  private static final String SUCCESS_IMPORT_HEADER_TEXT = "Importation réussie";
  private static final String SUCCESS_IMPORT_MESSAGE_TEXT = "Votre projet a bien été importé !";
  private static final String TAB_TASK_TITLE = "Tâches";
  private static final String TAB_COLLABORATORS_TITLE = "Ajout d'un collaborateur";
  private static final String TAB_NEW_PROJECT_TITLE = "Nouveau projet";
  private static final String ADD_COLLABORATOR = "Ajouter collaborateur";
  private static final String QUIT_COLLABORATION = "Supprimer collaboration";

  private ListProjectsViewController listProjectsViewController;
  private final ProjectController projectController;
  private final Application application;
  private final Stage stage;

  /**
   * ListProjectController constructor.
   *
   * @param stage       the stage to set
   * @param application the application model
   */
  public ListProjectsController(Stage stage, Application application) {
    this.stage = stage;
    this.application = application;
    this.application.addObserver(this);
    this.projectController = new ProjectController(this.application);
  }

  /**
   * Get the ListProjects view.
   *
   * @return the projects list view
   */
  public Parent getView() {
    Parent view = null;
    try {
      FXMLLoader loader = new FXMLLoader(
          ListProjectsViewController.class.getResource("/views/project/ListProjects.fxml"));
      loader.load();

      listProjectsViewController = loader.getController();
      listProjectsViewController.setListener(this);
      listProjectsViewController.initialiseTree(application.getUserParentProjects());

      view = loader.getRoot();
    } catch (IOException e) {

      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }
    return view;
  }

  @Override
  public void deleteProject(Project projectToRemove) {
    try {
      this.application.deleteProject(projectToRemove);
      resetProjectDataPanel();
      listProjectsViewController.initialiseTree(this.application.getUserParentProjects());
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    } catch (ConnectionFailedException e) {
      HomeController.displayErrorAlert(e.getMessage(), ErrorMessagesUtils.CONNECTION_MSG_ERROR);
    }
  }


  /**
   * Resets the data of the project in the right panel.
   */
  private void resetProjectDataPanel() {
    this.listProjectsViewController.setProjectTagsText("");
    this.listProjectsViewController.setProjectTagsDescription("");
    this.listProjectsViewController.setProjectEndTime("");
    this.listProjectsViewController.setProjectAuthorText("");
    this.listProjectsViewController.setProjectCollaboratorsText("");
  }

  @Override
  public void modifyProject(Project projectToEdit) {
    try {
      openProjectTab(projectToEdit.getTitle());
      this.projectController.setProject(projectToEdit);
      resetProjectDataPanel();
    } catch (DatabaseException e) {
      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_MESSAGE);
    }
  }

  @Override
  public void addProject() {
    openProjectTab(TAB_NEW_PROJECT_TITLE);
  }

  @Override
  public void openProject(Project project) {
    setDescription(project);
    setProjectTags(project);
    setProjectEndTime(project.getEndDate());
    setAuthorText(project.getAuthor());
    setCollaboratorsText(project);
    setCollaboratorsButtonText(project);
  }

  /**
   * Sets the project description for the UI representation.
   *
   * @param projectToOpen The project
   */
  private void setDescription(Project projectToOpen) {
    this.listProjectsViewController.setProjectTagsDescription(projectToOpen.getDescription());
  }

  /**
   * Sets the project tags that are concatenated for the UI representation.
   *
   * @param project project from which the tags are taken
   */
  private void setProjectTags(Project project) {
    this.listProjectsViewController.setProjectTagsText(displayTags(project.getTags()));
  }

  /**
   * Sets the project author for the UI representation.
   *
   * @param author the user that authored the project
   */
  private void setAuthorText(User author) {
    this.listProjectsViewController.setProjectAuthorText(author.getUsername());
  }

  /**
   * Sets the project collaborators that are concatenated for the UI representation.
   *
   * @param project The project
   */
  private void setCollaboratorsText(Project project) {
    this.listProjectsViewController.setProjectCollaboratorsText(
        displayCollaborators(project.getCollaborators()));
  }

  /**
   * Sets the text of the collaboration button according to the current user status.
   *
   * @param project the project
   */
  private void setCollaboratorsButtonText(Project project) {
    if (!project.isSubProject()) {
      this.listProjectsViewController.enableCollaborationButton();
      if (application.getUser().isAuthor(project)) {
        this.listProjectsViewController.setProjectCollaboratorsButtonText(ADD_COLLABORATOR);
      } else {
        this.listProjectsViewController.setProjectCollaboratorsButtonText(QUIT_COLLABORATION);
      }
    } else {
      this.listProjectsViewController.disableCollaborationButton();
    }
  }

  /**
   * Formats the list of collaborators in String.
   *
   * @param collaborators list of User
   * @return formatted string containing the collaborators
   */
  private String displayCollaborators(List<User> collaborators) {
    StringBuilder collaboratorsString = new StringBuilder();
    for (int i = 0; i < collaborators.size(); i++) {
      collaboratorsString.append(collaborators.get(i).getUsername());
      if (i != collaborators.size() - 1) {
        collaboratorsString.append(" | ");
      }
    }
    return collaboratorsString.toString();
  }

  /**
   * Formats the list of tags in a String.
   *
   * @param projectTags list of tags
   * @return formatted string containing tags
   */
  private String displayTags(List<Tag> projectTags) {
    StringBuilder text = new StringBuilder();
    if (projectTags != null && !projectTags.isEmpty()) {
      text.append(projectTags.get(0));
      for (Tag tag : projectTags.subList(1, projectTags.size())) {
        text.append(" | ").append(tag);
      }
    }
    return text.toString();
  }

  /**
   * Sets the project end time. The project end time is compared with the current time to
   * check whether the project has ended.
   *
   * @param projectEndDate the project end time
   */
  private void setProjectEndTime(long projectEndDate) {
    long currentTime = DateTimeUtils.getCurrentTime();
    String date = DateTimeUtils.formatDateToString(projectEndDate);

    if (projectEndDate < currentTime) {
      this.listProjectsViewController.setProjectFinished();
    } else {
      this.listProjectsViewController.setProjectEndTime(date);
    }
  }

  @Override
  public List<Project> getSubProjects(Project project) {
    return application.getSubProjects(project);
  }

  @Override
  public void importProject(File archive) {
    try {
      Path tempDir = Files.createTempDirectory("tempDir");
      File destination = tempDir.toFile();
      Archiver archiver = ArchiverFactory.createArchiver(
          ArchiveFormat.TAR, CompressionType.GZIP);
      archiver.extract(archive, destination);

      importProjectFromFile(destination);

    } catch (IOException | ClassNotFoundException
        | DatabaseException | ConnectionFailedException e) {
      HomeController.displayErrorAlert(ERROR_IMPORT_HEADER_TEXT,
          ERROR_IMPORT_MESSAGE_TEXT);
    }
  }

  /**
   * Imports a project from a file source.
   *
   * @param source the file containing the project to import
   * @throws IOException               if the source file is not found
   * @throws ClassNotFoundException    if the DRIVER class is not found
   *                                   It implies that the connection fails
   * @throws DatabaseException         if a problem occurs during the access to the database
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private void importProjectFromFile(File source)
      throws IOException, ClassNotFoundException, DatabaseException, ConnectionFailedException {

    if (Objects.requireNonNull(source.listFiles()).length > 0) {
      File file = Objects.requireNonNull(source.listFiles())[0];

      try (FileInputStream fileIn = new FileInputStream(file.getPath());
           ObjectInputStream in = new ObjectInputStream(fileIn)) {

        Project project = (Project) in.readObject();
        if (project.getAuthor().equals(application.getUser())) {
          if (canImportProject(project)) {
            insertImportedProject(project);
            HomeController.displayInformationAlert(SUCCESS_IMPORT_HEADER_TEXT,
                SUCCESS_IMPORT_MESSAGE_TEXT);
          } else {
            HomeController.displayErrorAlert(ERROR_IMPORT_HEADER_TEXT,
                "Un projet existe déjà dans votre liste de projets");
          }
        } else {
          HomeController.displayErrorAlert(ERROR_IMPORT_HEADER_TEXT,
              ERROR_IMPORT_AUTHOR_MESSAGE_TEXT);
        }
      }
      source.deleteOnExit();
    }
  }

  /**
   * Inserts the imported project in the user's projects list.
   *
   * @param project the imported project
   * @throws DatabaseException         if a problem occurs while deleting
   *                                   the project in projectDatabase
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private void insertImportedProject(Project project)
      throws DatabaseException, ConnectionFailedException {
    Project insertedProject = application.insertProject(
        project, project.getParentProject() != null);

    for (Task task : project.getTasks()) {
      application.addTaskToProject(insertedProject, task.getDescription(),
          task.getStartDate(), task.getEndDate());
    }

    for (Tag tag : project.getTags()) {
      Tag tagFromDatabase = application.getTagFromDatabase(tag.getDescription());
      if (tagFromDatabase == null) {
        Tag createdTag = application.createTag(tag.getDescription());
        application.addTagToProject(insertedProject, createdTag);
      } else {
        application.addTagToProject(insertedProject, tagFromDatabase);
      }
    }
    for (Project subProject : project.getSubProjects()) {
      subProject.setParentProject(insertedProject);
      insertImportedProject(subProject);
    }
  }

  /**
   * Checks if the project already exists in user projects list.
   *
   * @param projectToCheck the project to search
   * @return true if the project exists, false otherwise
   */
  private boolean isProjectExist(Project projectToCheck) {
    for (Project project : application.getUserProjects()) {
      if (project.getTitle().equals(projectToCheck.getTitle())
          && project.getAuthor().equals(projectToCheck.getAuthor())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Methods that verifies if an user can import the project.
   * It does a recursive call to check if a sub project is already
   * in the list of projects.
   *
   * @param project the project to import
   * @return true if he can import, false otherwise
   */
  private boolean canImportProject(Project project) {
    boolean temp = !isProjectExist(project);
    for (Project subproject : project.getSubProjects()) {
      temp = temp && canImportProject(subproject);
    }
    return temp;
  }

  @Override
  public void exportProject(File path, Project project) {
    String filename = project.getId() + "_" + project.getTitle();
    String extension = ".tmp";
    try {
      File temp = File.createTempFile(filename, extension);
      try (FileOutputStream fileOut = new FileOutputStream(temp);
           ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
        out.writeObject(project);
      }
      File destination = new File(path.getPath());
      File source = new File(temp.getPath());
      Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
      archiver.create(filename, destination, source);
      source.deleteOnExit();
      HomeController.displayInformationAlert(SUCCESS_EXPORT_HEADER_TEXT,
          SUCCESS_EXPORT_MESSAGE_TEXT);
    } catch (IOException i) {
      HomeController.displayErrorAlert(ERROR_EXPORT_HEADER_TEXT,
          ERROR_EXPORT_MESSAGE_TEXT);
    }
  }

  @Override
  public Stage getStage() {
    return this.stage;
  }

  @Override
  public void displayTasks(Project project) {
    if (project != null) {
      ListTasksController listTasksController = new ListTasksController(project, this.application);
      Tab tab = HomeController.addTab(listTasksController.getView(), TAB_TASK_TITLE, true);
      listTasksController.setTab(tab);
      HomeController.disableLeftTabs(tab);
    }
  }

  @Override
  public void handleCollaborations(Project project) {
    if (application.getUser().isAuthor(project)) {
      CollaboratorController collaboratorController =
          new CollaboratorController(project, application);
      Tab tab = HomeController.addTab(
          collaboratorController.getView(), TAB_COLLABORATORS_TITLE, true);
      collaboratorController.setCollaboratorTab(tab);
      HomeController.disableLeftTabs(tab);
    } else {
      if (this.listProjectsViewController.displayRemoveCollaboration()) {
        try {
          application.removeCollaborationFromProject(project);
          resetProjectDataPanel();
        } catch (DatabaseException e) {
          HomeController.displayErrorAlert(e.getMessage(),
              ErrorMessagesUtils.ERROR_MESSAGE);
        } catch (ConnectionFailedException e) {
          HomeController.displayErrorAlert(e.getMessage(),
              ErrorMessagesUtils.CONNECTION_MSG_ERROR);
        }
      }
    }
  }

  /**
   * Load the Project view.
   *
   * @param tabTitle the title of the tab
   */
  private void openProjectTab(String tabTitle) {
    Tab tab = HomeController.addTab(this.projectController.getProjectView(), tabTitle, true);
    this.projectController.setProjectTab(tab);
    HomeController.disableLeftTabs(tab);
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof Application && arg instanceof Project) {
      listProjectsViewController.initialiseTree(((Application) o).getUserParentProjects());
    }
  }
}

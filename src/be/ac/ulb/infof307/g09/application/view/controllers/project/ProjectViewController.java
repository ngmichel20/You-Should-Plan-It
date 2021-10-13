package be.ac.ulb.infof307.g09.application.view.controllers.project;

import be.ac.ulb.infof307.g09.application.models.Color;
import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Tag;
import be.ac.ulb.infof307.g09.application.utilities.DateTimeUtils;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;

/**
 * Handles the creation/edition of a new project.
 *
 * @author Maciej, Alexis
 */
public class ProjectViewController {

  private static final int DEFAULT_HOUR_VALUE = 12;

  private Listener listener;
  @FXML
  private TextField nameTextField;
  @FXML
  private TextField descriptionTextField;
  @FXML
  private DatePicker endDatePicker;
  @FXML
  private Spinner<Integer> endHourSpinner;
  @FXML
  private CheckBox subProjectCheckbox;
  @FXML
  private ComboBox<Project> parentProjectComboBox;
  @FXML
  private Label parentProjectLabel;
  @FXML
  private CheckComboBox<Tag> tagCheckComboBox;
  @FXML
  private ComboBox<Color> colorCombobox;


  /**
   * Initializes the format of the date picker once all the fxml are loaded.
   */
  @FXML
  private void initialize() {
    endDatePicker.setConverter(new DateStringConverter());
  }

  /**
   * Fill the combo box with the selection of colors.
   */
  public void fillColors() {
    for (Color color : Color.values()) {
      colorCombobox.getItems().add(color);
    }

    colorCombobox.setCellFactory(listView -> new ListCell<Color>() {
      @Override
      public void updateItem(Color item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setText(null);
        } else {
          setText(item.toString());
          setBackground(new Background(new BackgroundFill(
              javafx.scene.paint.Color.web(item.getHexadecimal()), null, null)));
        }
      }
    });
  }

  /**
   * Sets the current selected color in the combo box.
   *
   * @param color the color to set
   */
  public void setColor(Color color) {
    colorCombobox.getSelectionModel().select(color);
  }

  /**
   * Load all the projects from the database to the UI.
   *
   * @param listOfProjects the list of projects to to add in the parentProjectComboBox
   */
  public void loadProjectsFromDatabase(List<Project> listOfProjects) {
    parentProjectComboBox.getItems().addAll(listOfProjects);
  }

  /**
   * Load all the tags from the database to the UI.
   *
   * @param listOfTags the list of tags to add in the tagCheckComboBox
   */
  public void loadTagsFromDatabase(List<Tag> listOfTags) {
    tagCheckComboBox.getItems().addAll(listOfTags);
  }


  /**
   * Adds a new tag into the combo box of tags.
   *
   * @param tag the tag to add
   */
  public void addNewTag(Tag tag) {
    tagCheckComboBox.getItems().add(tag);
  }

  /**
   * Selecting the sub project checkbox will enable the sub project spinner and label.
   */
  public void bindParentProjectSpinner() {
    BooleanBinding subProjectBinding = subProjectCheckbox.selectedProperty().not();
    parentProjectComboBox.disableProperty().bind(subProjectBinding);
    parentProjectLabel.disableProperty().bind(subProjectBinding);
  }

  /**
   * Called when the user clicks on the clear button.
   * Clears all the fields.
   */
  @FXML
  void clearButton() {
    nameTextField.clear();
    descriptionTextField.clear();
    endDatePicker.getEditor().clear();
    setEndHourSpinnerValue(DEFAULT_HOUR_VALUE);
    subProjectCheckbox.setSelected(false);
    parentProjectComboBox.getSelectionModel().clearSelection();
    tagCheckComboBox.getCheckModel().clearChecks();
  }

  /**
   * Refreshes the project comboBox.
   */
  public void refreshTagComboBox() {
    for (Tag checkedItem : tagCheckComboBox.getCheckModel().getCheckedItems()) {
      tagCheckComboBox.getCheckModel().check(checkedItem);
    }
  }

  /**
   * Sets the nameTextField with the project title.
   *
   * @param projectTitle the title of the project to set
   */
  public void setProjectTitleTextField(String projectTitle) {
    this.nameTextField.setText(projectTitle);
  }

  /**
   * Sets the descriptionTextField with the project description.
   *
   * @param projectDescription the project description to set
   */
  public void setProjectDescriptionTextField(String projectDescription) {
    this.descriptionTextField.setText(projectDescription);
  }

  /**
   * Called when the user clicks on the confirm button.
   * A new project is created if the input is valid and the current tab is closed.
   *
   * @throws DatabaseException         when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  @FXML
  private void confirmProjectButton() throws DatabaseException, ConnectionFailedException {
    String projectTitle = nameTextField.getText();
    String projectDescription = descriptionTextField.getText();
    Color color = colorCombobox.getValue();
    Long endDate = getEndDateInEpochSecond();

    Integer endHour = endHourSpinner.getValue();
    Project parentProject = parentProjectComboBox.getValue();
    ObservableList<Tag> checkedTags = tagCheckComboBox.getCheckModel().getCheckedItems();
    List<Tag> correctCheckedTags = this.listener.getListOfTagsToInsert(checkedTags);
    ObservableList<Tag> allTags = tagCheckComboBox.getItems();

    List<Tag> uncheckedTags = this.listener.getListOfTagsToDelete(
        this.listener.getUncheckedTags(allTags, checkedTags));

    this.listener.confirmButtonAction(projectTitle, projectDescription,
        endDate, endHour, parentProject, color, correctCheckedTags, uncheckedTags);
  }

  /**
   * Returns the project end date in epoch seconds.
   *
   * @return the end date value in epoch seconds
   */
  private Long getEndDateInEpochSecond() {
    long endDate = 0;

    if (endDatePicker.getValue() != null) {
      LocalDate date = endDatePicker.getValue();
      ZoneId zoneId = ZoneId.systemDefault();
      endDate = date.atStartOfDay(zoneId).toEpochSecond();
    }

    return endDate;
  }

  /**
   * Checks if sub project was selected.
   *
   * @return true if sub project checked, false otherwise
   */
  public boolean isSubProjectCheckboxChecked() {
    return subProjectCheckbox.isSelected();
  }

  /**
   * Called when the user click on the + button to add a new tag.
   * Open a tab for the creation of a new tag.
   */
  @FXML
  private void createNewTag() {
    this.listener.createNewTag();
  }

  /**
   * Disable past dates from the end date picker.
   */
  public void disablePastDates() {
    this.endDatePicker.setDayCellFactory(picker -> new DateCell() {
      public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        LocalDate today = LocalDate.now();

        setDisable(empty || date.compareTo(today) < 0);
      }
    });
  }

  /**
   * Set the project hour in the end hour spinner.
   *
   * @param endHour the project end Hour
   */
  public void setEndHourSpinnerValue(int endHour) {
    this.endHourSpinner.getValueFactory().setValue(endHour);
  }

  /**
   * Set the project date in the date picker.
   *
   * @param projectDateValue the project date
   */
  public void setDatePickerValue(String projectDateValue) {
    this.endDatePicker.setValue(
        LocalDate.parse(projectDateValue, DateTimeUtils.DATE_FORMATTER));
  }

  /**
   * Disable the sub project checkbox and the parent spinner.
   *
   * @param project the project to disable
   */
  public void disableSubProjectSection(Project project) {
    if (project.getParentProject() != null) {
      this.subProjectCheckbox.setSelected(true);
    }
    this.subProjectCheckbox.setDisable(true);

    this.parentProjectComboBox.disableProperty().unbind();
    this.parentProjectLabel.disableProperty().unbind();
    this.parentProjectComboBox.getSelectionModel().select(project.getParentProject());
    this.parentProjectComboBox.setDisable(true);
  }

  /**
   * Select project tags in the UI.
   *
   * @param projectTags the project tags to check
   */
  public void selectProjectTags(List<Tag> projectTags) {
    IndexedCheckModel<Tag> checkModel = tagCheckComboBox.getCheckModel();
    for (Tag tag : projectTags) {
      checkModel.check(tag);
    }
  }

  /**
   * Sets the listener.
   *
   * @param listener the listener to set
   */
  public void setListener(Listener listener) {
    this.listener = listener;
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * Handle the action when the confirm button is pressed in UI.
     *
     * @param projectTitle       the project title
     * @param projectDescription the project description
     * @param endDate            the project end Date
     * @param endHour            the project end hour
     * @param parentProject      the project parent
     * @param color              the project color
     * @param checkedTags        the tags to add
     * @param uncheckedTags      the unchecked tags
     */
    void confirmButtonAction(String projectTitle, String projectDescription,
                             Long endDate, Integer endHour,
                             Project parentProject, Color color, List<Tag> checkedTags,
                             List<Tag> uncheckedTags);

    /**
     * Open a tab for the creation of a new tag.
     */
    void createNewTag();

    /**
     * Get the list of tags to insert.
     *
     * @param listOfTagsSelected the list of the tags selected
     * @return the list that has been added with the tags.
     * @throws DatabaseException         throws when something wrong happens
     *                                   during a database transaction
     * @throws ConnectionFailedException If the connection to the database fails
     */
    List<Tag> getListOfTagsToInsert(ObservableList<Tag> listOfTagsSelected)
        throws DatabaseException, ConnectionFailedException;

    /**
     * Get the list of tags that were unchecked by the user.
     *
     * @param listOfTagsUnselected the list of the unselected tags
     * @return the list that has been added with the tags.
     */
    List<Tag> getListOfTagsToDelete(List<Tag> listOfTagsUnselected);

    /**
     * Get the list of unchecked tags.
     *
     * @param allTags     the list of the tags
     * @param checkedTags the list of checked tags
     * @return the list of unchecked tags
     */
    List<Tag> getUncheckedTags(ObservableList<Tag> allTags, ObservableList<Tag> checkedTags);
  }


}

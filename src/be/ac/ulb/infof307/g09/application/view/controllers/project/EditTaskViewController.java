package be.ac.ulb.infof307.g09.application.view.controllers.project;

import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * Controller of createTask view.
 */
public class EditTaskViewController extends VBox {

  private static final String END_DATE_MODE = "Date de fin";
  private static final String DURATION_MODE = "Durée (jours)";
  private static final String HOUR_PROMPT = "Heure";
  private static final String HOUR_UNIT_SYMBOL = "h";
  private static final String DAY_UNIT_SYMBOL = "j";
  private static final int MAX_HOUR_VALUE = 23;
  private static final int MIN_HOUR_VALUE = 0;
  private static final int MIN_DURATION_VALUE = 1;
  private static final int MAX_DURATION_VALUE = 10000;
  private static final int AT_NOON = 12;
  private static final int INCREMENT_STEP_BY_STEP = 1;

  private Project project;

  @FXML
  private TextField descriptionTextField;
  @FXML
  private Label errorLabel;
  @FXML
  private DatePicker startDatePicker;
  @FXML
  private DatePicker endDatePicker;
  @FXML
  private Spinner<Integer> startHourSpinner;
  @FXML
  private Spinner<Integer> endHourSpinner;
  @FXML
  private ComboBox<String> selectModeComboBox;
  @FXML
  private Spinner<Integer> durationSpinner;
  @FXML
  private Listener listener;

  /**
   * Adds the hours unit to the hour spinners.
   * The format of the date pickers is set to european standard.
   */
  @FXML
  private void initialize() {
    startHourSpinner.setValueFactory(getSpinnerValueFactory(
        HOUR_UNIT_SYMBOL, MAX_HOUR_VALUE, MIN_HOUR_VALUE));
    endHourSpinner.setValueFactory(getSpinnerValueFactory(
        HOUR_UNIT_SYMBOL, MAX_HOUR_VALUE, MIN_HOUR_VALUE));
    durationSpinner.setValueFactory(getSpinnerValueFactory(
        DAY_UNIT_SYMBOL, MAX_DURATION_VALUE, MIN_DURATION_VALUE));

    startDatePicker.setConverter(new DateStringConverter());
    endDatePicker.setConverter(new DateStringConverter());
  }

  /**
   * Sets the project in order to retrieve its date.
   *
   * @param project the project that will be set
   */
  public void setProject(Project project) {
    this.project = project;
  }

  /**
   * Sets a prompt text for the hours spinners.
   */
  public void setPromptTextHours() {
    startHourSpinner.getEditor().setPromptText(HOUR_PROMPT);
    endHourSpinner.getEditor().setPromptText(HOUR_PROMPT);
  }

  /**
   * Sets the description field value.
   *
   * @param description the description to set
   */
  public void setDescriptionTextField(String description) {
    this.descriptionTextField.setText(description);
  }

  /**
   * Sets the default values for the date pickers.
   *
   * @param startDate the start date in epoch format
   * @param endDate   the end date in epoch format
   */
  public void setEndDatePickerDefaultValues(long startDate, long endDate) {
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startDate), zoneId);
    LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endDate), zoneId);

    this.startDatePicker.setValue(startDateTime.toLocalDate());
    this.startHourSpinner.getValueFactory().setValue(startDateTime.getHour());

    this.endDatePicker.setValue(endDateTime.toLocalDate());
    this.endHourSpinner.getValueFactory().setValue(endDateTime.getHour());
  }

  /**
   * When the end date mode is selected, hide the duration spinner.
   * Otherwise, show it and disable the end date picker.
   */
  public void handleModeSelection() {
    selectModeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
      if (newValue.equals(END_DATE_MODE)) {
        endDatePicker.setDisable(false);
        durationSpinner.setVisible(false);
      } else if (newValue.equals(DURATION_MODE)) {
        durationSpinner.setVisible(true);
        endDatePicker.setDisable(true);
        calculateEndDateValue();
      }
    });
    selectModeComboBox.getSelectionModel().select(0);
  }

  /**
   * Handle the duration time of the project. The duration is added to the start date of the project
   * and displayed in the end date field.
   */
  @FXML
  private void calculateEndDateValue() {
    if (startDatePicker.getValue() != null) {
      LocalDate startDate = startDatePicker.getValue();
      LocalDate endDate = startDate.plusDays(durationSpinner.getValue());
      endDatePicker.setValue(endDate);
    }
  }

  /**
   * Calculates the end date automatically when the duration spinner value is changed.
   */
  public void handleDurationSpinner() {
    durationSpinner.getEditor().textProperty().addListener(
        (observable, oldValue, newValue) -> calculateEndDateValue());
  }

  /**
   * Disable invalid dates from the end date picker.
   */
  public void disableInvalidDates() {
    long startDate = this.project.getStartDate();
    long endDate = this.project.getEndDate();
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startDate), zoneId);
    LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endDate), zoneId);
    LocalDate startDateProject = startDateTime.toLocalDate();
    LocalDate endDateProject = endDateTime.toLocalDate();

    this.endDatePicker.setDayCellFactory(picker -> new DateCell() {
      public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        setDisable(empty || date.compareTo(endDateProject) > 0
            || date.compareTo(startDateProject) < 0);
      }
    });

    this.startDatePicker.setDayCellFactory(picker -> new DateCell() {
      public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        setDisable(empty || date.compareTo(endDateProject) > 0
            || date.compareTo(startDateProject) < 0);
      }
    });
  }

  /**
   * Returns the hour spinner value factory. The value factory
   * adds the unit to the value displayed in the spinner.
   *
   * @param unit     the unit ("h","j") to display at the end of the Spinner
   * @param maxValue the max value of the spinner
   * @param minValue the min value of the spinner
   * @return a value factory that handle the hours unit
   */
  private SpinnerValueFactory.IntegerSpinnerValueFactory getSpinnerValueFactory(
      String unit, int maxValue, final int minValue) {

    SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory
        = new SpinnerValueFactory.IntegerSpinnerValueFactory(
            minValue, maxValue, AT_NOON, INCREMENT_STEP_BY_STEP);

    valueFactory.setConverter(new StringConverter<Integer>() {
      @Override
      public String toString(Integer value) {
        return value.toString() + " " + unit;
      }

      @Override
      public Integer fromString(String string) {
        String valueWithoutUnits = string.replaceAll(" " + unit, "").trim();
        if (valueWithoutUnits.isEmpty()) {
          return 0;
        } else {
          return Integer.valueOf(valueWithoutUnits);
        }
      }

    });

    return valueFactory;
  }

  /**
   * Fill mode selection in comboBox.
   */
  public void fillSelectModeComboBox() {
    List<String> list = new ArrayList<>();
    list.add(END_DATE_MODE);
    list.add(DURATION_MODE);
    this.selectModeComboBox.getItems().addAll(list);
  }

  /**
   * Handle the action of the validate button.
   *
   * @throws DatabaseException when something wrong happens during a database transaction
   * @throws ConnectionFailedException If the connection to the database fails
   */
  @FXML
  public void validateButton() throws DatabaseException, ConnectionFailedException {
    Integer endHour = this.endHourSpinner.getValue();
    Integer startHour = this.startHourSpinner.getValue();
    Long startDateHour = null;
    Long endDateHour = null;
    ZoneId zoneId = ZoneId.systemDefault();

    if (startDatePicker.getValue() != null) {
      LocalDate date = startDatePicker.getValue();
      startDateHour = date.atStartOfDay(zoneId).truncatedTo(ChronoUnit.DAYS)
          .withHour(startHour).toInstant().toEpochMilli();

    }
    if (endDatePicker.getValue() != null) {
      LocalDate date = endDatePicker.getValue();
      endDateHour = date.atStartOfDay(zoneId).truncatedTo(ChronoUnit.DAYS)
          .withHour(endHour).toInstant().toEpochMilli();
    }
    this.listener.validate(
        this.descriptionTextField.getText(), startDateHour,
        endDateHour);
  }

  /**
   * Fills the description field of the task.
   *
   * @param taskDescription the description task to fill
   */
  public void fillDescriptionField(String taskDescription) {
    this.descriptionTextField.setText(taskDescription);
  }

  /**
   * Shows the error message.
   *
   * @param errorMessage the error message to show
   */
  public void showErrorMessage(String errorMessage) {
    this.errorLabel.setText(errorMessage);
  }

  /**
   * Set the listener.
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
     * Validates the task and saves it into the database.
     *
     * @param description  the description of the task
     * @param startDate    the startDate of the task in Epoch format
     * @param endDate      the endDate of the task in Epoch format
     * @throws DatabaseException throws a database exceptions when the validation fails.
     * @throws ConnectionFailedException If the connection to the database fails
     */
    void validate(String description, Long startDate,
                  Long endDate) throws DatabaseException, ConnectionFailedException;
  }
}

package be.ac.ulb.infof307.g09.application.view.controllers.project;

import be.ac.ulb.infof307.g09.application.utilities.DateTimeUtils;
import java.time.LocalDate;
import javafx.util.StringConverter;

/**
 * Handles the conversion of the format of the local date.
 */
public class DateStringConverter extends StringConverter<LocalDate> {

  @Override
  public String toString(LocalDate date) {
    return (date != null) ? DateTimeUtils.DATE_FORMATTER.format(date) : "";
  }

  @Override
  public LocalDate fromString(String string) {
    return (string != null && !string.isEmpty())
        ? LocalDate.parse(string, DateTimeUtils.DATE_FORMATTER) : null;
  }
}

package be.ac.ulb.infof307.g09.application.utilities;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for date and time.
 */
public final class DateTimeUtils {

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
  private static final String TIME_FORMAT = "%02dh%02dmin";

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");
  public static final ZoneId ZONE_ID = ZoneId.systemDefault();
  public static final int MILLI_SECONDS = 1000;
  public static final int SECONDS_IN_HOUR = 3600;

  private DateTimeUtils(){}

  /**
   * Formats a date of type long to a String representation.
   *
   * @param date date to format
   * @return String representation of the date
   */
  public static String formatDateToString(long date) {
    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZONE_ID);
    return dateTime.format(DATE_TIME_FORMATTER);
  }

  /**
   * Returns the current time in milliseconds.
   *
   * @return timestamp in milliseconds in a long
   */
  public static long getCurrentTime() {
    Timestamp now = new Timestamp(System.currentTimeMillis());
    return now.getTime();
  }

  /**
   * Formats the time from milliseconds to hours and minutes.
   *
   * @param time the time given to reformat
   * @return the time given in milliseconds formatted as Hours-Minutes in a String
   */
  public static String formatTime(long time) {
    long hours = TimeUnit.MILLISECONDS.toHours(time);
    long minutes =
        TimeUnit.MILLISECONDS.toMinutes(time)
            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
    return String.format(TIME_FORMAT, hours, minutes);
  }

}

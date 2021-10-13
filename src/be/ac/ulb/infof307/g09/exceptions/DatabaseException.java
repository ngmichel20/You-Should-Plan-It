package be.ac.ulb.infof307.g09.exceptions;

/**
 * Exception class used when something wrong happens during a database transaction.
 */
public class DatabaseException extends Exception {

  /**
   * Creates a new database exception with an error message and
   * the exception that has been caught beforehand(Mostly SQLException).
   *
   * @param message The error message that the exception keeps
   * @param e The exception that has been thrown beforehand and launches a DatabaseException
   */
  public DatabaseException(String message, Throwable e) {
    super(message, e);
  }


  /**
   * Creates a new database exception with only a message.
   *
   * @param message The error message that the exception keeps
   */
  public DatabaseException(String message) {
    super(message);
  }
}
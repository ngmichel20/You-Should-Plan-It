package be.ac.ulb.infof307.g09.exceptions;

/**
 * Exception class used when the connection to the database fails.
 */
public class ConnectionFailedException extends Exception {

  /**
   * Creates a new connection failed exception with an error message and
   * the exception that has been caught beforehand(Mostly ClassNotFoundException).
   *
   * @param message The error message that the exception keeps
   * @param e       The exception that has been thrown beforehand
   *                and launches a ConnectionFailedException
   */
  public ConnectionFailedException(String message, Throwable e) {
    super(message, e);
  }
}

package be.ac.ulb.infof307.g09.database; 

import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import org.sqlite.SQLiteConfig;

/**
 * The database class.
 * The class is used to handle the connection to the database.
 *
 * @author Piekarski Maciej
 * @author El Bakkali Soufian
 */
abstract class Database {

  /**
   * Relative path to the database from the content root.
   */
  private static String databasePath = "DataBase.db";
  protected static final String DRIVER = "org.sqlite.JDBC";
  private static final int NONEXISTENT_ROW = -1;
  private static File database = new File(databasePath);


  /**
   * Reset the path of the database.
   *
   * @param filename the path of the database
   */
  public static void setDatabasePath(String filename) {
    databasePath = filename;
    database = new File(databasePath);
  }

  /**
   * Checks if the database exists.
   *
   * @return true if the database file exists, false otherwise
   */
  public boolean checkIfDatabaseExists() {
    return database.exists();
  }

  /**
   * Returns the database.
   *
   * @return the database
   */
  public File getDatabase() {
    return database;
  }

  /**
   * Connect to the database.
   *
   * @return the Connection object
   * @throws SQLException throws when something wrong happens during
   *                      the fetching of data from the database
   * @throws ClassNotFoundException If the DRIVER class is not found.
   *                                It implies that the connection fails.
   */
  protected static Connection connect() throws SQLException, ClassNotFoundException {
    String url = "jdbc:sqlite:" + databasePath;
    Connection conn;

    Class.forName(DRIVER);
    SQLiteConfig config = new SQLiteConfig();
    config.enforceForeignKeys(true);
    conn = DriverManager.getConnection(url, config.toProperties());

    return conn;
  }

  /**
   * Finds the id of the last inserted project (used in the above inserts).
   *
   * @param table the table in which we want to find the last inserted row
   * @return The id of the last inserted project (used in the above inserts)
   * @throws ConnectionFailedException If the connection to the database fails
   * @throws DatabaseException This exception is thrown to let the user
   *                           know that there is a database failure
   */
  protected static int findLastInsertedRow(String table)
      throws ConnectionFailedException, DatabaseException {
    int result = NONEXISTENT_ROW;

    String sql = "SELECT * FROM sqlite_sequence WHERE name = ?";

    try (Connection conn = connect();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

      preparedStatement.setString(1, table);
      try (ResultSet rs = preparedStatement.executeQuery()) {
        if (rs.next()) {
          result = rs.getInt("seq");
        }
      }
      if (result == NONEXISTENT_ROW) {
        throw new DatabaseException("findLastInsertedRow : Last inserted row not found");
      }
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
    return result;
  }

  /**
   * Creates a new database.
   *
   * @param startPath beginning of the path
   * @throws DatabaseException         if a problem occurs during the access to the database
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public static void createNewDatabaseFile(String startPath)
          throws ConnectionFailedException, DatabaseException {
    boolean databaseCreated;
    try {
      databaseCreated = database.createNewFile();
      if (!databaseCreated) {
        throw new IOException("Cannot create file");
      }

      URL databaseResource = Objects.requireNonNull(Paths.get(
          startPath + "resources/database/database.sql").toUri().toURL());
      String decodedPath = URLDecoder.decode(databaseResource.getPath(), "UTF-8");

      try (BufferedReader in = new BufferedReader(new FileReader(decodedPath))) {
        StringBuilder sqlQuery = new StringBuilder();
        while (in.ready()) {
          sqlQuery.append(in.readLine());
          if (sqlQuery.toString().endsWith(";")) {
            createBasicSqlTable(sqlQuery.toString());
            sqlQuery = new StringBuilder();
          }
        }
      }
    } catch (IOException e) {
      throwException(e);
    }
  }

  /**
   * Insert new tables in the database.
   *
   * @param sql the sql to execute
   * @throws DatabaseException         if a problem occurs during the access to the database
   * @throws ConnectionFailedException If the connection to the database fails
   */
  private static void createBasicSqlTable(String sql)
          throws ConnectionFailedException, DatabaseException {
    try (Connection conn = connect();
         Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
    } catch (SQLException e) {
      throwException(e);
    } catch (ClassNotFoundException e) {
      throwConnectionException(e);
    }
  }

  /**
   * Handles the exception when a query does not work well.
   *
   * @param throwable  The exception caught (mostly SQLException)
   * @throws DatabaseException This exception is thrown to let the user
   *                           know that there is a database failure
   */
  public static void throwException(Throwable throwable)
          throws DatabaseException {
    throw new DatabaseException("Erreur lors de l'accès à la base de données", throwable);
  }

  /**
   * Handles the exception when the connection to the database fails.
   *
   * @param throwable  The exception caught (mostly SQLException)
   * @throws ConnectionFailedException If the connection to the database fails
   */
  public static void throwConnectionException(Throwable throwable)
      throws ConnectionFailedException {
    throw new ConnectionFailedException("Erreur lors de l'accès à la base de données", throwable);
  }
}

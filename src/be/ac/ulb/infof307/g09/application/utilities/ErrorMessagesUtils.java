package be.ac.ulb.infof307.g09.application.utilities;

/**
 * ErrorMessagesUtils contains all error messages.
 */
public final class ErrorMessagesUtils {

  private ErrorMessagesUtils(){}

  public static final String ERROR_MESSAGE =
      "Si le problème persiste, contactez le support technique.";

  public static final String ERROR_LOADING_VIEW = "Erreur de chargement de l'affichage, "
      + "si le problème persiste, contactez le support technique.";

  public static final String CONNECTION_MSG_ERROR = "Erreur de connexion à la base de données, "
          + "si le problème persiste, contactez le support technique.";

  public static final String ERROR_USER =
          "Erreur lors de l'accès aux utilisateurs";

  public static final String ERROR_TAG_CREATION =
          "Erreur lors de la création/modification des étiquettes.";

  public static final String ERROR_TAG_DELETION =
          "Erreur lors de la suppression des étiquettes.";

  public static final String ERROR_PROJECT_UNIQUE_TAG =
      "Erreur lors de la recherche du projet avec les étiquettes uniques.";

  public static final String ERROR_COLLABORATORS_ACCESS =
      "Erreur lors de l'accès aux collaborateurs du projet.";

  public static final String ERROR_UPDATE_PROJECT =
          "Incapable de modifier le projet actuellement.";

  public static final String ERROR_DELETE_PROJECT =
          "Incapable de supprimer le projet actuellement.";

  public static final String ERROR_INSERT_PROJECT =
          "Incapable d'ajouter le projet actuellement.";
}

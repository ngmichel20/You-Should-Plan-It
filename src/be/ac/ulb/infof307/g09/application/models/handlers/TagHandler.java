package be.ac.ulb.infof307.g09.application.models.handlers;

import be.ac.ulb.infof307.g09.application.models.Project;
import be.ac.ulb.infof307.g09.application.models.Tag;
import be.ac.ulb.infof307.g09.application.models.User;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.database.ProjectTagDatabase;
import be.ac.ulb.infof307.g09.database.TagDatabase;
import be.ac.ulb.infof307.g09.exceptions.ConnectionFailedException;
import be.ac.ulb.infof307.g09.exceptions.DatabaseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sub Facade that handles the tags.
 */
class TagHandler {
  private final User user;
  private final TagDatabase tagDatabase;
  private final ProjectTagDatabase projectTagDatabase;

  /**
   * Initialises the facade with the given user.
   *
   * @param currentUser the user
   */
  TagHandler(User currentUser) {
    this.user = currentUser;
    this.tagDatabase = TagDatabase.getInstance();
    this.projectTagDatabase = ProjectTagDatabase.getInstance();
  }

  /**
   * Get all the Tags that exist in all the projects of the user.
   *
   * @return All tags of user
   */
  List<Tag> getAllUserTags() {
    Set<Tag> tags = new HashSet<>();

    for (Project project : this.user.getProjectList()) {
      tags.addAll(project.getTags());
      for (Project projectChild : project.getAllChildren()) {
        tags.addAll(projectChild.getTags());
      }
    }

    return new ArrayList<>(tags);
  }

  /**
   * Adds a tag to the project.
   *
   * @param project the project
   * @param tag     the tag
   * @throws DatabaseException         if an error occurs during the insertion of the tag
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void addTagToProject(Project project, Tag tag)
      throws DatabaseException, ConnectionFailedException {
    try {
      projectTagDatabase.addProjectTag(project.getId(), tag.getId());
      project.addTag(tag);
    } catch (DatabaseException e) {
      throw new DatabaseException(ErrorMessagesUtils.ERROR_TAG_CREATION, e);
    }
  }

  /**
   * Gets the tag from the database.
   *
   * @param tagDescription the description of the tag
   * @return the tag found
   * @throws DatabaseException         if a problem occurs during the access to the database
   * @throws ConnectionFailedException If the connection to the database fails
   */
  Tag getTagFromDatabase(String tagDescription)
      throws DatabaseException, ConnectionFailedException {
    return TagDatabase.getInstance().getTag(tagDescription);
  }

  /**
   * Deletes a tag from the project.
   *
   * @param project the project
   * @param tag     the tag
   * @throws DatabaseException         if an error occurs during the deletion of the tag
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void deleteTagFromProject(Project project, Tag tag)
      throws DatabaseException, ConnectionFailedException {
    try {
      this.projectTagDatabase.removeProjectTag(project.getId(), tag.getId());
      project.removeTag(tag);
    } catch (DatabaseException e) {
      throw new DatabaseException(ErrorMessagesUtils.ERROR_TAG_DELETION, e);
    }
  }

  /**
   * Returns the tag from the database by the specified description.
   *
   * @param description the description of the tag
   * @return the tag found in the database
   * @throws DatabaseException         if an error occurs during the fetching of the tag
   * @throws ConnectionFailedException If the connection to the database fails
   */
  Tag getTag(String description) throws DatabaseException, ConnectionFailedException {
    return tagDatabase.getTag(description);
  }

  /**
   * Creates a new tag in the database specified by the description.
   *
   * @param description the description of the new tag
   * @return the created tag in the database
   * @throws DatabaseException         if an error occurs during the creation of the tag
   * @throws ConnectionFailedException If the connection to the database fails
   */
  Tag createTag(String description) throws DatabaseException, ConnectionFailedException {
    return tagDatabase.createTag(description);
  }

  /**
   * Deletes a tag from the database specified by the id.
   *
   * @param tagId the id of the tag
   * @throws DatabaseException         if an error occurs during the deletion of the tag
   * @throws ConnectionFailedException If the connection to the database fails
   */
  void deleteTag(int tagId) throws DatabaseException, ConnectionFailedException {
    tagDatabase.deleteTag(tagId);
  }

}

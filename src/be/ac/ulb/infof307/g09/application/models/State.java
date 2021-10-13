package be.ac.ulb.infof307.g09.application.models;

/**
 * States that describe the changes in the application model.
 */
public enum State {
  /**
   * Indicates that the application is initiated.
   */
  APPLICATION_CREATED,

  /**
   * Indicates that a new project has been created.
   */
  PROJECT_CREATED,

  /**
   * Indicates that a project has been modified.
   */
  PROJECT_MODIFIED,

  /**
   * Indicates that a project has been deleted.
   */
  PROJECT_DELETED,

  /**
   * Indicates that a task has been created.
   */
  TASK_CREATED,

  /**
   * Indicates that a task has been modified.
   */
  TASK_MODIFIED,

  /**
   * Indicates that a task has been deleted.
   */
  TASK_DELETED,

  /**
   * Indicates that a tag has been created.
   */
  TAG_CREATED,

  /**
   * Indicates that a tag has been modified.
   */
  TAG_MODIFIED,

  /**
   * Indicates that a tag has been deleted.
   */
  TAG_DELETED,

  /**
   * Indicates that a collaborator has left the collaboration.
   */
  COLLABORATOR_REMOVED

}
package be.ac.ulb.infof307.g09.application.models;

import java.io.Serializable;

/**
 * The Tag class.
 *
 * @author Benoît Haal
 * @author Pap Sanou
 */
public class Tag implements Serializable {

  private final int id;
  private final String description;

  /**
   * Constructor of a tag.
   *
   * @param tagId the id of a tag
   * @param tagDescription the description of a tag
   */
  public Tag(int tagId, String tagDescription) {
    this.id = tagId;
    this.description = tagDescription;
  }

  /**
   * Returns the id of the tag.
   *
   * @return The id of the tag
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the description of the tag.
   *
   * @return The description of the tag
   */
  public String getDescription() {
    return this.description;
  }

  @Override
  public String toString() {
    return this.description;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (object == this) {
      return true;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    Tag other = (Tag) object;
    return this.getId() == (other.getId())
        && this.getDescription().equals((other.getDescription()));
  }

  @Override
  public int hashCode() {
    return id * description.hashCode();
  }
}

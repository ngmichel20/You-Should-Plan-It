package be.ac.ulb.infof307.g09.application.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

  private static final int TAG_ID = 11;
  private static final String TAG_DESCRIPTION = "This is a test";
  private static final int FALSE_TAG_ID = 55;
  private static final String FALSE_TAG_DESCRIPTION = "This is not a test";
  private static final Tag TAG = new Tag(TAG_ID, TAG_DESCRIPTION);

  @Test
  void testGetId() {
    assertEquals(TAG.getId(), TAG_ID);
  }

  @Test
  void testEqualsNull() {
    assertNotEquals(TAG, null);
  }

  @Test
  void testEqualsSame() {
    assertEquals(TAG, TAG);
  }

  @Test
  void testEqualsDifferentClass() {
    Object testEmptyString = "";
    assertNotEquals(TAG, testEmptyString);
  }

  @Test
  void testEqualsDifferentTag() {
    Tag tmp = new Tag(FALSE_TAG_ID, FALSE_TAG_DESCRIPTION);
    assertNotEquals(TAG, tmp);
  }

  @Test
  void testEqualsSameParameters() {
    Tag tmp = new Tag(TAG_ID, TAG_DESCRIPTION);
    assertEquals(TAG, tmp);
  }

  @Test
  void testToString() {
    assertEquals(TAG.toString(), TAG_DESCRIPTION);
  }

  @Test
  void testHashCode() {
    assertEquals(TAG.hashCode(), new Tag(TAG_ID, TAG_DESCRIPTION).hashCode());
  }

  @Test
  void testDifferentHashCode() {
    assertNotEquals(TAG.hashCode(), new Tag(FALSE_TAG_ID, FALSE_TAG_DESCRIPTION).hashCode());
  }
}
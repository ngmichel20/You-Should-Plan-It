package be.ac.ulb.infof307.g09.application.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilsTest {

  public static final long DATE_TO_FORMAT = 1616186446919L;

  @Test
  void testFormatDateToStringCorrect() {
    String expectedDate = "19/03/2021 21:40";
    assertEquals(expectedDate, DateTimeUtils.formatDateToString(DATE_TO_FORMAT));
  }

  @Test
  void testFormatDateToStringNotWorking() {
    String expectedDate = "19/03/2021 21:45";
    assertNotEquals(expectedDate, DateTimeUtils.formatDateToString(DATE_TO_FORMAT));
  }

  @Test
  void testGetCurrentTime() {
    long currentTime = DateTimeUtils.getCurrentTime();
    long expectedTime = DateTimeUtils.getCurrentTime();
    assertTrue(expectedTime - currentTime < 1000L);
  }

  @Test
  void testGetCurrentTimeDiffers() {
    long currentTime = DateTimeUtils.getCurrentTime();
    long notExpectedTime = 0;
    assertNotEquals(notExpectedTime, currentTime);
  }

  @Test
  void testFormatTime() {
    long valueToTest = 2760000;
    String expectedString = "00h46min";
    String valueToTestFormatted = DateTimeUtils.formatTime(valueToTest);
    assertEquals(expectedString, valueToTestFormatted);
  }
}
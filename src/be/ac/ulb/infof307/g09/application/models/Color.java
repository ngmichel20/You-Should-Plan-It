package be.ac.ulb.infof307.g09.application.models;

/**
 * The colors for the projects.
 */
public enum Color {
  /**
   * The color blue.
   */
  BLUE(1, "Bleu", "#abcee9"),

  /**
   * The color yellow.
   */
  YELLOW(2, "Jaune", "#fdf0d1"),

  /**
   * The color purple.
   */
  PURPLE(3, "Mauve", "#d7b4d8"),

  /**
   * The color rose.
   */
  ROSE(4, "Rose", "#ebb3ba"),

  /**
   * The color peach.
   */
  PEACH(5, "Pêche", "#fcdccd"),

  /**
   * The color brown.
   */
  BROWN(6, "Brun", "#dfb9a6"),

  /**
   * The color green.
   */
  GREEN(7, "Vert", "#d5edc6");

  private final int colorCode;
  private final String colorName;
  private final String hexadecimal;

  /**
   * Initializes a color with the specified parameters.
   *
   * @param code   the color of the code
   * @param name   the name of the color
   * @param colorHexadecimal the hexadecimal value of the color
   */
  Color(int code, String name, String colorHexadecimal) {
    this.colorCode = code;
    this.colorName = name;
    this.hexadecimal = colorHexadecimal;
  }

  /**
   * Returns the color code.
   *
   * @return the color code
   */
  public int getColorCode() {
    return colorCode;
  }

  /**
   * Returns the hexadecimal value of the color.
   *
   * @return the hexadecimal value
   */
  public String getHexadecimal() {
    return hexadecimal;
  }

  /**
   * Returns a color from a color code.
   *
   * @param colorCode the color of the code
   * @return the color
   */
  public static Color fromInteger(int colorCode) {
    Color colorFound = null;
    for (Color color : values()) {
      if (color.getColorCode() == colorCode) {
        colorFound = color;
      }
    }
    return colorFound;
  }

  @Override
  public String toString() {
    return this.colorName;
  }
}

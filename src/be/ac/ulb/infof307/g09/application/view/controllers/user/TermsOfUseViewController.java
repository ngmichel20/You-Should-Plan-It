package be.ac.ulb.infof307.g09.application.view.controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * TermsOfUseViewController Class that displays Terms Of Use.
 */
public class TermsOfUseViewController {

  private Listener listener;

  @FXML
  private TextArea termsTextArea;

  /**
   * Set the terms of use text inside the view.
   *
   * @param text the terms of use to show
   */
  public void setTermsOfUseText(String text) {
    this.termsTextArea.setText(text);
  }

  /**
   * Close the window once the close button is pressed.
   */
  @FXML
  private void closeButtonPressed() {
    listener.closeTermsOfUse();
  }


  /**
   * Sets the listener.
   *
   * @param listener the listener to set
   */
  public void setListener(Listener listener) {
    this.listener = listener;
  }

  /**
   * Listener interface to communicate with higher controller.
   */
  public interface Listener {

    /**
     * Close the terms of use window.
     */
    void closeTermsOfUse();
  }
}

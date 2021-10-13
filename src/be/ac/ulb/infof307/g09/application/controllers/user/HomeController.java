package be.ac.ulb.infof307.g09.application.controllers.user;

import be.ac.ulb.infof307.g09.application.controllers.project.CalendarController;
import be.ac.ulb.infof307.g09.application.controllers.project.DashboardController;
import be.ac.ulb.infof307.g09.application.controllers.project.ListProjectsController;
import be.ac.ulb.infof307.g09.application.models.handlers.Application;
import be.ac.ulb.infof307.g09.application.utilities.ErrorMessagesUtils;
import be.ac.ulb.infof307.g09.application.view.controllers.user.HomeViewController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;


/**
 * Home Controller class that handles actions in the Home UI,
 * such as adding or deletion of tabs.
 */
public class HomeController {
  private static final String APPLICATION_TITLE = "I(Should)PlanAll";
  private static final String TAB_HOME_TITLE = "Accueil";
  private static final String TAB_PROFILE_TITLE = "Profil";
  private static final String TAB_STATISTICS_TITLE = "Statistiques";
  private static final String TAB_CALENDAR_TITLE = "Calendrier";

  private static final int NUMBER_OF_PRIMORDIAL_TABS = 4;
  private final Stage stage;

  private static HomeViewController viewController;
  private final ProfileController profileController;
  private final DashboardController dashboardController;
  private final ListProjectsController listProjectsController;
  private final CalendarController calendarController;


  /**
   * Home controller constructor.
   *
   * @param stage       the Stage for the new Scene
   * @param application the application model
   */
  public HomeController(Stage stage, Application application) {
    this.stage = stage;
    this.profileController = new ProfileController(this.stage, application);
    this.dashboardController = new DashboardController(this.stage, application);
    this.listProjectsController = new ListProjectsController(this.stage, application);
    this.calendarController = new CalendarController(application);
  }

  /**
   * Show the Home Scene.
   */
  public void show() {
    try {
      FXMLLoader loader =
          new FXMLLoader(HomeViewController.class.getResource("/views/user/HomeView.fxml"));
      loader.load();
      if (viewController == null) {
        viewController = loader.getController();
      }
      viewController.setMinStageSize(this.stage);
      Parent root = loader.getRoot();
      stage.setScene(new Scene(root));
      stage.setTitle(APPLICATION_TITLE);
      addTabs();
      stage.show();
    } catch (IOException e) {

      HomeController.displayErrorAlert(e.getMessage(),
          ErrorMessagesUtils.ERROR_LOADING_VIEW);
    }
  }

  /**
   * Add the tabs Home, profile and statistics to the tab pane.
   */
  private void addTabs() {
    addTab(listProjectsController.getView(), TAB_HOME_TITLE, false);
    addTab(profileController.getView(), TAB_PROFILE_TITLE, false);
    addTab(dashboardController.getView(), TAB_STATISTICS_TITLE, false);
    addTab(calendarController.getView(), TAB_CALENDAR_TITLE, false);
  }

  /**
   * Add a new tab.
   *
   * @param view       the view to show
   * @param title      title of the tab
   * @param isClosable check if the tab can be closed or not
   * @return the new tab
   */
  public static Tab addTab(Parent view, String title, boolean isClosable) {
    return viewController.addTab(view, title, isClosable);
  }

  /**
   * Disable a tab.
   *
   * @param tabIndex   index of tab to disable
   * @param isDisabled check if the tab is disabled or not
   */
  public static void disableTab(int tabIndex, boolean isDisabled) {
    viewController.disableTab(tabIndex, isDisabled);
  }

  /**
   * Switch to the last tab.
   *
   * @param tabIndex the tab index to switch on it
   */
  public static void switchToTab(int tabIndex) {
    viewController.switchToTab(tabIndex);
  }

  /**
   * Close the tab.
   *
   * @param tabIndex the index of tab to remove
   */
  public static void closeTab(int tabIndex) {
    viewController.closeTab(tabIndex);
  }

  /**
   * Handle the closing of the tab.
   *
   * @param tab the tab to close
   */
  public static void enableTabsOnCloseRequest(Tab tab) {
    tab.setOnCloseRequest(event -> close(tab));
  }

  /**
   * Enable primordial tabs when the new tab is closed.
   * Once the tab is closed, goes back to the Home tab.
   *
   * @param tab the tab to close
   */
  public static void close(Tab tab) {
    int index = Integer.parseInt(tab.getId());
    if (index == NUMBER_OF_PRIMORDIAL_TABS) {
      for (int i = 0; i < index; i++) {
        HomeController.disableTab(i, false);
      }
      HomeController.switchToTab(0);
    } else {
      HomeController.disableTab(index - 1, false);
      HomeController.switchToTab(index - 1);

    }
  }

  /**
   * Display an error alert with the specified message,
   * title and header text.
   *
   * @param headerText   the header text of the header window
   * @param errorMessage error message to display
   */
  public static void displayErrorAlert(String headerText, String errorMessage) {
    viewController.displayErrorAlert(headerText, errorMessage);
  }

  /**
   * Display an information alert with the specified message,
   * title and header text.
   *
   * @param headerText         the header text of the header window
   * @param informationMessage information message to display
   */
  public static void displayInformationAlert(String headerText, String informationMessage) {
    viewController.displayInformationAlert(headerText, informationMessage);
  }

  /**
   * Disable all tabs on the left of the current tab.
   *
   * @param tab the current tab
   */
  public static void disableLeftTabs(Tab tab) {
    int index = Integer.parseInt(tab.getId());
    HomeController.switchToTab(index);
    for (int i = 0; i < index; i++) {
      HomeController.disableTab(i, true);
    }
  }

  /**
   * Resets the view controller.
   */
  public static void resetViewController() {
    viewController = null;
  }
}
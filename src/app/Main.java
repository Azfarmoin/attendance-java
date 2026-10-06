package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import services.DataService;
import services.SessionService;
import ui.LoginView;
import ui.MainView;

/**
 * AttendX - Futuristic Student Management System
 * Main application entry point
 */
public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Load fonts (system fonts, no download needed)
        loadFonts();

        // Initialize data service with seed data
        DataService.getInstance().initialize();

        // Start with login screen
        showLoginScreen();

        // Window settings
        stage.setTitle("AttendX — Futuristic Student Management");
        stage.setMinWidth(1100);
        stage.setMinHeight(680);
        stage.setWidth(1280);
        stage.setHeight(780);
        stage.show();

        // Handle close
        stage.setOnCloseRequest(e -> {
            DataService.getInstance().saveAll();
            Platform.exit();
        });
    }

    private void loadFonts() {
        // Load system fonts - no external download needed
        // JavaFX will fall back to system fonts gracefully
    }

    public static void showLoginScreen() {
        LoginView loginView = new LoginView();
        Scene scene = new Scene(loginView.getRoot(), 1280, 780);
        scene.getStylesheets().add(Main.class.getResource("/styles/dark.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public static void showMainScreen() {
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView.getRoot(), 1280, 780);
        scene.getStylesheets().add(Main.class.getResource("/styles/dark.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public static void logout() {
        SessionService.getInstance().clearSession();
        showLoginScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

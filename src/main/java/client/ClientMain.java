package client;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.StartScreen;

// Launches the chess client application and shows the start screen.
public class ClientMain extends Application {

    // Initializes the JavaFX application and displays the start screen.
    @Override
    public void start(Stage primaryStage) {
        new StartScreen(primaryStage).show();
    }

    // Entry point of the application.
    public static void main(String[] args) {
        launch(args);
    }
}

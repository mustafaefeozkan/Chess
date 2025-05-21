package client;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.StartScreen;

public class ClientMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        new StartScreen(primaryStage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/** Displays the end screen with the game result and options to restart or exit. */
public class EndScreen {
    private final Stage stage;
    private final String message;

    // Initializes the end screen with the given stage and result message.
    public EndScreen(Stage stage, String message) {
        this.stage = stage;
        this.message = message;
    }

    // Shows the end screen with result, "Play Again", and "End Game" buttons.
    public void show() {
        Label resultLabel = new Label(message);
        resultLabel.setFont(new Font(28));
        resultLabel.setTextFill(Color.WHITE);

        Button playAgainButton = new Button("Play Again");
        playAgainButton.setFont(new Font(16));
        playAgainButton.setOnAction(e -> {
            StartScreen startScreen = new StartScreen(stage);
            startScreen.show();
        });

        Button endButton = new Button("End Game");
        endButton.setFont(new Font(16));
        endButton.setOnAction(e -> System.exit(0));

        VBox layout = new VBox(20, resultLabel, playAgainButton, endButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(layout, 640, 400);
        stage.setScene(scene);
        stage.setTitle("Game Over");
        stage.show();
    }
}

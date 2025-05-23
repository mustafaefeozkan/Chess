package ui;

import client.ChessClient;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/** Displays the initial screen where the player can connect to the chess server. */
public class StartScreen {
    private final Stage stage;
    private ChessClient client;

    // Initializes the start screen with the given stage.
    public StartScreen(Stage stage) {
        this.stage = stage;
    }

    // Displays the default start screen message.
    public void show() {
        show("Press to connect");
    }

    // Displays the start screen with the specified message and connect button.
    public void show(String info) {
        Label label = new Label(info);
        label.setFont(new Font(18));
        label.setTextFill(Color.WHITE);

        Button connectButton = new Button("Connect to Server");
        connectButton.setFont(new Font(16));
        connectButton.setOnAction(e -> {
            try {
                System.out.println("Connecting to server...");
                client = new ChessClient("56.228.34.7", 5000);
                client.setMessageHandler(this::handleMessage);
                label.setText("Connected. Waiting for opponent...");
                connectButton.setDisable(true);
            } catch (Exception ex) {
                label.setText("Connection failed.");
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(20, label, connectButton);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Chess - Start");
        stage.centerOnScreen();
        stage.show();
    }

    // Handles incoming messages from the server and transitions to the game screen.
    private void handleMessage(String message) {
        if (message.startsWith("START")) {
            String color = message.split(" ")[1];
            Platform.runLater(() -> {
                System.out.println("Sending READY...");
                client.send("READY");
                GameScreen gameScreen = new GameScreen(stage, client, color);
                client.setMessageHandler(gameScreen::handleMessage);
                gameScreen.show();
            });
        }
    }
}

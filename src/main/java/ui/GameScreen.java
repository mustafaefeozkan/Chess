package ui;

import client.ChessClient;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.*;

import java.util.ArrayList;
import java.util.List;

/** Displays the chess game UI and manages gameplay interaction and state. */
public class GameScreen {
    private final Stage stage;
    private final ChessClient client;
    private static final int TILE_SIZE = 70;
    private final Board board;
    private final RuleEngine ruleEngine;
    private final String myColor;
    private String currentTurn = "white";
    private Piece selectedPiece;
    private String selectedPos;
    private String checkSourcePos = null;
    private final List<String> validMoves = new ArrayList<>();
    private final List<Move> moveHistory = new ArrayList<>();
    private final List<Piece> whiteCaptured = new ArrayList<>();
    private final List<Piece> blackCaptured = new ArrayList<>();
    private final Label statusLabel = new Label();
    private boolean alreadyLeft = false;
    private String lastMoveFrom = null;
    private String lastMoveTo = null;

    // Initializes the game screen with the player's color and sets up server communication.
    public GameScreen(Stage stage, ChessClient client, String myColor) {
        this.stage = stage;
        this.client = client;
        this.myColor = myColor;
        this.board = new Board();
        this.ruleEngine = new RuleEngine(board);
        client.setMessageHandler(this::handleMessage);

        stage.setOnCloseRequest(e -> {
            client.send("EXIT");
            Platform.exit();
            System.exit(0);
        });
    }

    // Displays the board, updates game state, highlights, and processes player interaction.
    public void show() {
        // Highlight the source of check if the current player is in check.
        checkSourcePos = ruleEngine.isCheck(myColor) ? ruleEngine.getCheckSource(myColor) : null;

        // Check for checkmate and display the end screen if applicable.
        if (ruleEngine.isCheckmate(currentTurn)) {
            String winner = currentTurn.equals("white") ? "BLACK" : "WHITE";
            new EndScreen(stage, "CHECKMATE - " + winner + " WINS").show();
            return;
        }

        // Check for stalemate and display the end screen if applicable.
        if (ruleEngine.isStalemate(currentTurn)) {
            new EndScreen(stage, "DRAW - STALEMATE").show();
            return;
        }

        GridPane boardGrid = new GridPane(); // Create the board grid layout.

        for (int i = 0; i < 8; i++) {
            int row = myColor.equals("white") ? i : 7 - i;
            for (int j = 0; j < 8; j++) {
                int col = myColor.equals("white") ? j : 7 - j;
                int displayRow = row;
                int displayCol = col;

                String pos = Board.toChessNotation(displayRow, displayCol);
                StackPane cell = new StackPane();
                cell.setPrefSize(TILE_SIZE, TILE_SIZE);

                boolean isSelected = pos.equals(selectedPos);
                boolean isLastFrom = pos.equals(lastMoveFrom);
                boolean isLastTo = pos.equals(lastMoveTo);

                String baseColor = (row + col) % 2 == 0 ? "#e3c16f" : "#b88746";
                String highlightColor = isSelected || isLastFrom || isLastTo ? "#00aa00" : baseColor;
                cell.setStyle("-fx-background-color: " + highlightColor + ";");

                Piece piece = board.getPiece(row, col);

                // Render piece and apply glow effects based on status (check, capturable, hover).
                if (piece != null) {
                    ImageView iv = getImageView(piece, 60);

                    if (pos.equals(checkSourcePos)) {
                        DropShadow redGlow = new DropShadow();
                        redGlow.setColor(Color.RED);
                        redGlow.setRadius(30);
                        redGlow.setSpread(0.5);
                        iv.setEffect(redGlow);
                    } else if (validMoves.contains(pos) && !piece.getColor().equals(myColor)) {
                        DropShadow redGlow = new DropShadow();
                        redGlow.setColor(Color.RED);
                        redGlow.setRadius(20);
                        redGlow.setSpread(0.4);
                        iv.setEffect(redGlow);
                    } else {
                        DropShadow greenGlow = new DropShadow();
                        greenGlow.setRadius(20);
                        greenGlow.setColor(Color.GREEN);
                        greenGlow.setSpread(0.3);
                        iv.setOnMouseEntered(e -> iv.setEffect(greenGlow));
                        iv.setOnMouseExited(e -> iv.setEffect(null));
                    }

                    cell.getChildren().add(iv);
                }

                // Show green dot on valid empty destination squares.
                if (validMoves.contains(pos) && board.getPiece(row, col) == null) {
                    Circle indicator = new Circle(7);
                    indicator.setFill(Color.GREEN);
                    cell.getChildren().add(indicator);
                }

                final String position = pos;
                final Piece pieceHere = piece;

                // Handle user clicks for selecting and moving pieces.
                cell.setOnMouseClicked(e -> {
                    if (!currentTurn.equals(myColor)) return;

                    if (selectedPiece == null && pieceHere != null && pieceHere.getColor().equals(myColor)) {
                        selectedPiece = pieceHere;
                        selectedPos = position;
                        validMoves.clear();
                        for (int rr = 0; rr < 8; rr++) {
                            for (int cc = 0; cc < 8; cc++) {
                                String to = Board.toChessNotation(rr, cc);
                                if (ruleEngine.isMoveValid(position, to, myColor)) {
                                    validMoves.add(to);
                                }
                            }
                        }
                        show();

                    } else if (selectedPiece != null) {
                        String from = selectedPos;
                        String to = position;

                        // Validate and execute the move if legal.
                        if (!to.equals(from) && ruleEngine.isMoveValid(from, to, myColor)) {
                            Piece captured = board.getPiece(to);
                            if (captured != null) {
                                // Track captured piece for later display.
                                if (captured.getColor().equals("white")) blackCaptured.add(captured);
                                else whiteCaptured.add(captured);
                            }

                            // Record move and prepare message.
                            Move move = new Move(from, to, selectedPiece, captured);
                            moveHistory.add(move);

                            String msg;
                            if (ruleEngine.canCastle(from, to, myColor)) {
                                String[] rookMoves = ruleEngine.getCastleRookMove(from, to, myColor);
                                msg = (rookMoves != null)
                                        ? "MOVE " + from + " " + to + " CASTLE " + rookMoves[0] + " " + rookMoves[1]
                                        : "MOVE " + from + " " + to;
                            } else {
                                msg = "MOVE " + from + " " + to;
                            }

                            // Send move to server and apply it to local board.
                            client.send(msg);
                            board.movePiece(from, to);
                            if (msg.contains("CASTLE")) {
                                String[] p = msg.split(" ");
                                board.movePiece(p[4], p[5]);
                            }

                            // Check for promotion and apply if needed.
                            Piece moved = board.getPiece(to);
                            if (ruleEngine.shouldPromote(to, moved)) {
                                board.setPiece(Board.fromChessNotation(to)[0],
                                        Board.fromChessNotation(to)[1],
                                        ruleEngine.promotePawn(moved.getColor()));
                            }

                            lastMoveFrom = from;
                            lastMoveTo = to;

                            // Change turn.
                            currentTurn = currentTurn.equals("white") ? "black" : "white";
                        }

                        // Clear selection and refresh view.
                        selectedPiece = null;
                        selectedPos = null;
                        validMoves.clear();
                        show();
                    }
                });

                boardGrid.add(cell, col, row);
            }
        }


        // Create a VBox to show captured black pieces under the white label.
        VBox leftCapturedBox = new VBox(5);
        leftCapturedBox.setStyle("-fx-background-color: #eeeeee;");
        Label leftLabel = new Label("WHITE");
        leftLabel.setTextFill(Color.BLACK);
        leftCapturedBox.getChildren().add(leftLabel);
        for (Piece p : blackCaptured) leftCapturedBox.getChildren().add(getImageView(p, 30));

// Create a VBox to show captured white pieces under the black label.
        VBox rightCapturedBox = new VBox(5);
        rightCapturedBox.setStyle("-fx-background-color: #eeeeee;");
        Label rightLabel = new Label("BLACK");
        rightLabel.setTextFill(Color.BLACK);
        rightCapturedBox.getChildren().add(rightLabel);
        for (Piece p : whiteCaptured) rightCapturedBox.getChildren().add(getImageView(p, 30));

// Update the status message to show check or turn information.
        statusLabel.setFont(new Font(18));
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setText(ruleEngine.isCheck(myColor) ? "CHECK!" :
                (currentTurn.equals(myColor) ? "YOUR TURN" : "OPPONENT'S TURN"));

// Layout middle row with captured pieces and the board in the center.
        HBox middle = new HBox(20, leftCapturedBox, boardGrid, rightCapturedBox);
        middle.setAlignment(Pos.CENTER);

// Layout the entire screen with the status label and board row.
        VBox root = new VBox(10, statusLabel, middle);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black;");

// Set the scene and show the stage.
        Scene scene = new Scene(root, 800, 700);
        stage.setScene(scene);
        stage.setTitle("Chess Game - " + myColor.toUpperCase());
        stage.show();
    }

    // Loads a piece image and returns it as an ImageView.
    private ImageView getImageView(Piece piece, int size) {
        String path = "/images/" + piece.getColor() + "-" + piece.getType() + ".png";
        Image img = new Image(getClass().getResourceAsStream(path));
        ImageView iv = new ImageView(img);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        return iv;
    }

    // Handles messages from the server such as moves, turn changes, and disconnections.
    public void handleMessage(String msg) {
        System.out.println("[INFO] Message from server: " + msg);

        if (msg.startsWith("MOVE")) {
            Platform.runLater(() -> {
                String[] parts = msg.split(" ");
                String from = parts[1];
                String to = parts[2];

                lastMoveFrom = from;
                lastMoveTo = to;

                Piece captured = board.getPiece(to);
                if (captured != null) {
                    if (captured.getColor().equals("white")) blackCaptured.add(captured);
                    else whiteCaptured.add(captured);
                }

                Move move = new Move(from, to, board.getPiece(from), captured);
                moveHistory.add(move);

                board.movePiece(from, to);
                if (parts.length == 6 && "CASTLE".equals(parts[3])) {
                    board.movePiece(parts[4], parts[5]);
                }

                Piece moved = board.getPiece(to);
                if (ruleEngine.shouldPromote(to, moved)) {
                    board.setPiece(Board.fromChessNotation(to)[0],
                            Board.fromChessNotation(to)[1],
                            ruleEngine.promotePawn(moved.getColor()));
                }

                currentTurn = currentTurn.equals("white") ? "black" : "white";
                show();
            });
        } else if (msg.startsWith("TURN")) {
            Platform.runLater(() -> {
                currentTurn = msg.split(" ")[1];
                show();
            });
        } else if (msg.equals("OPPONENT_LEFT")) {
            if (alreadyLeft) return;
            alreadyLeft = true;

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Opponent Disconnected");
                alert.setHeaderText(null);
                alert.setContentText("Your opponent has disconnected. Game is over.");
                alert.setOnHidden(e -> {
                    StartScreen startScreen = new StartScreen(stage);
                    startScreen.show("Opponent disconnected. Game over.");
                });
                alert.show();
            });
        }
    }
}


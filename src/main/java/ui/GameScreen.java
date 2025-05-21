package ui;

import client.ChessClient;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

public class GameScreen {
    private final Stage stage;
    private final ChessClient client;
    private static final int TILE_SIZE = 70;
    private final Board board;
    private final RuleEngine ruleEngine;
    private final String myColor;
    private String currentTurn = "white";
    private Piece selectedPiece = null;
    private String selectedPos = null;
    private final List<String> validMoves = new ArrayList<>();
    private final Label statusLabel = new Label();

    public GameScreen(Stage stage, ChessClient client, String myColor) {
        this.stage = stage;
        this.client = client;
        this.myColor = myColor;
        this.board = new Board();
        this.ruleEngine = new RuleEngine(board);
    }

    public void show() {
        Platform.runLater(() -> System.out.println("GameScreen.show() called for color: " + myColor));

        if (ruleEngine.isCheckmate(currentTurn)) {
            String winner = currentTurn.equals("white") ? "BLACK" : "WHITE";
            new EndScreen(stage, "CHECKMATE - " + winner + " WINS").show();
            return;
        }

        if (ruleEngine.isStalemate(currentTurn)) {
            new EndScreen(stage, "DRAW - STALEMATE").show();
            return;
        }

        GridPane boardGrid = new GridPane();
        boardGrid.getChildren().clear();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(TILE_SIZE, TILE_SIZE);

                String baseColor = (row + col) % 2 == 0 ? "#e3c16f" : "#b88746";
                String position = Board.toChessNotation(row, col);
                Piece currentPiece = board.getPiece(row, col);
                boolean isSelected = position.equals(selectedPos);

                String backgroundColor = isSelected ? "#00aa00" : baseColor;
                cell.setStyle("-fx-background-color: " + backgroundColor + ";");

                final String pos = position;
                final Piece pieceHere = currentPiece;

                if (currentPiece != null) {
                    String imageName = "/images/" + currentPiece.getColor() + "-" + currentPiece.getType() + ".png";
                    Image img = new Image(getClass().getResourceAsStream(imageName));
                    ImageView imageView = new ImageView(img);
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);

                    DropShadow glow = new DropShadow();
                    glow.setRadius(20);
                    glow.setColor(Color.GREEN);
                    glow.setSpread(0.3);

                    imageView.setOnMouseEntered(e -> imageView.setEffect(glow));
                    imageView.setOnMouseExited(e -> imageView.setEffect(null));

                    cell.getChildren().add(imageView);
                }

                if (validMoves.contains(position) && board.getPiece(row, col) == null) {
                    Circle indicator = new Circle(7);
                    indicator.setFill(Color.GREEN);
                    cell.getChildren().add(indicator);
                }

                cell.setOnMouseClicked(e -> {
                    if (!currentTurn.equals(myColor)) return;

                    if (selectedPiece == null && pieceHere != null && pieceHere.getColor().equals(myColor)) {
                        selectedPiece = pieceHere;
                        selectedPos = pos;
                        validMoves.clear();
                        for (int r = 0; r < 8; r++) {
                            for (int c = 0; c < 8; c++) {
                                String toPos = Board.toChessNotation(r, c);
                                if (ruleEngine.isMoveValid(pos, toPos, myColor)) {
                                    validMoves.add(toPos);
                                }
                            }
                        }
                        show();
                    } else if (selectedPiece != null) {
                        if (!pos.equals(selectedPos)) {
                            if (selectedPiece instanceof King && pieceHere instanceof Rook &&
                                    pieceHere.getColor().equals(myColor) &&
                                    !selectedPiece.hasMoved() && !pieceHere.hasMoved()) {

                                int castleRow = myColor.equals("white") ? 7 : 0;
                                String from = selectedPos;
                                String to, rookFrom, rookTo;

                                if (pos.equals(Board.toChessNotation(castleRow, 0))) {
                                    to = Board.toChessNotation(castleRow, 2);
                                    rookFrom = Board.toChessNotation(castleRow, 0);
                                    rookTo = Board.toChessNotation(castleRow, 3);
                                } else if (pos.equals(Board.toChessNotation(castleRow, 7))) {
                                    to = Board.toChessNotation(castleRow, 6);
                                    rookFrom = Board.toChessNotation(castleRow, 7);
                                    rookTo = Board.toChessNotation(castleRow, 5);
                                } else {
                                    selectedPiece = null;
                                    selectedPos = null;
                                    validMoves.clear();
                                    show();
                                    return;
                                }

                                if (ruleEngine.isMoveValid(from, to, myColor)) {
                                    String msg = "MOVE " + from + " " + to + " CASTLE " + rookFrom + " " + rookTo;
                                    client.send(msg);
                                }
                            } else if (ruleEngine.isMoveValid(selectedPos, pos, myColor)) {
                                String msg = "MOVE " + selectedPos + " " + pos;
                                client.send(msg);
                            }
                        }

                        selectedPiece = null;
                        selectedPos = null;
                        validMoves.clear();
                    }
                });

                boardGrid.add(cell, col, row);
            }
        }

        boardGrid.setAlignment(Pos.CENTER);

        statusLabel.setFont(new Font(18));
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setAlignment(Pos.CENTER);

        if (ruleEngine.isCheck(currentTurn)) {
            statusLabel.setText("CHECK!");
        } else {
            statusLabel.setText((currentTurn.equals(myColor) ? "YOUR TURN" : "OPPONENT'S TURN"));
        }

        VBox root = new VBox(10, statusLabel, boardGrid);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, 640, 680);
        stage.setScene(scene);
        stage.setTitle("Chess Game - " + myColor.toUpperCase());
        stage.show();
    }

    public void handleMessage(String msg) {
        if (msg.startsWith("MOVE")) {
            Platform.runLater(() -> {
                System.out.println("GameScreen.handleMessage received MOVE: " + msg);
                String[] parts = msg.split(" ");
                String from = parts[1];
                String to = parts[2];

                board.movePiece(from, to);

                if (parts.length == 6 && "CASTLE".equals(parts[3])) {
                    String rookFrom = parts[4];
                    String rookTo = parts[5];
                    board.movePiece(rookFrom, rookTo);
                }

                Piece moved = board.getPiece(to);
                if (ruleEngine.shouldPromote(to, moved)) {
                    board.setPiece(Board.fromChessNotation(to)[0], Board.fromChessNotation(to)[1],
                            ruleEngine.promotePawn(moved.getColor()));
                }

                currentTurn = currentTurn.equals("white") ? "black" : "white";
                show();
            });
        }
    }
}

package model;

import java.util.HashMap;
import java.util.Map;

/** Represents the chessboard and handles piece placement and movement. */
public class Board {
    private final Piece[][] grid;
    private final Map<String, Piece> positionMap;

    // Initializes the board and places all pieces in their starting positions.
    public Board() {
        grid = new Piece[8][8];
        positionMap = new HashMap<>();
        initializeBoard();
    }

    // Sets up the initial arrangement of pieces on the board.
    private void initializeBoard() {
        String[] order = {"rook", "knight", "bishop", "queen", "king", "bishop", "knight", "rook"};

        for (int col = 0; col < 8; col++) {
            grid[0][col] = createPiece("black", order[col]);
            grid[1][col] = new Pawn("black");

            grid[6][col] = new Pawn("white");
            grid[7][col] = createPiece("white", order[col]);

            positionMap.put(toChessNotation(0, col), grid[0][col]);
            positionMap.put(toChessNotation(1, col), grid[1][col]);
            positionMap.put(toChessNotation(6, col), grid[6][col]);
            positionMap.put(toChessNotation(7, col), grid[7][col]);
        }
    }

    // Creates a piece of the given color and type.
    private Piece createPiece(String color, String type) {
        return switch (type) {
            case "rook" -> new Rook(color);
            case "knight" -> new Knight(color);
            case "bishop" -> new Bishop(color);
            case "queen" -> new Queen(color);
            case "king" -> new King(color);
            default -> null;
        };
    }

    // Returns the piece at the given row and column.
    public Piece getPiece(int row, int col) {
        return grid[row][col];
    }

    // Returns the piece at the specified board position.
    public Piece getPiece(String pos) {
        return positionMap.get(pos);
    }

    // Places the given piece at the specified row and column.
    public void setPiece(int row, int col, Piece piece) {
        grid[row][col] = piece;
        positionMap.put(toChessNotation(row, col), piece);
    }

    // Moves a piece from one position to another.
    public void movePiece(String from, String to) {
        Piece piece = positionMap.get(from);
        if (piece == null) return;

        int[] fromRC = fromChessNotation(from);
        int[] toRC = fromChessNotation(to);

        grid[toRC[0]][toRC[1]] = piece;
        grid[fromRC[0]][fromRC[1]] = null;

        positionMap.remove(from);
        positionMap.put(to, piece);

        piece.setMoved(true);
    }

    // Converts row and column indexes to chess notation.
    public static String toChessNotation(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    // Converts chess notation to row and column indexes.
    public static int[] fromChessNotation(String pos) {
        int col = pos.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(pos.charAt(1));
        return new int[]{row, col};
    }
}

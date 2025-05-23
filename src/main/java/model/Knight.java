package model;

/** Represents a knight chess piece and its L-shaped movement logic. */
public class Knight extends Piece {

    // Creates a knight with the specified color.
    public Knight(String color) {
        super(color);
    }

    // Returns the type of this piece.
    @Override
    public String getType() {
        return "knight";
    }

    // Checks if the knight's move is valid based on L-shaped movement rules.
    @Override
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);

        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) return false;

        Piece target = board.getPiece(toRow, toCol);
        return target == null || !target.getColor().equals(this.color);
    }
}

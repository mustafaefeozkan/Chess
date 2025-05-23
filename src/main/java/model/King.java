package model;

/** Represents a king chess piece and its one-square movement logic. */
public class King extends Piece {

    // Creates a king with the specified color.
    public King(String color) {
        super(color);
    }

    // Returns the type of this piece.
    @Override
    public String getType() {
        return "king";
    }

    // Checks if the king's move is valid (one square in any direction).
    @Override
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);

        if (rowDiff > 1 || colDiff > 1) return false;

        Piece target = board.getPiece(toRow, toCol);
        return target == null || !target.getColor().equals(this.color);
    }
}

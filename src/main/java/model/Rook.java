package model;

/** Represents a rook chess piece and its horizontal or vertical movement. */
public class Rook extends Piece {

    // Creates a rook with the specified color.
    public Rook(String color) {
        super(color);
    }

    // Returns the type of this piece.
    @Override
    public String getType() {
        return "rook";
    }

    // Checks if the rook's move is valid along rows or columns.
    @Override
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        if (fromRow != toRow && fromCol != toCol) return false;

        if (!isPathClear(fromRow, fromCol, toRow, toCol, board)) return false;

        Piece target = board.getPiece(toRow, toCol);
        return target == null || !target.getColor().equals(this.color);
    }

    // Checks if the straight path from source to destination is clear.
    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        int rowDir = Integer.compare(toRow, fromRow);
        int colDir = Integer.compare(toCol, fromCol);

        int currentRow = fromRow + rowDir;
        int currentCol = fromCol + colDir;

        while (currentRow != toRow || currentCol != toCol) {
            if (board.getPiece(currentRow, currentCol) != null) {
                return false;
            }
            currentRow += rowDir;
            currentCol += colDir;
        }
        return true;
    }
}

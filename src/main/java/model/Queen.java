package model;

/** Represents a queen chess piece with both straight and diagonal movement. */
public class Queen extends Piece {

    // Creates a queen with the specified color.
    public Queen(String color) {
        super(color);
    }

    // Returns the type of this piece.
    @Override
    public String getType() {
        return "queen";
    }

    // Checks if the queen's move is valid, allowing straight or diagonal movement.
    @Override
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        boolean straight = fromRow == toRow || fromCol == toCol;
        boolean diagonal = Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol);

        if (!(straight || diagonal)) return false;
        if (!isPathClear(fromRow, fromCol, toRow, toCol, board)) return false;

        Piece target = board.getPiece(toRow, toCol);
        return target == null || !target.getColor().equals(this.color);
    }

    // Checks if the path from source to destination is clear for queen movement.
    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        int rowDir = Integer.compare(toRow, fromRow);
        int colDir = Integer.compare(toCol, fromCol);
        int r = fromRow + rowDir;
        int c = fromCol + colDir;

        while (r != toRow || c != toCol) {
            if (board.getPiece(r, c) != null) return false;
            r += rowDir;
            c += colDir;
        }
        return true;
    }
}

package model;

public class Queen extends Piece {
    public Queen(String color) {
        super(color);
    }

    @Override
    public String getType() {
        return "queen";
    }

    @Override
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        boolean straight = fromRow == toRow || fromCol == toCol;
        boolean diagonal = Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol);

        if (!(straight || diagonal)) return false;
        if (!isPathClear(fromRow, fromCol, toRow, toCol, board)) return false;

        Piece target = board.getPiece(toRow, toCol);
        return target == null || !target.getColor().equals(this.color);
    }

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

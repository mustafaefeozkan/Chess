package model;

public class King extends Piece {
    public King(String color) {
        super(color);
    }

    @Override
    public String getType() {
        return "king";
    }

    @Override
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);

        if (rowDiff > 1 || colDiff > 1) return false;

        Piece target = board.getPiece(toRow, toCol);
        return target == null || !target.getColor().equals(this.color);
    }
}

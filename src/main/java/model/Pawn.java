package model;

public class Pawn extends Piece {
    public Pawn(String color) {
        super(color);
    }

    @Override
    public String getType() {
        return "pawn";
    }

    @Override
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        int direction = color.equals("white") ? -1 : 1;
        Piece target = board.getPiece(toRow, toCol);

        if (fromCol == toCol) {
            if (toRow - fromRow == direction && target == null) {
                return true;
            }
            if ((color.equals("white") && fromRow == 6 || color.equals("black") && fromRow == 1)
                    && toRow - fromRow == 2 * direction) {
                int midRow = fromRow + direction;
                return target == null && board.getPiece(midRow, fromCol) == null;
            }
        } else if (Math.abs(fromCol - toCol) == 1 && toRow - fromRow == direction) {
            return target != null && !target.getColor().equals(this.color);
        }

        return false;
    }
}

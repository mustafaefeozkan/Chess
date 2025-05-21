package model;

public abstract class Piece {
    protected final String color;
    protected boolean hasMoved = false;

    public Piece(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public abstract String getType();

    public abstract boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Board board);

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved(boolean moved) {
        this.hasMoved = moved;
    }
}

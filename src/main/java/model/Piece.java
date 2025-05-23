package model;

/** Abstract base class for all chess pieces, defining common attributes and behaviors. */
public abstract class Piece {
    protected final String color;
    protected boolean hasMoved = false;

    // Initializes the piece with a specified color.
    public Piece(String color) {
        this.color = color;
    }

    // Returns the color of the piece.
    public String getColor() {
        return color;
    }

    // Returns the type of the piece (to be implemented by subclasses).
    public abstract String getType();

    // Checks if the move is valid for this piece (to be implemented by subclasses).
    public abstract boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Board board);

    // Returns whether the piece has moved.
    public boolean hasMoved() {
        return hasMoved;
    }

    // Sets the moved status of the piece.
    public void setMoved(boolean moved) {
        this.hasMoved = moved;
    }
}

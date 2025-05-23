package model;

/** Represents a move in the chess game, including source, destination, and involved pieces. */
public class Move {
    private final String from;
    private final String to;
    private final Piece movedPiece;
    private final Piece capturedPiece;

    // Creates a move with source, destination, moved piece, and optionally captured piece.
    public Move(String from, String to, Piece movedPiece, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
    }

    // Returns the source position of the move.
    public String getFrom() {
        return from;
    }

    // Returns the destination position of the move.
    public String getTo() {
        return to;
    }

    // Returns the piece that was moved.
    public Piece getMovedPiece() {
        return movedPiece;
    }

    // Returns the piece that was captured, if any.
    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    // Returns a string representation of the move.
    @Override
    public String toString() {
        String captureText = capturedPiece != null ? " captures " + capturedPiece.getType() : "";
        return movedPiece.getType() + " from " + from + " to " + to + captureText;
    }
}

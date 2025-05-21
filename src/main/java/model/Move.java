package model;

public class Move {
    private final String from;
    private final String to;
    private final Piece movedPiece;
    private final Piece capturedPiece;

    public Move(String from, String to, Piece movedPiece, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    @Override
    public String toString() {
        String captureText = capturedPiece != null ? " captures " + capturedPiece.getType() : "";
        return movedPiece.getType() + " from " + from + " to " + to + captureText;
    }
}

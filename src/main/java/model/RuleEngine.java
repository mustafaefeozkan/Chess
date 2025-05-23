package model;

public class RuleEngine {
    private final Board board;

    public RuleEngine(Board board) {
        this.board = board;
    }

    public boolean isMoveValid(String from, String to, String currentColor) {
        Piece piece = board.getPiece(from);
        if (piece == null || !piece.getColor().equals(currentColor)) return false;

        int[] fromRC = Board.fromChessNotation(from);
        int[] toRC = Board.fromChessNotation(to);

        if (piece instanceof King && Math.abs(fromRC[1] - toRC[1]) == 2 && fromRC[0] == toRC[0]) {
            return canCastle(from, to, currentColor);
        }

        if (!piece.isValidMove(fromRC[0], fromRC[1], toRC[0], toRC[1], board)) return false;

        Piece target = board.getPiece(to);
        if (target != null && target.getColor().equals(currentColor)) return false;

        Piece originalTarget = board.getPiece(to);
        board.setPiece(toRC[0], toRC[1], piece);
        board.setPiece(fromRC[0], fromRC[1], null);
        boolean check = isCheck(currentColor);
        board.setPiece(fromRC[0], fromRC[1], piece);
        board.setPiece(toRC[0], toRC[1], originalTarget);

        return !check;
    }

    public boolean isCheck(String color) {
        String kingPos = findKingPosition(color);
        if (kingPos == null) return false;

        String enemyColor = color.equals("white") ? "black" : "white";

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor().equals(enemyColor)) {
                    int[] kingRC = Board.fromChessNotation(kingPos);
                    if (p.isValidMove(r, c, kingRC[0], kingRC[1], board)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCheckmate(String color) {
        if (!isCheck(color)) return false;

        for (int fromR = 0; fromR < 8; fromR++) {
            for (int fromC = 0; fromC < 8; fromC++) {
                Piece p = board.getPiece(fromR, fromC);
                if (p != null && p.getColor().equals(color)) {
                    for (int toR = 0; toR < 8; toR++) {
                        for (int toC = 0; toC < 8; toC++) {
                            String from = Board.toChessNotation(fromR, fromC);
                            String to = Board.toChessNotation(toR, toC);
                            if (isMoveValid(from, to, color)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean isStalemate(String color) {
        if (isCheck(color)) return false;

        for (int fromR = 0; fromR < 8; fromR++) {
            for (int fromC = 0; fromC < 8; fromC++) {
                Piece p = board.getPiece(fromR, fromC);
                if (p != null && p.getColor().equals(color)) {
                    for (int toR = 0; toR < 8; toR++) {
                        for (int toC = 0; toC < 8; toC++) {
                            String from = Board.toChessNotation(fromR, fromC);
                            String to = Board.toChessNotation(toR, toC);
                            if (isMoveValid(from, to, color)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean canCastle(String from, String to, String color) {
        int row = color.equals("white") ? 7 : 0;
        Piece king = board.getPiece(from);
        if (!(king instanceof King) || king.hasMoved()) return false;

        if (!from.equals(Board.toChessNotation(row, 4))) return false;

        if (to.equals(Board.toChessNotation(row, 6))) {
            Piece rook = board.getPiece(row, 7);
            if (!(rook instanceof Rook) || rook.hasMoved() || !rook.getColor().equals(color)) return false;
            if (board.getPiece(row, 5) != null || board.getPiece(row, 6) != null) return false;
            if (isCheck(color)) return false;

            board.setPiece(row, 5, king);
            board.setPiece(row, 4, null);
            boolean inDanger = isCheck(color);
            board.setPiece(row, 4, king);
            board.setPiece(row, 5, null);

            return !inDanger;
        }

        if (to.equals(Board.toChessNotation(row, 2))) {
            Piece rook = board.getPiece(row, 0);
            if (!(rook instanceof Rook) || rook.hasMoved() || !rook.getColor().equals(color)) return false;
            if (board.getPiece(row, 1) != null || board.getPiece(row, 2) != null || board.getPiece(row, 3) != null) return false;
            if (isCheck(color)) return false;

            board.setPiece(row, 3, king);
            board.setPiece(row, 4, null);
            boolean inDanger = isCheck(color);
            board.setPiece(row, 4, king);
            board.setPiece(row, 3, null);

            return !inDanger;
        }

        return false;
    }

    public String[] getCastleRookMove(String from, String to, String color) {
        int row = color.equals("white") ? 7 : 0;
        if (!from.equals(Board.toChessNotation(row, 4))) return null;

        if (to.equals(Board.toChessNotation(row, 6))) {
            return new String[]{Board.toChessNotation(row, 7), Board.toChessNotation(row, 5)};
        }

        if (to.equals(Board.toChessNotation(row, 2))) {
            return new String[]{Board.toChessNotation(row, 0), Board.toChessNotation(row, 3)};
        }

        return null;
    }

    private String findKingPosition(String color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece instanceof King && piece.getColor().equals(color)) {
                    return Board.toChessNotation(r, c);
                }
            }
        }
        return null;
    }

    public boolean shouldPromote(String pos, Piece piece) {
        return piece instanceof Pawn &&
                ((piece.getColor().equals("white") && pos.endsWith("8")) ||
                        (piece.getColor().equals("black") && pos.endsWith("1")));
    }

    public Piece promotePawn(String color) {
        return new Queen(color);
    }

    public String getCheckSource(String color) {
        String kingPos = findKingPosition(color);
        if (kingPos == null) return null;

        String enemyColor = color.equals("white") ? "black" : "white";
        int[] kingRC = Board.fromChessNotation(kingPos);

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor().equals(enemyColor)) {
                    if (p.isValidMove(r, c, kingRC[0], kingRC[1], board)) {
                        return Board.toChessNotation(r, c);
                    }
                }
            }
        }
        return null;
    }

}

package server;

public class ChessRoom {
    private final ClientHandler whitePlayer;
    private final ClientHandler blackPlayer;
    private boolean whiteReady = false;
    private boolean blackReady = false;

    public ChessRoom(ClientHandler white, ClientHandler black) {
        this.whitePlayer = white;
        this.blackPlayer = black;
    }

    public void start() {
        whitePlayer.sendMessage("START white");
        blackPlayer.sendMessage("START black");
        System.out.println("Game started between " + whitePlayer.getName() + " and " + blackPlayer.getName());
    }

    public synchronized void setReady(ClientHandler player) {
        if (player == whitePlayer) whiteReady = true;
        else if (player == blackPlayer) blackReady = true;

        System.out.println(player.getName() + " is READY");

        if (whiteReady && blackReady) {
            whitePlayer.sendMessage("TURN white");
            blackPlayer.sendMessage("TURN white");
            whiteReady = false;
            blackReady = false;
        }
    }

    public void forwardMove(ClientHandler from, String moveMessage) {
        ClientHandler to = (from == whitePlayer) ? blackPlayer : whitePlayer;
        to.sendMessage(moveMessage);
    }

    private boolean hasNotified = false;

    public void notifyOpponentDisconnected(ClientHandler leaver) {
        if (hasNotified) return;
        hasNotified = true;

        ClientHandler remaining = (leaver == whitePlayer) ? blackPlayer : whitePlayer;
        if (remaining != null) {
            System.out.println("[INFO] " + leaver.getName() + " has disconnected. Notifying " + remaining.getName() + "...");
            remaining.sendMessage("OPPONENT_LEFT");
        }
    }

}

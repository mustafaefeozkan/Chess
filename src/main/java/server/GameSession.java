
package server;

public class GameSession {
    private final ClientHandler whitePlayer;
    private final ClientHandler blackPlayer;

    public GameSession(ClientHandler white, ClientHandler black) {
        this.whitePlayer = white;
        this.blackPlayer = black;
    }

    public void forwardMessage(ClientHandler sender, String message) {
        System.out.println(sender.getName() + " ➝ " + message);
        if (sender == whitePlayer) {
            blackPlayer.sendMessage(message);
        } else if (sender == blackPlayer) {
            whitePlayer.sendMessage(message);
        }
    }
}

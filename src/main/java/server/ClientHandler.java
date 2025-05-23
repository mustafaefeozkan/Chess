package server;

import java.io.*;
import java.net.Socket;

/** Handles communication between the server and a single connected client. */
public class ClientHandler implements Runnable {
    private static int userCount = 1;
    private final String name;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private ChessRoom room;

    // Initializes the client handler and assigns a unique username.
    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.name = "USER" + userCount++;
    }

    // Returns the client's name.
    public String getName() {
        return name;
    }

    // Sends a message to the client.
    public void sendMessage(String msg) {
        out.println(msg);
    }

    // Sets the game room the client belongs to.
    public void setRoom(ChessRoom room) {
        this.room = room;
    }

    // Listens for messages from the client and processes them.
    @Override
    public void run() {
        System.out.println("[INFO] " + name + " connected.");
        ServerMain.joinLobby(this);

        try {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("[INFO] " + name + " says: " + line);

                if (line.equals("READY") && room != null) {
                    room.setReady(this);
                } else if (line.startsWith("MOVE") && room != null) {
                    room.forwardMove(this, line);
                } else if (line.equals("EXIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("[INFO] " + name + " connection error: " + e.getMessage());
        } finally {
            System.out.println("[INFO] " + name + " has disconnected.");
            try {
                if (room != null) {
                    room.notifyOpponentDisconnected(this);
                }
                socket.close();
            } catch (IOException e) {
                System.out.println("[INFO] Error closing socket for " + name + ": " + e.getMessage());
            }
        }
    }
}

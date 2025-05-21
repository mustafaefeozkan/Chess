package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private static int userCount = 1;
    private final String name;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private ChessRoom room;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.name = "USER" + userCount++;
    }

    public String getName() {
        return name;
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public void setRoom(ChessRoom room) {
        this.room = room;
    }

    @Override
    public void run() {
        System.out.println(name + " connected.");
        ServerMain.joinLobby(this);
        try {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(name + " says: " + line);
                if (line.equals("READY") && room != null) {
                    room.setReady(this);
                } else if (line.startsWith("MOVE") && room != null) {
                    room.forwardMove(this, line);
                }
            }
        } catch (IOException e) {
            System.out.println(name + " disconnected.");
        }
    }
}

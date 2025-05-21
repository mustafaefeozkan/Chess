package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerMain {
    private static final int PORT = 5000;
    private static final List<ClientHandler> lobby = new CopyOnWriteArrayList<>();
    private static final List<ChessRoom> rooms = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("Server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                new Thread(handler).start();
            }
        }
    }

    public static synchronized void joinLobby(ClientHandler player) {
        lobby.add(player);
        System.out.println(player.getName() + " joined the lobby. Total: " + lobby.size());

        if (lobby.size() >= 2) {
            ClientHandler white = lobby.remove(0);
            ClientHandler black = lobby.remove(0);

            ChessRoom room = new ChessRoom(white, black);
            rooms.add(room);

            white.setRoom(room);
            black.setRoom(room);

            room.start();
        }
    }
}

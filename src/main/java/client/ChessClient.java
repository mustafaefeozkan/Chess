package client;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;
import javafx.application.Platform;

public class ChessClient {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private Consumer<String> messageHandler;

    public ChessClient(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        listen();
    }

    private void listen() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    String msg = line;
                    Platform.runLater(() -> {
                        System.out.println("[INFO] Message from server: " + msg);
                        if (messageHandler != null) {
                            messageHandler.accept(msg);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void send(String message) {
        out.println(message);
    }

    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }
}

package spring2020.mcon364.pong;

import java.io.IOException;
import java.net.ServerSocket;

class Server extends Connection {
    private ServerSocket server;

    Server() {
        try {
            server = new ServerSocket(12345, 100);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    void connect() throws IOException {
        socket = server.accept();
    }
}

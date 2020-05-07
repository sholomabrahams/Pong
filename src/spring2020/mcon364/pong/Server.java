package spring2020.mcon364.pong;

import java.io.IOException;
import java.net.ServerSocket;

class Server extends PongConnection {
    private ServerSocket server;

    Server(int port) {
        try {
            server = new ServerSocket(port, 100);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void connect() throws IOException {
        socket = server.accept();
    }
}

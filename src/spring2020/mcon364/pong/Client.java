package spring2020.mcon364.pong;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

class Client extends Connection {
    private final String HOST;
    private final int PORT;

    Client(String host, int port) {
        HOST = host;
        PORT = port;
    }

    void connect() throws IOException {
        socket = new Socket(InetAddress.getByName(HOST), 12345);
    }
}

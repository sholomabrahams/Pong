package spring2020.mcon364.pong;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

class Client extends Connection {
    private String chatServer;

    Client (String host) {
        chatServer = host;
    }

    void connect() throws IOException {
        System.out.println("Attempting connection");
        socket = new Socket(InetAddress.getByName(chatServer), 12345);
        System.out.println("Connected to: " + socket.getInetAddress().getHostName());
    }
}

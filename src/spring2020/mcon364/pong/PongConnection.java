package spring2020.mcon364.pong;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

abstract class PongConnection {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    Socket socket;

    abstract void connect() throws IOException;

    void sendData(Payload payload) {
        try {
            output.writeObject(payload);
            output.flush();
        } catch (IOException ioException) {
            System.out.println("Error writing object");
        }
    }

    void getStreams() throws IOException {
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socket.getInputStream());
        System.out.println("Got I/O streams");
    }

    Payload getInput() throws IOException, ClassNotFoundException {
        return (Payload) input.readObject();
    }

    void closeConnection() {
        System.out.println("Closing connection");
        try {
            output.close();
            input.close();
            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        System.exit(0);
    }
}

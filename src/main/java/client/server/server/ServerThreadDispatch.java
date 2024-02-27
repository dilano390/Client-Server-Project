package client.server.server;

import client.server.input.handlers.SocketReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerThreadDispatch extends Thread {
    public ServerSocket serverSocket;

    Thread serverThread;
    Socket clientSocket;
    public ServerThreadDispatch() {
    }


    public void initializeServer() throws java.io.IOException {
        serverSocket = new ServerSocket(52443);
        System.out.println(serverSocket.getLocalSocketAddress());
    }

    public void closeServer() throws java.io.IOException {
        serverSocket.close();
    }

    public void waitForSocket() throws IOException {
        System.out.println("Server waiting for connection");
        clientSocket = serverSocket.accept();
        System.out.println("Connected to " + clientSocket.getLocalSocketAddress());
        serverThread = new Thread(new ServerThread(clientSocket));
        serverThread.start();
    }



    @Override
    public void run() {
        try {
            initializeServer();
            while (!serverSocket.isClosed()) {
                waitForSocket();
            }

            closeServer();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

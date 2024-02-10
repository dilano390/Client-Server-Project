package ClientServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server extends Thread {
    public ServerSocket serverSocket;

    Socket clientSocket;
    SocketReader socketReader;
    Thread readerThread;
    int sleepTime = 500;
    int loopsSinceLastPing = 0;

    int loopLimit = 5;
    InputStream clientIs;
    OutputStream clientOs;

    String message = "\0";

    public Server() {
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
        clientIs = clientSocket.getInputStream();
        clientOs = clientSocket.getOutputStream();
        socketReader = new SocketReader(clientIs);
        System.out.println("Connected to " + clientSocket.getLocalSocketAddress());
    }

    public void listenToSocket() throws IOException, InterruptedException {
        readerThread = new Thread(socketReader);
        readerThread.start();
        while (loopsSinceLastPing <  loopLimit){
            clientOs.write(message.getBytes(StandardCharsets.UTF_8));
            Thread.sleep(sleepTime);
        }
        loopsSinceLastPing = 0;
        System.out.println("Connection timed out");
    }


    @Override
    public void run() {
        try {
            initializeServer();
            while (!serverSocket.isClosed()) {
                waitForSocket();
                listenToSocket();
            }

            closeServer();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}

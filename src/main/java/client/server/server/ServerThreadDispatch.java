package client.server.server;

import client.server.input.handlers.SocketReader;
import client.server.input.handlers.UserMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ServerThreadDispatch extends Thread {
    public ServerSocket serverSocket;

    int capacityMessageQueue = 50;
    BlockingQueue<UserMessage> MessageQueue = new ArrayBlockingQueue<UserMessage>(capacityMessageQueue);


    Thread serverThread;
    Socket clientSocket;

    public Map<String,Socket> users = new HashMap<String,Socket>();

    public ServerThreadDispatch() {
    }


    public void initializeServer() throws java.io.IOException {
        serverSocket = new ServerSocket(52443);
        System.out.println(serverSocket.getLocalSocketAddress());
    }
    //TODO RUN AN INPUT READER ON SERVER

    public void closeServer() throws java.io.IOException {
        serverSocket.close();
    }

    public void waitForSocket() throws IOException {
        System.out.println("Server waiting for connection");
        clientSocket = serverSocket.accept();
        System.out.println("Connected to " + clientSocket.getLocalSocketAddress());
        serverThread = new Thread(new ServerThread(clientSocket,MessageQueue,users));
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

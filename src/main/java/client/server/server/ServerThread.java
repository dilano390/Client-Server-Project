package client.server.server;

import client.server.MessageProcessor;
import client.server.input.handlers.SocketReader;
import client.server.input.handlers.UserMessage;
import client.server.MessageSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ServerThread extends Thread implements MessageProcessor {

    int sleepTime = 1000;
    int loopsSinceLastPing = 0;
    String pingPacket = "\0";
    String userId = null;
    int loopLimit = 5;
    Thread readerThread;
    Socket clientSocket;
    InputStream clientIs;
    OutputStream clientOs;
    SocketReader socketReader;

    boolean idSet = false;
    BlockingQueue<UserMessage> deliveryQueue;

    BlockingQueue<String> serverMessagesQueue;

    public ServerThread(Socket clientSocket, BlockingQueue<UserMessage> deliveryQueue, Map<String, Socket> users) throws IOException {
        this.clientSocket = clientSocket;
        clientIs = clientSocket.getInputStream();
        clientOs = clientSocket.getOutputStream();
        this.deliveryQueue = deliveryQueue;
        socketReader = new SocketReader(clientIs, this);
        serverMessagesQueue = new ArrayBlockingQueue<String>(15);
    }


    public void listenToSocket() throws IOException, InterruptedException {
        readerThread = new Thread(socketReader);
        readerThread.start();
        while (loopsSinceLastPing < loopLimit) {
            clientOs.write(pingPacket.getBytes(StandardCharsets.UTF_8));
            Thread.sleep(sleepTime);
        }
        loopsSinceLastPing = 0;
        System.out.println("Connection closed");
    }

    public void processMessage(String message) {
        if (!message.equals("\0")) {
            if (!idSet) {
                userId = message.trim();
                idSet = true;
                System.out.println("Set userId to " + userId);
            } else {
                System.out.println(userId + ": \"" + message + "\"");
                if (deliveryQueue != null) {
                    try {
                        deliveryQueue.put(new UserMessage(message, userId));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Connection established");
            listenToSocket();
        } catch (IOException | InterruptedException e) {
            if (e instanceof java.net.SocketException) {
                System.out.println("Socket Disconnected/ Socket Exception");

            } else {
                throw new RuntimeException(e);
            }
        }
        System.out.println(deliveryQueue.toString());

    }
}

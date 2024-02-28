package client.server.client;

import client.server.MessageProcessor;
import client.server.input.handlers.InputReader;
import client.server.input.handlers.SocketReader;
import client.server.MessageSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client extends Thread implements MessageSender, MessageProcessor {

    Socket clientSocket;
    OutputStream os;
    InputStream is;

    SocketReader socketReader;

    InputReader inputReader;
    Thread readerThread;

    Thread inputThread;

    boolean active = true;

    BlockingQueue<String> inputQueue = new ArrayBlockingQueue<String>(15);
    int sleepTime = 1000;
    int loopsSinceLastPing = 0;

    int loopLimit = 5;

    boolean userIdSet = false;
    String userId = null;

    String pingPacket = "\0";

    Client() {
    }

    public void initializeClient() throws java.io.IOException {
        clientSocket = new Socket("0.0.0.0", 52443);
        os = clientSocket.getOutputStream();
        is = clientSocket.getInputStream();
        socketReader = new SocketReader(is, this);
        inputReader = new InputReader(this);
        System.out.println("You have connected to the server");
    }

    public void closeClient() throws java.io.IOException {
        clientSocket.close();

        System.out.println("Socket has been closed: " + clientSocket.isClosed());
    }

    public void processMessage(String message) {
        if (!message.equals("\0")) {
            System.out.println("recieved: \"" + message + "\"");
        }
    }

    public void sendMessage(String message) {
        try {
            inputQueue.put(message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNextMessageToSend() {
        if(userIdSet){
            return "Type your message: ";
        }
        userIdSet = true;
        return "Enter your desired Username: ";
        //TODO make the client communicate to the server about setting username
        // Right now it will allow double usernames since there is no communication
    }

    public boolean isConnectionActive() {
        return active;
    }


    @Override
    public void run() {
        try {
            initializeClient();
            readerThread = new Thread(socketReader);
            readerThread.start();
            inputThread = new Thread(inputReader);
            inputThread.start();

            while (loopsSinceLastPing < loopLimit) {
                checkForAndSendMessages();
                if(!active) break;
            }
            System.out.println("Connection closed is timeout:" + active);
            active = false;
            closeClient();

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void checkForAndSendMessages() throws IOException, InterruptedException {
        String message;
        if (!inputQueue.isEmpty()) {
            message = inputQueue.poll();
            if(message.equals("Quit")){
                active = false;
            }
        } else {
            message = pingPacket;
        }
        os.write(message.getBytes(StandardCharsets.UTF_8));
        Thread.sleep(sleepTime);
    }

}

package client.server.client;

import client.server.input.handlers.InputReader;
import client.server.input.handlers.SocketReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client extends Thread {

    Socket clientSocket;
    OutputStream os;
    InputStream is;

    SocketReader socketReader;

    InputReader inputReader;
    Thread readerThread;

    Thread inputThread;

    BlockingQueue<String> inputQueue = new ArrayBlockingQueue<String>(5);
    int sleepTime = 1000;
    int loopsSinceLastPing = 0;

    int loopLimit = 5;

    String pingPacket = "\0";

    Client() {
    }

    public void initializeClient() throws java.io.IOException {
        clientSocket = new Socket("0.0.0.0", 52443);
        os = clientSocket.getOutputStream();
        is = clientSocket.getInputStream();
        socketReader = new SocketReader(is);
        inputReader = new InputReader(inputQueue);
        System.out.println("You have connected to the server");
    }

    public void closeClient() throws java.io.IOException {
        clientSocket.close();

        System.out.println("Socket has been closed: " + clientSocket.isClosed());
    }

    @Override
    public void run() {
        try {
            initializeClient();
            readerThread = new Thread(socketReader);
            readerThread.start();
            inputThread = new Thread(inputReader);
            inputThread.start();

            String message;

            while (loopsSinceLastPing <  loopLimit){


                if(!inputQueue.isEmpty()){
                    message = inputQueue.poll();
                    os.write(message.getBytes(StandardCharsets.UTF_8));
                    Thread.sleep(500);
                }
                else{
                    os.write(pingPacket.getBytes(StandardCharsets.UTF_8));
                }
                Thread.sleep(sleepTime);

            }

            System.out.println("Connection timeout");
            closeClient();

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }


    }

}

package ClientServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client extends Thread {

    Socket clientSocket;
    OutputStream os;
    InputStream is;

    SocketReader socketReader;

    InputReader inputReader;
    Thread readerThread;

    Thread inputThread;

    ByteArrayOutputStream userInputStream = new ByteArrayOutputStream();

    int sleepTime = 500;
    int loopsSinceLastPing = 0;

    int loopLimit = 5;

    String message = "\0";

    Client() {
    }

    public void initializeClient() throws java.io.IOException {
        clientSocket = new Socket("0.0.0.0", 52443);
        os = clientSocket.getOutputStream();
        is = clientSocket.getInputStream();
        inputReader = new InputReader(userInputStream);
        socketReader = new SocketReader(is);
    }

    public void closeClient() throws java.io.IOException {
        clientSocket.close();

        System.out.println("Socket has been closed: " + clientSocket.isClosed());
    }

    @Override
    public void run() {
        try {
            initializeClient();
            System.out.println(clientSocket.getLocalSocketAddress());
            readerThread = new Thread(socketReader);
            readerThread.start();
            inputThread = new Thread(inputReader);
            inputThread.start();

            while (loopsSinceLastPing < loopLimit) {
                message = userInputStream.toString();
                if (message.length() > 0) {
                    System.out.println("Sending: " + message);
                    os.write(message.getBytes(StandardCharsets.UTF_8));
                    message = "\0";
                }
                os.write(message.getBytes(StandardCharsets.UTF_8));
                Thread.sleep(sleepTime);
            }
            System.out.println("Connection timeout");
            closeClient();

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }


    }

}

//message = inputScanner.nextLine();
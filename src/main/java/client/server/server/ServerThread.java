package client.server.server;

import client.server.input.handlers.SocketReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ServerThread extends Thread{

    int sleepTime = 1000;
    int loopsSinceLastPing = 0;
    String pingPacket = "\0";
    int loopLimit = 5;
    Thread readerThread;
    Socket clientSocket;
    InputStream clientIs;
    OutputStream clientOs;
    SocketReader socketReader;
    public ServerThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        clientIs = clientSocket.getInputStream();
        clientOs = clientSocket.getOutputStream();
        socketReader = new SocketReader(clientIs);
    }


    public void listenToSocket() throws IOException, InterruptedException {
        readerThread = new Thread(socketReader);
        readerThread.start();
        while (loopsSinceLastPing <  loopLimit){
            clientOs.write(pingPacket.getBytes(StandardCharsets.UTF_8));
            Thread.sleep(sleepTime);
        }
        loopsSinceLastPing = 0;
        System.out.println("Connection closed");
    }


    @Override
    public void run() {
        try{
            System.out.println("Connection established");
            listenToSocket();
        } catch (IOException | InterruptedException e) {
            if(e instanceof java.net.SocketException){
                System.out.println("Socket Disconnected/ Socket Exception");

            }
            else{
                throw new RuntimeException(e);
            }
        }

    }
}

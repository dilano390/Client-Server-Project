package client.server.server;


import java.io.IOException;

public class ServerRunnable {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerThreadDispatch mainServer = new ServerThreadDispatch();
        mainServer.start();
    }
}
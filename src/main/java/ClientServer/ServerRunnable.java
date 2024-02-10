package ClientServer;


import java.io.IOException;

public class ServerRunnable {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server mainServer = new Server();
        mainServer.start();
    }
}
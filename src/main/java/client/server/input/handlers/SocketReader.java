package client.server.input.handlers;

import client.server.MessageProcessor;
import client.server.server.ServerThread;
import client.server.MessageSender;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class SocketReader implements Runnable{

    MessageProcessor messageProcessor;
    int loopsSinceLastPing = 0;

    int loopLimit = 500;

    int sleepTime = 500;
    InputStream is;

    public SocketReader(InputStream is, MessageProcessor messageProcessor){
        this.is = is;
        this.messageProcessor = messageProcessor;
    }
    private void readSocket() throws IOException, InterruptedException {
        while(loopsSinceLastPing <  loopLimit){
            if (is.available() > 0) {
                    byte[] readBytes = is.readNBytes(is.available());
                    String message = new String(readBytes, StandardCharsets.UTF_8);
                messageProcessor.processMessage(message);
                    loopsSinceLastPing = 0;
                }
            else {
                loopsSinceLastPing += 1;
            }

            Thread.sleep(sleepTime);
        }
    }


    @Override
    public void run() {
        try{
            readSocket();
            System.out.println("Exiting Socket Reader");
        }
        catch (IOException | InterruptedException e){
            throw new RuntimeException(e);
        }

    }
}

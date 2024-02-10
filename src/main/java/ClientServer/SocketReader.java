package ClientServer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class SocketReader implements Runnable{

    int loopsSinceLastPing = 0;

    int loopLimit = 5;

    int sleepTime = 500;
    InputStream is;

    public SocketReader(InputStream is) {
        this.is = is;
    }

    private void readSocket() throws IOException, InterruptedException {
        while(loopsSinceLastPing <  loopLimit){
            if (is.available() > 0) {
                byte[] readBytes = is.readNBytes(is.available());
                String message = new String(readBytes, StandardCharsets.UTF_8);
                if(!message.equals("\0")) System.out.println("Received: \"" + message + "\"");

                loopsSinceLastPing = 0;
            } else {
                System.out.println("Did not receive");
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

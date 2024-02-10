package client.server.input.handlers;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class InputReader implements Runnable {
    BlockingQueue<String> inputQueue;
    public InputReader(BlockingQueue<String> inputQueue){
        this.inputQueue = inputQueue;
    }

    Scanner inputScanner = new Scanner(System.in);



    @Override
    public void run() {
        String userInput;
        Boolean reading = true;
            while(reading){
                System.out.print("Enter your message: ");
                userInput = inputScanner.nextLine();
                try {
                    if(!userInput.isEmpty()) inputQueue.put(userInput);
                    userInput = "";
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(Objects.equals(userInput, "quit")){
                    System.out.println("Quit command received");
                    reading = false;
                }
            }
    }
}

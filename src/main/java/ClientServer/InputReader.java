package ClientServer;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class InputReader implements Runnable {
    ByteArrayOutputStream outStream;
    public InputReader(ByteArrayOutputStream outStream){
        this.outStream = outStream;
    }

    Scanner inputScanner = new Scanner(System.in);



    @Override
    public void run() {
        String userInput;
        Boolean reading = true;
            while(reading){
                userInput = inputScanner.nextLine();
                try {
                    outStream.write(userInput.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(Objects.equals(userInput, "\0Quit\0")) reading = false;
            }
    }
}

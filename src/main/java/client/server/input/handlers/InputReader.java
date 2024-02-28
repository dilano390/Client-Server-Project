package client.server.input.handlers;

import client.server.MessageSender;

import java.util.Scanner;

public class InputReader implements Runnable {

    MessageSender socketInteractable;

    public InputReader(MessageSender socketInteractable){
        this.socketInteractable = socketInteractable;
    }

    Scanner inputScanner = new Scanner(System.in);



    @Override
    public void run() {
        String userInput;
        Boolean reading = true;
            while(reading){
                System.out.print(socketInteractable.getNextMessageToSend());
                userInput = inputScanner.nextLine();
                try {
                    if(!userInput.isEmpty()) socketInteractable.sendMessage(userInput);
//                    userInput = "";
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
              reading = socketInteractable.isConnectionActive();
            }
    }
}

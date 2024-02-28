package client.server;

public interface MessageSender {
    void sendMessage(String message);

    String getNextMessageToSend(); //get the message that should be sent to the user in current context

    boolean isConnectionActive(); //return true if still open/active

}

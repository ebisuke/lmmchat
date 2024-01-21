package jp.mochisuke.lmmchat.chat;

public class ChatPreface implements IChatPreface {
    private final String message;

    public ChatPreface(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }
}

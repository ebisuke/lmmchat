package jp.mochisuke.lmmchat.chat;

public interface IChatBase {
    //async generate chat message by message
    ChatData generateChatMessage(ChatGenerationRequest req, ChatHistory chatHistory) throws InterruptedException;

}

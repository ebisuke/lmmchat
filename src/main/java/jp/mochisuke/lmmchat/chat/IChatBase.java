package jp.mochisuke.lmmchat.chat;

import io.reactivex.annotations.Nullable;

public interface IChatBase {
    //async generate chat message by message

    String generateChatMessage(ChatGenerationRequest req,ChatHistory chatHistory,@Nullable String supportMessage) throws InterruptedException;
    ChatData addConversationMessage(ChatGenerationRequest req,ChatHistory chatHistory,String message,Object optional) throws InterruptedException;
    ChatData conversationMessage(ChatGenerationRequest req,ChatHistory chatHistory,@Nullable String supportMessage) throws InterruptedException;
}

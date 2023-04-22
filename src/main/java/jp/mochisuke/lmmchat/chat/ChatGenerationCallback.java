package jp.mochisuke.lmmchat.chat;

public interface ChatGenerationCallback{
    void onChatGenerated(ChatGenerationRequest request,ChatData response);
}
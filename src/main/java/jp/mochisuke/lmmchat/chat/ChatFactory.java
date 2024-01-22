package jp.mochisuke.lmmchat.chat;

import jp.mochisuke.lmmchat.LMMChatConfig;

import static jp.mochisuke.lmmchat.LMMChatConfig.getEngine;

public class ChatFactory {
    public static IChatBase createChatBase(){
        switch (getEngine()){
            case OPENAI:
                return new OpenAIChat(LMMChatConfig.getApiKey(),null);
            case GEMINI:
                return new OpenAIChat(LMMChatConfig.getGeminiApiKey(),LMMChatConfig.getGeminiProxyBaseUrl());
            default:
                throw new RuntimeException("invalid engine");
        }
    }
}

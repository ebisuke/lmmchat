package jp.mochisuke.lmmchat.chat;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class ChatThread {
    Thread thread;
    IChatBase chatContoller;
    public static Logger logger = LogUtils.getLogger();
    Queue<ChatData> chatDataQueue;
    ConcurrentHashMap<String, ChatHistory> chatHistory;

    Queue<ChatGenerationRequest> chatGenerationRequestQueue;

    public ChatThread() {
        thread = new Thread(this::routine);
        chatContoller = new OpenAIChat();
        chatDataQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();
        chatGenerationRequestQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();
        chatHistory = new ConcurrentHashMap<>();
        start();
    }

    public void start() {
        thread.start();
    }

    public void routine() {
        try {
            while (true) {

                if (chatGenerationRequestQueue.size() > 0) {
                    ChatGenerationRequest chatData = chatGenerationRequestQueue.poll();
                    logger.info("chatData: " + chatData.getCallerMessage());
                    //pickup history if available
                    ChatHistory history;
                    if (chatHistory.containsKey(String.valueOf(chatData.getCaller().getId()))) {
                        history = chatHistory.get(String.valueOf(chatData.getCaller().getId()));
                    } else {
                        history = new ChatHistory();
                        chatHistory.put(String.valueOf(chatData.getCaller().getId()), history);
                    }

                    var ret = chatContoller.generateChatMessage(chatData, history);
                    //send message to minecraft
                    chatDataQueue.add(ret);
                }
                Thread.sleep(1000);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void PushRequest(ChatGenerationRequest chatGenerationRequest){
        chatGenerationRequestQueue.add(chatGenerationRequest);
    }
    public ChatData PopChatData(){
        return chatDataQueue.poll();
    }
}

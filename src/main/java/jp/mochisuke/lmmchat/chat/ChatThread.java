package jp.mochisuke.lmmchat.chat;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChatConfig;
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
    boolean isEnabled = true;
    public ChatThread() {
        thread = new Thread(this::routine);
        chatContoller = new OpenAIChat();
        chatDataQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();
        chatGenerationRequestQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();
        chatHistory = new ConcurrentHashMap<>();
        start();
    }
    public void disable(){
        //clear all chat queue
        chatDataQueue.clear();
        chatGenerationRequestQueue.clear();
        isEnabled=false;
    }
    public void enable(){
        isEnabled=true;
    }
    public void start() {
        thread.start();
    }

    public void routine() {

        while (true) {
            try {
                if(!isEnabled){
                    Thread.sleep(1000);
                    continue;
                }
                if (chatGenerationRequestQueue.size() > 0) {
                    ChatGenerationRequest chatData = chatGenerationRequestQueue.poll();
                    logger.info("chatData: " + chatData.getCallerMessage());
                    //pickup history if available
                    ChatHistory history;
                    if (chatHistory.containsKey(String.valueOf(chatData.getCallee().getId()))) {
                        history = chatHistory.get(String.valueOf(chatData.getCallee().getId()));
                    } else {
                        history = new ChatHistory();
                        chatHistory.put(String.valueOf(chatData.getCallee().getId()), history);
                    }
                    //if callee is died, clear history
                    if (chatData.getCallee()==null && chatData.getCallee().isDeadOrDying()) {
                        history.chatDataList.clear();
                        //remove from history if exist
                        chatHistory.remove(String.valueOf(chatData.getCallee().getId()));
                        continue;

                    }
                    try {
                        var ret = chatContoller.generateChatMessage(chatData, history);
                        if (chatData.getCallee()==null && chatData.getCallee().isDeadOrDying()) {
                            //remove from history if exist
                            history.chatDataList.clear();
                            chatHistory.remove(String.valueOf(chatData.getCallee().getId()));
                            continue;
                        }
                        if(ret==null){
                           //retry
                            PushRequest(chatData);
                        }
                        //send message to minecraft
                        chatDataQueue.add(ret);
                    } catch (TooLongConversationException e) {
                        //half history
                        logger.info("conversation is too long. compacting history..");
                        history.chatDataList.subList(0, history.chatDataList.size() / 2).clear();
                        //retry
                        chatGenerationRequestQueue.add(chatData);

                        Thread.sleep(1000);
                        continue;
                    }


                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    public void PushRequest (ChatGenerationRequest chatGenerationRequest){
        chatGenerationRequestQueue.add(chatGenerationRequest);
        while(chatGenerationRequestQueue.size()> LMMChatConfig.getMaxQueueSize()){
            chatGenerationRequestQueue.poll();
        }
    }
    public ChatData PopChatData () {
        return chatDataQueue.poll();
    }
}

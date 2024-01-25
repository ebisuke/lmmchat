package jp.mochisuke.lmmchat.chat;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChatConfig;
import jp.mochisuke.lmmchat.embedding.EmbeddingTask;
import jp.mochisuke.lmmchat.embedding.OpenAIEmbedder;
import jp.mochisuke.lmmchat.sounds.SoundPlayer;
import net.minecraft.world.entity.TamableAnimal;
import org.slf4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class ChatManager {
    Thread thread;
    IChatBase chatContoller;
    public static Logger logger = LogUtils.getLogger();
    Queue<ChatData> chatDataQueue;
    ConcurrentHashMap<String, ChatHistory> chatHistory;
    Queue<ChatGenerationRequest> chatGenerationRequestQueue;
    EmbeddingTask embeddingTask;
    SoundPlayer soundPlayer;
    boolean isEnabled = true;
    public ChatManager() {
        thread = new Thread(this::routine);
        chatContoller = ChatFactory.createChatBase();
        chatDataQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();
        chatGenerationRequestQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();
        chatHistory = new ConcurrentHashMap<>();
        embeddingTask = new EmbeddingTask(new OpenAIEmbedder());

        start();
    }
    public void disable(){
        //clear all chat queue
        chatContoller = ChatFactory.createChatBase();
        chatDataQueue.clear();
        chatGenerationRequestQueue.clear();
        isEnabled=false;
    }
    public void clearAll(){
        //clear all chat queue
        chatContoller = ChatFactory.createChatBase();
        chatDataQueue.clear();
        chatGenerationRequestQueue.clear();
        chatHistory.clear();
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
                        if(LMMChatConfig.isEnableEmbeddeding()){
                            //1st pass
                            var ret= chatContoller.generateChatMessage(chatData, history,null);
                            if (ret==null){
                                //retry
                                PushRequest(chatData);
                                continue;
                            }
                            // search similar message
                            var similarMessage = embeddingTask.searchSimilar(ret,LMMChatConfig.getEmbeddingInjectCount());
                            if(similarMessage!=null && similarMessage.size()>0) {
                                //2nd pass
                                logger.info("1st pass: " + ret);
                                String processedMessage="";
                                for(int i=0;i<similarMessage.size();i++) {
                                    var elem=similarMessage.get(i);
                                    var elem_a=elem.getA();
                                    var elem_b=elem.getB();
                                    if (elem instanceof BiFunction<?,?,?>) {
                                        TamableAnimal animal;
                                        if (chatData.getCallee() instanceof TamableAnimal) {
                                            animal = (TamableAnimal) chatData.getCallee();
                                        } else {
                                            animal = null;
                                        }
                                        processedMessage += ((BiFunction<TamableAnimal, Object[], String>) elem).apply(animal, elem_b)+"\n";
                                    } else {
                                        processedMessage += elem_a +"\n";
                                    }
                                }
                                processedMessage=processedMessage.trim();
                                logger.info("similar message: " + processedMessage);
                                processedMessage="以下はあなたがやりたいことに対する補足情報です。必要が無ければ無視しても構いません。\n"+processedMessage;
                                var ret2 = chatContoller.conversationMessage(chatData, history, processedMessage);
                                if(ret2==null){
                                    //retry
                                    PushRequest(chatData);
                                    continue;
                                }
                                logger.info("2nd pass: " + ret2.getCalleeMessage());
                                if (chatData.getCallee() == null && chatData.getCallee().isDeadOrDying()) {
                                    //remove from history if exist
                                    history.chatDataList.clear();
                                    chatHistory.remove(String.valueOf(chatData.getCallee().getId()));
                                    continue;
                                }
                                if (ret == null) {
                                    //retry
                                    PushRequest(chatData);
                                }
                                //send message to minecraft
                                chatDataQueue.add(ret2);
                            }else{
                                if (chatData.getCallee() == null && chatData.getCallee().isDeadOrDying()) {
                                    //remove from history if exist
                                    history.chatDataList.clear();
                                    chatHistory.remove(String.valueOf(chatData.getCallee().getId()));
                                    continue;
                                }
                                if (ret == null) {
                                    //retry
                                    PushRequest(chatData);
                                }
                                //
                                ChatData data= chatContoller.addConversationMessage(chatData, history, ret,null);
                                //send message to minecraft
                                chatDataQueue.add(data);
                            }
                        }else {
                                var ret = chatContoller.conversationMessage(chatData, history, null);
                                if(ret==null){
                                    //retry
                                    PushRequest(chatData);
                                    continue;
                                }
                                if (chatData.getCallee() == null && chatData.getCallee().isDeadOrDying()) {
                                    //remove from history if exist
                                    history.chatDataList.clear();
                                    chatHistory.remove(String.valueOf(chatData.getCallee().getId()));
                                    continue;
                                }
                                if (ret == null) {
                                    //retry
                                    PushRequest(chatData);
                                }
                                //send message to minecraft
                                chatDataQueue.add(ret);
                            }

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

package jp.mochisuke.lmmchat.chat;


import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.annotations.Nullable;
import jp.mochisuke.lmmchat.LMMChatConfig;
import net.minecraft.world.entity.Entity;
import retrofit2.adapter.rxjava2.HttpException;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Vector;

public class OpenAIChat implements IChatBase{

    OpenAiService service;
    public OpenAIChat(String apikey,@Nullable String baseurl){
        if (baseurl==null){
            baseurl="https://api.openai.com";
        }
        service = new OpenAiService(apikey, Duration.ofSeconds(LMMChatConfig.getApiTimeout()),baseurl);
    }

    public Vector<ChatMessage> convertChatHistory(String callerId, ChatHistory chatHistory){


        Vector<ChatMessage> chatMessages = new Vector<>();
        int i=0;
        for(ChatData chatData : chatHistory.getChatDataList()){
            var chatMessage = new ChatMessage();
            //caller
            if(chatData.isCallerIsSystem() && i==0){
                chatMessage.setRole(ChatMessageRole.SYSTEM.value());
            }else {
                chatMessage.setRole( ChatMessageRole.USER.value());
            }
            chatMessage.setContent(chatData.getCallerMessage());

            chatMessages.add(chatMessage);
            chatMessage = new ChatMessage();
            //callee
            if(chatData.isCalleeIsSystem()){
                chatMessage.setRole(ChatMessageRole.ASSISTANT.value());
            }else {
                chatMessage.setRole( ChatMessageRole.ASSISTANT.value());
            }
            chatMessage.setContent(chatData.getCalleeMessage());


            //add
            chatMessages.add(chatMessage);
            i++;
        }
        return chatMessages;
    }

    @Override
    public String generateChatMessage(ChatGenerationRequest req,ChatHistory chatHistory,@Nullable String supportMessage) throws InterruptedException {
        for(int retry=0;retry<10;retry++) {
            try {        // generate preface
                var chatMessages = this.convertChatHistory(String.valueOf(req.getCaller()!=null?req.getCaller().getId():-1), chatHistory);
                //generate preface

                ChatMessage prefacechat = new ChatMessage();
                prefacechat.setRole(ChatMessageRole.SYSTEM.value());
                prefacechat.setContent(req.getPreface().getMessage() );
                chatMessages.insertElementAt(prefacechat, 0);

                // add req
                ChatMessage reqchat = new ChatMessage();
                reqchat.setRole(req.getCaller()!=null?ChatMessageRole.USER.value():ChatMessageRole.USER.value());
                reqchat.setContent(req.getCallerMessage());
                chatMessages.add(reqchat);
                // add support message
                if(supportMessage!=null){
                    ChatMessage supportchat = new ChatMessage();
                    supportchat.setRole(ChatMessageRole.USER.value());
                    supportchat.setContent(supportMessage);
                    chatMessages.add(supportchat);
                }


                // prepare REST request
                ChatCompletionRequest request = ChatCompletionRequest.builder()
                        .model(LMMChatConfig.getModelName())
                        .messages(chatMessages)
                        .frequencyPenalty(0.02)
                        .presencePenalty(0.6)
                        .temperature(0.7)
                        .maxTokens(LMMChatConfig.getMaxTokens())
                        .stream(false)
                        .user("lmmchat")

                        .n(1)
                        .topP(1.0)
                        .build();
                //change timeout okhttp3




                var ret = service.createChatCompletion(request);



                return ret.getChoices().get(0).getMessage().getContent();
            }catch(OpenAiHttpException ex) {
                //if(ex.getMessage().contains("This model's maximum context length")){
                    throw new TooLongConversationException();
                //}
            }catch (RuntimeException e) {
                if (e.getCause() instanceof SocketTimeoutException) {
                    System.out.println("SocketTimeoutException");
                    Thread.sleep(2000);
                    continue;
                }
                if (e.getCause() instanceof HttpException) {
                    Thread.sleep(2000);
                    continue;
                }

                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public ChatData addConversationMessage(ChatGenerationRequest req,ChatHistory chatHistory, String message,Object optional) throws InterruptedException {
        Entity ent= req.getCaller() !=null? req.getCaller():req.getCallee();
        OpenAIChatData chatData = new OpenAIChatData(
                req.getCallerMessage(),
                message,
                //current minecraft timeofday
                ent.level().getGameTime(),
                req.getCaller(),
                req.getCallee(),
                req.isCallerIsSystem(),
                req.isCalleeIsSystem(),
                req.getConversationCount() + 1
        );

        //save
        chatHistory.Add(chatData);
        return chatData;
    }

    @Override
    public ChatData conversationMessage(ChatGenerationRequest req,ChatHistory chatHistory,@Nullable String supportMessage) throws InterruptedException {

                //change timeout okhttp3




        var ret = generateChatMessage(req,chatHistory,supportMessage);
        if(ret==null){
            return null;
        }
        // add to chat history
        Entity ent= req.getCaller() !=null? req.getCaller():req.getCallee();
        OpenAIChatData chatData = new OpenAIChatData(
                req.getCallerMessage(),
                ret,
                //current minecraft timeofday
                ent.level().getGameTime(),
                req.getCaller(),
                req.getCallee(),
                req.isCallerIsSystem(),
                req.isCalleeIsSystem(),
                req.getConversationCount() + 1
        );


        //add to history
        chatHistory.Add(chatData);
        return chatData;



    }
}

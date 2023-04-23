package jp.mochisuke.lmmchat.chat;


import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import jp.mochisuke.lmmchat.LMMChatConfig;
import net.minecraft.world.entity.Entity;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Objects;
import java.util.Vector;

public class OpenAIChat implements IChatBase{

    OpenAiService service;
    public OpenAIChat(){
        service = new OpenAiService(LMMChatConfig.getApiKey(), Duration.ofSeconds(LMMChatConfig.getApiTimeout()));
    }
    public Vector<ChatMessage> convertChatHistory(String callerId, ChatHistory chatHistory){


        Vector<ChatMessage> chatMessages = new Vector<>();
        for(ChatData chatData : chatHistory.getChatDataList()){
            var chatMessage = new ChatMessage();
            //caller
            if(chatData.isCallerIsSystem()){
                chatMessage.setRole(ChatMessageRole.SYSTEM.value());
            }else {
                chatMessage.setRole((Objects.equals(String.valueOf(chatData.getCaller().getId()), callerId) ?
                        ChatMessageRole.ASSISTANT.value() : ChatMessageRole.USER.value()));
            }
            chatMessage.setContent(chatData.getCallerMessage());


            //callee
            if(chatData.isCalleeIsSystem()){
                chatMessage.setRole(ChatMessageRole.SYSTEM.value());
            }else {
                chatMessage.setRole(Objects.equals(String.valueOf(chatData.getCallee().getId()), callerId) ?
                        ChatMessageRole.USER.value() : ChatMessageRole.ASSISTANT.value());
            }
            chatMessage.setContent(chatData.getCalleeMessage());


            //add
            chatMessages.add(chatMessage);
        }
        return chatMessages;
    }

    @Override
    public ChatData generateChatMessage(ChatGenerationRequest req,ChatHistory chatHistory) throws InterruptedException {
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
                reqchat.setRole(req.getCaller()!=null?ChatMessageRole.USER.value():ChatMessageRole.SYSTEM.value());
                reqchat.setContent(req.getCallerMessage());
                chatMessages.add(reqchat);
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

                // add to chat history
                Entity ent= req.getCaller() !=null? req.getCaller():req.getCallee();
                OpenAIChatData chatData = new OpenAIChatData(
                        req.getCallerMessage(),
                        ret.getChoices().get(0).getMessage().getContent(),

                        //current minecraft timeofday
                        ent.getLevel().getGameTime(),
                        req.getCaller(),
                        req.getCallee(),
                        req.isCallerIsSystem(),
                        req.isCalleeIsSystem(),
                        req.getConversationCount() + 1,
                        (int) ret.getUsage().getTotalTokens(),
                        ret,
                        request
                );

                //save
                chatHistory.Add(chatData);
                return chatData;
            }catch(OpenAiHttpException ex) {
                if(ex.getMessage().contains("This model's maximum context length")){
                    throw new TooLongConversationException();
                }
            }catch (RuntimeException e) {
                if (e.getCause() instanceof SocketTimeoutException) {
                    System.out.println("SocketTimeoutException");
                    Thread.sleep(1000);
                    continue;
                }


                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}

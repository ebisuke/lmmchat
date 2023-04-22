package jp.mochisuke.lmmchat.chat;


import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import jp.mochisuke.lmmchat.LMMChatConfig;
import net.minecraft.client.Minecraft;

import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.Vector;

public class OpenAIChat implements IChatBase{

    OpenAiService service;
    public OpenAIChat(){
        service = new OpenAiService(LMMChatConfig.getApiKey());
    }
    public Vector<ChatMessage> convertChatHistory(String callerId, ChatHistory chatHistory){


        Vector<ChatMessage> chatMessages = new Vector<>();
        for(ChatData chatData : chatHistory.getChatDataList()){
            var chatMessage = new ChatMessage();
            //caller
            chatMessage.setRole((Objects.equals(String.valueOf(chatData.getCaller().getId()), callerId) ?
                    ChatMessageRole.SYSTEM.value() : ChatMessageRole.USER.value()));
            chatMessage.setContent(chatData.getCallerMessage());


            //callee
            chatMessage.setRole(Objects.equals(String.valueOf( chatData.getCallee().getId()), callerId) ?
                    ChatMessageRole.USER.value(): ChatMessageRole.SYSTEM.value());
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
                ChatMessage preface = new ChatMessage();
                preface.setRole(ChatMessageRole.ASSISTANT.value());
                preface.setContent(LMMChatConfig.getPreface());

                var chatMessages = this.convertChatHistory(String.valueOf(req.getCaller().getId()), chatHistory);
                chatMessages.insertElementAt(preface, 0);

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

                var ret = service.createChatCompletion(request);


                // add to chat history

                OpenAIChatData chatData = new OpenAIChatData(
                        req.getCallerMessage(),
                        ret.getChoices().get(0).getMessage().getContent(),

                        //current minecraft timeofday
                        Minecraft.getInstance().player.getLevel().getGameTime(),
                        req.getCaller(),
                        req.getCallee(),
                        req.getConversationCount() + 1,
                        (int) ret.getUsage().getTotalTokens(),
                        ret,
                        request
                );

                //save
                chatHistory.Add(chatData);
                return chatData;
            } catch (RuntimeException e) {
                if (e.getCause() instanceof SocketTimeoutException) {
                    System.out.println("SocketTimeoutException");
                    Thread.sleep(1000);
                    continue;
                }
                e.printStackTrace();
                return null;
            }
        }
//        OpenAIController controller = new OpenAIController();
//        String message=controller.generate(chatHistory,req);
//
//        ChatData chatData = new ChatData(
//                req.getCallerMessage(),
//                message,
//
//                //current minecraft timeofday
//                Minecraft.getInstance().player.getLevel().getGameTime(),
//                req.getCaller(),
//                req.getCallee(),
//                req.getConversationCount()+1
//        );
//        chatHistory.Add(chatData);
//        return chatData;
        return null;
    }
}

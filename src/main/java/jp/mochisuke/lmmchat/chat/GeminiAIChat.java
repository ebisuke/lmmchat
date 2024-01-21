package jp.mochisuke.lmmchat.chat;

import com.google.api.client.util.Clock;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.preview.ContentMaker;
import com.google.cloud.vertexai.generativeai.preview.GenerativeModel;
import com.google.cloud.vertexai.generativeai.preview.ResponseHandler;
import jp.mochisuke.lmmchat.LMMChatConfig;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class GeminiAIChat implements IChatBase{
    GenerativeModel model;
    VertexAI vertexAI;
    Logger logging = Logger.getLogger("GeminiAIChat");
    public GeminiAIChat(){
        Clock clock = Clock.SYSTEM;
        var cred= GoogleCredentials.create(new AccessToken(LMMChatConfig.getGeminiApiKey(),null));
        vertexAI=new VertexAI("","us-central1",cred);
        model = new GenerativeModel("gemini-pro", vertexAI);
        GenerationConfig config = GenerationConfig.newBuilder()
                .setTemperature(0.7f)
                .setMaxOutputTokens(LMMChatConfig.getMaxTokens())
                .build();
        SafetySetting safetySetting = SafetySetting.newBuilder()
                .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT).setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT).setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH).setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT).setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                .setCategory(HarmCategory.HARM_CATEGORY_UNSPECIFIED).setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                .build();
        model.setGenerationConfig(config);
        model.setSafetySettings(List.of(safetySetting));


    }
    public List<Content> convertChatHistory(String callerId, ChatHistory chatHistory) {
        List<Content> contents = new ArrayList<>();
        for (ChatData chatData : chatHistory.getChatDataList()) {
            String role = "";


            //caller
            if (chatData.isCallerIsSystem()) {
                role = "model";
            } else {
                role = (Objects.equals(String.valueOf(chatData.getCaller().getId()), callerId) ?
                        "model" : "user");
            }
            var content = ContentMaker.forRole(role)
                    .fromString(chatData.getCallerMessage());
            contents.add(content);

            //callee
            if (chatData.isCalleeIsSystem()) {
                role = "model";
            } else {
                role = Objects.equals(String.valueOf(chatData.getCallee().getId()), callerId) ?
                        "user" : "model";
            }
            content = ContentMaker.forRole(role)
                    .fromString(chatData.getCalleeMessage());
            contents.add(content);
        }
        return contents;
    }
    @Override
    public String generateChatMessage(ChatGenerationRequest req, ChatHistory chatHistory, @Nullable String supportMessage) throws InterruptedException {
        //store conversation
        var contents = this.convertChatHistory(String.valueOf(req.getCaller() != null ? req.getCaller().getId() : -1), chatHistory);
        //generate preface

        var preface = ContentMaker.forRole("model")
                .fromString(req.getPreface().getMessage() );
        contents.add(0, preface);
        // add req
        contents.add(ContentMaker.forRole(req.getCaller()!=null?"user":"model")
                .fromString(req.getCallerMessage()));
        // add support message
        if(supportMessage!=null){
            contents.add(ContentMaker.forRole("model")
                    .fromString(supportMessage));
        }
        //generate
        try {
            var result = model.generateContent(contents);

            return ResponseHandler.getText(result);
        } catch (IOException e) {
            //fail
            logging.warning(e.getMessage());
        }
        return null;
    }

    @Override
    public ChatData addConversationMessage(ChatGenerationRequest req, ChatHistory chatHistory, String message, Object optional) throws InterruptedException {
        Entity ent= req.getCaller() !=null? req.getCaller():req.getCallee();
        GeminiChatData chatData = new GeminiChatData(
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
    public ChatData conversationMessage(ChatGenerationRequest req, ChatHistory chatHistory, @Nullable String supportMessage) throws InterruptedException {
        //change timeout okhttp3




        var ret = generateChatMessage(req,chatHistory,supportMessage);

        // add to chat history
        Entity ent= req.getCaller() !=null? req.getCaller():req.getCallee();
        GeminiChatData chatData = new GeminiChatData(
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

package jp.mochisuke.lmmchat.chat;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import net.minecraft.world.entity.LivingEntity;

public class OpenAIChatData extends ChatData{
    public OpenAIChatData(String callerMessage, String calleeMessage, long timestamp, LivingEntity caller, LivingEntity callee, boolean callerIsAssistant, boolean calleeIsAssistant,
                          int conversationCount, int usedTokensCount, ChatCompletionResult rawResponse, ChatCompletionRequest rawRequest) {
        super(callerMessage, calleeMessage, timestamp, caller, callee,callerIsAssistant,calleeIsAssistant, conversationCount);
        this.usedTokensCount = usedTokensCount;
        this.rawResponse = rawResponse;
        this.rawRequest = rawRequest;
    }

    private int usedTokensCount;

    public int getUsedTokensCount() {
        return usedTokensCount;
    }

    private ChatCompletionResult rawResponse;

    public ChatCompletionResult getRawResponse() {
        return rawResponse;
    }

    ChatCompletionRequest rawRequest;

    public ChatCompletionRequest getRawRequest() {
        return rawRequest;
    }




}

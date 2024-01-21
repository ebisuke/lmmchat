package jp.mochisuke.lmmchat.chat;

import net.minecraft.world.entity.LivingEntity;

public class GeminiChatData extends ChatData{
    public GeminiChatData(String callerMessage, String calleeMessage, long timestamp, LivingEntity caller, LivingEntity callee, boolean callerIsAssistant, boolean calleeIsAssistant,
                          int conversationCount ) {
        super(callerMessage, calleeMessage, timestamp, caller, callee,callerIsAssistant,calleeIsAssistant, conversationCount);
    }





}

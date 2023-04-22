package jp.mochisuke.lmmchat.chat;

import net.minecraft.world.entity.LivingEntity;

public class ChatGenerationRequest implements Cloneable{
    private String callerMessage;
    private LivingEntity caller;
    private LivingEntity callee;
    private long timestamp;
    private int conversationCount;
    private ChatPreface preface;

    private boolean callerIsAssistant = false;
    private boolean calleeIsAssistant = false;

    //callback for chat generation

    private ChatGenerationCallback callback;




    public ChatGenerationRequest(LivingEntity caller,LivingEntity callee,boolean callerIsAssistant,boolean calleeIsAssistant, String callerMessage,
                                 long timestamp,int conversationCount ,ChatPreface preface) {
        this.callerMessage = callerMessage;
        this.caller = caller;
        this.callee = callee;
        this.timestamp = timestamp;
        this.conversationCount = conversationCount;
        this.preface = preface;
        this.callerIsAssistant = callerIsAssistant;
        this.calleeIsAssistant = calleeIsAssistant;

    }

    public void setCallback(ChatGenerationCallback callback) {
        this.callback = callback;
    }
    public ChatGenerationCallback getCallback() {
        return callback;
    }

    public LivingEntity getCaller() {
        return caller;
    }
    public LivingEntity getCallee() {
        return callee;
    }

    public String getCallerMessage() {
        return callerMessage;
    }
    public int getConversationCount() {
        return conversationCount;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public ChatPreface GetPreface(){
        return preface;
    }

    public boolean isCallerIsAssistant() {
        return callerIsAssistant;
    }

    public void setCallerIsAssistant(boolean callerIsAssistant) {
        this.callerIsAssistant = callerIsAssistant;
    }

    public boolean isCalleeIsAssistant() {
        return calleeIsAssistant;
    }

    public void setCalleeIsAssistant(boolean calleeIsAssistant) {
        this.calleeIsAssistant = calleeIsAssistant;
    }


    @Override
    public ChatGenerationRequest clone() {

        try {
            ChatGenerationRequest clone = (ChatGenerationRequest) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }

    }
}

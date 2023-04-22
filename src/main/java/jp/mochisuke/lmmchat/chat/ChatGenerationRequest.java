package jp.mochisuke.lmmchat.chat;

import net.minecraft.world.entity.LivingEntity;

public class ChatGenerationRequest implements Cloneable{
    private String callerMessage;
    private LivingEntity caller;
    private LivingEntity callee;
    private long timestamp;
    private int conversationCount;
    private ChatPreface preface;

    private boolean callerIsSystem = false;
    private boolean calleeIsSystem = false;

    //callback for chat generation

    private ChatGenerationCallback callback;




    public ChatGenerationRequest(LivingEntity caller, LivingEntity callee, boolean callerIsSystem, boolean calleeIsSystem, String callerMessage,
                                 long timestamp, int conversationCount , ChatPreface preface) {
        this.callerMessage = callerMessage;
        this.caller = caller;
        this.callee = callee;
        this.timestamp = timestamp;
        this.conversationCount = conversationCount;
        this.preface = preface;
        this.callerIsSystem = callerIsSystem;
        this.calleeIsSystem = calleeIsSystem;

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
    public ChatPreface getPreface(){
        return preface;
    }

    public boolean isCallerIsSystem() {
        return callerIsSystem;
    }

    public void setCallerIsSystem(boolean callerIsSystem) {
        this.callerIsSystem = callerIsSystem;
    }

    public boolean isCalleeIsSystem() {
        return calleeIsSystem;
    }

    public void setCalleeIsSystem(boolean calleeIsSystem) {
        this.calleeIsSystem = calleeIsSystem;
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

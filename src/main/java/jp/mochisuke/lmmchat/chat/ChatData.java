package jp.mochisuke.lmmchat.chat;

import net.minecraft.world.entity.LivingEntity;

public class ChatData implements Cloneable{
    private String callerMessage;
    private String calleeMessage;
    private String id;
    private long timestamp;


    private LivingEntity caller;
    private LivingEntity callee;

    private boolean callerIsAssistant = false;
    private boolean calleeIsAssistant = false;

    private int conversationCount;

    public ChatData(String callerMessage, String calleeMessage, long timestamp, LivingEntity caller, LivingEntity callee,boolean callerIsAssistant,boolean calleeIsAssistant,int conversationCount) {
        this.callerMessage = callerMessage;
        this.calleeMessage = calleeMessage;
        this.callerIsAssistant = callerIsAssistant;
        this.calleeIsAssistant = calleeIsAssistant;
        //gerenate id
        this.id  = String.valueOf(caller.getId()) + ":"+String.valueOf(callee.getId()) +":"+ timestamp;
        this.timestamp = timestamp;
        this.caller = caller;
        this.callee = callee;
        this.conversationCount = conversationCount;
    }



    public String getCallerMessage() {
        return callerMessage;
    }

    public String getCalleeMessage() {
        return calleeMessage;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public LivingEntity getCaller() {
        return caller;
    }

    public LivingEntity getCallee() {
        return callee;
    }

    public int getConversationCount() {
        return conversationCount;
    }

    public boolean isCallerIsAssistant() {
        return callerIsAssistant;
    }

    public boolean isCalleeIsAssistant() {
        return calleeIsAssistant;
    }

    @Override
    public ChatData clone() {
        try {
            ChatData clone = (ChatData) super.clone();

            clone.callerMessage = callerMessage;
            clone.calleeMessage = calleeMessage;
            clone.id = id;
            clone.timestamp = timestamp;
            clone.caller = caller;
            clone.callee = callee;
            clone.conversationCount = conversationCount;

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}

package jp.mochisuke.lmmchat.chat;

import net.minecraft.world.entity.Entity;

public class ChatData implements Cloneable{
    private String callerMessage;
    private String calleeMessage;
    private String id;
    private long timestamp;


    private Entity caller;
    private Entity callee;

    private int conversationCount;

    public ChatData(String callerMessage, String calleeMessage, long timestamp, Entity caller, Entity callee,int conversationCount) {
        this.callerMessage = callerMessage;
        this.calleeMessage = calleeMessage;
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

    public Entity getCaller() {
        return caller;
    }

    public Entity getCallee() {
        return callee;
    }

    public int getConversationCount() {
        return conversationCount;
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

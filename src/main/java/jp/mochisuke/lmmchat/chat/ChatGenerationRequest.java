package jp.mochisuke.lmmchat.chat;

import net.minecraft.world.entity.Entity;

public class ChatGenerationRequest implements Cloneable{
    private String callerMessage;
    private Entity caller;
    private Entity callee;
    private long timestamp;
    private int conversationCount;

    public ChatGenerationRequest(Entity caller,Entity callee, String callerMessage, long timestamp,int conversationCount    ) {
        this.callerMessage = callerMessage;
        this.caller = caller;
        this.callee = callee;
        this.timestamp = timestamp;
        this.conversationCount = conversationCount;

    }


    public Entity getCaller() {
        return caller;
    }
    public Entity getCallee() {
        return callee;
    }

    public String getCallerMessage() {
        return callerMessage;
    }
    public int getConversationCount() {
        return conversationCount;
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

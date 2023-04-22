package jp.mochisuke.lmmchat.order;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.LMMChatConfig;
import jp.mochisuke.lmmchat.chat.ChatData;
import jp.mochisuke.lmmchat.chat.ChatGenerationCallback;
import jp.mochisuke.lmmchat.chat.ChatGenerationRequest;
import jp.mochisuke.lmmchat.chat.ChatPreface;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.slf4j.Logger;

import java.util.List;

public abstract class AIOrderBase implements ChatGenerationCallback {
    public static final Logger logger= LogUtils.getLogger();
    protected final Mob entity;
    public AIOrderBase(Mob entity, List<Object> args) {
        this.entity = entity;
    }

    public abstract void execute();

    public void onSuccess(){
        //nothing to do
    }
    public void onFailed(String reason){
        //nothing to do

    }

    public void notifyAI(String message){
        ChatPreface preface = new ChatPreface(LMMChatConfig.getPreface());
        var req=new ChatGenerationRequest(null,entity,true,false,message,
                Minecraft.getInstance().player.getLevel().getGameTime(),0, preface);
        req.setCallback(this);
        LMMChat.chatThread.PushRequest(req);
    }
    public void sendChat(String message,LivingEntity sendto){
        LMMChat.addChatMessage(
                this.entity,sendto,false,false,message,0
        );
    }
    public void sendChatFromAssitant(String message,LivingEntity sendto){
        LMMChat.addChatMessage(
                this.entity,sendto,true,false,message,0
        );
    }
    @Override
    public void onChatGenerated(ChatGenerationRequest request, ChatData response) {
        //nothing to do
        logger.info("AIOrderBase.onChatGenerated:REQ:"+response.getCallerMessage()+"RESP:"+response.getCalleeMessage());

        // send chat
        sendChatFromAssitant(response.getCalleeMessage(),request.getCaller());


    }
}

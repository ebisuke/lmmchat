package jp.mochisuke.lmmchat;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = LMMChat.MODID)
public class LMMChatController {
    //logger
    private static final Logger logger = LogUtils.getLogger();



    @SubscribeEvent
    public static void onTick(net.minecraftforge.event.TickEvent.ServerTickEvent event){
        // server only
        if(event.phase==net.minecraftforge.event.TickEvent.Phase.START){
            return;
        }
        while(true){
            var chatData=LMMChat.chatThread.PopChatData();
            if(chatData==null){
                break;
            }
            //get entity by encoded id
            var caller= chatData.getCaller();
            var callee= chatData.getCallee();
            //log

            logger.info ("TALK:"+caller.getName().getString()+":"+chatData.getCallerMessage()+":"+callee.getName().getString()+":"+chatData.getCalleeMessage());

            //say chat to nearest players
            callee.getCommandSenderWorld().getNearbyPlayers(TargetingConditions.forNonCombat(),(LivingEntity) callee,/*AABB*/ callee.getBoundingBox().inflate(10)
            ).forEach(player -> {
                player.displayClientMessage(Component.nullToEmpty(callee.getDisplayName().getString()+":"+chatData.getCalleeMessage()),true);
                player.sendSystemMessage(Component.nullToEmpty(callee.getDisplayName().getString()+":"+chatData.getCalleeMessage()));
            });



            //caller is lmm?
            if(caller.getClass().toString().contains("LittleMaidEntity")&&callee.getClass().toString().contains("LittleMaidEntity")&&
                    chatData.getConversationCount()  < LMMChatConfig.getConversationLimitForLmms() ){
                //conversation
                LMMChat.addChatMessage(callee,caller,chatData.getCalleeMessage(),chatData.getConversationCount());
            }
        }
    }
    //chat submitted
    @SubscribeEvent
    public static void onChat(ServerChatEvent.Submitted event){
        //server side only
        if(event.getPlayer().getCommandSenderWorld().isClientSide){
            return;
        }
        logger.info("onChat:"+event.getRawText());
        //get player
        var player=event.getPlayer();
        //get nearby entities
        var entities=player.getCommandSenderWorld().getNearbyEntities(
                LivingEntity.class,
                TargetingConditions.DEFAULT,
                player,
                player.getBoundingBox().inflate(10)

        );


        //filter lmm
        var lmm=entities.stream().filter(entity -> entity.getClass().toString().contains("LittleMaidEntity")).toList();
        logger.info("lmm count:"+lmm.size());
        //add message queue
        lmm.forEach(entity -> {
            LMMChat.addChatMessage(player,entity,event.getRawText(),0);
        });

    }
}

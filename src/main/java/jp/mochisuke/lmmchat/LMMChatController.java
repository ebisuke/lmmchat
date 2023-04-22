package jp.mochisuke.lmmchat;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.goal.AIOperationGoal;
import jp.mochisuke.lmmchat.order.AIOrderBase;
import jp.mochisuke.lmmchat.order.AIOrderParser;
import jp.mochisuke.lmmchat.order.VariablesContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sistr.littlemaidrebirth.entity.util.Tameable;
import org.slf4j.Logger;

import java.util.*;

@Mod.EventBusSubscriber(modid = LMMChat.MODID)
public class LMMChatController {
    //logger
    private static final Logger logger = LogUtils.getLogger();

    private static final Map<Integer, VariablesContext> contextMap=new HashMap<>();

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
            assert !chatData.isCallerIsSystem() || !chatData.isCalleeIsSystem();
            //parse
            var calleeMessage=chatData.getCalleeMessage();
            boolean forceOwner=false;
            //mention?
            if(calleeMessage.startsWith("@")){
                forceOwner=true;
            }
            if(!chatData.isCalleeIsSystem()) {
                logger.info("TALK:" + caller.getName().getString() + ":" + chatData.getCallerMessage() + ":" + callee.getName().getString() + ":" + chatData.getCalleeMessage());
                if(forceOwner){
                    logger.info("TALK:force owner");
                    var tamable=(Tameable)callee;
                    var owner=tamable.getTameOwner();
                    //owner.get().getser(callee.getDisplayName().getString() + ":" + chatData.getCalleeMessage()), true);
                    owner.get().sendSystemMessage(Component.nullToEmpty(callee.getDisplayName().getString() + ":" + chatData.getCalleeMessage()));

                }else {

                    //say chat to nearest players
                    callee.getCommandSenderWorld().getNearbyPlayers(TargetingConditions.forNonCombat(), (LivingEntity) callee,/*AABB*/ callee.getBoundingBox().inflate(10)
                    ).forEach(player -> {
                        //player.displayClientMessage(Component.nullToEmpty(callee.getDisplayName().getString() + ":" + chatData.getCalleeMessage()), true);
                        player.sendSystemMessage(Component.nullToEmpty(callee.getDisplayName().getString() + ":" + chatData.getCalleeMessage()));
                    });
                }

            }
            List<AIOrderBase> orders;
            try {
                //get context
                VariablesContext context;
                if(contextMap.containsKey(callee.getId())) {
                    context = contextMap.get(callee.getId());
                }else{
                    context=new VariablesContext();
                    contextMap.put(callee.getId(),context);
                }


                orders = AIOrderParser.parse((Mob) callee,context, calleeMessage);
            }catch (Exception e){
                logger.error("parse error",e);
                //notify
                LMMChat.addChatMessage(callee, caller,true,chatData.isCallerIsSystem(),
                        "parse error.maybe contains invalid order.", chatData.getConversationCount());
                continue;
            }
            //execute
            if(!orders.isEmpty()){
                Mob calleemob=(Mob)callee;
                var ai=((Mob) callee).goalSelector.getAvailableGoals().stream().
                        filter(goal -> goal.getClass().toString().contains("AIOperationGoal")).findFirst();
                if(ai.isPresent()){
                    var aiop=(AIOperationGoal)ai.get().getGoal();
                    aiop.activate(orders);
                }
                return;
            }

            //caller is lmm?
            if(!forceOwner){
                if (caller.getClass().toString().contains("LittleMaidEntity") && callee.getClass().toString().contains("LittleMaidEntity") &&
                        chatData.getConversationCount() < LMMChatConfig.getConversationLimitForLmms()) {
                    //conversation
                    LMMChat.addChatMessage(callee, caller,chatData.isCalleeIsSystem(),chatData.isCallerIsSystem(),
                            chatData.getCalleeMessage(), chatData.getConversationCount());
                }
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
        boolean mention=false;

        if(event.getRawText().startsWith("@")){
            //mention
            var substr=event.getRawText().substring(1);
            //split space
            var split=substr.split(" ");
            var name=split[0];
            //get entity in all world by name
            var allents=player.getLevel().getAllEntities();
            var allmaids=new ArrayList<TamableAnimal>();
            for(var entity:allents){
                if(entity instanceof TamableAnimal) {
                    TamableAnimal animal=(TamableAnimal)entity;
                    if (animal.getClass().toString().contains("LittleMaidEntity") &&
                            animal.isTame()&&animal.getOwner()==player) {
                        allmaids.add((TamableAnimal) entity);
                    }
                }
            }
            if(Objects.equals(name.strip(), "")){
                //broadcast
                allmaids.forEach(entity -> {
                    LMMChat.addChatMessage(player, entity, false, false,
                            substr, 0);
                });

            }else {
                //get name
                var target = allmaids.stream().filter(entity -> entity.getName().getString().equals(name)).findFirst();
                if (target.isPresent()) {
                    //add message queue
                    LMMChat.addChatMessage(player, target.get(), false, false,
                            substr, 0);
                    mention = true;
                } else {
                    player.displayClientMessage(Component.nullToEmpty("not found:" + name), true);
                }
            }

        }else {

            //filter lmm
            var lmm = new java.util.ArrayList<>(entities.stream().filter(entity -> entity.getClass().toString().contains("LittleMaidEntity")).toList());

            //filter by looking direction
            //limit
            lmm = new ArrayList<>(
                    lmm.subList(0, Math.min(lmm.size(), LMMChatConfig.getLimitOfResponsePerOneChat())));
            logger.info("lmm count:" + lmm.size());
            //add message queue
            lmm.forEach(entity -> {
                LMMChat.addChatMessage(player, entity, false, false,
                        event.getRawText(), 0);
            });
        }

    }
}

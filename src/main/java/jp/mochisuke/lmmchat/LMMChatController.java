package jp.mochisuke.lmmchat;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.goal.AIOperationGoal;
import jp.mochisuke.lmmchat.goal.ComputerCraftModeGoal;
import jp.mochisuke.lmmchat.helper.Helper;
import jp.mochisuke.lmmchat.lmm.LMMChatMode;
import jp.mochisuke.lmmchat.lmm.LMMEntityWrapper;
import jp.mochisuke.lmmchat.order.AIOrderBase;
import jp.mochisuke.lmmchat.order.AIOrderParser;
import jp.mochisuke.lmmchat.order.VariablesContext;
import jp.mochisuke.lmmchat.packets.SynthesisPacket;
import jp.mochisuke.lmmchat.packets.SynthesisPacketHandler;
import jp.mochisuke.lmmchat.sounds.SoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.*;

@Mod.EventBusSubscriber(modid = LMMChat.MODID)
public class LMMChatController {
    //logger
    private static final Logger logger = LogUtils.getLogger();

    private static final Map<Integer, VariablesContext> contextMap=new HashMap<>();
    @SubscribeEvent
    public static void onRenderTick(net.minecraftforge.event.TickEvent.RenderTickEvent event){
        if(event.side.isClient()){
            if(event.phase== net.minecraftforge.event.TickEvent.Phase.START){
                LMMChat.soundPlayer.onTick();
            }
        }
    }
    @SubscribeEvent
    public static void onTick(net.minecraftforge.event.TickEvent.ServerTickEvent event){
        if(event.side.isClient()){

            return;
        }
        // server only
        if(event.phase==net.minecraftforge.event.TickEvent.Phase.START){
            return;
        }
        //no login user?
        if(event.getServer().getPlayerCount()==0){
            //delete all chat queue
            LMMChat.chatManager.disable();
            return;
        }else{
            //enable chat queue
            LMMChat.chatManager.enable();
        }


        while(true){
            var chatData=LMMChat.chatManager.PopChatData();
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
            boolean forceOwner= calleeMessage.startsWith("@");
            //mention?
            logger.info("callee:"+calleeMessage);

            //------- ORDER -------
            List<AIOrderBase> orders;
            if(callee instanceof TamableAnimal) {
                try {
                    //get context
                    VariablesContext context;
                    if (contextMap.containsKey(callee.getId())) {
                        context = contextMap.get(callee.getId());
                    } else {
                        context = new VariablesContext();
                        contextMap.put(callee.getId(), context);
                    }
                    //put owner id

                    var owner = ((TamableAnimal) callee).getOwner();
                    if (owner != null) {
                        context.setVar("owner", (double) owner.getId());

                    }



                    //remove existing order
                    if (callee instanceof Mob calleemob) {
                        var ai = calleemob.goalSelector.getAvailableGoals().stream().filter(g -> g.getGoal() instanceof AIOperationGoal).findFirst();
                        if (ai.isPresent()) {
                            ai.get().getGoal().stop();
                        }
                    }

                    orders = AIOrderParser.parse(callee, context, calleeMessage);

                } catch (Exception e) {
                    logger.error("parse error", e);
                    //notify
                    LMMChat.addChatMessage(callee, caller, true, chatData.isCallerIsSystem(),
                            "parse error.maybe contains invalid order.", chatData.getConversationCount());
                    continue;
                }

                // --- EXECUTE GOAL --
                if (!orders.isEmpty()) {
                    Mob calleemob = (Mob) callee;
                    var ai = calleemob.goalSelector.getAvailableGoals().stream().filter(g -> g.getGoal() instanceof AIOperationGoal).findFirst();
                    var aiop = (AIOperationGoal) ai.get().getGoal();
                    //forget previous order
                    aiop.forget();
                    //check running thread
                    aiop.activate(orders);

                    return;
                }else{
                    //for computercraft processing
                    if(LMMChat.isEnableComputerCraft()){
                        LMMEntityWrapper wrapper = new LMMEntityWrapper((TamableAnimal) callee);
                        if(wrapper.getChatMode()== LMMChatMode.COMPUTERCRAFT ){
                            //inject chat
                            String maybeScript=calleeMessage;

                            ComputerCraftModeGoal goal=(ComputerCraftModeGoal)Helper.getGoal((Mob)callee, ComputerCraftModeGoal.class);
                            goal.processMessage(maybeScript);


                            calleeMessage="";
                        }
                    }
                }
            }
            // ---------- SHOW CHAT ---------
            var calleeMessageChat=AIOrderParser.parsedRemnant(calleeMessage);
            if(!calleeMessageChat.isBlank()) {
                if (!chatData.isCalleeIsSystem()) {

                    //logger.info("TALK:" + caller.getName().getString() + ":" + chatData.getCallerMessage() + ":" + callee.getName().getString() + ":" + chatData.getCalleeMessage());
                    if (forceOwner && callee instanceof TamableAnimal) {
                        logger.info("TALK:force owner");
                        var tamable = (TamableAnimal) callee;
                        var owner = Helper.getOwner(tamable);
                        if(owner!=null) {                        //owner.get().getser(callee.getDisplayName().getString() + ":" + chatData.getCalleeMessage()), true);
                            if(!isContainNGWord(calleeMessageChat)) {
                                if (LMMChatConfig.isEnableVoicevox()) {
                                    SynthesisPacketHandler.sendToClient(new SynthesisPacket(callee.getId(), LMMChatConfig.getVoiceVoxSpeakerId(), calleeMessageChat, owner.getUUID()), (ServerPlayer) owner);
                                }
                                owner.sendSystemMessage(Component.nullToEmpty(callee.getDisplayName().getString() + ":" + calleeMessageChat));
                            }
                        }else{
                            logger.info("TALK:owner not found");
                        }
                    } else {
                        if (callee instanceof TamableAnimal) {
                            var tamable = (TamableAnimal) callee;
                            var owner = Helper.getOwner(tamable);
                            //say chat to nearest players
                            String finalCalleeMessage = calleeMessageChat;
                            callee.getCommandSenderWorld().getNearbyPlayers(TargetingConditions.forNonCombat(), callee,/*AABB*/ callee.getBoundingBox().inflate(20)
                            ).forEach(player -> {
                                //player.displayClientMessage(Component.nullToEmpty(callee.getDisplayName().getString() + ":" + chatData.getCalleeMessage()), true);
                                if (!isContainNGWord(finalCalleeMessage)) {
                                    if (LMMChatConfig.isEnableVoicevox()) {
                                        if (player == owner) {
                                            SynthesisPacketHandler.sendToClient(
                                                    new SynthesisPacket(callee.getId(), LMMChatConfig.getVoiceVoxSpeakerId(), finalCalleeMessage, player.getUUID()), (ServerPlayer) player);
                                        } else {
                                            SynthesisPacketHandler.sendToClient(
                                                    new SynthesisPacket(callee.getId(), LMMChatConfig.getVoiceVoxNeutralSpeakerId(), finalCalleeMessage, player.getUUID()), (ServerPlayer) player);
                                        }
                                    }
                                    player.sendSystemMessage(Component.nullToEmpty(callee.getDisplayName().getString() + ":" + finalCalleeMessage));
                                }

                            });
                        }
                    }

                }
            }
            // --- CONVERSATION ---
            //caller is lmm?
            if(!forceOwner){

                if (caller!=null && callee!=null && caller.getClass().toString().contains("LittleMaidEntity") && callee.getClass().toString().contains("LittleMaidEntity") &&
                        chatData.getConversationCount() < LMMChatConfig.getConversationLimitForLmms()) {
                    //conversation

                    LMMChat.addChatMessage(callee, caller,chatData.isCalleeIsSystem(),chatData.isCallerIsSystem(),
                            chatData.getCalleeMessage(), chatData.getConversationCount());
                }
            }

        }
    }
    private static boolean isContainNGWord(String text){
        String[] ngwords=LMMChatConfig.getNGWord().split(",");
        for(var ngword:ngwords){
            if(text.contains(ngword)){
                return true;
            }
        }
        return false;
    }
    public static void synthesis(int sourceId,int speakerId, String text){

        SoundPlayer splayer=LMMChat.soundPlayer;
        if(splayer==null){
            logger.error("sound player is null");
            return;
        }
        if(!LMMChatConfig.isEnableVoicevox()){
            logger.debug("voicevox is disabled");
            return;
        }

        try {
            Thread th=new Thread(()->{
                //get entity
                assert Minecraft.getInstance().level != null;
                Entity entity=Minecraft.getInstance().level.getEntity(sourceId);
                if (entity==null){
                    logger.debug("entity not found:"+sourceId);
                    return;
                }
                splayer.generateAndPlay(sourceId, speakerId, text);
            });
            th.start();
            //splayer.generateAndPlay(sourceId, speakerId, text,false);
        }catch (Exception e){
            logger.error("sound play error",e);
        }
    }
    //chat submitted
    @SubscribeEvent
    public static void onChat(ServerChatEvent event){
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
                player.getBoundingBox().inflate(20)

        );
        boolean mention=false;

        if(event.getRawText().startsWith("@")){
            //mention
            var substr=event.getRawText().substring(1);
            //split space
            var split=substr.split(" ");
            var name=split[0];
            //get entity in all world by name
            var alllevel=player.getServer().getAllLevels();


            var allmaids=new ArrayList<TamableAnimal>();
            for(var level:alllevel) {
                var allents=level.getAllEntities();
                for (var entity : allents) {
                    if (entity instanceof TamableAnimal animal) {
                        if (animal.getClass().toString().contains("LittleMaidEntity") &&
                                animal.isTame() && Helper.getOwner(animal) == player) {
                            allmaids.add((TamableAnimal) entity);
                        }
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

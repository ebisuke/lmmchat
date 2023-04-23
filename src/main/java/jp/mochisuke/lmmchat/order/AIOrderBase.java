package jp.mochisuke.lmmchat.order;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.LMMChatConfig;
import jp.mochisuke.lmmchat.chat.ChatData;
import jp.mochisuke.lmmchat.chat.ChatGenerationCallback;
import jp.mochisuke.lmmchat.chat.ChatGenerationRequest;
import jp.mochisuke.lmmchat.chat.ChatPreface;
import jp.mochisuke.lmmchat.goal.AIGoalBase;
import jp.mochisuke.lmmchat.goal.AIOperationGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.slf4j.Logger;

import java.util.List;

public abstract class AIOrderBase implements ChatGenerationCallback,AIGoalBase.Callback {
    public static final Logger logger= LogUtils.getLogger();
    protected final LivingEntity entity;
    protected final VariablesContext context;
    protected List<Object> args;
    protected boolean isIntermediate=false;
    public AIOrderBase(LivingEntity entity,VariablesContext context, List<Object> args) {
        this.entity = entity;
        this.context = context;
        this.args = args;
    }
    protected abstract void startUp(LivingEntity entity,VariablesContext context, List<Object> args);
    protected abstract boolean isImmediate();

    public void setIntermediate(boolean intermediate) {
        isIntermediate=intermediate;
    }

    protected int val(String o){
        //name is number?
        var ret=context.eval(o);
        //unbox
        if(ret instanceof Double){
            return ((Double)ret).intValue();
        }else if(ret instanceof Integer){
            return (Integer)ret;
        }

        throw new IllegalArgumentException("not number:"+o);


    }
    protected void val(String o,double value){
        context.setVar(o,(double)value);
    }


    public interface Generator{
        AIOrderBase generate(LivingEntity entity,VariablesContext context, List<Object> args);
    }
    public boolean execute(){
        startUp(entity,context,args);
        executeImpl();
        if(isImmediate()) {
            return true;
        }else{
            return false;
        }
    }
    protected abstract void executeImpl();

    @Override
    public void onSuccess(){
        //nothing to do
    }
    @Override
    public void onFailed(String reason){
        //nothing to do
        //remove all order
        getOperationGoal().forget();
    }

    public void notifyAI(String message){
        logger.info("AIOrderBase.notifyAI:"+message);
        if(isIntermediate){
            logger.debug("AIOrderBase.notifyAI:intermediate order. skip notifyAI");
            return;
        }
        ChatPreface preface = new ChatPreface(LMMChatConfig.getPreface());
        var req=new ChatGenerationRequest(null,entity,true,false,message,
                LMMChat.getServerTime(),0, preface);
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

    public AIOperationGoal getOperationGoal(){
        Mob mob=(Mob) entity;

        //find
        var m=mob.goalSelector. getAvailableGoals().stream().filter(g->g.getGoal() instanceof AIOperationGoal).findFirst();
        if(m.isPresent()){
            return (AIOperationGoal) m.get().getGoal();
        }
        return null;

    }

    protected void prepareGoal(AIGoalBase goal){
    }

}

package jp.mochisuke.lmmchat.goal;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.LMMChatConfig;
import jp.mochisuke.lmmchat.chat.ChatGenerationRequest;
import jp.mochisuke.lmmchat.chat.IChatPreface;
import jp.mochisuke.lmmchat.chat.VariableChatPreface;
import jp.mochisuke.lmmchat.order.AIOrderBase;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import org.slf4j.Logger;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AIOperationGoal  <T extends Mob>  extends Goal implements AIGoalBase.Callback {
    static final Logger logger= LogUtils.getLogger();
    protected final T entity;
    private final Queue<AIOrderBase> orders;
    private AIOrderBase currentOrder=null;
    public AIOperationGoal(T entity) {
        this.entity = entity;
        orders=new ConcurrentLinkedQueue<>();

    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void activate(List<AIOrderBase> orders){
        for(var order:orders) {
            activate(order);
        }

    }
    public void activate(AIOrderBase order){
        this.orders.add(order);

    }
    @Override
    public void stop(){
        orders.clear();
        currentOrder=null;
    }
    @Override
    public boolean canUse() {
        return orders.size()>0 || currentOrder!=null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void onSuccess() {
        var cur=currentOrder;
        currentOrder=null;
        if(cur!=null) {
            cur.onSuccess();
        }
    }

    @Override
    public void onFailed(String reason) {
        var cur=currentOrder;
        currentOrder=null;
        if(cur!=null) {
            cur.onFailed(reason);
        }
        //dispose all orders
        orders.clear();
        //notify ai
        logger.info("AIOperationGoal.onFailed:"+reason);
        IChatPreface preface = new VariableChatPreface(LMMChatConfig.getPreface(),((TamableAnimal)entity).getOwner(),null,entity);
        var req=new ChatGenerationRequest(null,entity,true,false,reason,
                LMMChat.getServerTime()
                ,0, preface);
        LMMChat.chatManager.PushRequest(req);
    }

    @Override
    public void tick() {
        super.tick();
        if(currentOrder==null){
            currentOrder=orders.poll();
            if(currentOrder!=null){
                logger.info("AIOperationGoal.tick:execute order:"+currentOrder.getClass().getName());

                try {
                    if (currentOrder.execute()) {
                        //success
                        onSuccess();
                    }
                }catch (Exception e){
                    logger.info("AIOperationGoal.tick:execute order:"+currentOrder.getClass().getName()+" failed:"+e.getMessage());
                    onFailed(e.getMessage());
                }
            }
        }
    }

    public void forget() {
        logger.info("AIOperationGoal.forget");
        orders.clear();
        currentOrder=null;
    }
}

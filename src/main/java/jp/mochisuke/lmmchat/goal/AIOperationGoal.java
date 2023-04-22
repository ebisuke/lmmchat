package jp.mochisuke.lmmchat.goal;

import jp.mochisuke.lmmchat.order.AIOrderBase;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AIOperationGoal  <T extends Mob>  extends Goal implements CallbackedGoal.Callback {
    protected final T entity;
    private Queue<AIOrderBase> orders;
    private AIOrderBase currentOrder=null;
    public AIOperationGoal(T entity) {
        this.entity = entity;
        orders=new ConcurrentLinkedQueue<>();

    }
    public void activate(List<AIOrderBase> orders){
        this.orders.addAll(orders);

    }
    public void activate(AIOrderBase order){
        this.orders.add(order);

    }
    @Override
    public boolean canUse() {
        return !orders.isEmpty() && currentOrder==null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void onSuccess() {
        var cur=currentOrder;
        currentOrder=null;
        cur.onSuccess();
    }

    @Override
    public void onFailed(String reason) {
        var cur=currentOrder;
        currentOrder=null;
        cur.onFailed(reason);

    }

    @Override
    public void tick() {
        super.tick();
        if(currentOrder==null){
            currentOrder=orders.poll();
            if(currentOrder!=null){
                currentOrder.execute();
            }
        }
    }
}

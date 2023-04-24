package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.FortifyGoal;
import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class FortifyOrder extends AIOrderBase{

    boolean on;

    public FortifyOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        try {
            if (args.size() > 0) {
                on = Boolean.parseBoolean(args.get(0).toString());
            } else {
                on = true;
            }
        }catch (Exception e){
            notifyAI("fortify args cannot be recognized. defaulting to true");
            on=true;
        }
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("fortify failed:"+reason);
    }

    @Override
    public void onSuccess() {
        notifyAI("fortify mode exited");
    }

    @Override
    protected boolean isImmediate() {
        return false;
    }

    @Override
    protected void executeImpl() {
        FortifyGoal goal= (FortifyGoal) Helper.getGoal((Mob) entity,FortifyGoal.class);
        goal.setFortify(on);
    }
}

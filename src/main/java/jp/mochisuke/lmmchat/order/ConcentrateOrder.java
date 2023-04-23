package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.ConcentrateGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class ConcentrateOrder extends AIOrderBase{
    public ConcentrateOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {

    }

    @Override
    protected boolean isImmediate() {
        return true;
    }

    @Override
    public void onSuccess() {
        notifyAI("Concentrate mode activated");
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("Concentrate failed:"+reason);
    }

    @Override
    protected void executeImpl() {
        //get goal

        Mob mob = (Mob) entity;
        mob.goalSelector.getAvailableGoals().stream().filter(g -> g.getGoal() instanceof ConcentrateGoal).findFirst().ifPresent(g -> {
            ((ConcentrateGoal) g.getGoal()).resetConcentrateTime();
        });

    }
}

package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.HealOwnerGoal;
import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class HealOwnerOrder extends AIOrderBase{
    public HealOwnerOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {

    }

    @Override
    protected boolean isImmediate() {
        return false;
    }

    @Override
    protected void executeImpl() {
        Mob mob=(Mob)entity;
        HealOwnerGoal goal= (HealOwnerGoal) Helper.getGoal(mob, HealOwnerGoal.class);
        goal.setUp();
    }
}

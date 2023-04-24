package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.InteractGoal;
import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class InteractOrder extends AIOrderBase{

    int x;
    int y;
    int z;
    public InteractOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        x=val(args.get(0).toString());
        y=val(args.get(1).toString());
        z=val(args.get(2).toString());
    }

    @Override
    protected boolean isImmediate() {
        return false;
    }

    @Override
    public void onSuccess() {
        notifyAI("interact success");
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("interact failed:"+reason);
    }

    @Override
    protected void executeImpl() {



        Mob mob= (Mob) entity;
        InteractGoal goal= (InteractGoal) Helper.getGoal(mob,InteractGoal.class);
        goal.setup(x,y,z);
    }
}

package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.BlockInspectGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class BlockInspectOrder extends AIOrderBase{


    //x,y,z,itemname,minslotindex,maxslotindex
    private int x;
    private int y;
    private int z;
    public BlockInspectOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity,context, args);

    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        String x,y,z,minslotindex,maxslotindex;
        x= (String) args.get(0);
        y= (String) args.get(1);
        z= (String) args.get(2);
        this.x=val(x);
        this.y=val(y);
        this.z=val(z);
    }

    @Override
    protected boolean isImmediate() {
        return false;
    }

    @Override
    public void onSuccess() {
        notifyAI("inspect success");
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("inspect fail:"+reason);
    }

    @Override
    public void executeImpl() {
        Mob mob=(Mob) entity;
        var m=mob.goalSelector. getAvailableGoals().stream().filter(g->g.getGoal() instanceof BlockInspectGoal).findFirst();
        var goal=(BlockInspectGoal) m.get().getGoal();
        goal.setup(x,y,z);
    }
}

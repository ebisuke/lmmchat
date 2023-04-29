package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.ObserveGoal;
import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class ObserveOrder extends AIOrderBase{
    private int x;
    private int y;
    private int z;

    public ObserveOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        if(args.size()<3){
            x=val("x");
            y=val("y");
            z=val("z");
        }
        x = val((String) args.get(0));
        y =  val((String) args.get(1));
        z =  val((String) args.get(2));
    }

    @Override
    protected boolean isImmediate() {
        return false;
    }

    @Override
    protected void executeImpl() {
        ObserveGoal goal = (ObserveGoal) Helper.getGoal((Mob) entity,ObserveGoal.class);
        assert goal != null;
        goal.setUp(x,y,z);

    }
}

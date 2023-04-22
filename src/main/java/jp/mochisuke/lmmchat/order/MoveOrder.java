package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.MoveGoal;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class MoveOrder extends AIOrderBase{

    public MoveOrder(Mob entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }
    //x,y,z
    private double x,y,z;
    public void startUp(Mob entity,VariablesContext context, List<Object> args){
        String x,y,z;
        x= (String) args.get(0);
        y= (String) args.get(1);
        z= (String) args.get(2);
        this.x=val(x);
        this.y=val(y);
        this.z=val(z);
    }
    @Override
    public void onSuccess() {
        notifyAI("moved");
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("moving failed:"+reason);
    }

    @Override
    public void executeImpl() {

        activateGoal(MoveGoal.class,x,y,z);
    }
}

package jp.mochisuke.lmmchat.order;

import net.minecraft.world.entity.Mob;

import java.util.List;

public class PositionOrder extends AIOrderBase{
    //get current position
    public PositionOrder(Mob entity,VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(Mob entity, VariablesContext context, List<Object> args) {

    }

    @Override
    public void executeImpl() {
        notifyAI("position:"+entity.blockPosition().getX()+","+entity.blockPosition().getY()+","+entity.blockPosition().getZ());
        //store
        val("x",entity.blockPosition().getX());
        val("y",entity.blockPosition().getY());
        val("z",entity.blockPosition().getZ());
    }
}

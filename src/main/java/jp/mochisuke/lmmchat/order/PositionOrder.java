package jp.mochisuke.lmmchat.order;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class PositionOrder extends AIOrderBase{
    //get current position
    public PositionOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
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
    public void executeImpl() {
        notifyAI("position:"+entity.blockPosition().getX()+","+entity.blockPosition().getY()+","+entity.blockPosition().getZ());
        //store
        val("x",entity.blockPosition().getX());
        val("y",entity.blockPosition().getY());
        val("z",entity.blockPosition().getZ());
    }
}

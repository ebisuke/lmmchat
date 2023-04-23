package jp.mochisuke.lmmchat.order;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class SwapHandOrder extends AIOrderBase{
    public SwapHandOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
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
        notifyAI("Hand item swapped");
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("SwapHand failed:"+reason);
    }

    @Override
    protected void executeImpl() {
        //swap

        var mainhanditem=entity.getMainHandItem();
        var offhanditem=entity.getOffhandItem();
        entity.setItemInHand(InteractionHand.MAIN_HAND,offhanditem);
        entity.setItemInHand(InteractionHand.OFF_HAND,mainhanditem);

        //swing hand
        entity.swing(entity.getUsedItemHand());




    }
}

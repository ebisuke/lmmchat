package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.lmm.LMMChatMode;
import jp.mochisuke.lmmchat.lmm.LMMEntityWrapper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

import java.util.List;

public class TransitionNormalModeOrder extends AIOrderBase{
    public TransitionNormalModeOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
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
    protected void executeImpl() {
        LMMEntityWrapper wrapper = new LMMEntityWrapper((TamableAnimal) entity);
        wrapper.setChatMode(LMMChatMode.NORMAL);

    }
}

package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.lmm.LMMChatMode;
import jp.mochisuke.lmmchat.lmm.LMMEntityWrapper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

import java.util.List;

public class TransitionComputerCraftModeOrder extends AIOrderBase {
    private int x;
    private int y;
    private int z;
    public TransitionComputerCraftModeOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        x=val((String) args.get(0));
        y=val((String) args.get(1));
        z=val((String) args.get(2));
    }

    @Override
    protected boolean isImmediate() {
        return true;
    }

    @Override
    protected void executeImpl() {
        LMMEntityWrapper wrapper = new LMMEntityWrapper((TamableAnimal) entity);
        wrapper.setChatMode(LMMChatMode.COMPUTERCRAFT);
        //save block position


        wrapper.setNBTMeta("lmmchat:computerx",x);
        wrapper.setNBTMeta("lmmchat:computery",y);
        wrapper.setNBTMeta("lmmchat:computerz",z);

    }
}

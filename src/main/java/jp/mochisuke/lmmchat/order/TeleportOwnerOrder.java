package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.LMMChatConfig;
import jp.mochisuke.lmmchat.goal.TeleportOwnerGoal;
import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

import java.util.List;

public class TeleportOwnerOrder extends AIOrderBase {
    public TeleportOwnerOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
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
    public void onSuccess() {
        notifyAI("Teleport success");
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("Teleport failed: " + reason);
    }

    @Override
    protected void executeImpl() {
        //generate goal
        if(LMMChatConfig.getDisableTeleportOwner()){
            throw new IllegalStateException("teleportowner is disabled");
        }
        TeleportOwnerGoal goal = (TeleportOwnerGoal) Helper.getGoal((TamableAnimal) entity, TeleportOwnerGoal.class);
        if(goal.isCooldown()){
            throw new IllegalStateException("teleportowner is on cool down");
        }
        goal.setUp();

    }
}

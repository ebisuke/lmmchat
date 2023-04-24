package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.CraftingGoal;
import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class CraftingOrder extends AIOrderBase{

    protected String craftItemName;
    protected int craftAmount;
    public CraftingOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        craftItemName = (String) args.get(0);
        craftAmount = val((String) args.get(1));
    }

    @Override
    public void onSuccess() {
        notifyAI("craft "+craftAmount+ " "+craftItemName +" succeeded");
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("craft "+craftAmount+ " "+craftItemName +" failed: "+reason);
    }

    @Override
    protected boolean isImmediate() {
        return false;
    }

    @Override
    protected void executeImpl() {

        var craftingGoal= (CraftingGoal)Helper.getGoal((Mob) entity, CraftingGoal.class);
        craftingGoal.setup(craftItemName,craftAmount);


    }
}

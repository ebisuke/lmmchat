package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.AIGoalBase;
import jp.mochisuke.lmmchat.goal.BlockPlaceGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BlockPlaceOrder extends AIOrderBase{

    int x;
    int y;
    int z;
    String itemname;
    public BlockPlaceOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity,context, args);

    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        String x,y,z;
        x= (String) args.get(0);
        y= (String) args.get(1);
        z= (String) args.get(2);
        this.x=val(x);
        this.y=val(y);
        this.z=val(z);
        this.itemname=(String)args.get(3);
    }

    @Override
    protected boolean isImmediate() {
        return false;
    }

    @Override
    public void executeImpl() {
        AtomicReference<ItemStack> stack=null;
        ForgeRegistries.ITEMS.getValues().stream().filter(i->i.getName(ItemStack.EMPTY).getString().toLowerCase().
                contains(itemname)).findFirst().ifPresent(i->{
            stack.set(new ItemStack(i));
        });

        final ItemStack stack2=stack.get();
        Mob ai=(Mob)entity;
        ai.goalSelector.getAvailableGoals().stream().filter(g->g.getGoal() instanceof BlockPlaceGoal).findFirst().ifPresent(g->{
            logger.info("activate blockplacegoal");
            ((BlockPlaceGoal) g.getGoal()).setup(x,y,z,stack2);
            prepareGoal((AIGoalBase) g.getGoal());
        });
    }

}

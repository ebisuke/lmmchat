package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.BlockItemPickupGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BlockItemPickupOrder extends AIOrderBase{


    //x,y,z,itemname,minslotindex,maxslotindex
    private int x;
    private int y;
    private int z;
    private String itemname;
    private int minslotindex;
    private int maxslotindex;
    public BlockItemPickupOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);

    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        String x,y,z,minslotindex,maxslotindex;
        x= (String) args.get(0);
        y= (String) args.get(1);
        z= (String) args.get(2);
        this.x=val(x);
        this.y=val(y);
        this.z=val(z);
        minslotindex= (String) args.get(4);
        maxslotindex= (String) args.get(5);
        this.minslotindex=val(minslotindex);
        this.maxslotindex=val(maxslotindex);

        this.itemname=(String)args.get(3);
    }

    @Override
    protected boolean isImmediate() {
        return false;
    }

    @Override
    public void onSuccess() {
        notifyAI("Item picked ");
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("Item pick failed:"+reason);
    }

    @Override
    public void executeImpl() {

        //finditem from db
        AtomicReference<ItemStack> stack=new AtomicReference<>();

        ForgeRegistries.ITEMS.getValues().stream().filter(i->i.getName(ItemStack.EMPTY).getString().toLowerCase()
                .contains(itemname)).findFirst().ifPresent(i->{
            stack.set(new ItemStack(i));
        });

        final ItemStack stack2=stack.get();
        Mob mob=(Mob)entity;
        mob.goalSelector.getAvailableGoals().stream().filter(g->g.getGoal() instanceof BlockItemPickupGoal).findFirst().ifPresent(g->{
            logger.info("activate blockitempickupgoal");
            ((BlockItemPickupGoal) g.getGoal()).setup(x,y,z,stack2,minslotindex,maxslotindex);
        });
    }
}

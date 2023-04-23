package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.GiveItemGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ItemGiveOrder extends AIOrderBase{


    //x,y,z,itemname,minslotindex,maxslotindex
    private String itemname;
    private int targetid;
    private int itemcount;
    public ItemGiveOrder(LivingEntity entity,VariablesContext context, List<Object> args) {
        super(entity, context, args);

    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        this.itemname=(String)args.get(0);
        String targetid,itemcount;
        targetid= (String) args.get(1);
        itemcount= (String) args.get(2);
        this.targetid=Integer.parseInt(targetid);
        this.itemcount=val(itemcount);

    }

    @Override
    protected boolean isImmediate() {
        return false;
    }

    @Override
    public void onSuccess() {
        notifyAI("Item gave ");
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("Item giving failed:"+reason);
    }

    @Override
    public void executeImpl() {

        //finditem from db
        AtomicReference<ItemStack> stack=null;

        ForgeRegistries.ITEMS.getValues().stream().filter(i->i.getDescriptionId().toLowerCase().contains(itemname)).findFirst().ifPresent(i->{
            stack.set(new ItemStack(i));
        });

        final ItemStack stack2=stack.get();
        //entity id to entity
        var target=(LivingEntity) entity.getLevel().getEntity(targetid);
        Mob mob=(Mob)entity;
        mob.goalSelector.getAvailableGoals().stream().filter(g->g.getGoal() instanceof GiveItemGoal).findFirst().ifPresent(g->{
            ((GiveItemGoal) g.getGoal()).setup(target,stack2,itemcount);
            prepareGoal((GiveItemGoal) g.getGoal());
        });
    }
}

package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.GiveItemGoal;
import jp.mochisuke.lmmchat.goal.TakeItemGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ItemTakeOrder extends AIOrderBase{


    //x,y,z,itemname,minslotindex,maxslotindex
    private String itemname;
    private int targetid;
    private int itemcount;
    public ItemTakeOrder(Mob entity,VariablesContext context, List<Object> args) {
        super(entity, context, args);

    }

    @Override
    protected void startUp(Mob entity, VariablesContext context, List<Object> args) {
        this.itemname=(String)args.get(0);
        String targetid,itemcount;
        targetid= (String) args.get(1);
        itemcount= (String) args.get(2);
        this.targetid=val(targetid);
        this.itemcount=val(itemcount);

    }

    @Override
    public void onSuccess() {
        notifyAI("Item took ");
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("Item take failed:"+reason);
    }

    @Override
    public void executeImpl() {

        //finditem from db
        AtomicReference<ItemStack> stack=null;

        ForgeRegistries.ITEMS.getValues().stream().filter(i->i.getDescriptionId().contains(itemname)).findFirst().ifPresent(i->{
            stack.set(new ItemStack(i));
        });

        final ItemStack stack2=stack.get();
        //entity id to entity
        var target=(LivingEntity) entity.getLevel().getEntity(targetid);
        this.entity.goalSelector.getAvailableGoals().stream().filter(g->g.getGoal() instanceof GiveItemGoal).findFirst().ifPresent(g->{
            ((TakeItemGoal) g.getGoal()).activate(target,stack2,itemcount);
        });
    }
}

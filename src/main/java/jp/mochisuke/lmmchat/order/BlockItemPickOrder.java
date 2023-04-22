package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.BlockItemPickupGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BlockItemPickOrder extends AIOrderBase{


    //x,y,z,itemname,minslotindex,maxslotindex
    private int x;
    private int y;
    private int z;
    private String itemname;
    private int minslotindex;
    private int maxslotindex;
    public BlockItemPickOrder(Mob entity, List<Object> args, int x, int y, int z, String itemname, int minslotindex, int maxslotindex) {
        super(entity, args);

        this.x=x;
        this.y=y;
        this.z=z;
        this.itemname=itemname;
        this.minslotindex=minslotindex;
        this.maxslotindex=maxslotindex;
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
    public void execute() {

        //finditem from db
        AtomicReference<ItemStack> stack=null;

        ForgeRegistries.ITEMS.getValues().stream().filter(i->i.getDescriptionId().equals(itemname)).findFirst().ifPresent(i->{
            stack.set(new ItemStack(i));
        });

        final ItemStack stack2=stack.get();

        this.entity.goalSelector.getAvailableGoals().stream().filter(g->g.getGoal() instanceof BlockItemPickupGoal).findFirst().ifPresent(g->{
            ((BlockItemPickupGoal) g.getGoal()).activate(x,y,z,stack2,minslotindex,maxslotindex);
        });
    }
}

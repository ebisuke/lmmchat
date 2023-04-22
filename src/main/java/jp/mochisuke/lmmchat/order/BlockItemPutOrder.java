package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.goal.BlockItemPutGoal;
import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BlockItemPutOrder extends AIOrderBase{


    //x,y,z,itemname,minslotindex,maxslotindex
    private int x;
    private int y;
    private int z;
    private String itemname;
    private int minslotindex;
    private int maxslotindex;
    public BlockItemPutOrder(Mob entity, List<Object> args, int x, int y, int z, String itemname, int minslotindex, int maxslotindex) {
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
        notifyAI("Item put");
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("Item put failed:"+reason);
    }

    @Override
    public void execute() {
        //activate blockitemputgoal

        //convert itemname to item
        var container= Helper.getInventoryContainer(this.entity);
        ItemStack stack=null;
        for(int i=0;i<container.getContainerSize();i++){
            var item=container.getItem(i);
            if(item.getHoverName().getString().equals(itemname)){
                stack=item;
                break;
            }
        }
        final ItemStack stack2=stack;
        this.entity.goalSelector.getAvailableGoals().stream().filter(g->g.getGoal() instanceof BlockItemPutGoal).findFirst().ifPresent(g->{
            ((BlockItemPutGoal) g.getGoal()).activate(x,y,z,stack2,minslotindex,maxslotindex);
        });
    }
}

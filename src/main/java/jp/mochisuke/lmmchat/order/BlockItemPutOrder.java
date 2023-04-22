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
    public BlockItemPutOrder(Mob entity,VariablesContext context, List<Object> args) {
        super(entity,context, args);

    }

    @Override
    protected void startUp(Mob entity, VariablesContext context, List<Object> args) {
        String x,y,z,minslotindex,maxslotindex;
        x= (String) args.get(0);
        y= (String) args.get(1);
        z= (String) args.get(2);
        this.x=val(x);
        this.y=val(y);
        this.z=val(z);
        minslotindex= (String) args.get(4);
        maxslotindex= (String) args.get(5);
        this.itemname=(String)args.get(3);
        this.minslotindex=val(minslotindex);
        this.maxslotindex=val(maxslotindex);
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
    public void executeImpl() {
        //activate blockitemputgoal

        //convert itemname to item
        var container= Helper.getInventoryContainer(this.entity);
        ItemStack stack=null;
        for(int i=0;i<container.getContainerSize();i++){
            var item=container.getItem(i);
            if(item.getHoverName().getString().contains(itemname)){
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

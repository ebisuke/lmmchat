package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class InventoryOrder extends AIOrderBase{


    //x,y,z
    public InventoryOrder(Mob entity,VariablesContext context, List<Object> args) {
        super(entity, context, args);

    }

    @Override
    protected void startUp(Mob entity, VariablesContext context, List<Object> args) {

    }


    @Override
    public void executeImpl() {
        Container container= Helper.getInventoryContainer(entity);
        //enumerate inventory
        for(int i=0;i<container.getContainerSize();i++){
            if(container.getItem(i).isEmpty()){
                notifyAI("slot " + i + " : (empty)");
            }else {
                notifyAI("slot " + i + " : " + container.getItem(i).getItem().getDescriptionId() + " x" + container.getItem(i).getCount());
            }
        }

    }
}

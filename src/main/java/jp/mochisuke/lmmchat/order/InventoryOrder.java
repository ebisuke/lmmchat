package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class InventoryOrder extends AIOrderBase{


    //x,y,z
    public InventoryOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);

    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {

    }

    @Override
    protected boolean isImmediate() {
        return true;
    }


    @Override
    public void executeImpl() {
        Container container = Helper.getInventoryContainer(entity);
        //enumerate inventory
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (container.getItem(i).isEmpty()) {
                notifyAI("slot " + i + " : (empty)");
            }else {
                notifyAI("slot " + i + " : " + container.getItem(i).getDisplayName().getString() + " x" + container.getItem(i).getCount());
            }
        }

    }
}

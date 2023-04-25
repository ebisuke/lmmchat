package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CheckItemOrder extends AIOrderBase{

    String itemName;
    public CheckItemOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        if(args.size()>0){
            String slotIndexStr=args.get(0).toString();
            itemName=valstr(slotIndexStr);
        }else{
            itemName=null;
        }
    }

    @Override
    protected boolean isImmediate() {
        return true;
    }

    @Override
    protected void executeImpl() {
        //check main hand item
        Mob mob=(Mob)entity;
        Container inventory = Helper.getInventoryContainer(mob);
        ItemStack item;
        if(itemName!=null){
            //if itemname is integer, use it as slot index
            try {
                int slotindex = Integer.parseInt(itemName);
                item=inventory.getItem(slotindex);
            }catch(NumberFormatException e){
                item=Helper.findItemStack(inventory,itemName).getB();
            }

        }else{
            item=entity.getMainHandItem();
        }
        String message = "";
        //itemname,count,dur,enchants
        message+="Item name:"+item.getItem().getName(item).getString()+"\n";
        message+="Internal Name"+item.getItem().getDescriptionId()+"\n";
        message+="Count:"+item.getCount()+"\n";
        if(item.getMaxDamage()>0) {
            message += "Durability:" + item.getDamageValue() + "/" + item.getMaxDamage() + "\n";
        }
        //item kind
        message+="Item kind:"+item.getItem().getClass().getName()+"\n";
        message+="Enchantments:"+item.getEnchantmentTags().toString();
        notifyAI(message);

    }
}

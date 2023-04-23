package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WieldOrder extends AIOrderBase {

    int slotIndex;
    String to;
    public WieldOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        //get slot index
        this.slotIndex = val((String) args.get(0));
        this.to=(String)args.get(1);
    }

    @Override
    protected boolean isImmediate() {
        return true;
    }

    @Override
    public void onSuccess() {
        notifyAI("Wielded to main hand");
    }

    @Override
    public void onFailed(String reason) {
        notifyAI("WieldToMainHand failed:"+reason);
    }

    @Override
    protected void executeImpl() {
        //get item from inventory
        Mob mob = (Mob) entity;
        Container inventory = Helper.getInventoryContainer(mob);
        //OK
        var item = inventory.getItem(slotIndex);
        if (item.isEmpty()) {
            throw new RuntimeException("Item is empty");
        }
        ItemStack oldItem;
        switch(to){
            case "mainhand":
                oldItem=entity.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND);
                entity.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,item);
                entity.playSound(SoundEvents.ARMOR_EQUIP_GENERIC,1.0f,1.0f);
                break;
            case "offhand":
                oldItem=entity.getItemInHand(net.minecraft.world.InteractionHand.OFF_HAND);
                entity.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND,item);
                entity.playSound(SoundEvents.ARMOR_EQUIP_GENERIC,1.0f,1.0f);

                break;
            case "head":
                oldItem=entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
                entity.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,item);
                entity.playSound(net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC,1.0f,1.0f);

                break;
            case "chest":
                oldItem=entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
                entity.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST,item);
                entity.playSound(net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC,1.0f,1.0f);

                break;
            case "legs":
                oldItem=entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS);
                entity.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS,item);
                entity.playSound(net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC,1.0f,1.0f);

                break;
            case "feet":
                oldItem=entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET);
                entity.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET,item);
                entity.playSound(net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC,1.0f,1.0f);

                break;
            default:
                throw new RuntimeException("Invalid hand:"+to);
        }
        //put old item to inventory
        if(!oldItem.isEmpty()){
            inventory.setItem(slotIndex,oldItem);
        }else{
            inventory.removeItem(slotIndex,1);
        }


    }
}

package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WieldOrder extends AIOrderBase {

    String itemName;
    String to;
    public WieldOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        //get slot index
        this.itemName = valstr((String) args.get(0));
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
        ItemStack item;
        //OK
        int slotindex=0;
        if(itemName!=null){
            //if itemname is integer, use it as slot index
            try {
                slotindex = Integer.parseInt(itemName);
                item=inventory.getItem(slotindex);
            }catch(NumberFormatException e){
                var ret=Helper.findItemStack(inventory,itemName);
                item=ret.getB();
                slotindex=ret.getA();
            }

        }else{
            item=entity.getMainHandItem();
            slotindex=-1;
        }
        ItemStack oldItem;
        switch (to) {
            case "mainhand" -> {
                oldItem = entity.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND);
                entity.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, item);
                entity.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0f, 1.0f);
            }
            case "offhand" -> {
                oldItem = entity.getItemInHand(net.minecraft.world.InteractionHand.OFF_HAND);
                entity.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, item);
                entity.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0f, 1.0f);
            }
            case "head" -> {
                oldItem = entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
                entity.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, item);
                entity.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0f, 1.0f);
            }
            case "chest" -> {
                oldItem = entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
                entity.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, item);
                entity.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0f, 1.0f);
            }
            case "legs" -> {
                oldItem = entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS);
                entity.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS, item);
                entity.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0f, 1.0f);
            }
            case "feet" -> {
                oldItem = entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET);
                entity.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET, item);
                entity.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0f, 1.0f);
            }
            default -> throw new RuntimeException("Invalid hand:" + to);
        }
        //put old item to inventory
        if(slotindex!=-1){
            //mainhand
            if (!oldItem.isEmpty()) {
                entity.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, oldItem);
            } else {
                entity.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
            //swing
            entity.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        }else {
            if (!oldItem.isEmpty()) {
                inventory.setItem(slotindex, oldItem);
            } else {
                inventory.removeItem(slotindex, 1);
            }
        }



    }
}

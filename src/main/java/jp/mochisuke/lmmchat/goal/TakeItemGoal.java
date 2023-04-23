package jp.mochisuke.lmmchat.goal;

import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.sistr.littlemaidrebirth.entity.util.HasInventory;

import java.lang.reflect.Field;
import java.util.EnumSet;

public class TakeItemGoal  <T extends PathfinderMob & HasInventory>  extends AIGoalBase {
    protected final T entity;

    private LivingEntity targetEntity;

    private ItemStack giveItemStack;
    private int itemCount;
    public TakeItemGoal(T entity) {
        this.entity = entity;
    }
    @Override
    public EnumSet<Flag> getFlags() {
        return EnumSet.of(Flag.TARGET,Flag.MOVE);
    }

    public void setup(LivingEntity targetEntity, ItemStack giveItemStack,int itemCount){

        this.targetEntity=targetEntity;
        this.giveItemStack=giveItemStack;
        this.itemCount=itemCount;
        super.activate();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }

    @Override
    public void start() {
        //follow to target
        if(this.targetEntity==null || this.giveItemStack==null || this.itemCount<=0){
            return;
        }

    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return super.requiresUpdateEveryTick();
    }

    @Override
    public void tick() {
        if(!canUse()) {
            return;
        }
        //follow to target per 60 ticks
        if(this.entity.tickCount%60==1){
            this.entity.getNavigation().moveTo(this.targetEntity.getX(),this.targetEntity.getY(),this.targetEntity.getZ(),1);
        }
        // is target near?
        if(this.entity.distanceTo(this.targetEntity)<5){
            //give item

            //get inventory using java reflection
            Container container=null;

            //get container typed fields
            Field[] fields = targetEntity.getClass().getDeclaredFields();

            //find container field
            for(Field field:fields){
                if(field.getType().equals(Container.class)){
                    field.setAccessible(true);
                    try {
                        container=(Container)field.get(targetEntity);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            //found?
            if(container==null){
                fail("target is not have inventory");
                return;
            }
            int remain=itemCount;
            //get inventory
            for(int i=0;i<container.getContainerSize();i++){
                ItemStack itemStack=container.getItem(i);
                //is itemStack empty?
                if(itemStack.isEmpty()){
                    continue;
                }
                //is itemStack same as giveItemStack?
                if(itemStack.sameItem(giveItemStack)){
                    //give item
                    for(int idx=0;idx<entity.getInventory().getContainerSize();idx++){
                        ItemStack itemStack1=entity.getInventory().getItem(idx);

                        int storablecount;
                        //is itemStack1 empty?
                        if(itemStack1.isEmpty()) {
                            storablecount = itemStack1.getMaxStackSize();
                        }else {
                            storablecount = itemStack1.getMaxStackSize() - itemStack1.getCount();
                        }

                        //is itemStack1 same as giveItemStack?
                        if(itemStack1.sameItem(giveItemStack)){
                            //is itemStack1 full?
                            if(itemStack1.getCount()>=itemStack1.getMaxStackSize()){
                                continue;
                            }
                            //is itemStack1 can store more?
                            if(storablecount>remain){
                                storablecount=remain;
                            }
                            //give item
                            itemStack1.grow(storablecount);
                            itemStack.shrink(storablecount);
                            remain-=storablecount;
                            //is remain 0?
                            if(remain==0){
                                success();
                                return;
                            }
                        }else{
                            //is itemStack1 empty?
                            if(itemStack1.isEmpty()){
                                //give item
                                itemStack1=itemStack.copy();
                                itemStack1.setCount(storablecount);
                                itemStack.shrink(storablecount);
                                remain-=storablecount;
                                //is remain 0?
                                if(remain==0){
                                    success();
                                    return;
                                }
                            }
                        }

                    }

                }
            }

            fail("inventory is full");




        }
    }
}

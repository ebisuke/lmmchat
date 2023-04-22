package jp.mochisuke.lmmchat.goal;

import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.sistr.littlemaidrebirth.entity.util.HasInventory;

import java.lang.reflect.Field;

public class GiveItemGoal <T extends PathfinderMob & HasInventory>  extends CallbackedGoal {

    protected final T entity;

    private LivingEntity targetEntity;

    private ItemStack giveItemStack;

    public GiveItemGoal(T entity) {
        this.entity = entity;
    }

    public void activate(LivingEntity targetEntity, ItemStack giveItemStack){
        this.active=true;
        this.targetEntity=targetEntity;
        this.giveItemStack=giveItemStack;
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
        this.entity.moveTo(this.targetEntity.getX(),this.targetEntity.getY(),this.targetEntity.getZ());

    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
        fail("interrupted");
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return super.requiresUpdateEveryTick();
    }

    @Override
    public void tick() {
        //follow to target per 60 ticks
        if(this.entity.tickCount%60==0){
            this.entity.moveTo(this.targetEntity.getX(),this.targetEntity.getY(),this.targetEntity.getZ());
        }
        // is target near?
        if(this.entity.distanceTo(this.targetEntity)<1.5){
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
                fail("target entity has no inventory");
                return;
            }



            //find empty slot or stack item
            for(int slot=0;slot<container.getContainerSize();slot++ ){
                ItemStack itemStack=container.getItem(slot);
                if(itemStack.isEmpty()){
                    //empty slot
                    container.setItem(slot,giveItemStack);
                    success();
                    return;
                }else if(itemStack.sameItem(giveItemStack)){
                    //stack item
                    int stackSize=itemStack.getCount()+giveItemStack.getCount();
                    if(stackSize<=itemStack.getMaxStackSize()){
                        //stackable
                        itemStack.setCount(stackSize);
                        container.setItem(slot,itemStack);
                        success();
                        return;
                    }else{
                        //not stackable
                        int remain=stackSize-itemStack.getMaxStackSize();
                        itemStack.setCount(itemStack.getMaxStackSize());
                        container.setItem(slot,itemStack);
                        giveItemStack.setCount(remain);
                    }
                }
            }
            // not found empty slot or stack item
            fail("not found empty slot or stack item");
        }
    }
}

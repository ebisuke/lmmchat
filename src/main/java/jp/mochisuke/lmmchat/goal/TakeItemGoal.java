package jp.mochisuke.lmmchat.goal;

import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.sistr.littlemaidrebirth.entity.util.HasInventory;

import java.lang.reflect.Field;

public class TakeItemGoal  <T extends PathfinderMob & HasInventory>  extends CallbackedGoal {
    protected final T entity;

    private LivingEntity targetEntity;

    private ItemStack giveItemStack;

    public TakeItemGoal(T entity) {
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
                active=false;
                return;
            }

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
                        //is itemStack1 empty?
                        if(itemStack1.isEmpty()){
                            //put itemStack to entity inventory
                            entity.getInventory().setItem(idx,itemStack);
                            //remove item from target
                            container.removeItem(i,1);
                            //stop
                            active=false;
                            return;
                        }else if(itemStack1.sameItem(itemStack)){
                            //put itemStack to entity inventory
                            entity.getInventory().setItem(idx,itemStack);
                            //remove item from target
                            container.removeItem(i,1);
                            //stop
                            active=false;
                            return;
                        }
                    }

                }
            }

            // inventory is full
            active=false;




        }
    }
}
